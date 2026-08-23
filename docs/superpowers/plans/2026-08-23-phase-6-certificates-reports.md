# Phase 6 — Certificates & Reports Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port legacy ePT's certificate/report generation subsystem — two distinct mechanisms (AcroForm-fillable certificate templates, and hardcoded per-scheme "performance report" PDFs) — into this app, using Apache PDFBox/POI, scoped to Zimbabwe's report layout and the 9 active schemes.

**Architecture:** Two independent PDF pipelines, both wired into Phase 0.2's generic `ScheduledJob` async infrastructure (one job type, `REPORT_GENERATION`, payload `{shipmentId, reportType, certificateType}`) rather than a parallel queue table:
1. **Certificates** — admin-uploaded fillable PDF templates (participation/excellence per scheme), validated and filled via PDFBox's AcroForm API.
2. **Performance reports** — the Individual/Summary "Performance Report" PDFs, rendered by drawing text at fixed positions onto an imported report-format template page, scoped to the `layout == "zimbabwe"` rendering rules only (see Scope Correction below).

**Tech Stack:** Spring Boot 4, PDFBox (`org.apache.pdfbox:pdfbox`), Apache POI (`org.apache.poi:poi-ooxml`), React + Redux frontend, Chart.js + `react-chartjs-2` for in-app charts.

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 6 section) — this plan implements and, where the code disagreed with the spec's assumptions, corrects it (see below). Also: `/home/administrator/Documents/Development/ept/application/services/CertificateTemplates.php`, `library/Pt/Reports/{FpdiReport,IndividualPdf,SummaryPdf}.php`, `application/services/Evaluation.php` (job-queue methods, lines 4136–4400+), `application/modules/reports/controllers/FinalizeController.php`.

## Global Constraints

- PDF generation: **Apache PDFBox** only. Do not add iText (AGPL license conflict, per the spec's resolved decision).
- Excel/Word export: **Apache POI** (`poi-ooxml`) only, shared with Phase 2's bulk-import dependency if it's already added — do not add a second Excel/Word library.
- Charts: client-side **Chart.js + react-chartjs-2** for every in-app visual. Server-side chart rendering (JFreeChart) is reserved for the rare case a PDF needs an embedded chart image — do not build it speculatively.
- Report rendering scope: **`layout == "zimbabwe"` only.** The legacy renderer classes hardcode report layout/branding for ~6 other countries (Myanmar, Jamaica, Vietnam, Malawi, Philippines, plus a generic default) via string-literal `if/elseif` branches keyed on a `layout` property — mirroring the same multi-country pattern found in DTS scoring (Phase 5). Per the master plan's Zimbabwe-first scope decision, only the `zimbabwe` branches get ported. Do not implement the other countries' branches; do not remove them from the legacy reference reading, just don't build them.
- Async generation only — never generate a certificate or report PDF synchronously in a request thread. Always enqueue via `ScheduledJob` (Phase 0.2) and poll/notify on completion.

## Scope correction found during research (report to the master plan owner)

The master plan's Phase 6 section describes certificate generation as "overlay dynamic text (name, scheme, score, dates, watermark) onto a pre-uploaded PDF template" implying fixed-coordinate text overlay (matching legacy's FPDI usage in the *report* renderers). Reading `CertificateTemplates.php` directly shows this is **wrong for certificates specifically**: certificate templates are validated by extracting **AcroForm field names** via `pdftk ... dump_data_fields`, and `uploadTemplate()` requires the template to contain at least one named form field matching `participant_name`/`participantname`/`labname`/`participant`. This means certificates are **fillable PDF forms**, filled by field name, not overlaid at hardcoded coordinates — a materially different (and easier) mechanism to port with PDFBox's `PDAcroForm`/`PDField.setValue()` API. Coordinate-overlay rendering (via TCPDF/FPDI, `writeHTMLCell`, `RotatedText`, etc.) is what the **separate** Individual/Summary Performance Report renderers (`IndividualPdf.php`, `SummaryPdf.php`, `FpdiReport.php`) actually do — these are a **second, independent PDF pipeline**, not the certificate mechanism. This plan builds both, correctly separated (Task 3 vs. Tasks 5–6).

Also found: `getTemplateFilePath()`/AcroForm-fill usage has **no call site anywhere else in the legacy codebase** (grepped for `fillForm`/`AcroForm`/`setFieldValue` — only hits are in `CertificateTemplates.php` itself and its controller/view), and no real uploaded certificate-template files exist in this session's migrated file snapshot (`/home/administrator/Documents/Development/legacy-ept-files-target/` has no `certificate-templates/` directory). By contrast, the **report-format templates are real and in active use** — `global_config.report-format` points at `loEb2e-zw-template-RM--1-.pdf`, and 6 real Zimbabwe-branded template PDFs exist at `legacy-ept-files-target/uploads/report-formats/`. **Recommendation: build Tasks 5–6 (performance reports) before Task 3 (certificates)** — they have real production evidence and real test fixtures; certificates may be an unfinished/unused legacy feature. Confirm with the team before investing in certificate polish.

The current app's data model already anticipated both mechanisms correctly: `ReportConfiguration.layout` (String) exists as a field today, and `CertificateTemplate.certificateType` (a `CertificateType` enum), `fileRef`, `detectedFields` (String) all map directly onto the legacy concepts found above — confirms the target shape is already right.

---

## Task 1: Add PDF/Excel dependencies

**Files:**
- Modify: `pom.xml`

**Interfaces:**
- Produces: `org.apache.pdfbox:pdfbox` and `org.apache.poi:poi-ooxml` available on the classpath for all later tasks.

- [ ] **Step 1: Check for existing dependencies**

Run: `grep -n "pdfbox\|poi-ooxml" pom.xml`
Expected: no matches (confirmed absent as of this plan's writing — if Phase 2's bulk-import work already added `poi-ooxml`, skip adding it again here).

- [ ] **Step 2: Add PDFBox and POI to `pom.xml`**

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.3</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>
```

- [ ] **Step 3: Verify the build picks them up**

Run: `./mvnw -q dependency:tree -Dincludes=org.apache.pdfbox,org.apache.poi`
Expected: both artifacts listed.

- [ ] **Step 4: Commit**

```bash
git add pom.xml
git commit -m "build: add PDFBox and Apache POI for certificate/report generation"
```

---

## Task 2: Certificate template upload + AcroForm validation

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/CertificateTemplateValidationService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/CertificateTemplateValidationServiceTest.java`
- Modify: `src/main/java/zw/org/nmrl/ept/service/impl/CertificateTemplateServiceImpl.java` (wire validation into save/upload path)

**Interfaces:**
- Consumes: nothing new — reads an uploaded `MultipartFile`/`byte[]` PDF.
- Produces: `CertificateTemplateValidationService.validate(byte[] pdfBytes) -> ValidationResult` where `ValidationResult` is a record `(boolean valid, List<String> fields, String error)`. Later tasks (Task 3) consume `fields` to know which AcroForm field names are fillable.

Replaces legacy's external `pdftk dump_data_fields` shell-out (`CertificateTemplates.php:findPdftk()`/`validatePdfTemplate()`) with PDFBox's native AcroForm inspection — no external process dependency.

- [ ] **Step 1: Write the failing test**

```java
class CertificateTemplateValidationServiceTest {

    @Test
    void rejectsTemplateWithNoAcroFormFields() throws IOException {
        byte[] plainPdf = TestPdfFixtures.blankPdfNoForm(); // helper: PDDocument with one blank page, no AcroForm
        CertificateTemplateValidationService.ValidationResult result = new CertificateTemplateValidationService().validate(plainPdf);
        assertThat(result.valid()).isFalse();
        assertThat(result.error()).contains("No form fields detected");
    }

    @Test
    void acceptsTemplateWithParticipantNameField() throws IOException {
        byte[] pdfWithField = TestPdfFixtures.pdfWithTextField("participant_name");
        CertificateTemplateValidationService.ValidationResult result = new CertificateTemplateValidationService().validate(pdfWithField);
        assertThat(result.valid()).isTrue();
        assertThat(result.fields()).contains("participant_name");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -q test -Dtest=CertificateTemplateValidationServiceTest`
Expected: FAIL — `CertificateTemplateValidationService` does not exist yet (also add a `TestPdfFixtures` test helper under `src/test/java/zw/org/nmrl/ept/service/` that builds minimal in-memory PDFs with PDFBox's `PDAcroForm`/`PDTextField` for use here and in Task 3's tests).

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;

@Service
public class CertificateTemplateValidationService {

    private static final Set<String> REQUIRED_PARTICIPANT_FIELDS = Set.of(
        "participant_name", "participantname", "labname", "participant"
    );

    public record ValidationResult(boolean valid, List<String> fields, String error) {}

    public ValidationResult validate(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            List<String> fields = new ArrayList<>();
            if (acroForm != null) {
                for (PDField field : acroForm.getFieldTree()) {
                    fields.add(field.getFullyQualifiedName());
                }
            }
            boolean hasRequiredField = fields.stream()
                .anyMatch(f -> REQUIRED_PARTICIPANT_FIELDS.contains(f.toLowerCase()));
            if (!hasRequiredField) {
                String error = fields.isEmpty()
                    ? "No form fields detected in the PDF. Required: one of " + REQUIRED_PARTICIPANT_FIELDS
                    : "Missing required participant name field. Found: " + fields + ". Required: one of " + REQUIRED_PARTICIPANT_FIELDS;
                return new ValidationResult(false, fields, error);
            }
            return new ValidationResult(true, fields, "");
        } catch (IOException e) {
            return new ValidationResult(false, List.of(), "Failed to read PDF: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -q test -Dtest=CertificateTemplateValidationServiceTest`
Expected: PASS

- [ ] **Step 5: Wire into `CertificateTemplateServiceImpl`'s save/upload path**

Modify the existing `save`/create flow so an uploaded template's bytes run through `CertificateTemplateValidationService.validate(...)` before persisting; on failure, throw a `BadRequestAlertException` with the validation error (matching this repo's existing REST error-handling convention — check `web/rest/errors/` for the exact exception class name used elsewhere). On success, persist `detectedFields` (comma-joined `fields()`) onto the `CertificateTemplate` entity — this field already exists.

- [ ] **Step 6: Run the full test class plus the existing `CertificateTemplateResourceIT`**

Run: `./mvnw -q test -Dtest=CertificateTemplateValidationServiceTest,CertificateTemplateResourceIT`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/CertificateTemplateValidationService.java \
        src/test/java/zw/org/nmrl/ept/service/CertificateTemplateValidationServiceTest.java \
        src/main/java/zw/org/nmrl/ept/service/impl/CertificateTemplateServiceImpl.java
git commit -m "feat: validate certificate template AcroForm fields via PDFBox"
```

---

## Task 3: Certificate generation (AcroForm fill)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/CertificateGenerationService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/CertificateGenerationServiceTest.java`

**Interfaces:**
- Consumes: `CertificateTemplateValidationService.ValidationResult.fields()` (Task 2) to know which field names are safe to set.
- Produces: `CertificateGenerationService.generate(byte[] templatePdf, Map<String, String> fieldValues) -> byte[]` (filled, flattened PDF bytes). Task 4's job processor calls this.

- [ ] **Step 1: Write the failing test**

```java
class CertificateGenerationServiceTest {

    @Test
    void fillsParticipantNameFieldAndFlattens() throws IOException {
        byte[] template = TestPdfFixtures.pdfWithTextField("participant_name");
        byte[] filled = new CertificateGenerationService().generate(template, Map.of("participant_name", "Sally Mugabe Central Hospital"));

        try (PDDocument doc = Loader.loadPDF(filled)) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
            // After flattening, the AcroForm should be gone (or fields non-editable) — assert the rendered text is present instead.
            assertThat(form == null || form.getFields().isEmpty()).isTrue();
        }
        String extractedText = new PDFTextStripper().getText(Loader.loadPDF(filled));
        assertThat(extractedText).contains("Sally Mugabe Central Hospital");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -q test -Dtest=CertificateGenerationServiceTest`
Expected: FAIL — class doesn't exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;

@Service
public class CertificateGenerationService {

    public byte[] generate(byte[] templatePdf, Map<String, String> fieldValues) throws IOException {
        try (PDDocument document = Loader.loadPDF(templatePdf)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                throw new IllegalArgumentException("Template has no AcroForm — was it validated by CertificateTemplateValidationService?");
            }
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                PDField field = acroForm.getField(entry.getKey());
                if (field != null) {
                    field.setValue(entry.getValue());
                }
            }
            acroForm.flatten();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -q test -Dtest=CertificateGenerationServiceTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/CertificateGenerationService.java \
        src/test/java/zw/org/nmrl/ept/service/CertificateGenerationServiceTest.java
git commit -m "feat: generate certificates by filling AcroForm fields via PDFBox"
```

---

## Task 4: Wire report/certificate generation into the async job queue

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/domain/enumeration/` — add a `ScheduledJobType` value (or extend whatever enum Phase 0.2 introduced for job types; if Phase 0.2 hasn't landed yet, this task blocks on it — check `docs/superpowers/plans/2026-08-23-phase-0-cross-cutting-foundations.md` for the exact enum/class name before writing this task's real code).
- Create: `src/main/java/zw/org/nmrl/ept/service/ReportGenerationJobProcessor.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ReportGenerationJobProcessorTest.java`

**Interfaces:**
- Consumes: Phase 0.2's job-enqueue/poll contract (exact method names TBD by that phase's plan — this task's steps below use placeholder names `JobQueueService.enqueue(...)`/`JobQueueService.claim(...)` that MUST be corrected to match Phase 0.2's actual implementation before execution, per that plan).
- Produces: a shipment's `ScheduledJob` row (type `REPORT_GENERATION`, payload `{"shipmentId": ..., "reportType": "evaluated"|"finalized", "certificateType": "participation"|"excellence"|null}`) transitions to `COMPLETED` with the generated PDF bytes persisted via Phase 0.4's `FileStoreService`.

Mirrors legacy's `queueReportsGeneration()` (`Evaluation.php:4195`) and its dedicated `queue_report_generation` table — but per the master plan's "map onto the existing `ScheduledJob` entity" principle (Phase 0.2), this does NOT introduce a parallel table; it's one more job type on the shared queue.

- [ ] **Step 1: Write the failing test** (structure only — exact `JobQueueService` API to be filled in once Phase 0.2 lands)

```java
class ReportGenerationJobProcessorTest {

    @Test
    void processesReportGenerationJobAndPersistsPdf() {
        // Arrange: a finalized-eligible shipment with scored results exists (use a test fixture builder).
        // Act: enqueue a REPORT_GENERATION job for it, run the processor.
        // Assert: job transitions PENDING -> PROCESSING -> COMPLETED, and FileStoreService has a readable PDF at the expected path.
    }
}
```

- [ ] **Step 2–4:** Implement once Task 5/6 (the actual PDF renderers this processor calls) and Phase 0.2's job contract both exist — this task is a thin adapter, not new rendering logic. Do not write it before Phase 0.2 and Tasks 5/6 are done; sequence it last within this plan.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ReportGenerationJobProcessor.java \
        src/test/java/zw/org/nmrl/ept/service/ReportGenerationJobProcessorTest.java
git commit -m "feat: process report/certificate generation via the async job queue"
```

---

## Task 5: Individual Performance Report PDF (Zimbabwe layout)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/report/IndividualPerformanceReportRenderer.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/report/IndividualPerformanceReportRendererTest.java`

**Interfaces:**
- Consumes: a scored `ParticipantResult`/shipment-participant record (exact type depends on Phase 5's output — check that phase's plan for the finalized DTO/entity name before writing real code) plus a `ReportConfiguration` row (`layout`, `format` → template filename, `topMargin`, `instituteAddressPosition`, `logo`).
- Produces: `IndividualPerformanceReportRenderer.render(...) -> byte[]` (one PDF page/document per participant).

Ports `IndividualPdf.php`'s `zimbabwe`-layout branches only (verified against the real code — the class also branches on `myanmar`/`jamaica`/`vietnam`/default, all explicitly out of scope per this plan's Global Constraints):
- Header: for `schemeType` in `{recency, dts, vl, eid, tb, generic-test}` (the `generic-test` label covers this app's 4 config-driven schemes — `HBV RDT`/`HCV RDT`/`mRDT`/`SYPH RDT` — uniformly; the 5 bespoke schemes each get their own title line), logo placed at approximately `x=88, y=15, width=25mm` centered, header text drawn via a text block, then a per-scheme title line ("Proficiency Testing Report - Rapid HIV Serology Test" for `dts`, "...Rapid Test for Recent Infection (RTRI)" for `recency`, "...Viral Load using Dried Tube Specimen" for `vl`, "...Early Infant Diagnosis Using Dried Blood Spots" for `eid`, none for `tb`), then (except `tb`) a "FINAL " (if finalized) + "INDIVIDUAL PERFORMANCE REPORT" line, then a horizontal rule.
- Footer: fixed text "NATIONAL MICROBIOLOGY REFERENCE LABORATORY EXTERNAL QUALITY ASSURANCE SURVEY" + "*** All the contents of this report are strictly confidential ***" (red), centered — no "Report generated on {date}" line (that's the non-Zimbabwe default). Page number bottom-right, unless `schemeType == tb`.
- Import the report-format template's first page as the document background (PDFBox: `Utils.importPage`-equivalent via `PDPageContentStream` drawing the imported page's content stream, or use PDFBox's page-import utilities — the legacy mechanism is FPDI's `setSourceFile`/`ImportPage`/`useImportedPage`).

- [ ] **Step 1: Write the failing test using a real fixture**

```java
class IndividualPerformanceReportRendererTest {

    @Test
    void rendersZimbabweDtsIndividualReportWithFinalPrefix() throws IOException {
        byte[] template = Files.readAllBytes(Path.of(
            "/home/administrator/Documents/Development/legacy-ept-files-target/uploads/report-formats/loEb2e-zw-template-RM--1-.pdf"
        )); // real Zimbabwe report-format template, confirmed present in the migrated file store
        ReportConfiguration config = reportConfigWithLayout("zimbabwe");
        var renderedReport = someScoredDtsShipmentParticipant(finalized: true);

        byte[] pdf = new IndividualPerformanceReportRenderer().render(renderedReport, config, template);

        String text = new PDFTextStripper().getText(Loader.loadPDF(pdf));
        assertThat(text).contains("Proficiency Testing Report - Rapid HIV Serology Test");
        assertThat(text).contains("FINAL");
        assertThat(text).contains("NATIONAL MICROBIOLOGY REFERENCE LABORATORY EXTERNAL QUALITY ASSURANCE SURVEY");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -q test -Dtest=IndividualPerformanceReportRendererTest`
Expected: FAIL — class doesn't exist; `someScoredDtsShipmentParticipant`/`reportConfigWithLayout` test helpers need to be written once Phase 5's actual result type is known.

- [ ] **Step 3: Write minimal implementation**

Use `PDDocument`, `PDPage`, `LayerUtility` (for importing the template's first page as a form XObject background) and `PDPageContentStream` with `beginText()`/`showText()`/`newLineAtOffset()` calls at the coordinates translated from the legacy `writeHTMLCell(0, 0, x, y, ...)` calls documented above (PDFBox coordinates are bottom-left-origin in points; legacy TCPDF coordinates in the read source are top-left-origin in mm — convert carefully, and write a small `MmToPt`/Y-flip helper covered by its own unit test before using it here, since a coordinate-system bug here is silent and easy to ship wrong).

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -q test -Dtest=IndividualPerformanceReportRendererTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/report/IndividualPerformanceReportRenderer.java \
        src/test/java/zw/org/nmrl/ept/service/report/IndividualPerformanceReportRendererTest.java
git commit -m "feat: render Zimbabwe-layout individual performance report PDFs"
```

---

## Task 6: Summary Performance Report PDF (Zimbabwe layout)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/report/SummaryPerformanceReportRenderer.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/report/SummaryPerformanceReportRendererTest.java`

**Interfaces:**
- Consumes: all scored participants for a shipment (a `List<...>` of Phase 5's result type) + the same `ReportConfiguration`.
- Produces: `SummaryPerformanceReportRenderer.render(...) -> byte[]` (one multi-participant "All Participants Results Report" PDF).

Mirrors Task 5's structure but ported from `SummaryPdf.php` (388 lines, same `layout == 'zimbabwe'` branching pattern confirmed at lines 66, 93, 124, 140, 165, 188, 199, 334, 360, 368 of that file — read the exact zimbabwe-branch content at each of those line numbers before writing the real rendering code, don't assume symmetry with `IndividualPdf.php`). Also note `FpdiReport.php`'s distinct footer for this report family: a 3-column table — "Effective Date {date}" (left) | report version (center) | "Page X of Y" (right), 9px font.

- [ ] **Step 1: Write the failing test** (same fixture-based pattern as Task 5, asserting the summary-specific footer table content and multi-participant listing).
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Read the 10 zimbabwe-branch line numbers listed above in `SummaryPdf.php` and write the implementation** against their actual content — don't guess it's identical to `IndividualPdf.php`.
- [ ] **Step 4: Run test to verify it passes.**
- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/report/SummaryPerformanceReportRenderer.java \
        src/test/java/zw/org/nmrl/ept/service/report/SummaryPerformanceReportRendererTest.java
git commit -m "feat: render Zimbabwe-layout summary performance report PDFs"
```

---

## Task 7: Finalize gate

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/ShipmentFinalizationService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentFinalizationServiceTest.java`

**Interfaces:**
- Consumes: a shipment ID + optional `{resultsApprovedOn, resultsApprovedBy}` (matching legacy's `recordResultsApproval()` params).
- Produces: `ShipmentFinalizationService.finalize(Long shipmentId, FinalizeRequest request)` — sets the shipment's finalized state one-way, records `resultsApprovedOn`/`resultsApprovedBy` (falling back to now/current-admin if blank, **never overwriting an existing approval record** on re-finalize — port this exact guard from `Evaluation.php:recordResultsApproval()`), and enqueues a `REPORT_GENERATION` job (Task 4) with `reportType = "finalized"`.

- [ ] **Step 1: Write the failing test**

```java
class ShipmentFinalizationServiceTest {

    @Test
    void firstFinalizeRecordsApprovalWithFallbackToNowAndCurrentAdmin() { /* ... */ }

    @Test
    void reFinalizingWithBlankApprovalDoesNotOverwriteExistingApproval() {
        // Finalize once with an explicit approver/date, finalize again with blank fields,
        // assert the original approver/date are unchanged.
    }

    @Test
    void finalizedShipmentBecomesVisibleToParticipant() { /* assert participant-facing read endpoint now returns the result */ }

    @Test
    void unfinalizedShipmentResultsAreNotVisibleToParticipant() { /* negative case */ }
}
```

- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write minimal implementation**, referencing the exact approval-recording logic read from `Evaluation.php:4163-4193` (`recordResultsApproval`) — same fallback and never-overwrite rules.
- [ ] **Step 4: Run test to verify it passes.**
- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentFinalizationService.java \
        src/test/java/zw/org/nmrl/ept/service/ShipmentFinalizationServiceTest.java
git commit -m "feat: add one-way shipment finalize gate with approval recording"
```

---

## Task 8: Excel/Word export

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/report/ReportExportService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/report/ReportExportServiceTest.java`

**Interfaces:**
- Consumes: the same scored-participants data as Task 6.
- Produces: `ReportExportService.toExcel(...) -> byte[]` and `ReportExportService.toWord(...) -> byte[]` via Apache POI's `XSSFWorkbook`/`XWPFDocument`.

- [ ] **Step 1: Write the failing test** asserting an exported workbook has the expected header row + one row per participant with correct score/result values.
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write minimal implementation** using `XSSFWorkbook`.
- [ ] **Step 4: Run test to verify it passes.**
- [ ] **Step 5: Repeat Steps 1–4 for Word export** using `XWPFDocument`, confirming with the team first whether Word export has any real current usage before investing further than a minimal working version (per the master plan's note: "confirm real usage with the team... default to build since the incremental cost is near zero" — the "near zero" default still means "build it," just don't gold-plate it without confirmed demand).
- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/report/ReportExportService.java \
        src/test/java/zw/org/nmrl/ept/service/report/ReportExportServiceTest.java
git commit -m "feat: export shipment reports to Excel and Word via Apache POI"
```

---

## Task 9: Report visual charts (React)

**Files:**
- Create: `src/main/webapp/app/modules/reports/components/pass-fail-chart.tsx` (or the equivalent path once Phase 8's dashboard module structure is decided — check that plan first; this component should live wherever the report-viewing screens are, not a new top-level module if one is already planned)
- Create: `src/main/webapp/app/modules/reports/components/pass-fail-chart.spec.tsx`

**Interfaces:**
- Consumes: pass/fail counts or z-score distributions from the REST API (exact endpoint depends on which report-type task exposes it — see Task 10).
- Produces: a reusable Chart.js-backed React component other report screens import.

- [ ] **Step 1: Install dependencies**

```bash
npm install chart.js react-chartjs-2
```

- [ ] **Step 2: Write the failing test**

```tsx
import { render, screen } from '@testing-library/react';
import PassFailChart from './pass-fail-chart';

test('renders a chart canvas given pass/fail data', () => {
  render(<PassFailChart data={{ pass: 12, fail: 3 }} />);
  expect(screen.getByRole('img', { hidden: true })).toBeInTheDocument(); // Chart.js renders to <canvas>, react-chartjs-2 exposes it with role="img" by default
});
```

- [ ] **Step 3: Run test to verify it fails**

Run: `npm test -- pass-fail-chart.spec.tsx`
Expected: FAIL — component doesn't exist.

- [ ] **Step 4: Write minimal implementation**

```tsx
import React from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

export interface PassFailChartProps {
  data: { pass: number; fail: number };
}

const PassFailChart = ({ data }: PassFailChartProps) => (
  <Pie
    data={{
      labels: ['Pass', 'Fail'],
      datasets: [{ data: [data.pass, data.fail], backgroundColor: ['#28a745', '#dc3545'] }],
    }}
  />
);

export default PassFailChart;
```

- [ ] **Step 5: Run test to verify it passes.**
- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/app/modules/reports/components/pass-fail-chart.tsx \
        src/main/webapp/app/modules/reports/components/pass-fail-chart.spec.tsx \
        package.json package-lock.json
git commit -m "feat: add Chart.js pass/fail visual for report screens"
```

---

## Task 10: Report-type screens (follow-on, not fully detailed here)

Not deep-read during this plan's research (per this plan's directive, only skimmed) — write a dedicated, separate implementation plan for this task once Tasks 1–9 land, since each report type needs its own data-query design:

- [ ] Annual report
- [ ] Detailed report
- [ ] Distribution report
- [ ] Participant Performance report
- [ ] Participant Trends report
- [ ] Shipments report
- [ ] Disease-specific reports (only for the 9 active schemes — confirm with the team which of the legacy disease-specific report controllers, e.g. `TbAllSitesResults`, `TbParticipantsPerCountry`, still apply)

Prioritize by real usage if `AuditLog`/access-pattern data is available in the migrated snapshot; otherwise build Annual/Detailed/Distribution/Shipments first (broadest utility), disease-specific reports last.

---

## Self-review

**Spec coverage:** every bullet in `ept_project_plan.md`'s Phase 6 task list is covered: certificate template upload/validation (Task 2), individual/summary certificate generation (Task 3), async job wiring (Task 4), finalize gate (Task 7), Excel/Word export (Task 8), chart rendering (Task 9). The two "Report" PDF renderers (Task 5–6) are the master plan's "Individual certificate PDF generation" / "Summary/batch report PDF generation" bullets, retargeted per the Scope Correction section above. Report-type prioritization is Task 10, intentionally left as a follow-on plan since it wasn't deep-read.

**Placeholder scan:** Task 4 and Task 10 are the two spots with acknowledged incompleteness — both are explicitly flagged as blocked on another phase's plan (Task 4 on Phase 0.2, Task 10 needs its own dedicated research pass) rather than silently guessed. This is disclosed, not hidden.

**Type consistency:** `IndividualPerformanceReportRenderer`/`SummaryPerformanceReportRenderer`/`ReportExportService` all consume "Phase 5's result type" by name-TBD — flagged consistently across Tasks 5, 6, 8 rather than each guessing a different type name. Whoever executes this plan must resolve that one name once (from Phase 5's finished plan/code) and it propagates correctly to all three.
