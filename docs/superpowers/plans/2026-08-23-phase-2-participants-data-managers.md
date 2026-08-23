# Phase 2 — Participants & Data Managers Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring participant and data-manager management to legacy parity — country/PTCC assignment validation, the participant↔data-manager mapping workflow, Excel bulk import/export matching the legacy spreadsheet format exactly, and the data-manager auth flows (email verification, password reset) that depend on Phase 0.1's role model and Phase 0.3's email infrastructure.

**Architecture:** The JHipster-generated CRUD scaffolding for `Participant` and `DataManager` already exists (entity/repository/service/resource/React module) and the `participant_manager_map` many-to-many relationship is already modeled in JPA (`DataManager.participantses` ↔ `Participant.dataManagerses`). This plan adds the business logic the scaffolding is missing: real validation, a bulk-import pipeline (Apache POI), export, and auth-flow wiring. No new entities are needed.

**Tech Stack:** Spring Boot 4, Spring Data JPA, Apache POI (`org.apache.poi:poi-ooxml` — add to `pom.xml`), MapStruct, React + Redux (JHipster React blueprint), JUnit 5 + Spring Boot Test for `*ResourceIT.java` integration tests.

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 2 section) and the legacy reference below.

## Global Constraints

- Bulk import must match the real legacy template column-for-column: `Participant-Bulk-Import-Excel-Format-v2.xlsx` (columns A–T: `S.No.`, `Participant ID`, `Individual Participant (yes/no)`, `Participant First Name / Lab Name`, `Participant Last Name`, `Institute Name`, `Department`, `Address`, `Shipping Address`, `District/County`, `Province/State`, `Region`, `Country Name`, `Zip`, `Longitude`, `Latitude`, `Mobile Number`, `Email ID`, `Password`, `Additional Email ID`) — confirmed by inspecting the actual migrated template at `/home/administrator/Documents/Development/legacy-ept-files/files/Participant-Bulk-Import-Excel-Format-v2.xlsx`. Do not redesign this layout.
- A participant can be its own data manager (legacy design, preserve it — don't add a uniqueness constraint that forbids `participant.id == dataManager` linkage).
- Every service method that touches participant/data-manager PII must go through the Phase 0.1 role-scoping mechanism once that phase lands — this plan's tasks build the domain logic; wiring the `@PreAuthorize`/ownership-filter layer on top is Phase 0.1's job, referenced but not re-implemented here.

---

### Task 1: Participant unique-identifier validation + country requirement

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/impl/ParticipantServiceImpl.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/ParticipantValidationException.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/impl/ParticipantServiceImplTest.java`

**Interfaces:**
- Consumes: `ParticipantDTO` (existing, has `uniqueIdentifier: String`, `country: CountryDTO`).
- Produces: `ParticipantServiceImpl.save(ParticipantDTO)` now throws `ParticipantValidationException` (extends `RuntimeException`) instead of silently persisting an invalid `uniqueIdentifier` or a null `country`. Later tasks (bulk import, Task 3/4) catch this exception per-row rather than letting one bad row abort a whole file.

Legacy reference: `application/models/DbTable/Participants.php:MiscUtility::normalizeUniqueId()` (validates the PT-ID contains only letters, numbers, and hyphens — rejects spaces/special characters) — port the same validation rule, not a stricter or looser one.

- [ ] **Step 1: Write the failing test**

```java
@Test
void save_rejectsUniqueIdentifierWithInvalidCharacters() {
    ParticipantDTO dto = new ParticipantDTO();
    dto.setUniqueIdentifier("PT 001!"); // space + special char, matches legacy's rejected case
    dto.setCountry(existingCountryDTO());

    assertThatThrownBy(() -> participantService.save(dto))
        .isInstanceOf(ParticipantValidationException.class)
        .hasMessageContaining("letters, numbers and hyphens");
}

@Test
void save_rejectsMissingCountry() {
    ParticipantDTO dto = new ParticipantDTO();
    dto.setUniqueIdentifier("PT-001");
    dto.setCountry(null);

    assertThatThrownBy(() -> participantService.save(dto))
        .isInstanceOf(ParticipantValidationException.class)
        .hasMessageContaining("country");
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=ParticipantServiceImplTest test`
Expected: FAIL — `save()` currently persists both cases without validation.

- [ ] **Step 3: Write minimal implementation**

```java
// ParticipantValidationException.java
package zw.org.nmrl.ept.service;

public class ParticipantValidationException extends RuntimeException {
    public ParticipantValidationException(String message) {
        super(message);
    }
}
```

```java
// ParticipantServiceImpl.java — add before the existing persist call in save()
private static final Pattern UNIQUE_IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z0-9-]+$");

private void validate(ParticipantDTO participantDTO) {
    String id = participantDTO.getUniqueIdentifier();
    if (id == null || !UNIQUE_IDENTIFIER_PATTERN.matcher(id).matches()) {
        throw new ParticipantValidationException(
            "Unique ID '" + id + "' is invalid — use only letters, numbers and hyphens (no spaces or other special characters)."
        );
    }
    if (participantDTO.getCountry() == null) {
        throw new ParticipantValidationException("Participant country is required.");
    }
}

@Override
public ParticipantDTO save(ParticipantDTO participantDTO) {
    validate(participantDTO);
    // ...existing persist logic unchanged...
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=ParticipantServiceImplTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ParticipantValidationException.java \
        src/main/java/zw/org/nmrl/ept/service/impl/ParticipantServiceImpl.java \
        src/test/java/zw/org/nmrl/ept/service/impl/ParticipantServiceImplTest.java
git commit -m "feat: validate participant unique identifier and require a country"
```

---

### Task 2: Participant ↔ data-manager mapping service

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/ParticipantManagerMappingService.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/impl/ParticipantManagerMappingServiceImpl.java`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/ParticipantManagerMappingResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ParticipantManagerMappingResourceIT.java`

**Interfaces:**
- Consumes: `ParticipantRepository`, `DataManagerRepository` (both exist), the JPA relationship `DataManager.participantses` / `Participant.dataManagerses` (already mapped, `@JoinTable`).
- Produces: `ParticipantManagerMappingService.map(Long participantId, Long dataManagerId): void`, `unmap(Long participantId, Long dataManagerId): void`, `findDataManagersForParticipant(Long participantId): List<DataManagerDTO>`, `findParticipantsForDataManager(Long dataManagerId): List<ParticipantDTO>`. Later Phase 0.1 ownership-scoping work calls `findParticipantsForDataManager` to build the allowed-participant-ID filter.

Legacy reference: `application/services/Participants.php:addParticipantManagerMap($params, $type)` (line 231), `application/services/DataManagers.php:getParticipantDatamanagerListByPid()` / `getDatamanagerParticipantListByDid()` (lines 816–822) — same two lookup directions, port the shape not the SQL.

- [ ] **Step 1: Write the failing test**

```java
@Test
@Transactional
void map_thenFindDataManagersForParticipant_returnsMappedManager() throws Exception {
    Participant participant = participantRepository.saveAndFlush(createParticipant());
    DataManager manager = dataManagerRepository.saveAndFlush(createDataManager());

    restMockMvc.perform(
        post("/api/participant-manager-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"participantId\":" + participant.getId() + ",\"dataManagerId\":" + manager.getId() + "}")
    ).andExpect(status().isNoContent());

    List<DataManagerDTO> result = participantManagerMappingService.findDataManagersForParticipant(participant.getId());
    assertThat(result).extracting(DataManagerDTO::getId).containsExactly(manager.getId());
}

@Test
@Transactional
void map_participantCanMapToItself() throws Exception {
    // legacy design: a participant can be its own data manager — must not be rejected
    Participant participant = participantRepository.saveAndFlush(createParticipant());
    DataManager selfManager = dataManagerRepository.saveAndFlush(createDataManagerLinkedToParticipant(participant));

    participantManagerMappingService.map(participant.getId(), selfManager.getId());

    assertThat(participantManagerMappingService.findDataManagersForParticipant(participant.getId()))
        .extracting(DataManagerDTO::getId)
        .contains(selfManager.getId());
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=ParticipantManagerMappingResourceIT test`
Expected: FAIL — `/api/participant-manager-mappings` doesn't exist yet (404).

- [ ] **Step 3: Write minimal implementation**

```java
// ParticipantManagerMappingService.java
package zw.org.nmrl.ept.service;

import java.util.List;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;

public interface ParticipantManagerMappingService {
    void map(Long participantId, Long dataManagerId);
    void unmap(Long participantId, Long dataManagerId);
    List<DataManagerDTO> findDataManagersForParticipant(Long participantId);
    List<ParticipantDTO> findParticipantsForDataManager(Long dataManagerId);
}
```

```java
// ParticipantManagerMappingServiceImpl.java
package zw.org.nmrl.ept.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.service.ParticipantManagerMappingService;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.mapper.DataManagerMapper;
import zw.org.nmrl.ept.service.mapper.ParticipantMapper;

@Service
@Transactional
public class ParticipantManagerMappingServiceImpl implements ParticipantManagerMappingService {

    private final ParticipantRepository participantRepository;
    private final DataManagerRepository dataManagerRepository;
    private final ParticipantMapper participantMapper;
    private final DataManagerMapper dataManagerMapper;

    public ParticipantManagerMappingServiceImpl(
        ParticipantRepository participantRepository,
        DataManagerRepository dataManagerRepository,
        ParticipantMapper participantMapper,
        DataManagerMapper dataManagerMapper
    ) {
        this.participantRepository = participantRepository;
        this.dataManagerRepository = dataManagerRepository;
        this.participantMapper = participantMapper;
        this.dataManagerMapper = dataManagerMapper;
    }

    @Override
    public void map(Long participantId, Long dataManagerId) {
        Participant participant = participantRepository.findById(participantId).orElseThrow();
        DataManager manager = dataManagerRepository.findById(dataManagerId).orElseThrow();
        manager.addParticipantses(participant);
        dataManagerRepository.save(manager);
    }

    @Override
    public void unmap(Long participantId, Long dataManagerId) {
        Participant participant = participantRepository.findById(participantId).orElseThrow();
        DataManager manager = dataManagerRepository.findById(dataManagerId).orElseThrow();
        manager.removeParticipantses(participant);
        dataManagerRepository.save(manager);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataManagerDTO> findDataManagersForParticipant(Long participantId) {
        Participant participant = participantRepository.findById(participantId).orElseThrow();
        return participant.getDataManagerses().stream().map(dataManagerMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findParticipantsForDataManager(Long dataManagerId) {
        DataManager manager = dataManagerRepository.findById(dataManagerId).orElseThrow();
        return manager.getParticipantses().stream().map(participantMapper::toDto).toList();
    }
}
```

```java
// ParticipantManagerMappingResource.java — minimal REST surface
package zw.org.nmrl.ept.web.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.org.nmrl.ept.service.ParticipantManagerMappingService;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;

record MappingRequest(Long participantId, Long dataManagerId) {}

@RestController
@RequestMapping("/api")
public class ParticipantManagerMappingResource {

    private final ParticipantManagerMappingService service;

    public ParticipantManagerMappingResource(ParticipantManagerMappingService service) {
        this.service = service;
    }

    @PostMapping("/participant-manager-mappings")
    public ResponseEntity<Void> map(@RequestBody MappingRequest request) {
        service.map(request.participantId(), request.dataManagerId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/participant-manager-mappings")
    public ResponseEntity<Void> unmap(@RequestParam Long participantId, @RequestParam Long dataManagerId) {
        service.unmap(participantId, dataManagerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participants/{participantId}/data-managers")
    public List<DataManagerDTO> dataManagersForParticipant(@PathVariable Long participantId) {
        return service.findDataManagersForParticipant(participantId);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=ParticipantManagerMappingResourceIT test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ParticipantManagerMappingService.java \
        src/main/java/zw/org/nmrl/ept/service/impl/ParticipantManagerMappingServiceImpl.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ParticipantManagerMappingResource.java \
        src/test/java/zw/org/nmrl/ept/web/rest/ParticipantManagerMappingResourceIT.java
git commit -m "feat: add participant-manager mapping service and endpoint"
```

---

### Task 3: Bulk participant import — file parsing + template validation

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/ParticipantBulkImportService.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImpl.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/dto/BulkImportRowResult.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImplTest.java`
- Test fixture: `src/test/resources/fixtures/participant-bulk-import-valid.xlsx` (build from the real template's column layout in Global Constraints, 2 valid data rows)
- Test fixture: `src/test/resources/fixtures/participant-bulk-import-wrong-headers.xlsx` (same file with column B's header text changed, to test header-mismatch rejection)

**Interfaces:**
- Consumes: none from earlier tasks in this file, but Task 1's `ParticipantValidationException` is caught per-row here (not propagated).
- Produces: `ParticipantBulkImportService.importFile(InputStream xlsx): BulkImportSummary` where `BulkImportSummary` is `record BulkImportSummary(List<BulkImportRowResult> imported, List<BulkImportRowResult> failed)` and `BulkImportRowResult` is `record BulkImportRowResult(int rowNumber, String participantId, String errorMessage)` (`errorMessage` null on success). Task 4 extends this same service with row-processing/persistence logic; this task only covers structural parsing and header validation.

Legacy reference: `application/models/DbTable/Participants.php:processBulkImport()` (line 1730) — header validation happens before any row processing (`validateUploadedFile($fileName, $templateFilePath)`), reject the whole file with a list of header mismatches rather than partially processing a malformed file.

- [ ] **Step 1: Write the failing test**

```java
@Test
void importFile_rejectsFileWithWrongHeaders() throws Exception {
    try (InputStream xlsx = getClass().getResourceAsStream("/fixtures/participant-bulk-import-wrong-headers.xlsx")) {
        BulkImportSummary summary = bulkImportService.importFile(xlsx);
        assertThat(summary.imported()).isEmpty();
        assertThat(summary.failed()).hasSize(1);
        assertThat(summary.failed().get(0).errorMessage()).contains("does not match the expected template");
    }
}

@Test
void importFile_parsesHeaderRowAndSkipsIt() throws Exception {
    try (InputStream xlsx = getClass().getResourceAsStream("/fixtures/participant-bulk-import-valid.xlsx")) {
        BulkImportSummary summary = bulkImportService.importFile(xlsx);
        // 2 data rows in the fixture, row 1 is the header and must not appear as row 1 data
        assertThat(summary.imported()).extracting(BulkImportRowResult::rowNumber).containsExactly(2, 3);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=ParticipantBulkImportServiceImplTest test`
Expected: FAIL — `ParticipantBulkImportService` doesn't exist yet.

- [ ] **Step 3: Write minimal implementation**

```java
// BulkImportRowResult.java
package zw.org.nmrl.ept.service.dto;

public record BulkImportRowResult(int rowNumber, String participantId, String errorMessage) {
    public boolean isSuccess() {
        return errorMessage == null;
    }
}
```

```java
// ParticipantBulkImportService.java
package zw.org.nmrl.ept.service;

import java.io.InputStream;
import java.util.List;
import zw.org.nmrl.ept.service.dto.BulkImportRowResult;

public interface ParticipantBulkImportService {
    record BulkImportSummary(List<BulkImportRowResult> imported, List<BulkImportRowResult> failed) {}

    BulkImportSummary importFile(InputStream xlsx);
}
```

```java
// ParticipantBulkImportServiceImpl.java — parsing + header validation only in this task
package zw.org.nmrl.ept.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.service.ParticipantBulkImportService;
import zw.org.nmrl.ept.service.dto.BulkImportRowResult;

@Service
public class ParticipantBulkImportServiceImpl implements ParticipantBulkImportService {

    // Column order confirmed against the real legacy template
    // /home/administrator/Documents/Development/legacy-ept-files/files/Participant-Bulk-Import-Excel-Format-v2.xlsx
    private static final List<String> EXPECTED_HEADERS = List.of(
        "S.No.", "Participant ID", "Individual Participant\n(yes/no)", "Participant First Name\n(OR)\nLab Name",
        "Participant Last Name", "Institute Name", "Department", "Address", "Shipping\nAddress", "District/County",
        "Province/State", "Region", "Country Name\n(Pick from dropdown)", "Zip", "Longitude", "Latitude",
        "Mobile Number", "Email ID"
        // columns S (Password) and T (Additional Email ID) headers vary by legacy version; not enforced here
    );

    @Override
    public BulkImportSummary importFile(InputStream xlsx) {
        List<BulkImportRowResult> imported = new ArrayList<>();
        List<BulkImportRowResult> failed = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(xlsx)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            List<String> mismatches = validateHeaders(headerRow);
            if (!mismatches.isEmpty()) {
                failed.add(new BulkImportRowResult(1, null, "File headers do not match the expected template: " + mismatches));
                return new BulkImportSummary(imported, failed);
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isBlankRow(row)) {
                    continue;
                }
                // Row-level processing (validation, duplicate detection, persistence) is Task 4.
                imported.add(new BulkImportRowResult(rowIndex + 1, cellString(row, 1), null));
            }
        } catch (Exception e) {
            failed.add(new BulkImportRowResult(0, null, "The spreadsheet could not be read: " + e.getMessage()));
        }
        return new BulkImportSummary(imported, failed);
    }

    private List<String> validateHeaders(Row headerRow) {
        List<String> mismatches = new ArrayList<>();
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            String actual = cellString(headerRow, i);
            String expected = EXPECTED_HEADERS.get(i);
            if (!expected.equals(actual)) {
                mismatches.add("column " + (i + 1) + ": expected '" + expected + "', found '" + actual + "'");
            }
        }
        return mismatches;
    }

    private boolean isBlankRow(Row row) {
        return cellString(row, 0).isBlank() && cellString(row, 2).isBlank() && cellString(row, 3).isBlank();
    }

    private String cellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=ParticipantBulkImportServiceImplTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ParticipantBulkImportService.java \
        src/main/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImpl.java \
        src/main/java/zw/org/nmrl/ept/service/dto/BulkImportRowResult.java \
        src/test/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImplTest.java \
        src/test/resources/fixtures/participant-bulk-import-valid.xlsx \
        src/test/resources/fixtures/participant-bulk-import-wrong-headers.xlsx
git commit -m "feat: add participant bulk-import file parsing and header validation"
```

---

### Task 4: Bulk import row processing — duplicate detection and the leading-zero PT-ID guard

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImpl.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImplTest.java`
- Test fixture: `src/test/resources/fixtures/participant-bulk-import-numeric-id-loses-zero.xlsx` (a row whose PT-ID column is formatted as a *number* `1001`, where a zero-padded participant `01001` already exists in the seeded test data)

**Interfaces:**
- Consumes: Task 3's `ParticipantBulkImportServiceImpl` parsing loop (extends the per-row branch that currently just records success), Task 1's `ParticipantValidationException`.
- Produces: no new public interface — this task makes `importFile()` actually persist rows and reject the documented historical failure mode.

**This is the single most important row-validation rule in this task and is not optional.** Legacy reference: `application/models/DbTable/Participants.php:1759-1798` (comment block explains a real production incident: Excel silently drops a leading zero from a PT-ID cell that Excel auto-formats as a number, turning `01001` into `1001` — this created 459 wrong site IDs on the legacy system's 2026-A enrollment). The guard: if a numeric-typed cell's digit-only value, once zero-padded, already exists as a real participant `uniqueIdentifier`, or is shorter than the widest all-digit ID seen elsewhere in the same file, **reject the row** — do not guess the correct padding and do not silently import it un-padded.

- [ ] **Step 1: Write the failing test**

```java
@Test
void importFile_rejectsNumericIdThatLostALeadingZero() throws Exception {
    participantRepository.saveAndFlush(createParticipant("01001")); // existing zero-padded site

    try (InputStream xlsx = getClass().getResourceAsStream("/fixtures/participant-bulk-import-numeric-id-loses-zero.xlsx")) {
        // fixture's PT-ID cell is the NUMBER 1001 (Excel numeric cell type), not the text "1001"
        BulkImportSummary summary = bulkImportService.importFile(xlsx);

        assertThat(summary.imported()).isEmpty();
        assertThat(summary.failed()).hasSize(1);
        assertThat(summary.failed().get(0).errorMessage())
            .contains("came through as a number")
            .contains("ePT already has '01001'");
    }
}

@Test
void importFile_persistsValidRowsAsParticipants() throws Exception {
    try (InputStream xlsx = getClass().getResourceAsStream("/fixtures/participant-bulk-import-valid.xlsx")) {
        BulkImportSummary summary = bulkImportService.importFile(xlsx);
        assertThat(summary.imported()).hasSize(2);
        assertThat(participantRepository.findAll()).hasSize(2);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=ParticipantBulkImportServiceImplTest test`
Expected: FAIL — rows aren't persisted yet and the leading-zero guard doesn't exist.

- [ ] **Step 3: Write minimal implementation**

```java
// ParticipantBulkImportServiceImpl.java — replace the per-row branch from Task 3

private final ParticipantRepository participantRepository;
private final ParticipantService participantService; // for validate() via save()
private final CountryRepository countryRepository;

// constructor updated to inject the three repositories/services above

@Override
public BulkImportSummary importFile(InputStream xlsx) {
    // ...header validation from Task 3 unchanged...

    List<String> allDigitIds = new ArrayList<>();
    for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        String rawId = cellString(sheet.getRow(rowIndex), 1);
        if (rawId.chars().allMatch(Character::isDigit) && !rawId.isBlank()) {
            allDigitIds.add(rawId);
        }
    }
    int expectedIdWidth = allDigitIds.stream().mapToInt(String::length).max().orElse(0);

    for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        if (row == null || isBlankRow(row)) continue;

        Cell idCell = row.getCell(1);
        String rawId = cellString(row, 1);
        int rowNumber = rowIndex + 1;

        if (idCell != null && idCell.getCellType() == CellType.NUMERIC && rawId.chars().allMatch(Character::isDigit)) {
            String paddedOnce = "0" + rawId;
            String paddedTwice = "00" + rawId;
            boolean paddedExists = participantRepository.findByUniqueIdentifier(paddedOnce).isPresent()
                || participantRepository.findByUniqueIdentifier(paddedTwice).isPresent();
            if (paddedExists) {
                String existing = participantRepository.findByUniqueIdentifier(paddedOnce).isPresent() ? paddedOnce : paddedTwice;
                failed.add(new BulkImportRowResult(rowNumber, rawId,
                    "PT-ID '" + rawId + "' came through as a number and ePT already has '" + existing
                    + "'. Excel drops leading zeros from number cells. Format the PT-ID column as Text and upload again."));
                continue;
            }
            if (expectedIdWidth > 0 && rawId.length() < expectedIdWidth) {
                failed.add(new BulkImportRowResult(rowNumber, rawId,
                    "PT-ID '" + rawId + "' is shorter than the other IDs in this file (" + expectedIdWidth
                    + " digits) and came through as a number, so a leading zero was probably lost. Format the PT-ID column as Text and upload again."));
                continue;
            }
        }

        try {
            ParticipantDTO dto = mapRowToParticipant(row, rawId);
            participantService.save(dto);
            imported.add(new BulkImportRowResult(rowNumber, rawId, null));
        } catch (ParticipantValidationException e) {
            failed.add(new BulkImportRowResult(rowNumber, rawId, e.getMessage()));
        }
    }
    return new BulkImportSummary(imported, failed);
}

private ParticipantDTO mapRowToParticipant(Row row, String uniqueIdentifier) {
    ParticipantDTO dto = new ParticipantDTO();
    dto.setUniqueIdentifier(uniqueIdentifier);
    dto.setInstituteName(cellString(row, 5));
    dto.setDepartmentName(cellString(row, 6));
    dto.setAddress(cellString(row, 7));
    dto.setShippingAddress(cellString(row, 8));
    dto.setDistrict(cellString(row, 9));
    dto.setState(cellString(row, 10));
    dto.setRegion(cellString(row, 11));
    dto.setZip(cellString(row, 13));
    dto.setMobile(cellString(row, 16));
    dto.setEmail(cellString(row, 17));
    CountryDTO country = new CountryDTO();
    country.setId(countryRepository.findByName(cellString(row, 12)).orElseThrow().getId());
    dto.setCountry(country);
    return dto;
}
```

*(Requires adding `Optional<Participant> findByUniqueIdentifier(String uniqueIdentifier);` to `ParticipantRepository` and `Optional<Country> findByName(String name);` to `CountryRepository` — both are trivial Spring Data derived-query additions, add them as part of this step.)*

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=ParticipantBulkImportServiceImplTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImpl.java \
        src/main/java/zw/org/nmrl/ept/repository/ParticipantRepository.java \
        src/main/java/zw/org/nmrl/ept/repository/CountryRepository.java \
        src/test/java/zw/org/nmrl/ept/service/impl/ParticipantBulkImportServiceImplTest.java \
        src/test/resources/fixtures/participant-bulk-import-numeric-id-loses-zero.xlsx
git commit -m "feat: persist bulk-import rows and guard against leading-zero PT-ID loss"
```

---

### Task 5: Bulk participant export

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/ParticipantBulkExportService.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/impl/ParticipantBulkExportServiceImpl.java`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/ParticipantBulkExportResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/impl/ParticipantBulkExportServiceImplTest.java`

**Interfaces:**
- Consumes: `ParticipantRepository` (existing), the same column order as Task 3's `EXPECTED_HEADERS` (export must mirror import exactly — a round-trip export→import must be lossless per the master plan's exit criteria).
- Produces: `ParticipantBulkExportService.exportAll(): byte[]` (an `.xlsx` file byte array), exposed via `GET /api/participants/bulk-export`.

- [ ] **Step 1: Write the failing test**

```java
@Test
void exportAll_roundTripsThroughImport() throws Exception {
    Participant seeded = participantRepository.saveAndFlush(createParticipant("PT-777"));

    byte[] exported = exportService.exportAll();

    BulkImportSummary reimported = bulkImportService.importFile(new ByteArrayInputStream(exported));
    assertThat(reimported.failed()).isEmpty();
    assertThat(reimported.imported()).extracting(BulkImportRowResult::participantId).contains("PT-777");
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=ParticipantBulkExportServiceImplTest test`
Expected: FAIL — `ParticipantBulkExportService` doesn't exist yet.

- [ ] **Step 3: Write minimal implementation**

```java
// ParticipantBulkExportServiceImpl.java
package zw.org.nmrl.ept.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.service.ParticipantBulkExportService;

@Service
public class ParticipantBulkExportServiceImpl implements ParticipantBulkExportService {

    private final ParticipantRepository participantRepository;

    public ParticipantBulkExportServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public byte[] exportAll() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Participants");
            writeHeaderRow(sheet.createRow(0));
            int rowNum = 1;
            for (Participant participant : participantRepository.findAll()) {
                writeParticipantRow(sheet.createRow(rowNum++), participant);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeHeaderRow(Row row) {
        // Same 18 headers as ParticipantBulkImportServiceImpl.EXPECTED_HEADERS, columns 0-17
        String[] headers = {
            "S.No.", "Participant ID", "Individual Participant\n(yes/no)", "Participant First Name\n(OR)\nLab Name",
            "Participant Last Name", "Institute Name", "Department", "Address", "Shipping\nAddress", "District/County",
            "Province/State", "Region", "Country Name\n(Pick from dropdown)", "Zip", "Longitude", "Latitude",
            "Mobile Number", "Email ID"
        };
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
    }

    private void writeParticipantRow(Row row, Participant participant) {
        row.createCell(1).setCellValue(participant.getUniqueIdentifier()); // cell type STRING preserves leading zeros
        row.createCell(5).setCellValue(participant.getInstituteName());
        row.createCell(6).setCellValue(participant.getDepartmentName());
        row.createCell(7).setCellValue(participant.getAddress());
        row.createCell(8).setCellValue(participant.getShippingAddress());
        row.createCell(9).setCellValue(participant.getDistrict());
        row.createCell(10).setCellValue(participant.getState());
        row.createCell(11).setCellValue(participant.getRegion());
        row.createCell(12).setCellValue(participant.getCountry() != null ? participant.getCountry().getName() : "");
        row.createCell(13).setCellValue(participant.getZip());
        row.createCell(16).setCellValue(participant.getMobile());
        row.createCell(17).setCellValue(participant.getEmail());
    }
}
```

**Important:** `row.createCell(1).setCellValue(String)` on a POI cell defaults to string type, which preserves leading zeros — this is the deliberate fix for the exact bug Task 4 guards against on the import side. Do not switch this to a numeric cell type.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=ParticipantBulkExportServiceImplTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ParticipantBulkExportService.java \
        src/main/java/zw/org/nmrl/ept/service/impl/ParticipantBulkExportServiceImpl.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ParticipantBulkExportResource.java \
        src/test/java/zw/org/nmrl/ept/service/impl/ParticipantBulkExportServiceImplTest.java
git commit -m "feat: add participant bulk export mirroring the import template"
```

---

### Task 6: Data-manager email verification

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/DataManagerService.java` (or create if the interface doesn't yet exist under this exact name — confirm via `find src/main/java -iname 'DataManager*Service*.java'` before writing this task's detailed plan for execution)
- Test: `src/test/java/zw/org/nmrl/ept/service/DataManagerVerificationServiceIT.java`

**Interfaces:**
- Consumes: Phase 0.3's `MailTemplate` rendering + `EmailMessage` queue (this task assumes Phase 0.3 is already implemented — if it isn't yet, this task blocks on it; don't stub the email send).
- Produces: `DataManagerVerificationService.sendVerificationEmail(Long dataManagerId): void`, `confirmEmail(String token): boolean`.

Legacy reference: `application/services/DataManagers.php:confirmPrimaryMail()` (line 118), `resentDMVerifyMail()` (line 147) — a token-based confirm flow with a resend action; port the token-based confirm/resend shape, not the exact token format (use a `java.util.UUID` token rather than legacy's format).

- [ ] **Step 1: Write the failing test**

```java
@Test
@Transactional
void sendVerificationEmail_thenConfirm_activatesDataManager() {
    DataManager manager = dataManagerRepository.saveAndFlush(createUnverifiedDataManager());

    verificationService.sendVerificationEmail(manager.getId());
    String token = verificationTokenRepository.findByDataManagerId(manager.getId()).orElseThrow().getToken();

    boolean confirmed = verificationService.confirmEmail(token);

    assertThat(confirmed).isTrue();
    assertThat(dataManagerRepository.findById(manager.getId()).orElseThrow().getStatus()).isEqualTo(Status.ACTIVE);
}

@Test
void confirmEmail_rejectsUnknownToken() {
    assertThat(verificationService.confirmEmail("not-a-real-token")).isFalse();
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=DataManagerVerificationServiceIT test`
Expected: FAIL — verification service doesn't exist yet.

- [ ] **Step 3: Write minimal implementation**

```java
// DataManagerVerificationToken.java — new small entity: id, token (UUID string, unique), dataManagerId, createdAt
// DataManagerVerificationTokenRepository.java — JpaRepository<DataManagerVerificationToken, Long> + findByToken(String), findByDataManagerId(Long)

// DataManagerVerificationServiceImpl.java
@Service
@Transactional
public class DataManagerVerificationServiceImpl implements DataManagerVerificationService {

    private final DataManagerRepository dataManagerRepository;
    private final DataManagerVerificationTokenRepository tokenRepository;
    private final MailTemplateRenderingService mailTemplateRenderingService; // from Phase 0.3
    private final EmailQueueService emailQueueService; // from Phase 0.3

    // constructor injection omitted for brevity — standard Spring pattern matching this repo's other services

    @Override
    public void sendVerificationEmail(Long dataManagerId) {
        DataManager manager = dataManagerRepository.findById(dataManagerId).orElseThrow();
        DataManagerVerificationToken token = new DataManagerVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setDataManagerId(dataManagerId);
        token.setCreatedAt(Instant.now());
        tokenRepository.save(token);

        var rendered = mailTemplateRenderingService.render("data-manager-verification", Map.of(
            "firstName", manager.getFirstName(),
            "verificationLink", "/verify-email?token=" + token.getToken()
        ));
        emailQueueService.enqueue(manager.getPrimaryEmail(), rendered.subject(), rendered.body());
    }

    @Override
    public boolean confirmEmail(String token) {
        return tokenRepository.findByToken(token)
            .map(t -> {
                DataManager manager = dataManagerRepository.findById(t.getDataManagerId()).orElseThrow();
                manager.setStatus(Status.ACTIVE);
                dataManagerRepository.save(manager);
                tokenRepository.delete(t);
                return true;
            })
            .orElse(false);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=DataManagerVerificationServiceIT test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/domain/DataManagerVerificationToken.java \
        src/main/java/zw/org/nmrl/ept/repository/DataManagerVerificationTokenRepository.java \
        src/main/java/zw/org/nmrl/ept/service/DataManagerVerificationService.java \
        src/main/java/zw/org/nmrl/ept/service/impl/DataManagerVerificationServiceImpl.java \
        src/test/java/zw/org/nmrl/ept/service/DataManagerVerificationServiceIT.java
git commit -m "feat: add data-manager email verification flow"
```

---

### Task 7: Data-manager self-service password reset

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/DataManagerPasswordResetService.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/impl/DataManagerPasswordResetServiceImpl.java`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/DataManagerPasswordResetResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/DataManagerPasswordResetResourceIT.java`

**Interfaces:**
- Consumes: Task 6's `DataManagerVerificationToken` pattern (reuse the same token-entity shape for reset tokens — don't invent a second token mechanism), Phase 0.3's email queue, the `BCryptPasswordEncoder` bean (already exists in `SecurityConfiguration`).
- Produces: `DataManagerPasswordResetService.requestReset(String email): void`, `completeReset(String token, String newPassword): boolean`.

Legacy reference: `application/services/DataManagers.php:resetPassword($email)` (line 210), `resetPasswordFromAdmin()` (line 336, sets `forcePasswordReset` — already a field on the current `DataManager` entity, confirmed) — this task covers self-service reset only; admin-triggered reset (setting `forcePasswordReset=true` on an existing account) is a small follow-on not required for this phase's exit criteria and can be a fast-follow task using the same `forcePasswordReset` field.

- [ ] **Step 1: Write the failing test**

```java
@Test
@Transactional
void requestReset_thenCompleteReset_changesPassword() {
    DataManager manager = dataManagerRepository.saveAndFlush(createDataManagerWithPassword("oldPassword123"));

    resetService.requestReset(manager.getPrimaryEmail());
    String token = passwordResetTokenRepository.findByDataManagerId(manager.getId()).orElseThrow().getToken();

    boolean result = resetService.completeReset(token, "newPassword456");

    assertThat(result).isTrue();
    DataManager reloaded = dataManagerRepository.findById(manager.getId()).orElseThrow();
    assertThat(passwordEncoder.matches("newPassword456", reloaded.getPasswordHash())).isTrue();
    assertThat(reloaded.getForcePasswordReset()).isFalse();
}

@Test
void requestReset_unknownEmail_doesNotRevealAccountExistence() {
    // must not throw or leak whether the email exists — matches standard security practice
    assertThatCode(() -> resetService.requestReset("nobody@example.com")).doesNotThrowAnyException();
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Dtest=DataManagerPasswordResetResourceIT test`
Expected: FAIL — reset service doesn't exist yet.

- [ ] **Step 3: Write minimal implementation**

```java
// DataManagerPasswordResetServiceImpl.java
@Service
@Transactional
public class DataManagerPasswordResetServiceImpl implements DataManagerPasswordResetService {

    private final DataManagerRepository dataManagerRepository;
    private final DataManagerPasswordResetTokenRepository tokenRepository; // same shape as Task 6's verification token
    private final PasswordEncoder passwordEncoder;
    private final MailTemplateRenderingService mailTemplateRenderingService;
    private final EmailQueueService emailQueueService;

    // constructor injection omitted for brevity

    @Override
    public void requestReset(String email) {
        dataManagerRepository.findByPrimaryEmail(email).ifPresent(manager -> {
            DataManagerPasswordResetToken token = new DataManagerPasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setDataManagerId(manager.getId());
            token.setCreatedAt(Instant.now());
            tokenRepository.save(token);

            var rendered = mailTemplateRenderingService.render("data-manager-password-reset", Map.of(
                "firstName", manager.getFirstName(),
                "resetLink", "/reset-password?token=" + token.getToken()
            ));
            emailQueueService.enqueue(manager.getPrimaryEmail(), rendered.subject(), rendered.body());
        });
        // intentionally no else-branch: same response whether or not the email exists
    }

    @Override
    public boolean completeReset(String token, String newPassword) {
        return tokenRepository.findByToken(token)
            .map(t -> {
                DataManager manager = dataManagerRepository.findById(t.getDataManagerId()).orElseThrow();
                manager.setPasswordHash(passwordEncoder.encode(newPassword));
                manager.setForcePasswordReset(false);
                dataManagerRepository.save(manager);
                tokenRepository.delete(t);
                return true;
            })
            .orElse(false);
    }
}
```

*(Requires `Optional<DataManager> findByPrimaryEmail(String email);` on `DataManagerRepository` — a trivial derived query, add it as part of this step. Confirm the exact password-hash field name on `DataManager` — earlier inspection found `legacyPasswordHash` for migrated legacy accounts; this task's `passwordHash` field may need to be added or may already exist under a different name; verify via `grep -n "password" src/main/java/zw/org/nmrl/ept/domain/DataManager.java` before implementing and adjust the field name used above to match.)*

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Dtest=DataManagerPasswordResetResourceIT test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/domain/DataManagerPasswordResetToken.java \
        src/main/java/zw/org/nmrl/ept/repository/DataManagerPasswordResetTokenRepository.java \
        src/main/java/zw/org/nmrl/ept/service/DataManagerPasswordResetService.java \
        src/main/java/zw/org/nmrl/ept/service/impl/DataManagerPasswordResetServiceImpl.java \
        src/main/java/zw/org/nmrl/ept/web/rest/DataManagerPasswordResetResource.java \
        src/test/java/zw/org/nmrl/ept/web/rest/DataManagerPasswordResetResourceIT.java
git commit -m "feat: add data-manager self-service password reset"
```

---

## Self-review notes

- **Spec coverage:** all 5 bulleted tasks from `ept_project_plan.md`'s Phase 2 section are covered — Participant CRUD+assignment (Task 1), data-manager mapping (Task 2), bulk import (Tasks 3–4), bulk export (Task 5), auth flows (Tasks 6–7). Admin-triggered password reset and bulk password reset (legacy `resetPasswordFromAdmin`/`bulkResetPasswordsFromAdmin`) are explicitly noted as a fast-follow in Task 7 rather than silently dropped.
- **Placeholder scan:** Task 6's file-path note ("confirm via find... before writing this task's detailed plan for execution") and Task 7's field-name verification note are flagged explicitly as pre-execution checks, not vague TODOs — they exist because this plan was written without exhaustively reading every line of `DataManagerService`/`DataManager.java`; resolve them as the first step of executing those two tasks, don't skip.
- **Type consistency:** `ParticipantValidationException` (Task 1) is reused in Task 4's row-processing catch block. `BulkImportRowResult`/`BulkImportSummary` (Task 3) are reused unchanged through Task 4 and round-tripped in Task 5's test. The token-entity pattern introduced in Task 6 is explicitly reused (not reinvented) in Task 7.
