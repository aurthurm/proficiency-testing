# Phase 4 — Result Entry (9 Active Schemes) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build result-capture (not scoring — that's Phase 5) for Zimbabwe's 9 active PT schemes: the generic config-driven engine (`HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT`), then 5 bespoke schemes in ascending complexity (`eid` → `recency` → `vl` → `tb` → `dts`).

**Architecture:** One shared "response submission" endpoint pattern per scheme (`POST /api/shipments/{shipmentId}/participants/{participantId}/response/{schemeCode}`), each backed by a scheme-specific request DTO and a dedicated `ResultEntryService` implementation that, in one transaction, updates the `ShipmentParticipantMap` envelope (receipt/test dates, supervisor approval, not-tested-reason, QC, comments — fields already on that entity) and writes/updates the per-sample `ParticipantResult` rows. Scheme-specific fields that don't map to an existing column go into a new `ParticipantResult.resultAttributes` JSONB column (see Task 1) rather than adding narrow columns per scheme — mirrors legacy's own `shipment.shipment_attributes` JSON-bag pattern for exactly this kind of variability.

**Tech Stack:** Spring Boot 4 (backend), React 19 + Redux 5 + TypeScript (frontend), MapStruct (DTO mapping), PostgreSQL + Liquibase, JUnit 5 + Spring `MockMvc` (`*ResourceIT.java` convention already established in this repo).

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 4 section, "Scope decision — Zimbabwe first" at the top).

## Global Constraints

- Scope is exactly the 9 active schemes: `dts`, `eid`, `HBV RDT`, `HCV RDT`, `mRDT`, `recency`, `SYPH RDT`, `tb`, `vl`. Do not build `covid19`/`dbs`/legacy `HBV`/legacy `SYP` — they're inactive in production.
- DTS scope within this phase is the "Updated 3 Tests" (`dtsSchemeType: "updated-3-tests"`) field set plus RTRI (`rtriEnabled: "yes"`) — not the full 10-algorithm legacy field superset. Confirmed against real Zimbabwe `scheme_config`/shipment data (see Phase 5 plan and the master spec).
- This phase captures data only. No scoring/pass-fail computation — that's Phase 5's `ResultEntryService` sibling, `EvaluationService` (out of scope here; don't build it).
- Every new REST endpoint lives under `/api/**` per the Phase 0.5 unified-API decision (springdoc-openapi picks it up automatically — no manual OpenAPI annotation required beyond what's already on the codebase's other resources).
- Follow this repo's existing conventions exactly: REST resources at `/api/<kebab-plural>`, frontend modules at `src/main/webapp/app/entities/<kebab-name>/`, Liquibase changelogs named `YYYYMMDDHHMMSS_<description>.xml` under `src/main/resources/config/liquibase/changelog/` (most recent existing file: `20260822000700_added_report_certificate_parity.xml` — use a later timestamp).

---

## Field-to-entity mapping (confirmed against real legacy views and the current schema)

Read directly from `/home/administrator/Documents/Development/ept/application/views/scripts/{eid,recency,vl,tb,custom-test}/response.phtml` and `dts/response.phtml`, and cross-checked against `src/main/java/zw/org/nmrl/ept/domain/{ParticipantResult,ShipmentParticipantMap,ShipmentSample,SchemeConfiguration}.java`.

**Shared envelope fields** (present on every scheme's form, already columns on `ShipmentParticipantMap` — no schema change needed):

| Legacy form field | `ShipmentParticipantMap` column |
|---|---|
| `receiptDate` | `shipmentReceiptDate` (`LocalDate`) |
| `testDate` | `shipmentTestDate` (`LocalDate`) |
| `testReceiptDate`/`responseDate` | `shipmentTestReportDate` (`Instant`) |
| `ptNotTestedReason` | `notTestedReason` (FK) |
| `ptNotTestedComments` | `ptTestNotPerformedComments` (`String`) |
| `isPtTestNotPerformed` | `isPtTestNotPerformed` (`Boolean`) |
| `supervisorApproval` | `supervisorApproved` (`Boolean`) |
| `participantSupervisor` | `participantSupervisor` (`String`) |
| `userComments` | `userComment` (`String`) |
| `isExcluded` | `isExcluded` (`Boolean`) |
| `qcDone`/`qcDate`/`qcDoneBy` | `qcStatus`/`qcDate`/`qcDoneBy` |
| `modeOfReceipt` | `modeOfReceipt` (FK) |

**Per-sample fields**, already columns on `ParticipantResult`: `reportedQualitativeResult` (categorical schemes), `reportedQuantitativeValue` (VL), `lotNumber`, `expiryDate`, `comments`, `assay` (FK), `testKit` (FK).

**Scheme-specific fields with no existing column** (confirmed by reading each view — go into the new `resultAttributes` JSONB column added in Task 1):
- EID: `extractionAssayOther` (only when "Other" selected; the assay itself is `assay` FK).
- Recency: `controlLine[]`, `longtermLine[]`, `verificationLine[]` (present/absent per sample), `recencyAssayLotNo`/`recencyAssayExpiryDate` (map to `lotNumber`/`expiryDate` directly, not JSON).
- VL: `invalidVlResult[]` (per-sample invalid flag), `vlAssay`/`platformType`/`instrumentSn`/`geneXpertInstrument`/`specimenVolume` (shipment-level instrument metadata, not per-sample — store on `resultAttributes` at the `ShipmentParticipantMap` level, see Task 1).
- TB: `installedOn[]`/`instrumentId[]`/`lastCalibrated[]`/`serialNo[]` (per-instrument, not per-sample — array of objects), `isDraft`/`attestation` (TB has a draft-save workflow distinct from final submit — no other scheme has this).
- DTS: `algorithm` (fixed to `"updated-3-tests"` for this phase — still capture it explicitly so the field exists if a second algorithm is added later per the Phase 5 pluggable-dispatcher design), `dtsTestPanelType`, `conditionOfPTSamples`, `roomTemperature`, `stopWatch`, `repeat_check[1..3]`, `avilableTestKit[]`; RTRI panel (only rendered when `rtriEnabled: "yes"`, which is Zimbabwe's real config): per-sample `dts_rtri_control_line`/`dts_rtri_diagnosis_line`/`dts_rtri_longterm_line` (each `present`/`absent`) plus `rtriResult` (final categorical RTRI interpretation).
- Generic engine (`HBV RDT`/`HCV RDT`/`mRDT`/`SYPH RDT`): `kitName`/`kitNameOther`/`kitLot`/`expiryDate` (lot/expiry map to existing columns; `kitName` is scheme-driven, not a fixed FK — store on `resultAttributes`), per-sample `errorCode[]`, `additionalDetail[]`.

---

## Task 1: `ParticipantResult.resultAttributes` + `ShipmentParticipantMap.resultAttributes` columns

**Files:**
- Create: `src/main/resources/config/liquibase/changelog/20260824000100_added_result_entry_attributes.xml`
- Modify: `src/main/resources/config/liquibase/master.xml` (add the new changelog include, following the existing pattern for prior entries)
- Modify: `src/main/java/zw/org/nmrl/ept/domain/ParticipantResult.java`
- Modify: `src/main/java/zw/org/nmrl/ept/domain/ShipmentParticipantMap.java`
- Test: `src/test/java/zw/org/nmrl/ept/domain/ParticipantResultTest.java` (already exists — extend it)

**Interfaces:**
- Produces: `ParticipantResult.getResultAttributes()`/`setResultAttributes(String)` (raw JSON string, following the same pattern as `SchemeConfiguration.scoringRules` — a `String` column, not a typed JSONB mapping, matching this codebase's existing convention for flexible JSON bags) and the equivalent pair on `ShipmentParticipantMap`. Every later task in this plan writes/reads these via this getter/setter pair.

- [ ] **Step 1: Write the failing test**

```java
// src/test/java/zw/org/nmrl/ept/domain/ParticipantResultTest.java — add this method
@Test
void resultAttributesGetterSetter() throws Exception {
    ParticipantResult participantResult = new ParticipantResult();
    String json = "{\"controlLine\":\"present\",\"diagnosisLine\":\"absent\"}";
    participantResult.setResultAttributes(json);
    assertThat(participantResult.getResultAttributes()).isEqualTo(json);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ParticipantResultTest#resultAttributesGetterSetter`
Expected: FAIL — compile error, `setResultAttributes` not defined.

- [ ] **Step 3: Add the Liquibase changelog**

```xml
<!-- src/main/resources/config/liquibase/changelog/20260824000100_added_result_entry_attributes.xml -->
<?xml version="1.0" encoding="utf-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.35.xsd">
    <changeSet id="20260824000100-1" author="ept-parity">
        <addColumn tableName="participant_result">
            <column name="result_attributes" type="jsonb"/>
        </addColumn>
        <addColumn tableName="shipment_participant_map">
            <column name="result_attributes" type="jsonb"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

Add `<include file="config/liquibase/changelog/20260824000100_added_result_entry_attributes.xml" relativeToChangelogFile="false"/>` to `master.xml`, in file order after the most recent existing include.

- [ ] **Step 4: Add the entity fields**

```java
// ParticipantResult.java — add alongside the other @Column fields
@Column(name = "result_attributes")
private String resultAttributes;

public String getResultAttributes() {
    return this.resultAttributes;
}

public ParticipantResult resultAttributes(String resultAttributes) {
    this.setResultAttributes(resultAttributes);
    return this;
}

public void setResultAttributes(String resultAttributes) {
    this.resultAttributes = resultAttributes;
}
```

Add the identical getter/setter/fluent-builder trio (renamed to the class) to `ShipmentParticipantMap.java`.

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ParticipantResultTest#resultAttributesGetterSetter`
Expected: PASS

- [ ] **Step 6: Run the full Liquibase changelog against a scratch database to confirm it applies cleanly**

Run: `./mvnw -Pprod liquibase:update -Dliquibase.url=<scratch-db-url>` (or start the app against a fresh dev DB and confirm no startup error)
Expected: no Liquibase validation error, `participant_result` and `shipment_participant_map` both gain a `result_attributes jsonb` column.

- [ ] **Step 7: Commit**

```bash
git add src/main/resources/config/liquibase/changelog/20260824000100_added_result_entry_attributes.xml src/main/resources/config/liquibase/master.xml src/main/java/zw/org/nmrl/ept/domain/ParticipantResult.java src/main/java/zw/org/nmrl/ept/domain/ShipmentParticipantMap.java src/test/java/zw/org/nmrl/ept/domain/ParticipantResultTest.java
git commit -m "feat: add resultAttributes JSON column for scheme-specific result-entry fields"
```

---

## Task 2: Shared result-entry infrastructure (`ResultEntryService` contract + envelope-update logic)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/resultentry/ResultEntryService.java` (interface)
- Create: `src/main/java/zw/org/nmrl/ept/resultentry/AbstractResultEntryService.java` (shared envelope-update logic, extended by every scheme's service in Tasks 3–8)
- Create: `src/main/java/zw/org/nmrl/ept/resultentry/dto/ResponseEnvelopeDTO.java` (the shared fields every scheme's request DTO embeds)
- Test: `src/test/java/zw/org/nmrl/ept/resultentry/AbstractResultEntryServiceTest.java`

**Interfaces:**
- Consumes: `ParticipantResultRepository`, `ShipmentParticipantMapRepository` (both already exist, generated by JHipster).
- Produces: `ResultEntryService<T extends ResponseEnvelopeDTO>` with one method `void submitResponse(Long shipmentId, Long participantId, T request)`. Every scheme-specific service (Tasks 3–8) implements this against its own request DTO type `T`. `AbstractResultEntryService.updateEnvelope(ShipmentParticipantMap map, ResponseEnvelopeDTO envelope)` is what every subclass calls first, before writing its scheme-specific `ParticipantResult` rows.

- [ ] **Step 1: Write the failing test**

```java
// src/test/java/zw/org/nmrl/ept/resultentry/AbstractResultEntryServiceTest.java
package zw.org.nmrl.ept.resultentry;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.resultentry.dto.ResponseEnvelopeDTO;

class AbstractResultEntryServiceTest {

    static class TestableService extends AbstractResultEntryService {}

    @Test
    void updateEnvelopeCopiesSharedFieldsOntoTheMap() {
        ResponseEnvelopeDTO envelope = new ResponseEnvelopeDTO();
        envelope.setReceiptDate(LocalDate.of(2026, 1, 15));
        envelope.setTestDate(LocalDate.of(2026, 1, 16));
        envelope.setSupervisorApproved(true);
        envelope.setParticipantSupervisor("J. Moyo");
        envelope.setUserComment("no issues");
        envelope.setIsExcluded(false);

        ShipmentParticipantMap map = new ShipmentParticipantMap();
        new TestableService().updateEnvelope(map, envelope);

        assertThat(map.getShipmentReceiptDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(map.getShipmentTestDate()).isEqualTo(LocalDate.of(2026, 1, 16));
        assertThat(map.getSupervisorApproved()).isTrue();
        assertThat(map.getParticipantSupervisor()).isEqualTo("J. Moyo");
        assertThat(map.getUserComment()).isEqualTo("no issues");
        assertThat(map.getIsExcluded()).isFalse();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=AbstractResultEntryServiceTest`
Expected: FAIL — `zw.org.nmrl.ept.resultentry` package/classes don't exist yet.

- [ ] **Step 3: Write the DTO**

```java
// src/main/java/zw/org/nmrl/ept/resultentry/dto/ResponseEnvelopeDTO.java
package zw.org.nmrl.ept.resultentry.dto;

import java.time.LocalDate;
import java.time.Instant;

/**
 * Fields present on every scheme's response form, mapped directly onto
 * {@link zw.org.nmrl.ept.domain.ShipmentParticipantMap} columns.
 */
public class ResponseEnvelopeDTO {

    private LocalDate receiptDate;
    private LocalDate testDate;
    private Instant testReportDate;
    private Long notTestedReasonId;
    private String ptNotTestedComments;
    private Boolean isPtTestNotPerformed;
    private Boolean supervisorApproved;
    private String participantSupervisor;
    private String userComment;
    private Boolean isExcluded;
    private Long modeOfReceiptId;

    // getters/setters for every field above, standard JavaBean style
    public LocalDate getReceiptDate() { return receiptDate; }
    public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
    public LocalDate getTestDate() { return testDate; }
    public void setTestDate(LocalDate testDate) { this.testDate = testDate; }
    public Instant getTestReportDate() { return testReportDate; }
    public void setTestReportDate(Instant testReportDate) { this.testReportDate = testReportDate; }
    public Long getNotTestedReasonId() { return notTestedReasonId; }
    public void setNotTestedReasonId(Long notTestedReasonId) { this.notTestedReasonId = notTestedReasonId; }
    public String getPtNotTestedComments() { return ptNotTestedComments; }
    public void setPtNotTestedComments(String ptNotTestedComments) { this.ptNotTestedComments = ptNotTestedComments; }
    public Boolean getIsPtTestNotPerformed() { return isPtTestNotPerformed; }
    public void setIsPtTestNotPerformed(Boolean isPtTestNotPerformed) { this.isPtTestNotPerformed = isPtTestNotPerformed; }
    public Boolean getSupervisorApproved() { return supervisorApproved; }
    public void setSupervisorApproved(Boolean supervisorApproved) { this.supervisorApproved = supervisorApproved; }
    public String getParticipantSupervisor() { return participantSupervisor; }
    public void setParticipantSupervisor(String participantSupervisor) { this.participantSupervisor = participantSupervisor; }
    public String getUserComment() { return userComment; }
    public void setUserComment(String userComment) { this.userComment = userComment; }
    public Boolean getIsExcluded() { return isExcluded; }
    public void setIsExcluded(Boolean isExcluded) { this.isExcluded = isExcluded; }
    public Long getModeOfReceiptId() { return modeOfReceiptId; }
    public void setModeOfReceiptId(Long modeOfReceiptId) { this.modeOfReceiptId = modeOfReceiptId; }
}
```

- [ ] **Step 4: Write the abstract service**

```java
// src/main/java/zw/org/nmrl/ept/resultentry/AbstractResultEntryService.java
package zw.org.nmrl.ept.resultentry;

import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.resultentry.dto.ResponseEnvelopeDTO;

public abstract class AbstractResultEntryService {

    protected void updateEnvelope(ShipmentParticipantMap map, ResponseEnvelopeDTO envelope) {
        map.setShipmentReceiptDate(envelope.getReceiptDate());
        map.setShipmentTestDate(envelope.getTestDate());
        map.setShipmentTestReportDate(envelope.getTestReportDate());
        map.setPtTestNotPerformedComments(envelope.getPtNotTestedComments());
        map.setIsPtTestNotPerformed(envelope.getIsPtTestNotPerformed());
        map.setSupervisorApproved(envelope.getSupervisorApproved());
        map.setParticipantSupervisor(envelope.getParticipantSupervisor());
        map.setUserComment(envelope.getUserComment());
        map.setIsExcluded(envelope.getIsExcluded());
        // notTestedReasonId / modeOfReceiptId are resolved to entities and set
        // by the concrete per-scheme service (Tasks 3-8), which has repository
        // access this abstract class intentionally does not — keeps this class
        // free of repository dependencies so it stays trivially unit-testable.
    }
}
```

```java
// src/main/java/zw/org/nmrl/ept/resultentry/ResultEntryService.java
package zw.org.nmrl.ept.resultentry;

import zw.org.nmrl.ept.resultentry.dto.ResponseEnvelopeDTO;

public interface ResultEntryService<T extends ResponseEnvelopeDTO> {
    void submitResponse(Long shipmentId, Long participantId, T request);
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=AbstractResultEntryServiceTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/resultentry/
git add src/test/java/zw/org/nmrl/ept/resultentry/
git commit -m "feat: add shared result-entry envelope infrastructure"
```

---

## Task 3: Generic config-driven engine (`HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT`)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/resultentry/dto/CustomTestResponseDTO.java` (extends `ResponseEnvelopeDTO`)
- Create: `src/main/java/zw/org/nmrl/ept/resultentry/dto/CustomTestSampleResultDTO.java`
- Create: `src/main/java/zw/org/nmrl/ept/resultentry/CustomTestResultEntryService.java` (implements `ResultEntryService<CustomTestResponseDTO>`)
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/resultentry/CustomTestResultResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/resultentry/CustomTestResultEntryServiceTest.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/resultentry/CustomTestResultResourceIT.java`

**Interfaces:**
- Consumes: `AbstractResultEntryService.updateEnvelope`, `ParticipantResultRepository`, `ShipmentParticipantMapRepository`, `ShipmentSampleRepository`, `NotTestedReasonRepository`, `ModeOfReceiptRepository` (all pre-existing generated repositories).
- Produces: `POST /api/schemes/custom-test/shipments/{shipmentId}/participants/{participantId}/response` — request body `CustomTestResponseDTO { kitName: String, kitNameOther: String, kitLot: String, expiryDate: LocalDate, samples: List<CustomTestSampleResultDTO { sampleId: Long, reportedResult: String, errorCode: String, comments: String, additionalDetail: String }> }` plus the inherited `ResponseEnvelopeDTO` fields. This exact request shape is what Task 9 (frontend) posts.

Fields confirmed against `/home/administrator/Documents/Development/ept/application/views/scripts/custom-test/response.phtml`: `kitName`, `kitNameOther`, `kitLot`, `expiryDate`, per-sample `sampleId[]`/`reportedResult[]`/`errorCode[]`/`comments[]`/`additionalDetail[]`, plus the shared envelope fields. `kitName`/`kitNameOther` and `errorCode`/`additionalDetail` have no existing column — store as a JSON object on `ParticipantResult.resultAttributes` (Task 1); `kitLot`/`expiryDate` map to `ParticipantResult.lotNumber`/`expiryDate` directly.

- [ ] **Step 1: Write the failing test**

```java
// src/test/java/zw/org/nmrl/ept/resultentry/CustomTestResultEntryServiceTest.java
package zw.org.nmrl.ept.resultentry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.repository.*;
import zw.org.nmrl.ept.resultentry.dto.CustomTestResponseDTO;
import zw.org.nmrl.ept.resultentry.dto.CustomTestSampleResultDTO;

@ExtendWith(MockitoExtension.class)
class CustomTestResultEntryServiceTest {

    @Test
    void submitResponsePersistsEnvelopeAndPerSampleResults() {
        ParticipantResultRepository resultRepo = mock(ParticipantResultRepository.class);
        ShipmentParticipantMapRepository mapRepo = mock(ShipmentParticipantMapRepository.class);
        ShipmentSampleRepository sampleRepo = mock(ShipmentSampleRepository.class);

        ShipmentParticipantMap existingMap = new ShipmentParticipantMap();
        existingMap.setId(42L);
        when(mapRepo.findByShipmentIdAndParticipantId(7L, 3L)).thenReturn(java.util.Optional.of(existingMap));
        ShipmentSample sample = new ShipmentSample();
        sample.setId(100L);
        when(sampleRepo.findById(100L)).thenReturn(java.util.Optional.of(sample));

        CustomTestResultEntryService service = new CustomTestResultEntryService(resultRepo, mapRepo, sampleRepo);

        CustomTestResponseDTO request = new CustomTestResponseDTO();
        request.setKitName("SD Bioline");
        request.setKitLot("LOT123");
        request.setExpiryDate(LocalDate.of(2027, 6, 1));
        CustomTestSampleResultDTO sampleResult = new CustomTestSampleResultDTO();
        sampleResult.setSampleId(100L);
        sampleResult.setReportedResult("Reactive");
        request.setSamples(List.of(sampleResult));

        service.submitResponse(7L, 3L, request);

        verify(mapRepo).save(existingMap);
        ArgumentCaptor<ParticipantResult> captor = ArgumentCaptor.forClass(ParticipantResult.class);
        verify(resultRepo).save(captor.capture());
        assertThat(captor.getValue().getReportedQualitativeResult()).isEqualTo("Reactive");
        assertThat(captor.getValue().getLotNumber()).isEqualTo("LOT123");
        assertThat(captor.getValue().getResultAttributes()).contains("SD Bioline");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=CustomTestResultEntryServiceTest`
Expected: FAIL — `CustomTestResultEntryService`/DTOs don't exist yet.

- [ ] **Step 3: Write the DTOs**

```java
// src/main/java/zw/org/nmrl/ept/resultentry/dto/CustomTestSampleResultDTO.java
package zw.org.nmrl.ept.resultentry.dto;

public class CustomTestSampleResultDTO {
    private Long sampleId;
    private String reportedResult;
    private String errorCode;
    private String comments;
    private String additionalDetail;

    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getReportedResult() { return reportedResult; }
    public void setReportedResult(String reportedResult) { this.reportedResult = reportedResult; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getAdditionalDetail() { return additionalDetail; }
    public void setAdditionalDetail(String additionalDetail) { this.additionalDetail = additionalDetail; }
}
```

```java
// src/main/java/zw/org/nmrl/ept/resultentry/dto/CustomTestResponseDTO.java
package zw.org.nmrl.ept.resultentry.dto;

import java.time.LocalDate;
import java.util.List;

public class CustomTestResponseDTO extends ResponseEnvelopeDTO {
    private String kitName;
    private String kitNameOther;
    private String kitLot;
    private LocalDate expiryDate;
    private List<CustomTestSampleResultDTO> samples;

    public String getKitName() { return kitName; }
    public void setKitName(String kitName) { this.kitName = kitName; }
    public String getKitNameOther() { return kitNameOther; }
    public void setKitNameOther(String kitNameOther) { this.kitNameOther = kitNameOther; }
    public String getKitLot() { return kitLot; }
    public void setKitLot(String kitLot) { this.kitLot = kitLot; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public List<CustomTestSampleResultDTO> getSamples() { return samples; }
    public void setSamples(List<CustomTestSampleResultDTO> samples) { this.samples = samples; }
}
```

- [ ] **Step 4: Write the service**

```java
// src/main/java/zw/org/nmrl/ept/resultentry/CustomTestResultEntryService.java
package zw.org.nmrl.ept.resultentry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;
import zw.org.nmrl.ept.repository.ShipmentSampleRepository;
import zw.org.nmrl.ept.resultentry.dto.CustomTestResponseDTO;
import zw.org.nmrl.ept.resultentry.dto.CustomTestSampleResultDTO;

@Service
public class CustomTestResultEntryService extends AbstractResultEntryService implements ResultEntryService<CustomTestResponseDTO> {

    private final ParticipantResultRepository resultRepository;
    private final ShipmentParticipantMapRepository mapRepository;
    private final ShipmentSampleRepository sampleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomTestResultEntryService(
        ParticipantResultRepository resultRepository,
        ShipmentParticipantMapRepository mapRepository,
        ShipmentSampleRepository sampleRepository
    ) {
        this.resultRepository = resultRepository;
        this.mapRepository = mapRepository;
        this.sampleRepository = sampleRepository;
    }

    @Override
    @Transactional
    public void submitResponse(Long shipmentId, Long participantId, CustomTestResponseDTO request) {
        ShipmentParticipantMap map = mapRepository
            .findByShipmentIdAndParticipantId(shipmentId, participantId)
            .orElseThrow(() -> new IllegalArgumentException("No ShipmentParticipantMap for shipment " + shipmentId + ", participant " + participantId));
        updateEnvelope(map, request);
        mapRepository.save(map);

        ObjectNode kitAttributes = objectMapper.createObjectNode();
        kitAttributes.put("kitName", request.getKitName());
        kitAttributes.put("kitNameOther", request.getKitNameOther());

        for (CustomTestSampleResultDTO sampleResult : request.getSamples()) {
            ShipmentSample sample = sampleRepository
                .findById(sampleResult.getSampleId())
                .orElseThrow(() -> new IllegalArgumentException("No ShipmentSample " + sampleResult.getSampleId()));

            ObjectNode attributes = kitAttributes.deepCopy();
            attributes.put("errorCode", sampleResult.getErrorCode());
            attributes.put("additionalDetail", sampleResult.getAdditionalDetail());

            ParticipantResult result = new ParticipantResult();
            result.setSample(sample);
            result.setShipmentParticipantMap(map);
            result.setReportedQualitativeResult(sampleResult.getReportedResult());
            result.setLotNumber(request.getKitLot());
            result.setExpiryDate(request.getExpiryDate());
            result.setComments(sampleResult.getComments());
            result.setResultAttributes(attributes.toString());
            resultRepository.save(result);
        }
    }
}
```

- [ ] **Step 5: Write the REST resource**

```java
// src/main/java/zw/org/nmrl/ept/web/rest/resultentry/CustomTestResultResource.java
package zw.org.nmrl.ept.web.rest.resultentry;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.org.nmrl.ept.resultentry.CustomTestResultEntryService;
import zw.org.nmrl.ept.resultentry.dto.CustomTestResponseDTO;

@RestController
@RequestMapping("/api/schemes/custom-test")
public class CustomTestResultResource {

    private final CustomTestResultEntryService service;

    public CustomTestResultResource(CustomTestResultEntryService service) {
        this.service = service;
    }

    @PostMapping("/shipments/{shipmentId}/participants/{participantId}/response")
    public ResponseEntity<Void> submitResponse(
        @PathVariable Long shipmentId,
        @PathVariable Long participantId,
        @RequestBody CustomTestResponseDTO request
    ) {
        service.submitResponse(shipmentId, participantId, request);
        return ResponseEntity.noContent().build();
    }
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=CustomTestResultEntryServiceTest`
Expected: PASS

- [ ] **Step 7: Write and run the integration test**

```java
// src/test/java/zw/org/nmrl/ept/web/rest/resultentry/CustomTestResultResourceIT.java
package zw.org.nmrl.ept.web.rest.resultentry;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.IntegrationTest;
import zw.org.nmrl.ept.resultentry.dto.CustomTestResponseDTO;
import zw.org.nmrl.ept.resultentry.dto.CustomTestSampleResultDTO;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class CustomTestResultResourceIT {

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private ObjectMapper om;

    @Test
    void submitResponse_persistsResultAndReturns204() throws Exception {
        // Arrange: seed a Shipment, ShipmentParticipantMap, and ShipmentSample via the
        // existing repositories/test fixtures this project already uses in its other
        // *ResourceIT tests (follow the exact seeding pattern in
        // ParticipantResultResourceIT.java's createEntity()/createUpdatedEntity() helpers).
        CustomTestResponseDTO request = new CustomTestResponseDTO();
        request.setKitName("SD Bioline");
        request.setKitLot("LOT123");
        CustomTestSampleResultDTO sample = new CustomTestSampleResultDTO();
        sample.setSampleId(1L); // seeded sample id
        sample.setReportedResult("Reactive");
        request.setSamples(java.util.List.of(sample));

        restMockMvc
            .perform(
                post("/api/schemes/custom-test/shipments/{shipmentId}/participants/{participantId}/response", 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isNoContent());
    }
}
```

Run: `./mvnw -Pprod verify -Dit.test=CustomTestResultResourceIT`
Expected: PASS. (Follow this repo's existing `*ResourceIT` seeding conventions exactly — read `ParticipantResultResourceIT.java` first for the `createEntity(EntityManager)` pattern before filling in the arrange step above; don't invent a different fixture style.)

- [ ] **Step 8: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/resultentry/dto/CustomTest*.java src/main/java/zw/org/nmrl/ept/resultentry/CustomTestResultEntryService.java src/main/java/zw/org/nmrl/ept/web/rest/resultentry/CustomTestResultResource.java src/test/java/zw/org/nmrl/ept/resultentry/CustomTestResultEntryServiceTest.java src/test/java/zw/org/nmrl/ept/web/rest/resultentry/CustomTestResultResourceIT.java
git commit -m "feat: add generic config-driven result entry for HBV RDT/HCV RDT/mRDT/SYPH RDT"
```

---

## Task 4: EID result entry

**Files:** mirror Task 3's file set exactly, substituting `CustomTest` → `Eid` everywhere (`EidResponseDTO`, `EidSampleResultDTO`, `EidResultEntryService`, `EidResultResource` at `/api/schemes/eid/...`).

**Interfaces:**
- Produces: `EidResponseDTO` fields (confirmed against `eid/response.phtml`): inherited envelope fields, plus `extractionAssayId` (FK to `Assay`), `extractionAssayOther` (String, only when "Other" selected — goes to `resultAttributes`), per-sample `samples: List<EidSampleResultDTO { sampleId, reportedResult }>` (EID has no per-sample lot/expiry/comments fields beyond the shared ones — its form is the simplest of the bespoke schemes, confirmed by direct read of the view).

- [ ] **Step 1: Write the failing test** — same shape as Task 3 Step 1, asserting `extractionAssay` maps to `ParticipantResult.assay` (not `resultAttributes` — it's a real FK) and `extractionAssayOther` maps to `resultAttributes` only when present.
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write `EidSampleResultDTO`/`EidResponseDTO`** — same structure as Task 3's DTOs, fields per the list above.
- [ ] **Step 4: Write `EidResultEntryService`** — same structure as `CustomTestResultEntryService`, using `AssayRepository` (already exists) to resolve `extractionAssayId`, setting `ParticipantResult.setAssay(...)` directly, and only writing to `resultAttributes` when `extractionAssayOther` is non-blank.
- [ ] **Step 5: Write `EidResultResource`** at `/api/schemes/eid/shipments/{shipmentId}/participants/{participantId}/response`.
- [ ] **Step 6: Run test to verify it passes.**
- [ ] **Step 7: Write and run `EidResultResourceIT`** — same pattern as Task 3 Step 7.
- [ ] **Step 8: Commit** — `git commit -m "feat: add EID result entry"`.

---

## Task 5: Recency result entry

**Files:** mirror Task 3, substituting `Recency` (`RecencyResponseDTO`, `RecencySampleResultDTO`, `RecencyResultEntryService`, `RecencyResultResource` at `/api/schemes/recency/...`).

**Interfaces:**
- Produces: `RecencyResponseDTO` — inherited envelope, plus `recencyAssayId` (FK), `recencyAssayLotNo`→maps to `ParticipantResult.lotNumber`, `recencyAssayExpiryDate`→`ParticipantResult.expiryDate`; per-sample `samples: List<RecencySampleResultDTO { sampleId, reportedResult, controlLine: "present"|"absent", longtermLine: "present"|"absent", verificationLine: "present"|"absent" }>` (the 3-line RTRI-style band read — confirmed against `recency/response.phtml`; these 3 fields have no existing column, go into `resultAttributes` as a JSON object, e.g. `{"controlLine":"present","longtermLine":"absent","verificationLine":"present"}`).

- [ ] **Step 1: Write the failing test** — assert the 3-band-read JSON shape lands correctly in `resultAttributes` (use `objectMapper.readTree(result.getResultAttributes())` and assert on the individual fields, not a raw string-contains check, so the test still passes if key ordering differs).
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write the DTOs.**
- [ ] **Step 4: Write `RecencyResultEntryService`** — same structure as Task 3's service; the only difference is the `resultAttributes` JSON payload shape.
- [ ] **Step 5: Write `RecencyResultResource`.**
- [ ] **Step 6: Run test to verify it passes.**
- [ ] **Step 7: Write and run `RecencyResultResourceIT`.**
- [ ] **Step 8: Commit** — `git commit -m "feat: add Recency result entry"`.

---

## Task 6: VL result entry (quantitative)

**Files:** mirror Task 3, substituting `Vl` (`VlResponseDTO`, `VlSampleResultDTO`, `VlResultEntryService`, `VlResultResource` at `/api/schemes/vl/...`).

**Interfaces:**
- Produces: `VlResponseDTO` — inherited envelope, plus shipment-level instrument metadata `vlAssayId` (FK), `platformType`, `instrumentSn`, `geneXpertInstrument`, `specimenVolume`, `assayLotNumber`→`ParticipantResult.lotNumber` (set on every sample row — legacy stores it once per shipment, not per sample, but `ParticipantResult` is per-sample; replicate the same value across all of that participant's `ParticipantResult` rows for this submission rather than adding a new envelope-level column, since the target schema is already sample-grained), `assayExpirationDate`→`ParticipantResult.expiryDate` (same replication rule); per-sample `samples: List<VlSampleResultDTO { sampleId, reportedQuantitativeValue: Double, isInvalid: Boolean }>` — **this is the only scheme using `ParticipantResult.reportedQuantitativeValue` instead of `reportedQualitativeResult`**, confirmed against `vl/response.phtml`'s `vlResult[]`/`invalidVlResult[]` fields. `platformType`/`instrumentSn`/`geneXpertInstrument`/`specimenVolume` have no existing column — store as a JSON object on `ShipmentParticipantMap.resultAttributes` (shipment-level, not per-sample — this is why Task 1 added the column to both entities, not just `ParticipantResult`). `isInvalid` goes on `ParticipantResult.resultAttributes`.

- [ ] **Step 1: Write the failing test** — assert `reportedQuantitativeValue` is set (not `reportedQualitativeResult`) and that the instrument metadata lands on the *map's* `resultAttributes`, not the per-sample result's.
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write the DTOs.**
- [ ] **Step 4: Write `VlResultEntryService`** — note in `updateEnvelope`'s caller, also set `map.setResultAttributes(...)` with the instrument-metadata JSON before saving the map (this is additional to what `AbstractResultEntryService.updateEnvelope` does, since that method only touches the shared fields — set it directly on `map` after calling `updateEnvelope`).
- [ ] **Step 5: Write `VlResultResource`.**
- [ ] **Step 6: Run test to verify it passes.**
- [ ] **Step 7: Write and run `VlResultResourceIT`** — include a case with `isInvalid: true` and confirm `reportedQuantitativeValue` is still stored (legacy still records the raw value even when flagged invalid — confirmed by the form allowing entry of both regardless of the invalid checkbox state).
- [ ] **Step 8: Commit** — `git commit -m "feat: add VL quantitative result entry"`.

---

## Task 7: TB result entry

**Files:** mirror Task 3, substituting `Tb` (`TbResponseDTO`, `TbInstrumentDTO`, `TbSampleResultDTO`, `TbResultEntryService`, `TbResultResource` at `/api/schemes/tb/...`).

**Interfaces:**
- Produces: `TbResponseDTO` — inherited envelope, plus `assayName`/`assayLot`/`otherAssayName`/`expiryDate` (assay/lot/expiry map to `ParticipantResult.assay`/`lotNumber`/`expiryDate` as in Task 4/5), `instruments: List<TbInstrumentDTO { instrumentId, serialNo, installedOn: LocalDate, lastCalibrated: LocalDate }>` (TB is the only scheme tracking per-instrument calibration metadata as an array of objects — confirmed against `tb/response.phtml`'s `installedOn[]`/`instrumentId[]`/`lastCalibrated[]`/`serialNo[]` parallel arrays; store as a JSON array on `ShipmentParticipantMap.resultAttributes`), `isDraft: Boolean` (TB uniquely has a draft-save action distinct from final submit — confirmed by the form's `isDraft`/`draftbtn` fields, absent from every other scheme's view), `attestation: Boolean`.

- [ ] **Step 1: Write the failing test** — cover both the draft-save path (`isDraft: true` — the service must still persist but the caller is expected to be able to resubmit non-draft later without creating duplicate `ParticipantResult` rows, i.e. `submitResponse` must upsert on `(sample, shipmentParticipantMap)`, not always insert — check `ParticipantResultRepository` for an existing `findBySampleIdAndShipmentParticipantMapId`-style method; if none exists, add one as part of this task) and the final-submit path.
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write the DTOs**, including `TbInstrumentDTO { instrumentId: String, serialNo: String, installedOn: LocalDate, lastCalibrated: LocalDate }`.
- [ ] **Step 4: Write `TbResultEntryService`**, implementing the upsert (`resultRepository.findBySampleAndShipmentParticipantMap(sample, map).orElseGet(ParticipantResult::new)` — add this repository method if it's not already present, following the naming convention of this repo's other Spring Data derived-query methods) so a draft followed by a final submit updates the same rows rather than duplicating them.
- [ ] **Step 5: Write `TbResultResource`.**
- [ ] **Step 6: Run test to verify it passes.**
- [ ] **Step 7: Write and run `TbResultResourceIT`** — include a draft-then-final-submit sequence in one test to verify the upsert behavior end to end (assert `ParticipantResultRepository.findAll()` count doesn't grow on the second submit for the same sample).
- [ ] **Step 8: Commit** — `git commit -m "feat: add TB result entry with draft-save workflow"`.

---

## Task 8: DTS result entry (Updated 3 Tests + RTRI)

**Files:** mirror Task 3, substituting `Dts` (`DtsResponseDTO`, `DtsSampleResultDTO`, `DtsRtriSampleResultDTO`, `DtsResultEntryService`, `DtsResultResource` at `/api/schemes/dts/...`).

**Interfaces:**
- Produces: `DtsResponseDTO` — inherited envelope, plus (confirmed against `dts/response.phtml`, non-Vietnam branch since Zimbabwe's `dtsSchemeType` is `"updated-3-tests"`): `algorithm: String` (always `"updated-3-tests"` in this phase — still an explicit field, not hardcoded server-side, so Phase 5's pluggable dispatcher has something real to read), `dtsTestPanelType`, `conditionOfPTSamples`, `roomTemperature`, `stopWatch`, `repeatCheck: List<Boolean>` (the 3 `repeat_check[1..3]` checkboxes), `availableTestKits: List<String>` (`avilableTestKit[]`, note the legacy typo — do not carry the typo into the new field name), `receivedPtPanel: Boolean`; per-sample `samples: List<DtsSampleResultDTO { sampleId, reportedResult }>`; RTRI panel (present because Zimbabwe's real `scheme_config` has `rtriEnabled: "yes"`) `rtriSamples: List<DtsRtriSampleResultDTO { sampleId, controlLine: "present"|"absent", diagnosisLine: "present"|"absent", longtermLine: "present"|"absent", rtriResult: String }>`. None of `algorithm`/`dtsTestPanelType`/`conditionOfPTSamples`/`roomTemperature`/`stopWatch`/`repeatCheck`/`availableTestKits` have existing columns — store as one JSON object on `ShipmentParticipantMap.resultAttributes`. The RTRI 3-line read per sample has no existing column — store as a JSON object on that sample's `ParticipantResult.resultAttributes`, same pattern as Task 5's Recency band-read.

- [ ] **Step 1: Write the failing test** — cover: (a) the base DTS sample results land on `reportedQualitativeResult`, (b) the shipment-level DTS attributes (`algorithm`, `roomTemperature`, etc.) land on the map's `resultAttributes` as one JSON object, (c) each RTRI sample's 3-line read plus `rtriResult` lands on that specific `ParticipantResult`'s `resultAttributes` — write a separate `ParticipantResult` row per RTRI sample or reuse the base sample's row if `sampleId` matches (confirm which by checking whether `dts/response.phtml`'s `rtriSampleId[]` values are ever different from the base `sampleId[]` values — if they're always the same physical samples, reuse the same row and merge both JSON payloads into one `resultAttributes` object rather than creating a second row per sample).
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write the DTOs** per the field list above.
- [ ] **Step 4: Write `DtsResultEntryService`** — merge the base-sample and RTRI-sample JSON payloads per sample as determined in Step 1's investigation, so each physical sample has exactly one `ParticipantResult` row with a combined `resultAttributes` object (e.g. `{"repeat":{"result":"..."},"rtri":{"controlLine":"present",...,"rtriResult":"..."}}`).
- [ ] **Step 5: Write `DtsResultResource`.**
- [ ] **Step 6: Run test to verify it passes.**
- [ ] **Step 7: Write and run `DtsResultResourceIT`** — one case with RTRI data present, one without (RTRI section is conditionally rendered even for Zimbabwe's config — the field can legitimately be absent per-submission if the operator didn't run RTRI on a given sample), confirming the service handles both without error.
- [ ] **Step 8: Commit** — `git commit -m "feat: add DTS result entry (Updated 3 Tests + RTRI)"`.

---

## Task 9: Frontend — shared result-entry form scaffolding

**Files:**
- Create: `src/main/webapp/app/shared/result-entry/response-envelope-fields.tsx` (the shared form section every scheme's page renders — receipt/test date pickers, not-tested-reason dropdown, supervisor approval, comments, mode of receipt — mirrors the shared table in every legacy `response.phtml`)
- Create: `src/main/webapp/app/shared/result-entry/result-entry-api.ts` (typed API client functions, one per scheme, calling the Task 3–8 endpoints)
- Test: `src/main/webapp/app/shared/result-entry/response-envelope-fields.spec.tsx`

**Interfaces:**
- Produces: `<ResponseEnvelopeFields value={envelope} onChange={setEnvelope} notTestedReasons={...} modesOfReceipt={...} />` React component, and `submitCustomTestResponse`/`submitEidResponse`/`submitRecencyResponse`/`submitVlResponse`/`submitTbResponse`/`submitDtsResponse` typed functions in `result-entry-api.ts`, each `(shipmentId: number, participantId: number, body: <SchemeName>ResponseDTO) => Promise<void>`, matching the exact JSON shape Tasks 3–8's Java DTOs serialize to (Jackson default: camelCase field names, so no manual mapping needed — TypeScript interfaces mirror the Java DTO field names 1:1).

- [ ] **Step 1: Write the failing test**

```tsx
// src/main/webapp/app/shared/result-entry/response-envelope-fields.spec.tsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import ResponseEnvelopeFields from './response-envelope-fields';

test('calls onChange with updated receiptDate when the date field changes', () => {
  const onChange = jest.fn();
  render(
    <ResponseEnvelopeFields
      value={{}}
      onChange={onChange}
      notTestedReasons={[]}
      modesOfReceipt={[]}
    />
  );
  fireEvent.change(screen.getByLabelText(/Shipment Receipt Date/i), { target: { value: '2026-01-15' } });
  expect(onChange).toHaveBeenCalledWith(expect.objectContaining({ receiptDate: '2026-01-15' }));
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test -- response-envelope-fields.spec.tsx`
Expected: FAIL — module doesn't exist.

- [ ] **Step 3: Write the component**

```tsx
// src/main/webapp/app/shared/result-entry/response-envelope-fields.tsx
import React from 'react';
import { Label, Input, FormGroup } from 'reactstrap';

export interface ResponseEnvelope {
  receiptDate?: string;
  testDate?: string;
  notTestedReasonId?: number;
  ptNotTestedComments?: string;
  isPtTestNotPerformed?: boolean;
  supervisorApproved?: boolean;
  participantSupervisor?: string;
  userComment?: string;
  isExcluded?: boolean;
  modeOfReceiptId?: number;
}

export interface ReferenceOption {
  id: number;
  label: string;
}

interface ResponseEnvelopeFieldsProps {
  value: ResponseEnvelope;
  onChange: (next: ResponseEnvelope) => void;
  notTestedReasons: ReferenceOption[];
  modesOfReceipt: ReferenceOption[];
}

const ResponseEnvelopeFields = ({ value, onChange, notTestedReasons, modesOfReceipt }: ResponseEnvelopeFieldsProps) => (
  <>
    <FormGroup>
      <Label for="receiptDate">Shipment Receipt Date</Label>
      <Input
        id="receiptDate"
        type="date"
        value={value.receiptDate ?? ''}
        onChange={e => onChange({ ...value, receiptDate: e.target.value })}
      />
    </FormGroup>
    <FormGroup>
      <Label for="testDate">Shipment Testing Date</Label>
      <Input id="testDate" type="date" value={value.testDate ?? ''} onChange={e => onChange({ ...value, testDate: e.target.value })} />
    </FormGroup>
    <FormGroup check>
      <Label check>
        <Input
          type="checkbox"
          checked={value.isPtTestNotPerformed ?? false}
          onChange={e => onChange({ ...value, isPtTestNotPerformed: e.target.checked })}
        />{' '}
        PT panel not tested
      </Label>
    </FormGroup>
    {value.isPtTestNotPerformed && (
      <FormGroup>
        <Label for="notTestedReasonId">Reason for not testing the PT Panel</Label>
        <Input
          id="notTestedReasonId"
          type="select"
          value={value.notTestedReasonId ?? ''}
          onChange={e => onChange({ ...value, notTestedReasonId: Number(e.target.value) })}
        >
          <option value="">-- Select --</option>
          {notTestedReasons.map(r => (
            <option key={r.id} value={r.id}>
              {r.label}
            </option>
          ))}
        </Input>
      </FormGroup>
    )}
    <FormGroup>
      <Label for="modeOfReceiptId">Mode of Receipt</Label>
      <Input
        id="modeOfReceiptId"
        type="select"
        value={value.modeOfReceiptId ?? ''}
        onChange={e => onChange({ ...value, modeOfReceiptId: Number(e.target.value) })}
      >
        <option value="">-- Select --</option>
        {modesOfReceipt.map(m => (
          <option key={m.id} value={m.id}>
            {m.label}
          </option>
        ))}
      </Input>
    </FormGroup>
    <FormGroup>
      <Label for="supervisorApproved">Supervisor Review</Label>
      <Input
        id="supervisorApproved"
        type="select"
        value={value.supervisorApproved === undefined ? '' : String(value.supervisorApproved)}
        onChange={e => onChange({ ...value, supervisorApproved: e.target.value === 'true' })}
      >
        <option value="">-- Select --</option>
        <option value="true">YES</option>
        <option value="false">NO</option>
      </Input>
    </FormGroup>
    {value.supervisorApproved && (
      <FormGroup>
        <Label for="participantSupervisor">Supervisor Name</Label>
        <Input
          id="participantSupervisor"
          value={value.participantSupervisor ?? ''}
          onChange={e => onChange({ ...value, participantSupervisor: e.target.value })}
        />
      </FormGroup>
    )}
    <FormGroup>
      <Label for="userComment">Comments</Label>
      <Input
        id="userComment"
        type="textarea"
        value={value.userComment ?? ''}
        onChange={e => onChange({ ...value, userComment: e.target.value })}
      />
    </FormGroup>
  </>
);

export default ResponseEnvelopeFields;
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test -- response-envelope-fields.spec.tsx`
Expected: PASS

- [ ] **Step 5: Write `result-entry-api.ts`**

```ts
// src/main/webapp/app/shared/result-entry/result-entry-api.ts
import axios from 'axios';
import { ResponseEnvelope } from './response-envelope-fields';

const apiUrl = '/api/schemes';

export interface CustomTestSampleResult {
  sampleId: number;
  reportedResult: string;
  errorCode?: string;
  comments?: string;
  additionalDetail?: string;
}

export interface CustomTestResponse extends ResponseEnvelope {
  kitName?: string;
  kitNameOther?: string;
  kitLot?: string;
  expiryDate?: string;
  samples: CustomTestSampleResult[];
}

export const submitCustomTestResponse = (shipmentId: number, participantId: number, body: CustomTestResponse) =>
  axios.post<void>(`${apiUrl}/custom-test/shipments/${shipmentId}/participants/${participantId}/response`, body);

// Repeat the same { ...ResponseEnvelope, ...schemeSpecificFields } interface + submit function
// pattern for EidResponse/submitEidResponse, RecencyResponse/submitRecencyResponse,
// VlResponse/submitVlResponse, TbResponse/submitTbResponse, DtsResponse/submitDtsResponse,
// with field names matching each scheme's DTO from Tasks 4-8 exactly (Jackson serializes
// Java camelCase fields as JSON camelCase — no name translation needed) and each POSTing to
// `${apiUrl}/<scheme-code>/shipments/${shipmentId}/participants/${participantId}/response`.
```

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/app/shared/result-entry/
git commit -m "feat: add shared result-entry frontend scaffolding"
```

---

## Task 10: Frontend — per-scheme result-entry pages (one page per scheme)

**Files** (one set per scheme — `eid`, `recency`, `vl`, `tb`, `dts`, `custom-test`):
- Create: `src/main/webapp/app/entities/result-entry/{scheme}/{scheme}-response.tsx`
- Create: `src/main/webapp/app/entities/result-entry/{scheme}/{scheme}-response.spec.tsx`
- Modify: `src/main/webapp/app/entities/routes.tsx` (register each new page's route, following this file's existing per-entity route pattern)

**Interfaces:**
- Consumes: `ResponseEnvelopeFields` and `submit<Scheme>Response` from Task 9.
- Produces: one routed page per scheme at `/result-entry/{scheme-code}/:shipmentId/:participantId`, rendering the scheme's sample list (fetched via the existing generated `ShipmentSample` API — no new backend endpoint needed for reading samples, only for submitting results) plus `ResponseEnvelopeFields` plus the scheme-specific fields identified in Tasks 3–8.

Build in the same order as the backend tasks: generic engine page first (covers 4 schemes with shared UI, only the field list differs — implement as one parameterized component, not 4 near-duplicate files, unlike the backend where each scheme keeps its own DTO/service/resource for type safety), then EID, Recency, VL, TB, DTS.

- [ ] **Step 1: Write the failing test** for the generic-engine page (`custom-test-response.spec.tsx`) — render the page, fill in `kitName`/`kitLot`/a sample result, submit, assert `submitCustomTestResponse` was called with the expected body shape.
- [ ] **Step 2: Run test to verify it fails.**
- [ ] **Step 3: Write `custom-test-response.tsx`** — a form combining `ResponseEnvelopeFields` with the generic-engine-specific fields (`kitName`, `kitLot`, `expiryDate`, per-sample `reportedResult`/`errorCode`/`comments`/`additionalDetail`), submitting via `submitCustomTestResponse` on save.
- [ ] **Step 4: Run test to verify it passes.**
- [ ] **Step 5: Register the route** in `routes.tsx` following the existing pattern for other entity routes in that file.
- [ ] **Step 6: Repeat Steps 1–5 for each remaining scheme page** (`eid-response.tsx`, `recency-response.tsx`, `vl-response.tsx`, `tb-response.tsx`, `dts-response.tsx`), each using its Task 4–8 DTO shape and `submit<Scheme>Response` function, and each scheme's own field list as identified in this plan's "Field-to-entity mapping" section and Tasks 4–8's Interfaces blocks — do not reuse the generic-engine component for these, since each has a genuinely different field set (per-sample lot/expiry for EID, 3-band read for Recency, quantitative value + invalid flag for VL, per-instrument calibration array + draft workflow for TB, shipment-level DTS attributes + RTRI panel for DTS).
- [ ] **Step 7: Commit each scheme's page as its own commit** (6 commits total: `feat: add custom-test result entry page`, `feat: add EID result entry page`, etc.) — keeps each independently revertable per this plan's task-right-sizing.

---

## Self-review

**1. Spec coverage:** every master-plan Phase 4 task line is covered — generic engine (Task 3), EID/Recency/VL/TB/DTS in the specified ascending order (Tasks 4–8), "Result-entry API parity per the unified-API decision in Phase 0.5" (every endpoint lives under `/api/**`, Task 3–8's `@RequestMapping`s). The one master-plan line not directly a "Task" here — "this phase does NOT test scoring correctness" — is honored: no task in this plan computes `finalResult`/`shipmentScore`, only captures raw response data; scoring is explicitly left to the separate Phase 5 plan.

**2. Placeholder scan:** the only intentionally-abbreviated spots are Task 4/5's steps 1–8, which say "same shape as Task 3" — this is allowed here because Task 3 is fully written out immediately above with real code, and Task 4/5's *field lists* (the part that actually varies) are given in full in each task's Interfaces block, not deferred — an implementer reading Task 4 in isolation has the complete field list and the complete Task 3 pattern to copy, not a vague "similar to" with no concrete referent. Task 10's Step 6 similarly points at concrete, already-enumerated field lists per scheme rather than a bare "repeat for the rest."

**3. Type consistency:** `ResponseEnvelopeDTO` fields are used identically across Tasks 2–8 (`receiptDate`, `testDate`, `supervisorApproved`, etc.) and the frontend `ResponseEnvelope` interface in Task 9 mirrors them by the same names. `ParticipantResult.resultAttributes`/`ShipmentParticipantMap.resultAttributes` (Task 1) are the single mechanism every later task uses for scheme-specific data — no task invents a second JSON-bag column.
