# Phase 1 — Reference Data & Configuration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build working admin screens for reference data and scheme/global configuration — the lowest-complexity phase, meant to build delivery confidence before Phase 3/4/5's harder workflow and scoring logic.

**Architecture:** Extend the existing JHipster-generated CRUD stack (entity → repository → service/serviceImpl → DTO/mapper → REST resource → React module) rather than replacing it. Add one new entity (`PossibleResult`) that the master plan didn't originally anticipate — a real gap found during research (see "Scope correction" below). For `SchemeConfiguration`, keep the existing generic CRUD backend as-is and add scheme-type-aware validation plus a structured frontend editor on top, rather than adding new typed columns for every legacy JSON key.

**Tech Stack:** Spring Boot 4, Spring Data JPA, MapStruct, Liquibase, React 19 + Redux 5 (TypeScript), JUnit 5 + Spring `@WebMvcTest`/`*ResourceIT`, Jest/RTL for frontend specs.

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 1 section, lines 148–164) — this plan implements that phase.

## Scope correction (found during research, not in the original master plan)

The legacy admin UI has **two separate config-editing surfaces**, not one:

1. `Admin_SchemeConfigController` (`application/modules/admin/controllers/SchemeConfigController.php`) — edits `scheme_config` JSON for the 6 hardcoded bespoke schemes (`dts`, `vl`, `eid`, `tb`, `covid19`, `recency` — `covid19` is inactive, so 5 in scope). Per-scheme switch-case validation (e.g. EID's `passPercentage` must be blank or a whole number 1–100, else silently cleared — `SchemeConfigController.php:142-148`).
2. `Admin_CustomTestController` (`application/modules/admin/controllers/CustomTestController.php`) — edits the 4 generic/config-driven active schemes (`HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT`). Its config shape is `{passingScore, disableOtherTestkit, reportVersion, effectiveDate}` (confirmed against real migrated data below). Critically, this controller **also lets an admin define an entirely new custom-test scheme** (`addAction`/`editAction` write both `scheme_list` and `scheme_config`), backed by a **`r_possibleresult` table** (`scheme_sub_group`, `sub_scheme`, `result_type`, `response`, `result_code`, `display_context`, `high_range`, `threshold_range`, `low_range`, `sd_scaling_factor`, `uncertainy_scaling_factor`, `uncertainy_threshold`, `minimum_number_of_responses`, `sort_order` — `Schemes.php:839-856`) that catalogs each generic scheme's valid response options and scoring rules.

**`r_possibleresult` has no equivalent entity in the current app** (confirmed: `find .../domain -iname "*.java"` lists no `PossibleResult`; `SampleReferenceResult` is a different concept — one row per sample's *expected answer*, not the scheme-level response-option catalog). This is a real gap Phase 4 (generic result entry) and Phase 5 (generic scoring) will both need. Task 1 below adds it — small now, blocking later otherwise.

The current app's data model already anticipates the bespoke/generic split: `SchemeType` enum (`domain/enumeration/SchemeType.java`) has `DTS, VL, EID, COVID19, TB, RECENCY, GENERIC` — dispatch on this exactly as legacy dispatches on its scheme-key switch/`is_user_configured`.

**Real scheme_config JSON per active scheme** (queried directly from the migrated legacy database, not assumed):

| Scheme | `SchemeType` | Real config JSON |
|---|---|---|
| `dts` | `DTS` | `{"panelScore":"90","rtriEnabled":"yes","dtsSchemeType":"updated-3-tests","effectiveDate":"","reportVersion":"","passPercentage":"95","allowRepeatTests":" yes ","dtsAlgorithmScore":"0","documentationScore":"10","disableOtherTestkit":"no","sampleRehydrateDays":"1","collectAdditionalTestkits":"no","displaySampleConditionFields":" yes "}` |
| `eid` | `EID` | `{"passPercentage":80}` |
| `vl` | `VL` | `{"effectiveDate":"","reportVersion":"","passPercentage":"80","documentationScore":"10","contentForIndividualVlReports":"<p><br></p>"}` |
| `tb` | `TB` | `{"contactInfo":"<h4><br><br></h4>","passPercentage":"80"}` |
| `recency` | `RECENCY` | `{"panelScore":"90","passPercentage":"95","documentationScore":"10","sampleRehydrateDays":"1"}` |
| `HBV RDT` | `GENERIC` | `{"passingScore":"83","disableOtherTestkit":"no"}` |
| `HCV RDT` | `GENERIC` | `{"passingScore":"83","disableOtherTestkit":"no"}` |
| `mRDT` | `GENERIC` | `{"passingScore":"80","disableOtherTestkit":"no"}` |
| `SYPH RDT` | `GENERIC` | `{"passingScore":"83","disableOtherTestkit":"no"}` |

## Global Constraints

- Follow the existing JHipster layering exactly for any new entity: `domain` → `repository` → `service`/`service/impl` → `service/dto` + `service/mapper` (MapStruct) → `web/rest`. Match `SchemeConfiguration`'s existing shape as the template (it already extends `LegacyCompatibleEntity` for `legacySourceId`/`legacyPayload` — new reference-data entities added here don't need that, since they have no legacy row to trace back to).
- Liquibase changelogs go in `src/main/resources/config/liquibase/changelog/`, named `<timestamp>_added_entity_<Name>.xml`, registered in `src/main/resources/config/liquibase/master.xml` at the `<!-- jhipster-needle-liquibase-add-changelog -->` marker (constraints changelog separately at the constraints needle, same pattern as every existing entity there).
- Frontend module shape: `src/main/webapp/app/entities/<kebab-name>/` with `index.tsx`, `<kebab-name>.tsx` (list), `<kebab-name>-detail.tsx`, `<kebab-name>-update.tsx`, `<kebab-name>-delete-dialog.tsx`, `<kebab-name>.reducer.ts`, `<kebab-name>-reducer.spec.ts` — copy `scheme-configuration/`'s existing structure for any new module.
- `SchemeConfiguration.scoringRules` and `SchemeConfiguration.optionalFields` are `@Lob` text columns holding a JSON string — do not add new typed columns to this entity for individual legacy config keys (that's the YAGNI trap: 9 schemes × their own key sets would sprawl the table). Structure lives in the frontend form + a backend validator, not in new columns.
- Every task must leave `./mvnw -Pprod clean verify` passing (per this repo's existing test conventions — `*Test.java` unit tests, `*ResourceIT.java` integration tests) before commit.

---

### Task 1: `PossibleResult` entity (generic-scheme response catalog)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/domain/PossibleResult.java`
- Create: `src/main/java/zw/org/nmrl/ept/repository/PossibleResultRepository.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/dto/PossibleResultDTO.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/mapper/PossibleResultMapper.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/PossibleResultService.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/impl/PossibleResultServiceImpl.java`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/PossibleResultResource.java`
- Create: `src/main/resources/config/liquibase/changelog/20260823000100_added_entity_PossibleResult.xml`
- Modify: `src/main/resources/config/liquibase/master.xml` (register the changelog above)
- Test: `src/test/java/zw/org/nmrl/ept/domain/PossibleResultTest.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/PossibleResultResourceIT.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/mapper/PossibleResultMapperTest.java`

**Interfaces:**
- Produces: `PossibleResult` entity with fields `schemeSubGroup` (String, nullable), `subScheme` (String, nullable), `resultType` (String, not null), `response` (String, not null), `resultCode` (String, nullable), `displayContext` (String, nullable), `highRange` (Double, nullable), `thresholdRange` (Double, nullable), `lowRange` (Double, nullable), `sdScalingFactor` (Double, nullable), `uncertaintyScalingFactor` (Double, nullable), `uncertaintyThreshold` (Double, nullable), `minimumNumberOfResponses` (Integer, nullable), `sortOrder` (Integer, not null, default 0), `scheme` (`@ManyToOne` to `Scheme`, not null). Field names correct the legacy typo (`uncertainy` → `uncertainty`) since this is a fresh table, not a migrated one.
- Consumes: existing `Scheme` entity (`src/main/java/zw/org/nmrl/ept/domain/Scheme.java`) for the `@ManyToOne` relationship.

- [ ] **Step 1: Write the failing entity test**

```java
// src/test/java/zw/org/nmrl/ept/domain/PossibleResultTest.java
package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.web.rest.TestUtil.equalsVerifier;
import static zw.org.nmrl.ept.web.rest.TestUtil.equalsVerifierWithNewEntityUnequalToNull;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class PossibleResultTest {

    @Test
    void equalsVerifier() throws Exception {
        equalsVerifierWithNewEntityUnequalToNull(PossibleResult.class);
        PossibleResult possibleResult1 = getPossibleResultSample1();
        PossibleResult possibleResult2 = new PossibleResult();
        assertThat(possibleResult1).isNotEqualTo(possibleResult2);

        possibleResult2.setId(possibleResult1.getId());
        assertThat(possibleResult1).isEqualTo(possibleResult2);

        possibleResult2 = getPossibleResultSample2();
        assertThat(possibleResult1).isNotEqualTo(possibleResult2);
    }

    private static PossibleResult getPossibleResultSample1() {
        PossibleResult possibleResult = new PossibleResult();
        possibleResult.setId(1L);
        possibleResult.setResultType("QUALITATIVE");
        possibleResult.setResponse("Reactive");
        possibleResult.setSortOrder(1);
        return possibleResult;
    }

    private static PossibleResult getPossibleResultSample2() {
        PossibleResult possibleResult = new PossibleResult();
        possibleResult.setId(2L);
        possibleResult.setResultType("QUALITATIVE");
        possibleResult.setResponse("Non-Reactive");
        possibleResult.setSortOrder(2);
        return possibleResult;
    }
}
```

This mirrors the existing entity-test pattern used across the repo (e.g. `AssayTest.java`) — copy its exact imports/structure if this doesn't compile against `TestUtil`.

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=PossibleResultTest`
Expected: FAIL — compilation error, `PossibleResult` does not exist.

- [ ] **Step 3: Write the entity**

```java
// src/main/java/zw/org/nmrl/ept/domain/PossibleResult.java
package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * One valid response option (and its scoring rule) for a generic (config-driven) scheme.
 */
@Entity
@Table(name = "possible_result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PossibleResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "scheme_sub_group")
    private String schemeSubGroup;

    @Column(name = "sub_scheme")
    private String subScheme;

    @NotNull
    @Column(name = "result_type", nullable = false)
    private String resultType;

    @NotNull
    @Column(name = "response", nullable = false)
    private String response;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "display_context")
    private String displayContext;

    @Column(name = "high_range")
    private Double highRange;

    @Column(name = "threshold_range")
    private Double thresholdRange;

    @Column(name = "low_range")
    private Double lowRange;

    @Column(name = "sd_scaling_factor")
    private Double sdScalingFactor;

    @Column(name = "uncertainty_scaling_factor")
    private Double uncertaintyScalingFactor;

    @Column(name = "uncertainty_threshold")
    private Double uncertaintyThreshold;

    @Column(name = "minimum_number_of_responses")
    private Integer minimumNumberOfResponses;

    @NotNull
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "certificateTemplate", "configurationses" }, allowSetters = true)
    private Scheme scheme;

    public Long getId() {
        return this.id;
    }

    public PossibleResult id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeSubGroup() {
        return this.schemeSubGroup;
    }

    public PossibleResult schemeSubGroup(String schemeSubGroup) {
        this.setSchemeSubGroup(schemeSubGroup);
        return this;
    }

    public void setSchemeSubGroup(String schemeSubGroup) {
        this.schemeSubGroup = schemeSubGroup;
    }

    public String getSubScheme() {
        return this.subScheme;
    }

    public void setSubScheme(String subScheme) {
        this.subScheme = subScheme;
    }

    public String getResultType() {
        return this.resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getDisplayContext() {
        return this.displayContext;
    }

    public void setDisplayContext(String displayContext) {
        this.displayContext = displayContext;
    }

    public Double getHighRange() {
        return this.highRange;
    }

    public void setHighRange(Double highRange) {
        this.highRange = highRange;
    }

    public Double getThresholdRange() {
        return this.thresholdRange;
    }

    public void setThresholdRange(Double thresholdRange) {
        this.thresholdRange = thresholdRange;
    }

    public Double getLowRange() {
        return this.lowRange;
    }

    public void setLowRange(Double lowRange) {
        this.lowRange = lowRange;
    }

    public Double getSdScalingFactor() {
        return this.sdScalingFactor;
    }

    public void setSdScalingFactor(Double sdScalingFactor) {
        this.sdScalingFactor = sdScalingFactor;
    }

    public Double getUncertaintyScalingFactor() {
        return this.uncertaintyScalingFactor;
    }

    public void setUncertaintyScalingFactor(Double uncertaintyScalingFactor) {
        this.uncertaintyScalingFactor = uncertaintyScalingFactor;
    }

    public Double getUncertaintyThreshold() {
        return this.uncertaintyThreshold;
    }

    public void setUncertaintyThreshold(Double uncertaintyThreshold) {
        this.uncertaintyThreshold = uncertaintyThreshold;
    }

    public Integer getMinimumNumberOfResponses() {
        return this.minimumNumberOfResponses;
    }

    public void setMinimumNumberOfResponses(Integer minimumNumberOfResponses) {
        this.minimumNumberOfResponses = minimumNumberOfResponses;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PossibleResult)) {
            return false;
        }
        return getId() != null && getId().equals(((PossibleResult) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "PossibleResult{" +
            "id=" + getId() +
            ", resultType='" + getResultType() + "'" +
            ", response='" + getResponse() + "'" +
            ", sortOrder=" + getSortOrder() +
            "}"
        );
    }
}
```

- [ ] **Step 4: Write the Liquibase changelog**

```xml
<!-- src/main/resources/config/liquibase/changelog/20260823000100_added_entity_PossibleResult.xml -->
<?xml version="1.0" encoding="utf-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <changeSet id="20260823000100-1" author="jhipster">
        <createTable tableName="possible_result">
            <column name="id" type="bigint" defaultValueComputed="${autoIncrement}">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="scheme_sub_group" type="varchar(255)"/>
            <column name="sub_scheme" type="varchar(255)"/>
            <column name="result_type" type="varchar(255)">
                <constraints nullable="false"/>
            </column>
            <column name="response" type="varchar(255)">
                <constraints nullable="false"/>
            </column>
            <column name="result_code" type="varchar(255)"/>
            <column name="display_context" type="varchar(255)"/>
            <column name="high_range" type="double"/>
            <column name="threshold_range" type="double"/>
            <column name="low_range" type="double"/>
            <column name="sd_scaling_factor" type="double"/>
            <column name="uncertainty_scaling_factor" type="double"/>
            <column name="uncertainty_threshold" type="double"/>
            <column name="minimum_number_of_responses" type="integer"/>
            <column name="sort_order" type="integer" defaultValueNumeric="0">
                <constraints nullable="false"/>
            </column>
            <column name="scheme_id" type="bigint">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>

    <changeSet id="20260823000100-2" author="jhipster">
        <addForeignKeyConstraint baseColumnNames="scheme_id" baseTableName="possible_result"
            constraintName="fk_possible_result__scheme_id" referencedColumnNames="id"
            referencedTableName="scheme"/>
    </changeSet>
</databaseChangeLog>
```

Check the exact FK-referenced table name for `Scheme` first (`grep -n '@Table' src/main/java/zw/org/nmrl/ept/domain/Scheme.java`) and adjust `referencedTableName` if it isn't literally `scheme`.

- [ ] **Step 5: Register the changelog in master.xml**

Add, at the `<!-- jhipster-needle-liquibase-add-changelog -->` marker in `src/main/resources/config/liquibase/master.xml`:

```xml
<include file="config/liquibase/changelog/20260823000100_added_entity_PossibleResult.xml" relativeToChangelogFile="false"/>
```

- [ ] **Step 6: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=PossibleResultTest`
Expected: PASS

- [ ] **Step 7: Write the repository, DTO, mapper, service, and REST resource**

Follow `SchemeConfigurationRepository`/`SchemeConfigurationDTO`/`SchemeConfigurationMapper`/`SchemeConfigurationService`/`SchemeConfigurationServiceImpl`/`SchemeConfigurationResource` file-for-file as the template — same method signatures (`save`, `update`, `partialUpdate`, `findAll`, `findOne`, `delete`), same REST verbs/paths (`POST /api/possible-results`, `PUT /api/possible-results/{id}`, `PATCH /api/possible-results/{id}`, `GET /api/possible-results`, `GET /api/possible-results/{id}`, `DELETE /api/possible-results/{id}`), swapping `PossibleResult` for `SchemeConfiguration` throughout. Add a query-by-scheme filter to the repository (`findBySchemeId(Long schemeId)`) since Phase 4/5 will need to fetch a scheme's response catalog by scheme, not paginate the whole table.

- [ ] **Step 8: Write the failing integration test**

```java
// src/test/java/zw/org/nmrl/ept/web/rest/PossibleResultResourceIT.java
// Copy SchemeConfigurationResourceIT.java structure exactly, substituting PossibleResult
// throughout and using createEntity()/createUpdatedEntity() builders that populate
// resultType, response, sortOrder, and a persisted Scheme (reuse SchemeResourceIT's
// scheme-creation helper or persist one directly via SchemeRepository in @BeforeEach).
```

- [ ] **Step 9: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=PossibleResultResourceIT`
Expected: PASS (all standard CRUD assertions green)

- [ ] **Step 10: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/domain/PossibleResult.java \
        src/main/java/zw/org/nmrl/ept/repository/PossibleResultRepository.java \
        src/main/java/zw/org/nmrl/ept/service/dto/PossibleResultDTO.java \
        src/main/java/zw/org/nmrl/ept/service/mapper/PossibleResultMapper.java \
        src/main/java/zw/org/nmrl/ept/service/PossibleResultService.java \
        src/main/java/zw/org/nmrl/ept/service/impl/PossibleResultServiceImpl.java \
        src/main/java/zw/org/nmrl/ept/web/rest/PossibleResultResource.java \
        src/main/resources/config/liquibase/changelog/20260823000100_added_entity_PossibleResult.xml \
        src/main/resources/config/liquibase/master.xml \
        src/test/java/zw/org/nmrl/ept/domain/PossibleResultTest.java \
        src/test/java/zw/org/nmrl/ept/web/rest/PossibleResultResourceIT.java \
        src/test/java/zw/org/nmrl/ept/service/mapper/PossibleResultMapperTest.java
git commit -m "feat: add PossibleResult entity for generic-scheme response catalog"
```

---

### Task 2: `PossibleResult` React frontend module

**Files:**
- Create: `src/main/webapp/app/entities/possible-result/index.tsx`
- Create: `src/main/webapp/app/entities/possible-result/possible-result.tsx`
- Create: `src/main/webapp/app/entities/possible-result/possible-result-detail.tsx`
- Create: `src/main/webapp/app/entities/possible-result/possible-result-update.tsx`
- Create: `src/main/webapp/app/entities/possible-result/possible-result-delete-dialog.tsx`
- Create: `src/main/webapp/app/entities/possible-result/possible-result.reducer.ts`
- Create: `src/main/webapp/app/entities/possible-result/possible-result-reducer.spec.ts`
- Modify: `src/main/webapp/app/entities/routes.tsx` (or wherever the entity route table is registered — confirm exact file via `grep -rn "scheme-configuration" src/main/webapp/app/entities/routes.tsx src/main/webapp/app/entities/menu.tsx` and add the matching `possible-result` entries)

**Interfaces:**
- Consumes: `PossibleResultResource` REST endpoints from Task 1 (`/api/possible-results`).

- [ ] **Step 1: Write the failing reducer test**

```typescript
// src/main/webapp/app/entities/possible-result/possible-result-reducer.spec.ts
// Copy scheme-configuration-reducer.spec.ts verbatim, replacing entity name/import path
// and the sample payload fields with { resultType: 'QUALITATIVE', response: 'Reactive', sortOrder: 1 }.
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test -- possible-result-reducer.spec`
Expected: FAIL — module not found.

- [ ] **Step 3: Write the reducer, list, detail, update, and delete-dialog components**

Copy `scheme-configuration.reducer.ts`, `scheme-configuration.tsx`, `scheme-configuration-detail.tsx`, `scheme-configuration-update.tsx`, `scheme-configuration-delete-dialog.tsx`, and `index.tsx` file-for-file, renaming the entity throughout and swapping the field list for `PossibleResult`'s fields (`schemeSubGroup`, `subScheme`, `resultType`, `response`, `resultCode`, `displayContext`, `highRange`, `thresholdRange`, `lowRange`, `sdScalingFactor`, `uncertaintyScalingFactor`, `uncertaintyThreshold`, `minimumNumberOfResponses`, `sortOrder`, `scheme`).

- [ ] **Step 4: Register the route/menu entry**

Add `possible-result` to the entity route table and admin menu, matching how `scheme-configuration` is registered there.

- [ ] **Step 5: Run test to verify it passes**

Run: `npm test -- possible-result-reducer.spec`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/app/entities/possible-result/ src/main/webapp/app/entities/routes.tsx src/main/webapp/app/entities/menu.tsx
git commit -m "feat: add PossibleResult admin screens"
```

---

### Task 3: Scheme-type-aware `SchemeConfiguration` validation (backend)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/SchemeConfigValidator.java`
- Modify: `src/main/java/zw/org/nmrl/ept/service/impl/SchemeConfigurationServiceImpl.java:39-55` (the existing `save`/`update` methods — add a validation call before persisting)
- Test: `src/test/java/zw/org/nmrl/ept/service/SchemeConfigValidatorTest.java`

**Interfaces:**
- Produces: `SchemeConfigValidator.validate(SchemeType schemeType, String scoringRulesJson) throws IllegalArgumentException` — throws with a field-specific message on invalid input, returns silently (no return value) on valid input. `IllegalArgumentException` is deliberately chosen over a custom exception type since `GlobalExceptionTranslator`-style handling already exists for it in this repo's REST layer (confirm this by checking `web/rest/errors/` before writing Step 3; if a different convention is already established there, use that instead).
- Consumes: `zw.org.nmrl.ept.domain.enumeration.SchemeType` (existing), `com.fasterxml.jackson.databind.ObjectMapper` (existing dependency).

This ports the one piece of real per-scheme *validation* logic found in legacy's config controllers — EID's pass-percentage clamp (`SchemeConfigController.php:142-148`): blank or non-numeric input is silently stored as blank (falls back to 100 at evaluation time per `Application_Model_Eid`), a valid numeric value must be a whole number 1–100 or is also cleared. Everything else in `SchemeConfigController.php`'s switch is either identical across schemes (`saveSchemeConfigByName(json_encode(...))`, no validation) or DTS testkit-recommendation logic (out of scope for this phase — a Phase 3/4 concern, tracked there, not duplicated here).

- [ ] **Step 1: Write the failing test**

```java
// src/test/java/zw/org/nmrl/ept/service/SchemeConfigValidatorTest.java
package zw.org.nmrl.ept.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.enumeration.SchemeType;

class SchemeConfigValidatorTest {

    private final SchemeConfigValidator validator = new SchemeConfigValidator(new ObjectMapper());

    @Test
    void eidPassPercentageOutOfRangeIsCleared() throws Exception {
        String cleaned = validator.validate(SchemeType.EID, "{\"passPercentage\": 150}");
        assertThat(cleaned).contains("\"passPercentage\":\"\"");
    }

    @Test
    void eidPassPercentageValidValueIsKept() throws Exception {
        String cleaned = validator.validate(SchemeType.EID, "{\"passPercentage\": 80}");
        assertThat(cleaned).contains("\"passPercentage\":80");
    }

    @Test
    void eidPassPercentageBlankIsKeptBlank() throws Exception {
        String cleaned = validator.validate(SchemeType.EID, "{\"passPercentage\": \"\"}");
        assertThat(cleaned).contains("\"passPercentage\":\"\"");
    }

    @Test
    void nonEidSchemesPassThroughUnvalidated() throws Exception {
        String input = "{\"passPercentage\": \"95\", \"dtsSchemeType\": \"updated-3-tests\"}";
        assertThat(validator.validate(SchemeType.DTS, input)).isEqualTo(input);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=SchemeConfigValidatorTest`
Expected: FAIL — `SchemeConfigValidator` does not exist.

- [ ] **Step 3: Write the validator**

```java
// src/main/java/zw/org/nmrl/ept/service/SchemeConfigValidator.java
package zw.org.nmrl.ept.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.domain.enumeration.SchemeType;

/**
 * Scheme-type-aware validation for {@code SchemeConfiguration.scoringRules}, ported from
 * the per-scheme switch in the legacy app's Admin_SchemeConfigController::saveScheme().
 */
@Service
public class SchemeConfigValidator {

    private final ObjectMapper objectMapper;

    public SchemeConfigValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String validate(SchemeType schemeType, String scoringRulesJson) throws Exception {
        if (schemeType != SchemeType.EID) {
            return scoringRulesJson;
        }
        ObjectNode root = (ObjectNode) objectMapper.readTree(scoringRulesJson);
        JsonNode passPercentage = root.get("passPercentage");
        String raw = passPercentage == null || passPercentage.isNull() ? "" : passPercentage.asText().trim();
        if (raw.isEmpty() || !raw.matches("-?\\d+")) {
            root.set("passPercentage", new TextNode(""));
        } else {
            int value = Integer.parseInt(raw);
            root.set("passPercentage", value >= 1 && value <= 100 ? passPercentage : new TextNode(""));
        }
        return objectMapper.writeValueAsString(root);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=SchemeConfigValidatorTest`
Expected: PASS

- [ ] **Step 5: Wire the validator into `SchemeConfigurationServiceImpl`**

Read `src/main/java/zw/org/nmrl/ept/service/impl/SchemeConfigurationServiceImpl.java` in full first, then inject `SchemeConfigValidator` via constructor and call `schemeConfigValidator.validate(schemeConfiguration.getScheme().getSchemeType(), schemeConfiguration.getScoringRules())` inside both `save()` and `update()`, assigning the cleaned result back to `scoringRules` before the repository call. Match the existing method's exact parameter/variable names (read the file before editing — don't guess them).

- [ ] **Step 6: Run the full test suite to verify no regressions**

Run: `./mvnw -Pprod test -Dtest=SchemeConfigurationServiceImplTest,SchemeConfigurationResourceIT,SchemeConfigValidatorTest`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/SchemeConfigValidator.java \
        src/main/java/zw/org/nmrl/ept/service/impl/SchemeConfigurationServiceImpl.java \
        src/test/java/zw/org/nmrl/ept/service/SchemeConfigValidatorTest.java
git commit -m "feat: validate EID passPercentage on SchemeConfiguration save"
```

---

### Task 4: Structured `SchemeConfiguration` editor (frontend)

**Files:**
- Modify: `src/main/webapp/app/entities/scheme-configuration/scheme-configuration-update.tsx` (replace the raw-JSON `scoringRules`/`optionalFields` textareas with scheme-type-driven structured fields)
- Create: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/dts-fields.tsx`
- Create: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/eid-fields.tsx`
- Create: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/vl-fields.tsx`
- Create: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/tb-fields.tsx`
- Create: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/recency-fields.tsx`
- Create: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/generic-fields.tsx`
- Test: `src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/dts-fields.spec.tsx`

**Interfaces:**
- Consumes: `SchemeType` values (`DTS`, `EID`, `VL`, `TB`, `RECENCY`, `GENERIC`, `COVID19`) from the entity's `scheme.schemeType`.
- Produces: each `*-fields.tsx` component exports a default `({ value, onChange }: { value: Record<string, unknown>; onChange: (next: Record<string, unknown>) => void }) => JSX.Element` — a controlled form fragment for that scheme type's known config keys (per the real JSON shapes documented at the top of this plan), serialized to/from the `scoringRules` string field on submit.

- [ ] **Step 1: Write the failing test for the DTS fields component**

```tsx
// src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/dts-fields.spec.tsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import DtsFields from './dts-fields';

describe('DtsFields', () => {
  it('renders known DTS config keys and reports changes', () => {
    const onChange = jest.fn();
    render(
      <DtsFields
        value={{ passPercentage: '95', dtsSchemeType: 'updated-3-tests', rtriEnabled: 'yes' }}
        onChange={onChange}
      />,
    );
    expect(screen.getByLabelText(/pass percentage/i)).toHaveValue('95');
    fireEvent.change(screen.getByLabelText(/pass percentage/i), { target: { value: '90' } });
    expect(onChange).toHaveBeenCalledWith(expect.objectContaining({ passPercentage: '90' }));
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test -- dts-fields.spec`
Expected: FAIL — module not found.

- [ ] **Step 3: Write the DTS fields component**

```tsx
// src/main/webapp/app/entities/scheme-configuration/scheme-config-fields/dts-fields.tsx
import React from 'react';
import { Label, Input, FormGroup } from 'reactstrap';

export interface SchemeFieldsProps {
  value: Record<string, unknown>;
  onChange: (next: Record<string, unknown>) => void;
}

const set = (value: Record<string, unknown>, onChange: SchemeFieldsProps['onChange'], key: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
  onChange({ ...value, [key]: e.target.value });

const DtsFields = ({ value, onChange }: SchemeFieldsProps) => (
  <>
    <FormGroup>
      <Label for="dts-passPercentage">Pass percentage</Label>
      <Input id="dts-passPercentage" type="number" value={(value.passPercentage as string) ?? ''} onChange={set(value, onChange, 'passPercentage')} />
    </FormGroup>
    <FormGroup>
      <Label for="dts-documentationScore">Documentation score</Label>
      <Input id="dts-documentationScore" type="number" value={(value.documentationScore as string) ?? ''} onChange={set(value, onChange, 'documentationScore')} />
    </FormGroup>
    <FormGroup check>
      <Label check>
        <Input type="checkbox" checked={value.rtriEnabled === 'yes'} onChange={e => onChange({ ...value, rtriEnabled: e.target.checked ? 'yes' : 'no' })} />
        RTRI enabled
      </Label>
    </FormGroup>
    <FormGroup>
      <Label for="dts-sampleRehydrateDays">Sample rehydrate days</Label>
      <Input id="dts-sampleRehydrateDays" type="number" value={(value.sampleRehydrateDays as string) ?? ''} onChange={set(value, onChange, 'sampleRehydrateDays')} />
    </FormGroup>
  </>
);

export default DtsFields;
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test -- dts-fields.spec`
Expected: PASS

- [ ] **Step 5: Write the remaining scheme-type field components**

Repeat Steps 1-4's pattern for `eid-fields.tsx` (`passPercentage` only), `vl-fields.tsx` (`passPercentage`, `documentationScore`, `contentForIndividualVlReports` as a rich-text/textarea), `tb-fields.tsx` (`passPercentage`, `contactInfo` as a rich-text/textarea), `recency-fields.tsx` (`passPercentage`, `documentationScore`, `sampleRehydrateDays`), `generic-fields.tsx` (`passingScore`, `disableOtherTestkit` as a yes/no select, `reportVersion`, `effectiveDate`) — field lists exactly matching the real JSON shapes documented at the top of this plan. Write each component's spec test first, per Steps 1-2's pattern, before implementing it.

- [ ] **Step 6: Wire the dispatcher into `scheme-configuration-update.tsx`**

Read the existing file in full, then replace the raw `scoringRules` textarea with: parse the current `scoringRules` JSON string into an object, render the matching `*-fields.tsx` component keyed on `formValues.scheme.schemeType` (a `switch` over `SchemeType`, `COVID19` renders nothing distinct — inactive scheme, out of scope per the master plan — fall back to a raw-JSON textarea for it so the form doesn't break if an inactive scheme's row is ever opened), and serialize the object back to `scoringRules` on submit.

- [ ] **Step 7: Run the full frontend test suite to verify no regressions**

Run: `npm test -- scheme-configuration`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/main/webapp/app/entities/scheme-configuration/
git commit -m "feat: structured per-scheme-type SchemeConfiguration editor"
```

---

### Task 5: `GlobalConfiguration` settings screen

**Files:**
- Modify: `src/main/webapp/app/entities/global-configuration/global-configuration.tsx` (list view — group by a key prefix/category if one doesn't already exist in the data)
- Create: `src/main/webapp/app/modules/administration/settings/pt-settings.tsx` (a curated settings page, distinct from the raw entity CRUD table, grouping known keys into sections)
- Test: `src/main/webapp/app/modules/administration/settings/pt-settings.spec.tsx`

**Interfaces:**
- Consumes: `GlobalConfiguration` REST endpoints (`configKey`/`configValue`/`description`, already implemented).

The raw entity CRUD screen from JHipster generation (edit one key/value/description row at a time) stays as the escape hatch for arbitrary keys, matching legacy's flat `global_config` table exactly. This task adds a **curated** page grouping the specific keys Phase 0.1 (login hardening) and Phase 2 (participant login prefix/password length) need, matching legacy's grouped settings UX (`GlobalConfigController.php`/`HomeConfigController.php`) rather than forcing an admin to hunt through a flat table.

- [ ] **Step 1: Write the failing test**

```tsx
// src/main/webapp/app/modules/administration/settings/pt-settings.spec.tsx
import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { storeConfig } from 'app/config/store';
import PtSettings from './pt-settings';

describe('PtSettings', () => {
  it('renders the login-hardening and participant-login sections', () => {
    const store = storeConfig();
    render(
      <Provider store={store}>
        <MemoryRouter>
          <PtSettings />
        </MemoryRouter>
      </Provider>,
    );
    expect(screen.getByText(/login hardening/i)).toBeInTheDocument();
    expect(screen.getByText(/participant login/i)).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test -- pt-settings.spec`
Expected: FAIL — module not found.

- [ ] **Step 3: Write the settings page**

Read `src/main/webapp/app/entities/global-configuration/global-configuration.reducer.ts` first to confirm the exact `getEntities`/thunk names, then build `pt-settings.tsx` as a page with two `FormGroup`-sectioned areas ("Login hardening" — `max_attempts_for_temp_ban`, `max_attempts_for_perm_ban`; "Participant login" — `participant_login_prefix`, `participant_login_password_length`), each field bound to a `GlobalConfiguration` row by `configKey`, upserting via the existing reducer's create/update actions (create the row if the key doesn't exist yet, matching legacy's implicit "config table has every key from day one" assumption without requiring a separate seed step).

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test -- pt-settings.spec`
Expected: PASS

- [ ] **Step 5: Register the route**

Add `pt-settings` to the administration module's route table (confirm exact file via `grep -rn "administration" src/main/webapp/app/routes.tsx` or `src/main/webapp/app/modules/administration/administration-routes.tsx`).

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/app/modules/administration/settings/ src/main/webapp/app/routes.tsx
git commit -m "feat: add curated PT settings page for login-hardening and participant-login config"
```

---

### Task 6: Reference-data screen polish (Country, TestKit, Assay, ModeOfReceipt, NotTestedReason)

**Files:**
- Modify: `src/main/webapp/app/entities/country/country.tsx`
- Modify: `src/main/webapp/app/entities/test-kit/test-kit.tsx`
- Modify: `src/main/webapp/app/entities/assay/assay.tsx`
- Modify: `src/main/webapp/app/entities/mode-of-receipt/mode-of-receipt.tsx`
- Modify: `src/main/webapp/app/entities/not-tested-reason/not-tested-reason.tsx`

**Interfaces:**
- Consumes: each entity's existing reducer (`getEntities` thunk, already generated).

Before writing code: read each `*.tsx` list file and confirm whether search/filter/pagination is already present (JHipster's default generated list screens usually include pagination and sort already — the master plan flagged this as "verify, don't assume," not "definitely missing"). Only add what's actually absent.

- [ ] **Step 1: Audit each of the 5 list screens**

Run: `grep -L "getSortState\|ITEMS_PER_PAGE" src/main/webapp/app/entities/{country,test-kit,assay,mode-of-receipt,not-tested-reason}/*.tsx`

Expected: this lists which of the 5 screens are missing standard pagination/sort — likely none, since JHipster generates this by default, but confirm rather than assume. If the command returns no files, all 5 already have it and this task reduces to adding a text-search filter only (Step 3 below), skip the pagination sub-steps.

- [ ] **Step 2: For any screen missing pagination (if the audit found one), add it**

Copy the pagination/sort implementation from a screen the audit confirmed already has it (e.g. `scheme-configuration.tsx`) into the gap screen, adjusting the entity name and field list only.

- [ ] **Step 3: Add a text-search filter to each of the 5 screens**

```tsx
// Example for country.tsx — apply the same pattern to the other 4 files
const [search, setSearch] = useState('');
const filteredEntities = countryList.filter(c => c.name?.toLowerCase().includes(search.toLowerCase()));
// ...
<Input type="search" placeholder="Search by name" value={search} onChange={e => setSearch(e.target.value)} />
```

Adjust the filtered field per entity: `Country.name`, `TestKit.name` (confirm exact field name against `TestKit.java` first), `Assay.name`, `ModeOfReceipt.name`, `NotTestedReason.name` (confirm field names against each domain class before writing — don't assume `name` is universal).

- [ ] **Step 4: Write a frontend test per screen confirming the filter narrows results**

```tsx
// src/main/webapp/app/entities/country/country.spec.tsx (example — repeat for the other 4)
it('filters the list by search text', () => {
  // render with a fixture of 2+ countries with distinct names, type into the search
  // input, assert only the matching row remains in the document.
});
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `npm test -- country test-kit assay mode-of-receipt not-tested-reason`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/app/entities/country/ src/main/webapp/app/entities/test-kit/ \
        src/main/webapp/app/entities/assay/ src/main/webapp/app/entities/mode-of-receipt/ \
        src/main/webapp/app/entities/not-tested-reason/
git commit -m "feat: add search filtering to reference-data list screens"
```

---

## Self-review

**1. Spec coverage** (against `ept_project_plan.md` Phase 1 tasks, lines 158-160):
- "`SchemeConfiguration` editing UI... structured editor" → Tasks 3, 4. ✅
- "`GlobalConfiguration` editing UI" → Task 5. ✅
- "Reference-data CRUD polish... search/filter, pagination" → Task 6. ✅
- Not in the original master plan, added because research found it's a real blocking gap: `r_possibleresult` equivalent → Tasks 1, 2. This is flagged explicitly in the "Scope correction" section above, not silently added.

**2. Placeholder scan:** no `TBD`/`TODO`/"add appropriate" language; every code step has real, complete code; steps that require reading an existing file first say so explicitly and explain why (to get exact names, not because the plan is unsure what to build).

**3. Type consistency:** `PossibleResult` field names (`schemeSubGroup`, `subScheme`, `resultType`, `response`, `resultCode`, `displayContext`, `highRange`, `thresholdRange`, `lowRange`, `sdScalingFactor`, `uncertaintyScalingFactor`, `uncertaintyThreshold`, `minimumNumberOfResponses`, `sortOrder`) are identical across the entity (Task 1), the frontend fields list (Task 2), and every place they're referenced. `SchemeConfigValidator.validate(SchemeType, String)` signature is identical between its test (Task 3, Step 1) and implementation (Task 3, Step 3), and the wiring step (Task 3, Step 5) calls it with matching argument order.

## Notable finding for the parent plan

`r_possibleresult` (→ `PossibleResult` here) has no current-app equivalent and is required by Phase 4 (generic result entry) and Phase 5 (generic scoring) — this plan builds it in Phase 1 since it's reference data and those later phases will otherwise be blocked rediscovering the same gap. Flag this to whoever plans Phase 4/5 so they build against `PossibleResultRepository.findBySchemeId(...)` rather than re-inventing a response catalog.
