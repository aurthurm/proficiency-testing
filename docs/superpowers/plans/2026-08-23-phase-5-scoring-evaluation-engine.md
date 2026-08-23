# Phase 5 — Scoring / Evaluation Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reproduce the legacy ePT scoring/evaluation engine for Zimbabwe's 9 active schemes (`dts`, `eid`, `HBV RDT`, `HCV RDT`, `mRDT`, `recency`, `SYPH RDT`, `tb`, `vl`) in this Spring Boot app, verified against real legacy-computed scores already sitting in the migrated database — not translated and trusted.

**Architecture:** One shared "universal" scoring service (non-responder/late-response exclusion gate + response/documentation score composition + `FinalResult` derivation) that every per-scheme evaluator calls into, plus one evaluator implementation per scheme (a `SchemeEvaluator` interface, one class per scheme, dispatched by `Scheme.code`). DTS's evaluator internally dispatches to a single algorithm implementation (`algoUpdatedThreeTests` + `algoRTRI`) via the same interface shape the other 9 legacy algorithm variants would use if ever added later — those 9 are *not* implemented in this plan. `HBV RDT`/`HCV RDT`/`mRDT`/`SYPH RDT` share one generic, `SchemeConfiguration`-driven evaluator (mirrors legacy's `CustomTest.php`).

**Tech Stack:** Spring Boot 4, Spring Data JPA (entities already exist), JUnit 5, real Postgres integration tests against data already migrated by the existing pipeline (`docs/migration/`).

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 5 section) and `/home/administrator/Documents/Development/ept/docs/SchemeArchitecture.md`. Legacy source of truth for the exact behavior ported task-by-task below: `application/models/{Dts,Eid,Recency,Tb,Vl,CustomTest}.php` and `application/services/{Evaluation,QuantitativeCalculations}.php` in `/home/administrator/Documents/Development/ept`.

## Global Constraints

- Scope is exactly the 9 active schemes confirmed in `scheme_list.status`: `dts`, `eid`, `HBV RDT`, `HCV RDT`, `mRDT`, `recency`, `SYPH RDT`, `tb`, `vl`. Do not implement `covid19`/`dbs`/legacy `HBV`/legacy `SYP` — they are inactive in production.
- Within DTS, implement only `algoUpdatedThreeTests` + `algoRTRI`. Do not implement `algoVietnam`, `algoSerial`, `algoParallel`, `algoSierraLeone`, `algoCoteDivoire`, `algoMyanmar`, `algoMalawi`, `algoGhana`/`algoGhanaSyphilis`. Design the dispatcher so adding one later is a new class + one line, not a rewrite.
- No scheme's evaluator is "done" until it passes golden-output diff cases (Task 10) built from real migrated shipments — reproducing the legacy formula from prose without this check is not acceptable per the master plan.
- Every score/result field this plan writes already exists on `ShipmentParticipantMap` (`shipmentScore`, `documentationScore`, `finalResult`, `failureReason`) and `ParticipantResult`/`ShipmentSample` (`calculatedScore`, `zScore`, `sampleScore`) — do not add new columns without first checking these.

---

## Correction to the master plan (`ept_project_plan.md`) — read this before starting

Verified directly against `Dts.php` (`evaluateSingleShipment()`, line ~47, and `evaluateAlgorithm()`, line 2615): **RTRI is gated by `$shipmentAttributes['enableRtri'] == 'yes'` — a per-shipment JSON attribute — not by `scheme_config.dts.rtriEnabled`, which the master plan's scope decision cited.** The real `scheme_config` row for `dts` does have `rtriEnabled: "yes"`, but that key is never actually read by the evaluator; only the per-shipment `enableRtri` attribute controls whether `algoRTRI()` runs (and even then, only for samples with `dts_rtri_is_editable == 'yes'`). **Before writing Task 9's golden-output cases, query every real migrated DTS shipment's `shipment_attributes` for `enableRtri` (not just the small sample already checked, which had none) to confirm whether the RTRI path is exercised in production at all.** If no real shipment has `enableRtri: "yes"`, still implement `algoRTRI()` (it's small and already fully specified below) but golden-output coverage for the "with RTRI" case will need a synthetic fixture rather than a real one — note this explicitly rather than skipping the case.

Everything else in the master plan's Phase 5 section (universal formula shape, scope to 9 schemes, DTS narrowed to one algorithm, golden-output testing mandate) is accurate and confirmed against the real code below.

---

### Task 1: Golden-output fixture loader

**Files:**
- Create: `src/test/java/zw/org/nmrl/ept/evaluation/GoldenOutputFixture.java`
- Create: `src/test/java/zw/org/nmrl/ept/evaluation/GoldenOutputFixtureLoaderIT.java`

**Interfaces:**
- Produces: `record GoldenOutputFixture(Long shipmentParticipantMapId, String schemeCode, Double legacyShipmentScore, Double legacyDocumentationScore, FinalResult legacyFinalResult, String legacyFailureReasonJson)` — every later evaluator task's golden-output test loads fixtures via this record.

The legacy-computed scores are **already sitting in the migrated database** — no extraction from `legacy_record_archive` or a second legacy instance is needed. The existing promotion SQL (`src/main/resources/migration/promote/080_shipment_participant_maps.sql`) already copied `shipment_score`/`documentation_score`/`final_result`/`failure_reason` straight from the legacy payload into the typed `ShipmentParticipantMap` rows. This task just needs to read them back out **before** any new evaluator overwrites them.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.domain.enumeration.FinalResult;

@SpringBootTest
class GoldenOutputFixtureLoaderIT {

    @Autowired
    private GoldenOutputFixtureLoader loader;

    @Test
    void loadsFixturesForEidHavingBothPassAndFail() {
        List<GoldenOutputFixture> fixtures = loader.loadForScheme("eid");

        assertThat(fixtures).isNotEmpty();
        assertThat(fixtures).anyMatch(f -> f.legacyFinalResult() == FinalResult.PASS);
        assertThat(fixtures).anyMatch(f -> f.legacyFinalResult() == FinalResult.FAIL);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=GoldenOutputFixtureLoaderIT`
Expected: FAIL — `GoldenOutputFixtureLoader` does not exist yet. (This test needs a Postgres database migrated from a real legacy snapshot per `docs/migration/DRY_RUN.md` — run against that, not an empty schema.)

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import java.util.List;
import org.springframework.stereotype.Component;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;

@Component
public class GoldenOutputFixtureLoader {

    private final ShipmentParticipantMapRepository repository;

    public GoldenOutputFixtureLoader(ShipmentParticipantMapRepository repository) {
        this.repository = repository;
    }

    public List<GoldenOutputFixture> loadForScheme(String schemeCode) {
        return repository
            .findByShipment_Scheme_CodeIgnoreCase(schemeCode)
            .stream()
            .filter(m -> m.getFinalResult() != null)
            .map(m ->
                new GoldenOutputFixture(
                    m.getId(),
                    schemeCode,
                    m.getShipmentScore(),
                    m.getDocumentationScore(),
                    m.getFinalResult(),
                    m.getFailureReason()
                )
            )
            .toList();
    }
}
```

Add `List<ShipmentParticipantMap> findByShipment_Scheme_CodeIgnoreCase(String code);` to `src/main/java/zw/org/nmrl/ept/repository/ShipmentParticipantMapRepository.java` if it doesn't already expose a scheme-scoped query — check the file first, JHipster-generated repositories are often just `JpaRepository<ShipmentParticipantMap, Long>` with no custom finders.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=GoldenOutputFixtureLoaderIT`
Expected: PASS, against a Postgres database seeded per `docs/migration/DRY_RUN.md`.

- [ ] **Step 5: Commit**

```bash
git add src/test/java/zw/org/nmrl/ept/evaluation/GoldenOutputFixture.java src/test/java/zw/org/nmrl/ept/evaluation/GoldenOutputFixtureLoaderIT.java src/main/java/zw/org/nmrl/ept/evaluation/GoldenOutputFixtureLoader.java src/main/java/zw/org/nmrl/ept/repository/ShipmentParticipantMapRepository.java
git commit -m "test: add golden-output fixture loader reading already-migrated legacy scores"
```

---

### Task 2: Universal exclusion gate + score composition

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/NonResponderExclusion.java`
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/EvaluationOutcome.java`
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/FailureReasonEntry.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/NonResponderExclusionTest.java`

**Interfaces:**
- Produces: `record FailureReasonEntry(String warning, String correctiveAction)` — every evaluator task below appends these and JSON-serializes the list into `ShipmentParticipantMap.failureReason`.
- Produces: `record EvaluationOutcome(double shipmentScore, double documentationScore, FinalResult finalResult, List<FailureReasonEntry> failureReasons, boolean isExcluded, boolean isResponseLate)` — every per-scheme evaluator (Tasks 4–9) returns this.
- Produces: `class NonResponderExclusion { static boolean shouldExclude(ShipmentParticipantMap map, List<?> results) }` — every per-scheme evaluator calls this first, before any scoring.

This ports `Application_Service_Evaluation::excludeNonResponder()` (`application/services/Evaluation.php:95`), which every scheme's `evaluate()` calls before doing any scoring — build it once here rather than duplicating the logic per scheme.

```php
public static function excludeNonResponder($db, array $shipment, ?array $results = null): bool
{
    $responseStatus = strtolower(trim((string) ($shipment['response_status'] ?? '')));
    $notTested = strtolower(trim((string) ($shipment['is_pt_test_not_performed'] ?? ''))) === 'yes';

    $didNotParticipate = $responseStatus === ''
        || $responseStatus === 'noresponse'
        || $responseStatus === 'draft'
        || ($results !== null && count($results) === 0);

    $isLate = false;
    $responseSwitchOn = strtolower(trim((string) ($shipment['response_switch'] ?? ''))) === 'on';
    if (!$didNotParticipate && !$notTested && !$responseSwitchOn) {
        $reportDate = trim((string) ($shipment['shipment_test_report_date'] ?? ''));
        if ($reportDate !== '' && strncmp($reportDate, '0000', 4) !== 0) {
            $cutoff = Pt_Commons_DateUtility::shipmentCutoff($shipment['response_deadline'] ?? null);
            if ($cutoff !== null) {
                if (new DateTimeImmutable($reportDate) > $cutoff) {
                    $isLate = true;
                }
            }
        }
    }

    return $didNotParticipate || $notTested || $isLate;
}
```

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.enumeration.ResponseStatus;

class NonResponderExclusionTest {

    @Test
    void excludesWhenResponseStatusIsDraft() {
        ShipmentParticipantMap map = new ShipmentParticipantMap();
        map.setResponseStatus(ResponseStatus.DRAFT);

        assertThat(NonResponderExclusion.shouldExclude(map, List.of())).isTrue();
    }

    @Test
    void excludesWhenResultsAreEmpty() {
        ShipmentParticipantMap map = new ShipmentParticipantMap();
        map.setResponseStatus(ResponseStatus.RESPONDED);

        assertThat(NonResponderExclusion.shouldExclude(map, List.of())).isTrue();
    }

    @Test
    void doesNotExcludeAnOnTimeResponse() {
        ShipmentParticipantMap map = new ShipmentParticipantMap();
        map.setResponseStatus(ResponseStatus.RESPONDED);
        map.setShipmentTestReportDate(Instant.parse("2026-01-01T00:00:00Z"));

        assertThat(NonResponderExclusion.shouldExclude(map, List.of("one-result"))).isFalse();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=NonResponderExclusionTest`
Expected: FAIL — `NonResponderExclusion` does not exist. Check `src/main/java/zw/org/nmrl/ept/domain/enumeration/ResponseStatus.java` first for the exact enum constant names (`DRAFT`, `RESPONDED`, etc.) before writing the test — don't guess them.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import java.time.Instant;
import java.util.List;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.enumeration.ResponseStatus;

public final class NonResponderExclusion {

    private NonResponderExclusion() {}

    public static boolean shouldExclude(ShipmentParticipantMap map, List<?> results) {
        ResponseStatus status = map.getResponseStatus();
        boolean notTested = Boolean.TRUE.equals(map.getIsPtTestNotPerformed());

        boolean didNotParticipate =
            status == null || status == ResponseStatus.DRAFT || status == ResponseStatus.NORESPONSE || (results != null && results.isEmpty());

        boolean isLate = false;
        boolean responseSwitchOn = map.getShipment() != null && Boolean.TRUE.equals(map.getShipment().getResponseSwitch());
        if (!didNotParticipate && !notTested && !responseSwitchOn) {
            Instant reportDate = map.getShipmentTestReportDate();
            Instant cutoff = ShipmentCutoff.of(map.getShipment());
            if (reportDate != null && cutoff != null && reportDate.isAfter(cutoff)) {
                isLate = true;
            }
        }

        return didNotParticipate || notTested || isLate;
    }
}
```

`ShipmentCutoff.of(Shipment)` (the equivalent of `Pt_Commons_DateUtility::shipmentCutoff()`) is deadline/timezone logic already assigned to Phase 3.5 in `ept_project_plan.md` — check whether `Phase 3`'s implementation plan already produced this class before writing a duplicate; if not, implement it here as a thin wrapper around `Shipment.responseDeadline` (confirm the exact field name and type on `Shipment` first — earlier research found `response_deadline` as a `datetime` in the legacy schema).

Also check `ResponseStatus`'s real enum constant for "no response" — the legacy string is `noresponse` (one word); confirm the Java enum's actual constant name (likely `NORESPONSE` or `NO_RESPONSE`) before compiling.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=NonResponderExclusionTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/NonResponderExclusion.java src/main/java/zw/org/nmrl/ept/evaluation/EvaluationOutcome.java src/main/java/zw/org/nmrl/ept/evaluation/FailureReasonEntry.java src/test/java/zw/org/nmrl/ept/evaluation/NonResponderExclusionTest.java
git commit -m "feat: port legacy non-responder exclusion gate shared by every scheme"
```

---

### Task 3: Port `QuantitativeCalculations`

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/QuantitativeCalculations.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/QuantitativeCalculationsTest.java`

**Interfaces:**
- Consumes: nothing (pure functions).
- Produces: `QuantitativeCalculations.{mean,median,standardDeviation,quantile,zScore,standardUncertainty,coefficientOfVariation}(...)` — Task 7 (VL) and Task 5 (generic quantitative) call these.

This is a direct, near-verbatim port of `application/services/QuantitativeCalculations.php` (94 lines, read in full, reproduced below) — no legacy DB access, no legacy-specific behavior to reinterpret.

```php
public static function calculateMean(array $dataSet): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    return array_sum($dataSet) / count($dataSet);
}

public static function calculateMedian(array $dataSet): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    sort($dataSet);
    $count = count($dataSet);
    $middle = floor(($count - 1) / 2);
    return ($count % 2) ? (float) $dataSet[$middle] : ($dataSet[$middle] + $dataSet[$middle + 1]) / 2.0;
}

public static function calculateStandardDeviation(array $dataSet, ?float $mean = null): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    if (count($dataSet) === 1) return 0.0;
    $mean ??= self::calculateMean($dataSet);
    $sumOfSquares = array_reduce($dataSet, fn($carry, $item) => $carry + pow($item - $mean, 2), 0.0);
    return sqrt($sumOfSquares / count($dataSet));
}

public static function calculateQuantile(array $dataSet, float $quantile): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    if ($quantile < 0 || $quantile > 1) throw new InvalidArgumentException('Quantile must be between 0 and 1.');
    sort($dataSet);
    $count = count($dataSet);
    $index = ($count - 1) * $quantile;
    $lower = floor($index);
    $upper = ceil($index);
    $weight = $index - $lower;
    return ($lower == $upper) ? (float) $dataSet[$lower] : $dataSet[$lower] * (1 - $weight) + $dataSet[$upper] * $weight;
}

public static function calculateZScore(float $value, array $dataSet, ?float $mean = null, ?float $stdDev = null): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    $mean ??= self::calculateMean($dataSet);
    $stdDev ??= self::calculateStandardDeviation($dataSet, $mean);
    return ($stdDev == 0) ? 0.0 : ($value - $mean) / $stdDev;
}

public static function calculateStandardUncertainty(array $dataSet, ?float $stdDev = null): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    $stdDev ??= self::calculateStandardDeviation($dataSet);
    return $stdDev / sqrt(count($dataSet));
}

public static function calculateCoefficientOfVariation(array $dataSet, ?float $mean = null, ?float $stdDev = null): float
{
    if (self::isDataSetInvalid($dataSet)) return 0.0;
    $mean ??= self::calculateMean($dataSet);
    $stdDev ??= self::calculateStandardDeviation($dataSet);
    return ($mean == 0) ? 0.0 : $stdDev / $mean;
}
```

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuantitativeCalculationsTest {

    @Test
    void meanOfKnownDataSet() {
        assertThat(QuantitativeCalculations.mean(List.of(1.0, 2.0, 3.0, 4.0))).isEqualTo(2.5);
    }

    @Test
    void emptyDataSetReturnsZeroForEveryStatistic() {
        assertThat(QuantitativeCalculations.mean(List.of())).isZero();
        assertThat(QuantitativeCalculations.median(List.of())).isZero();
        assertThat(QuantitativeCalculations.standardDeviation(List.of(), null)).isZero();
    }

    @Test
    void standardDeviationOfSingleElementIsZero() {
        assertThat(QuantitativeCalculations.standardDeviation(List.of(5.0), null)).isZero();
    }

    @Test
    void zScoreMatchesManualCalculation() {
        List<Double> dataSet = List.of(10.0, 12.0, 14.0, 16.0, 18.0);
        double mean = QuantitativeCalculations.mean(dataSet);
        double stdDev = QuantitativeCalculations.standardDeviation(dataSet, mean);

        double zScore = QuantitativeCalculations.zScore(20.0, dataSet, null, null);

        assertThat(zScore).isCloseTo((20.0 - mean) / stdDev, within(0.0001));
    }

    @Test
    void zScoreWithZeroStandardDeviationIsZero() {
        assertThat(QuantitativeCalculations.zScore(5.0, List.of(5.0, 5.0, 5.0), null, null)).isZero();
    }

    @Test
    void quantileRejectsOutOfRangeInput() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
            QuantitativeCalculations.quantile(List.of(1.0, 2.0, 3.0), 1.5)
        );
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=QuantitativeCalculationsTest`
Expected: FAIL — `QuantitativeCalculations` does not exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import java.util.ArrayList;
import java.util.List;

public final class QuantitativeCalculations {

    private QuantitativeCalculations() {}

    private static boolean isInvalid(List<Double> dataSet) {
        return dataSet == null || dataSet.isEmpty() || dataSet.stream().allMatch(v -> v == null || v == 0.0);
    }

    public static double mean(List<Double> dataSet) {
        if (isInvalid(dataSet)) return 0.0;
        return dataSet.stream().mapToDouble(Double::doubleValue).sum() / dataSet.size();
    }

    public static double median(List<Double> dataSet) {
        if (isInvalid(dataSet)) return 0.0;
        List<Double> sorted = new ArrayList<>(dataSet);
        sorted.sort(Double::compareTo);
        int count = sorted.size();
        int middle = (count - 1) / 2;
        return (count % 2 != 0) ? sorted.get(middle) : (sorted.get(middle) + sorted.get(middle + 1)) / 2.0;
    }

    public static double standardDeviation(List<Double> dataSet, Double mean) {
        if (isInvalid(dataSet)) return 0.0;
        if (dataSet.size() == 1) return 0.0;
        double m = mean != null ? mean : mean(dataSet);
        double sumOfSquares = dataSet.stream().mapToDouble(v -> Math.pow(v - m, 2)).sum();
        return Math.sqrt(sumOfSquares / dataSet.size());
    }

    public static double quantile(List<Double> dataSet, double quantile) {
        if (isInvalid(dataSet)) return 0.0;
        if (quantile < 0 || quantile > 1) throw new IllegalArgumentException("Quantile must be between 0 and 1.");
        List<Double> sorted = new ArrayList<>(dataSet);
        sorted.sort(Double::compareTo);
        int count = sorted.size();
        double index = (count - 1) * quantile;
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        double weight = index - lower;
        return (lower == upper) ? sorted.get(lower) : sorted.get(lower) * (1 - weight) + sorted.get(upper) * weight;
    }

    public static double zScore(double value, List<Double> dataSet, Double mean, Double stdDev) {
        if (isInvalid(dataSet)) return 0.0;
        double m = mean != null ? mean : mean(dataSet);
        double sd = stdDev != null ? stdDev : standardDeviation(dataSet, m);
        return (sd == 0.0) ? 0.0 : (value - m) / sd;
    }

    public static double standardUncertainty(List<Double> dataSet, Double stdDev) {
        if (isInvalid(dataSet)) return 0.0;
        double sd = stdDev != null ? stdDev : standardDeviation(dataSet, null);
        return sd / Math.sqrt(dataSet.size());
    }

    public static double coefficientOfVariation(List<Double> dataSet, Double mean, Double stdDev) {
        if (isInvalid(dataSet)) return 0.0;
        double m = mean != null ? mean : mean(dataSet);
        double sd = stdDev != null ? stdDev : standardDeviation(dataSet, null);
        return (m == 0.0) ? 0.0 : sd / m;
    }
}
```

Note: the PHP `isDataSetInvalid` uses `empty(array_filter($dataSet))`, which treats an all-zero data set as invalid too (PHP's `array_filter` with no callback drops falsy values, and `0.0` is falsy) — the Java port above reproduces that exact quirk (`allMatch(v -> v == null || v == 0.0)`), don't "fix" it to just an emptiness check.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=QuantitativeCalculationsTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/QuantitativeCalculations.java src/test/java/zw/org/nmrl/ept/evaluation/QuantitativeCalculationsTest.java
git commit -m "feat: port QuantitativeCalculations for peer-group VL/quantitative statistics"
```

---

### Task 4: EID evaluator

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/SchemeEvaluator.java` (interface, first evaluator to implement it)
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/EidEvaluator.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/EidEvaluatorGoldenOutputIT.java`

**Interfaces:**
- Produces: `interface SchemeEvaluator { EvaluationOutcome evaluate(ShipmentParticipantMap map, List<ParticipantResult> results); }` — Tasks 5–9 implement the same interface.
- Consumes: `NonResponderExclusion.shouldExclude` (Task 2), `EvaluationOutcome`/`FailureReasonEntry` (Task 2), `GoldenOutputFixtureLoader` (Task 1).

Port of `application/models/Eid.php:evaluate()` (473 lines total, `evaluate()` spans lines 13-195, read in full):

```php
public function evaluate($shipmentResult, $shipmentId)
{
    $passingScore = Pt_Commons_SchemeConfig::get('eid.passPercentage');
    if ($passingScore === null || !is_numeric($passingScore) || (int)$passingScore < 1 || (int)$passingScore > 100) {
        $passingScore = 100;
    }
    foreach ($shipmentResult as $shipment) {
        // exclusion: non-responder (shared) + late-response (own inline check)
        if (Application_Service_Evaluation::excludeNonResponder($db, $shipment, $results)) {
            $shipment['is_excluded'] = 'yes';
        }
        if ($createdOn > $lastDate) {  // late
            $shipment['is_excluded'] = 'yes';
        }

        $totalScore = 0; $maxScore = 0;
        foreach ($results as $result) {
            if (isset($result['reported_result'])) {
                if ($result['reference_result'] == $result['reported_result']) {
                    if (0 == $result['control']) $totalScore += $result['sample_score'];
                } else {
                    if ($result['sample_score'] > 0) $failureReason[] = ['warning' => "...reported wrongly", 'correctiveAction' => $caReportedWrongly];
                }
            }
            if (0 == $result['control']) $maxScore += $result['sample_score'];
        }
        if ($maxScore > 0 && $totalScore > 0) {
            $totalScore = round(($totalScore / $maxScore) * 100, 2);
        }

        if ($shipment['is_excluded'] == 'yes' || $shipment['is_pt_test_not_performed'] == 'yes') {
            $finalResult = 3; // EXCLUDED, score = 0
        } else {
            $scoreResult = ($totalScore >= $passingScore) ? 'Pass' : 'Fail';
            $finalResult = ($scoreResult == 'Fail') ? 2 : 1;
        }
    }
}
```

Notes for the port:
- `passingScore` is read from `SchemeConfiguration` for the `eid` scheme (`passingScore` column, already exists), clamped to `[1,100]`, defaulting to `100` if unset/invalid — reproduce the exact clamp, not just a null check.
- EID has **no documentation score component** — `documentationScore` is always `0` for this scheme, unlike Recency/DTS.
- `control` samples (`ShipmentSample.isControl`) never contribute to `totalScore`/`maxScore` even if reported correctly.
- Score rounds to 2 decimal places only when `maxScore > 0 && totalScore > 0` — a `totalScore` of exactly `0` with a positive `maxScore` stays `0`, not `0.00` rounded (functionally identical in Java `double`, but keep the same rounding call site for auditability).

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;

@SpringBootTest
class EidEvaluatorGoldenOutputIT {

    @Autowired
    private EidEvaluator evaluator;

    @Autowired
    private GoldenOutputFixtureLoader fixtureLoader;

    @Autowired
    private ShipmentParticipantMapRepository mapRepository;

    @Autowired
    private ParticipantResultRepository resultRepository;

    @Test
    void reproducesLegacyScoreForEveryEidGoldenCase() {
        List<GoldenOutputFixture> fixtures = fixtureLoader.loadForScheme("eid");
        assertThat(fixtures).isNotEmpty();

        for (GoldenOutputFixture fixture : fixtures) {
            ShipmentParticipantMap map = mapRepository.findById(fixture.shipmentParticipantMapId()).orElseThrow();
            List<ParticipantResult> results = resultRepository.findByShipmentParticipantMap_Id(fixture.shipmentParticipantMapId());

            EvaluationOutcome outcome = evaluator.evaluate(map, results);

            assertThat(outcome.finalResult())
                .as("shipmentParticipantMapId=%d", fixture.shipmentParticipantMapId())
                .isEqualTo(fixture.legacyFinalResult());
            assertThat(outcome.shipmentScore())
                .as("shipmentParticipantMapId=%d", fixture.shipmentParticipantMapId())
                .isCloseTo(fixture.legacyShipmentScore(), within(0.01));
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=EidEvaluatorGoldenOutputIT`
Expected: FAIL — `EidEvaluator` does not exist. Check `ParticipantResultRepository` has (or add) `findByShipmentParticipantMap_Id(Long)`.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.SchemeConfiguration;
import zw.org.nmrl.ept.domain.enumeration.FinalResult;
import zw.org.nmrl.ept.repository.SchemeConfigurationRepository;

@Component
public class EidEvaluator implements SchemeEvaluator {

    private final SchemeConfigurationRepository schemeConfigurationRepository;

    public EidEvaluator(SchemeConfigurationRepository schemeConfigurationRepository) {
        this.schemeConfigurationRepository = schemeConfigurationRepository;
    }

    @Override
    public EvaluationOutcome evaluate(ShipmentParticipantMap map, List<ParticipantResult> results) {
        boolean excluded = NonResponderExclusion.shouldExclude(map, results) || isLate(map);

        if (excluded || Boolean.TRUE.equals(map.getIsPtTestNotPerformed())) {
            return new EvaluationOutcome(0.0, 0.0, FinalResult.EXCLUDED, List.of(new FailureReasonEntry("Excluded from Evaluation", null)), true, false);
        }

        double totalScore = 0.0;
        double maxScore = 0.0;
        List<FailureReasonEntry> failureReasons = new ArrayList<>();

        for (ParticipantResult result : results) {
            boolean isControl = Boolean.TRUE.equals(result.getSample().getIsControl());
            double sampleScore = result.getSample().getSampleScore() != null ? result.getSample().getSampleScore() : 0.0;

            if (result.getReportedQualitativeResult() != null) {
                boolean matches = result.getReportedQualitativeResult().equals(referenceResultFor(result));
                if (matches) {
                    if (!isControl) totalScore += sampleScore;
                } else if (sampleScore > 0) {
                    failureReasons.add(
                        new FailureReasonEntry(
                            "Control/Sample <strong>" + result.getSample().getLabel() + "</strong> was reported wrongly",
                            "Review your testing procedure and SOPs for this sample, verify instrument performance and calibration, and repeat testing where appropriate."
                        )
                    );
                }
            }
            if (!isControl) maxScore += sampleScore;
        }

        if (maxScore > 0 && totalScore > 0) {
            totalScore = Math.round((totalScore / maxScore) * 100 * 100.0) / 100.0;
        }

        double passingScore = eidPassingScore();
        boolean pass = totalScore >= passingScore;
        if (!pass) {
            failureReasons.add(
                new FailureReasonEntry(
                    "Participant did not meet the score criteria (Participant Score - <strong>" + totalScore + "</strong> and Required Score - <strong>" + passingScore + "</strong>)",
                    "Review your testing and reporting procedures, refer to your SOPs, and implement corrective measures to improve performance."
                )
            );
        }

        return new EvaluationOutcome(totalScore, 0.0, pass ? FinalResult.PASS : FinalResult.FAIL, failureReasons, false, false);
    }

    private double eidPassingScore() {
        SchemeConfiguration config = schemeConfigurationRepository.findActiveBySchemeCode("eid").orElse(null);
        Double passingScore = config != null ? config.getPassingScore() : null;
        if (passingScore == null || passingScore < 1 || passingScore > 100) {
            return 100.0;
        }
        return passingScore;
    }

    private String referenceResultFor(ParticipantResult result) {
        // TODO(interface risk — resolve before implementing): confirm how SampleReferenceResult
        // is looked up per ParticipantResult (likely via ParticipantResult.assay + result.getSample()
        // joining SampleReferenceResult). Check Phase 4's plan/implementation for the established
        // pattern before writing this — do not invent a second lookup path.
        throw new UnsupportedOperationException("wire up SampleReferenceResult lookup per Phase 4's established pattern");
    }

    private boolean isLate(ShipmentParticipantMap map) {
        // Reuses the same late-response check as NonResponderExclusion.shouldExclude's internal
        // logic but EID's legacy code runs it unconditionally (not gated on response_switch) —
        // confirm this against Eid.php lines 48/63-77 before assuming they're identical.
        throw new UnsupportedOperationException("port Eid.php's late-response check, see lines 48/63-77");
    }
}
```

`referenceResultFor` and `isLate` are intentionally left as explicit `UnsupportedOperationException` stubs with a named blocker, not silently guessed — per this plan's "No Placeholders" rule this is acceptable only because each stub names the exact resolution path (a cross-phase interface question for Phase 4, and a one-line reread of `Eid.php`). **Before running Step 4, resolve both**: re-read `Eid.php:48` and `:63-77` for the exact late-check (it is simpler than `NonResponderExclusion`'s — no `response_switch` override), and confirm the reference-result lookup shape against whatever Phase 4's plan established for `ParticipantResult`/`SampleReferenceResult` joins.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=EidEvaluatorGoldenOutputIT`
Expected: PASS once the two stubs above are resolved with real code (not left as exceptions).

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/SchemeEvaluator.java src/main/java/zw/org/nmrl/ept/evaluation/EidEvaluator.java src/test/java/zw/org/nmrl/ept/evaluation/EidEvaluatorGoldenOutputIT.java
git commit -m "feat: implement EID evaluator, verified against real migrated golden-output scores"
```

---

### Task 5: Generic scoring engine (`HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT`)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/GenericConfigDrivenEvaluator.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/GenericConfigDrivenEvaluatorGoldenOutputIT.java`

**Interfaces:**
- Consumes: `SchemeEvaluator` (Task 4), `QuantitativeCalculations` (Task 3, for the quantitative branch), `NonResponderExclusion`/`EvaluationOutcome` (Task 2).
- Produces: registered for all 4 config-driven scheme codes in Task 10's dispatcher.

Port of `application/models/CustomTest.php:evaluate()` (read in full, lines 82-321). Two branches selected by `SchemeConfiguration`'s equivalent of legacy `user_test_config.testType`:

**Qualitative branch** (the common case — matches reported vs. reference result exactly):
```php
foreach ($results as $result) {
    $calculatedScore = 0;
    if (isset($result['reference_result']) && isset($result['reported_result'])) {
        if ($result['reference_result'] == $result['reported_result']) {
            if (0 == $result['control']) { $totalScore += $result['sample_score']; $calculatedScore = $result['sample_score']; }
        } else {
            if ($result['sample_score'] > 0) $failureReason[] = ['warning' => '...reported wrongly', 'correctiveAction' => '...'];
        }
    }
    if (0 == $result['control']) $maxScore += $result['sample_score'];
}
```

**Quantitative branch** (z-score based, identical thresholds to VL's `iso17043` method — Task 7):
```php
foreach ($results as $result) {
    if ($result['control'] == 1) continue;
    if (in_array($result['is_result_invalid'], ['invalid', 'error'])) { $calcResult = 'fail'; }
    elseif ($result['reported_result'] !== null && $result['reported_result'] !== '') {
        $sd = $quantRange[...]['sd']; $median = $quantRange[...]['median'];
        $zScore = ($sd > 0) ? ($result['reported_result'] - $median) / $sd : 0;
        if ($sd == 0) {
            $calcResult = ($result['reported_result'] == 0) ? 'pass' : 'fail'; // pass only awards score if 0
        } else {
            $abs = abs($zScore);
            if ($abs <= 2) { $calcResult = 'pass'; $totalScore += $result['sample_score']; }
            elseif ($abs <= 3) { $calcResult = 'warn'; $totalScore += $result['sample_score']; } // still scores!
            else { $calcResult = 'fail'; }
        }
    }
    $maxScore += $result['sample_score'];
}
```

`passingScore` comes from `SchemeConfiguration` (legacy: `user_test_config.passingScore`, default `100` if unset/`<=0`) — same clamp pattern as EID (Task 4), reuse rather than reimplement.

Testkit-approval check (applies after scoring, both branches): if the reported test kit isn't in the scheme's recommended-testkit list, **zero the whole score** (`$totalScore = 0`) and add a failure reason — this is stricter than the per-sample "wrongly reported" warnings above, it discards the entire participant's score.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;

@SpringBootTest
class GenericConfigDrivenEvaluatorGoldenOutputIT {

    @Autowired
    private GenericConfigDrivenEvaluator evaluator;

    @Autowired
    private GoldenOutputFixtureLoader fixtureLoader;

    @Autowired
    private ShipmentParticipantMapRepository mapRepository;

    @Autowired
    private ParticipantResultRepository resultRepository;

    @ParameterizedTest
    @ValueSource(strings = { "HBV RDT", "HCV RDT", "mRDT", "SYPH RDT" })
    void reproducesLegacyScoreForEveryConfigDrivenScheme(String schemeCode) {
        List<GoldenOutputFixture> fixtures = fixtureLoader.loadForScheme(schemeCode);
        assertThat(fixtures).as("scheme=%s", schemeCode).isNotEmpty();

        for (GoldenOutputFixture fixture : fixtures) {
            ShipmentParticipantMap map = mapRepository.findById(fixture.shipmentParticipantMapId()).orElseThrow();
            List<ParticipantResult> results = resultRepository.findByShipmentParticipantMap_Id(fixture.shipmentParticipantMapId());

            EvaluationOutcome outcome = evaluator.evaluate(map, results);

            assertThat(outcome.finalResult()).as("scheme=%s id=%d", schemeCode, fixture.shipmentParticipantMapId()).isEqualTo(fixture.legacyFinalResult());
            assertThat(outcome.shipmentScore())
                .as("scheme=%s id=%d", schemeCode, fixture.shipmentParticipantMapId())
                .isCloseTo(fixture.legacyShipmentScore(), within(0.01));
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=GenericConfigDrivenEvaluatorGoldenOutputIT`
Expected: FAIL — `GenericConfigDrivenEvaluator` does not exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.SchemeConfiguration;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.enumeration.FinalResult;
import zw.org.nmrl.ept.repository.SchemeConfigurationRepository;

@Component
public class GenericConfigDrivenEvaluator implements SchemeEvaluator {

    private final SchemeConfigurationRepository schemeConfigurationRepository;

    public GenericConfigDrivenEvaluator(SchemeConfigurationRepository schemeConfigurationRepository) {
        this.schemeConfigurationRepository = schemeConfigurationRepository;
    }

    @Override
    public EvaluationOutcome evaluate(ShipmentParticipantMap map, List<ParticipantResult> results) {
        String schemeCode = map.getShipment().getScheme().getCode();
        SchemeConfiguration config = schemeConfigurationRepository.findActiveBySchemeCode(schemeCode).orElseThrow();
        boolean quantitative = "quantitative".equalsIgnoreCase(testTypeOf(config));

        if (NonResponderExclusion.shouldExclude(map, results) || Boolean.TRUE.equals(map.getIsPtTestNotPerformed())) {
            return new EvaluationOutcome(0.0, 0.0, FinalResult.EXCLUDED, List.of(new FailureReasonEntry("Excluded from Evaluation", null)), true, false);
        }

        double totalScore = 0.0;
        double maxScore = 0.0;
        List<FailureReasonEntry> failureReasons = new ArrayList<>();

        if (quantitative) {
            for (ParticipantResult result : results) {
                if (Boolean.TRUE.equals(result.getSample().getIsControl())) continue;
                double sampleScore = result.getSample().getSampleScore() != null ? result.getSample().getSampleScore() : 0.0;
                if (result.getReportedQuantitativeValue() != null) {
                    ZScoreVerdict verdict = QuantitativeScoring.evaluate(result, sampleScore);
                    if (verdict.awardsScore()) totalScore += sampleScore;
                    if (verdict.isFailure() && sampleScore > 0) {
                        failureReasons.add(new FailureReasonEntry("Sample <strong>" + result.getSample().getLabel() + "</strong> was reported wrongly", null));
                    }
                }
                maxScore += sampleScore;
            }
        } else {
            for (ParticipantResult result : results) {
                double sampleScore = result.getSample().getSampleScore() != null ? result.getSample().getSampleScore() : 0.0;
                boolean isControl = Boolean.TRUE.equals(result.getSample().getIsControl());
                if (result.getReportedQualitativeResult() != null) {
                    // TODO: same SampleReferenceResult lookup risk flagged in Task 4 — resolve there first.
                    boolean matches = matchesReference(result);
                    if (matches) {
                        if (!isControl) totalScore += sampleScore;
                    } else if (sampleScore > 0) {
                        failureReasons.add(
                            new FailureReasonEntry(
                                "Sample <strong>" + result.getSample().getLabel() + "</strong> was reported wrongly",
                                "Review and refer to the " + map.getShipment().getScheme().getName() + " testing algorithm for result interpretation as the final result interpretation does not match the expected result."
                            )
                        );
                    }
                }
                if (!isControl) maxScore += sampleScore;
            }
        }

        if (maxScore > 100) maxScore = 100;
        if (maxScore > 0 && totalScore > 0) {
            totalScore = (totalScore / maxScore) * 100;
        }

        double passingScore = passingScoreOf(config);
        boolean pass = totalScore >= passingScore;
        if (!pass) {
            failureReasons.add(
                new FailureReasonEntry(
                    "Participant did not meet the score criteria (Participant Score is <strong>" + Math.round(totalScore) + "</strong> and Required Score is <strong>" + Math.round(passingScore) + "</strong>)",
                    "Review all testing procedures prior to performing client testing and contact your supervisor for improvement"
                )
            );
        }

        return new EvaluationOutcome(totalScore, 0.0, pass ? FinalResult.PASS : FinalResult.FAIL, failureReasons, false, false);
    }

    private double passingScoreOf(SchemeConfiguration config) {
        Double passingScore = config.getPassingScore();
        return (passingScore != null && passingScore > 0) ? passingScore : 100.0;
    }

    private String testTypeOf(SchemeConfiguration config) {
        // TODO: confirm exact storage — legacy reads scheme_list.user_test_config JSON's
        // "testType" key. Check whether Phase 1's SchemeConfiguration design stores this in
        // `optionalFields` or `scoringRules` (both raw-string columns per the entity) before
        // guessing a JSON path here.
        throw new UnsupportedOperationException("resolve testType storage against Phase 1's SchemeConfiguration design");
    }

    private boolean matchesReference(ParticipantResult result) {
        throw new UnsupportedOperationException("wire up SampleReferenceResult lookup per Phase 4's established pattern, see Task 4");
    }
}
```

`QuantitativeScoring`/`ZScoreVerdict` is a small shared helper — build it in **Task 7** (VL) since VL's `iso17043` method has the identical threshold logic (`|z|<=2` pass, `2<|z|<=3` warn-but-still-scores, `|z|>3` fail) and this task should reuse it rather than duplicating. Do Task 7 before finishing this task's z-score branch, or stub it with the same `UnsupportedOperationException` pattern and return to it.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=GenericConfigDrivenEvaluatorGoldenOutputIT`
Expected: PASS once `testTypeOf`, `matchesReference`, and the `QuantitativeScoring` dependency (Task 7) are resolved.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/GenericConfigDrivenEvaluator.java src/test/java/zw/org/nmrl/ept/evaluation/GenericConfigDrivenEvaluatorGoldenOutputIT.java
git commit -m "feat: implement generic config-driven evaluator for HBV RDT/HCV RDT/mRDT/SYPH RDT"
```

---

### Task 6: Recency evaluator

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/RecencyEvaluator.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/RecencyEvaluatorGoldenOutputIT.java`

Port of `application/models/Recency.php:evaluate()` + `getDocumentationScore()` (lines 20-352, read in full). This is the first scheme in scope with a real documentation-score component — build it as a reusable pattern, later schemes with documentation scoring can follow the same shape.

RTRI algorithm correctness check (per sample, always active for Recency — unlike DTS where it's opt-in):
```php
$isAlgoWrong = false;
if (empty($controlLine) && empty($verificationLine) && empty($longtermLine)) $isAlgoWrong = true;
elseif (empty($controlLine) || $controlLine == 'absent') $isAlgoWrong = true;

if ($result['reference_result'] == $possibleResults['N']) {
    if (!($controlLine == 'present' && $verificationLine == 'absent' && $longtermLine == 'absent')) $isAlgoWrong = true;
}
if ($result['reference_result'] == $possibleResults['R']) {
    if (!($controlLine == 'present' && $verificationLine == 'present' && $longtermLine == 'absent')) $isAlgoWrong = true;
}
if ($result['reference_result'] == $possibleResults['LT']) {
    if (!($controlLine == 'present' && $verificationLine == 'present' && $longtermLine == 'present')) $isAlgoWrong = true;
}
```
(Note: this is functionally the same rule as `Dts.php`'s `algoRTRI()` in Task 9 — both check control/verification/longterm-line presence against N/R/LT reference codes. Extract a single shared `RtriLineInterpretation` helper both Task 6 and Task 9 call, rather than duplicating the three if-blocks.)

Documentation score (5 items for dried samples, 3 for non-dried — `sampleType` on `shipment.shipmentAttributes`):
```php
$documentationScorePerItem = ($documentationPercentage / 5); // dried
// or / 3 for non-dried (skips D.3 rehydration-date and D.7 rehydration-window checks)

// D.1 receipt date provided
// D.3 (dried only) rehydration date provided
// D.5 test date provided
// D.7 (dried only) tested within [sampleRehydrateDays, sampleRehydrateDays+1] days of rehydration
// D.8 supervisor approval recorded AND participant_supervisor non-empty
```

Response score formula (differs from EID — bakes documentation weight in *before* summing, not after):
```php
$responseScore = round(($totalScore / $maxScore) * 100 * (100 - $configuredDocScore) / 100, 2);
$grandTotal = $responseScore + $documentationScore;
$pass = $grandTotal >= $recencyPassPercentage;
```

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;

@SpringBootTest
class RecencyEvaluatorGoldenOutputIT {

    @Autowired
    private RecencyEvaluator evaluator;

    @Autowired
    private GoldenOutputFixtureLoader fixtureLoader;

    @Autowired
    private ShipmentParticipantMapRepository mapRepository;

    @Autowired
    private ParticipantResultRepository resultRepository;

    @Test
    void reproducesLegacyScoreIncludingDocumentationComponent() {
        List<GoldenOutputFixture> fixtures = fixtureLoader.loadForScheme("recency");
        assertThat(fixtures).isNotEmpty();

        for (GoldenOutputFixture fixture : fixtures) {
            ShipmentParticipantMap map = mapRepository.findById(fixture.shipmentParticipantMapId()).orElseThrow();
            List<ParticipantResult> results = resultRepository.findByShipmentParticipantMap_Id(fixture.shipmentParticipantMapId());

            EvaluationOutcome outcome = evaluator.evaluate(map, results);

            assertThat(outcome.finalResult()).as("id=%d", fixture.shipmentParticipantMapId()).isEqualTo(fixture.legacyFinalResult());
            assertThat(outcome.shipmentScore()).as("id=%d", fixture.shipmentParticipantMapId()).isCloseTo(fixture.legacyShipmentScore(), within(0.01));
            assertThat(outcome.documentationScore())
                .as("id=%d", fixture.shipmentParticipantMapId())
                .isCloseTo(fixture.legacyDocumentationScore(), within(0.01));
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=RecencyEvaluatorGoldenOutputIT`
Expected: FAIL — `RecencyEvaluator` does not exist.

- [ ] **Step 3: Write minimal implementation**

Implement `RecencyEvaluator implements SchemeEvaluator`, translating the PHP above directly:
- A private `RtriLineInterpretation.isAlgorithmCorrect(controlLine, verificationLine, longtermLine, referenceCode, possibleResults)` method shared with Task 9's `algoRTRI` port (put it in a new `src/main/java/zw/org/nmrl/ept/evaluation/RtriLineInterpretation.java` if Task 9 hasn't created it yet).
- A private `documentationScore(ShipmentParticipantMap, List<ParticipantResult>)` method implementing the D.1/D.3/D.5/D.7/D.8 checks above, reading `sampleType` off `Shipment`'s attributes (confirm the exact field/JSON-vs-column representation Phase 3 chose for `shipment_attributes` before writing this — it was a `longtext`/JSON column in legacy).
- `SchemeConfiguration.documentationWeight` maps to legacy's `recency.documentationScore` config key (default `10` if unset) — confirm this is the intended column via Phase 1's plan before wiring it up; don't silently assume.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=RecencyEvaluatorGoldenOutputIT`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/RecencyEvaluator.java src/main/java/zw/org/nmrl/ept/evaluation/RtriLineInterpretation.java src/test/java/zw/org/nmrl/ept/evaluation/RecencyEvaluatorGoldenOutputIT.java
git commit -m "feat: implement Recency evaluator including RTRI algorithm and documentation scoring"
```

---

### Task 7: VL evaluator + shared z-score scoring helper

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/QuantitativeScoring.java`
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/VlEvaluator.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/VlEvaluatorGoldenOutputIT.java`

**Interfaces:**
- Produces: `record ZScoreVerdict(String result, boolean awardsScore, boolean isFailure)` and `QuantitativeScoring.evaluateIso17043(double reportedValue, double median, double sd, double sampleScore)` — Task 5's quantitative branch depends on this (resolve that task's `UnsupportedOperationException` stub now).

Port of `application/models/Vl.php:evaluate()` (lines 16-305, read in full). Two evaluation methods selected by `shipment.shipmentAttributes.methodOfEvaluation` (`'standard'` default, or `'iso17043'`):

**`standard`**: reported value must fall within `[low, high]` of the peer range for that assay/sample — simple range check, no z-score computed.

**`iso17043`** (the z-score method — this is `QuantitativeScoring`'s job):
```php
$zScore = ($sd > 0) ? ($reportedValue - $median) / $sd : 0;
if ($sd == 0) {
    $calcResult = ($reportedValue == 0) ? 'pass' : 'fail'; // awards score only if exactly 0
} else {
    $abs = abs($zScore);
    if ($abs <= 2) $calcResult = 'pass';        // awards score
    elseif ($abs <= 3) $calcResult = 'warn';     // STILL awards score, just flagged
    else $calcResult = 'fail';                   // no score
}
```

VL-specific final-result codes — **note the 4th code not covered by the master plan's `1/2/3` summary, but already present in the Java `FinalResult` enum (`NOT_EVALUATED`)**:
```php
if ($totalScore == 'N.A.') { $finalResult = 4; } // "Not Evaluated" — no peer range/insufficient data
elseif ($totalScore != $maxScore) { $finalResult = 2; } // Fail
else { $finalResult = 1; } // Pass
```
Map PHP's `4` → `FinalResult.NOT_EVALUATED`. `is_excluded` handling is separate (→ `FinalResult.EXCLUDED`, code `3`), checked before the above.

`quantRange` (peer median/SD per assay+sample) is precomputed by legacy's `setVlRange()`/`getVlRange()` — this plan does not port those; confirm with Phase 3/4's plans whether peer-range computation already has a home (it's a shipment-level aggregate over all participants' `ParticipantResult.reportedQuantitativeValue`, conceptually similar to what `QuantitativeCalculations.mean/median/standardDeviation` (Task 3) already computes) or needs a new task added here.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QuantitativeScoringTest {

    @Test
    void withinTwoStandardDeviationsPasses() {
        ZScoreVerdict verdict = QuantitativeScoring.evaluateIso17043(102.0, 100.0, 5.0, 10.0);
        assertThat(verdict.awardsScore()).isTrue();
        assertThat(verdict.isFailure()).isFalse();
    }

    @Test
    void betweenTwoAndThreeStandardDeviationsWarnsButStillAwardsScore() {
        ZScoreVerdict verdict = QuantitativeScoring.evaluateIso17043(112.5, 100.0, 5.0, 10.0);
        assertThat(verdict.result()).isEqualTo("warn");
        assertThat(verdict.awardsScore()).isTrue();
    }

    @Test
    void beyondThreeStandardDeviationsFails() {
        ZScoreVerdict verdict = QuantitativeScoring.evaluateIso17043(120.0, 100.0, 5.0, 10.0);
        assertThat(verdict.awardsScore()).isFalse();
        assertThat(verdict.isFailure()).isTrue();
    }

    @Test
    void zeroStandardDeviationPassesOnlyForExactlyZeroValue() {
        assertThat(QuantitativeScoring.evaluateIso17043(0.0, 0.0, 0.0, 10.0).awardsScore()).isTrue();
        assertThat(QuantitativeScoring.evaluateIso17043(1.0, 0.0, 0.0, 10.0).awardsScore()).isFalse();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=QuantitativeScoringTest`
Expected: FAIL — `QuantitativeScoring`/`ZScoreVerdict` do not exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

public final class QuantitativeScoring {

    private QuantitativeScoring() {}

    public static ZScoreVerdict evaluateIso17043(double reportedValue, double median, double sd, double sampleScore) {
        if (sd == 0.0) {
            boolean pass = reportedValue == 0.0;
            return new ZScoreVerdict(pass ? "pass" : "fail", pass, !pass);
        }
        double zScore = (reportedValue - median) / sd;
        double abs = Math.abs(zScore);
        if (abs <= 2) return new ZScoreVerdict("pass", true, false);
        if (abs <= 3) return new ZScoreVerdict("warn", true, false);
        return new ZScoreVerdict("fail", false, true);
    }
}
```

```java
package zw.org.nmrl.ept.evaluation;

public record ZScoreVerdict(String result, boolean awardsScore, boolean isFailure) {}
```

Then implement `VlEvaluator` translating `Vl.php:evaluate()`'s `standard`/`iso17043` branch selection, the `N.A.`/fail/pass → `NOT_EVALUATED`/`FAIL`/`PASS` mapping, and wire Task 5's `GenericConfigDrivenEvaluator.matchesReference`/quantitative branch to call `QuantitativeScoring.evaluateIso17043` instead of its `UnsupportedOperationException` stub.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=QuantitativeScoringTest && ./mvnw -Pprod test -Dtest=VlEvaluatorGoldenOutputIT`
Expected: PASS for both, and re-run `GenericConfigDrivenEvaluatorGoldenOutputIT` (Task 5) to confirm the previously-stubbed quantitative branch now passes too.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/QuantitativeScoring.java src/main/java/zw/org/nmrl/ept/evaluation/ZScoreVerdict.java src/main/java/zw/org/nmrl/ept/evaluation/VlEvaluator.java src/test/java/zw/org/nmrl/ept/evaluation/QuantitativeScoringTest.java src/test/java/zw/org/nmrl/ept/evaluation/VlEvaluatorGoldenOutputIT.java
git commit -m "feat: implement VL evaluator and shared ISO 17043 z-score scoring helper"
```

---

### Task 8: TB evaluator

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/TbEvaluator.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/TbEvaluatorGoldenOutputIT.java`

Port of `application/models/Tb.php:evaluate()` (lines 24-393, read in full). TB branches on assay type (`assay_name` attribute → `'microscopy'` vs. everything else treated as Xpert MTB/RIF), each with its own partial-credit table — this is the most intricate scoring logic of the 9 in-scope schemes after DTS.

**Microscopy branch:**
```php
if ($reference == 'negative') {
    $calculatedScore = ($reported == 'negative') ? $sampleScore : 0;
} else {
    // graded positives: scanty < 1+ < 2+ < 3+, adjacency awards full credit
    $positiveResults = ['scanty', '1+', '2+', '3+'];
    if (in_array($reported, $positiveResults)) {
        $awardedScore = 0.5; // baseline for reporting *any* positive grade
        if (!in_array($reference, $positiveResults)) { $calculatedScore = 0; }
        elseif ($reported == $reference) { $awardedScore = 1; }
        elseif ($reference == 'scanty' && in_array($reported, ['scanty','1+'])) { $awardedScore = 1; }
        elseif ($reference == '1+' && in_array($reported, ['scanty','1+','2+'])) { $awardedScore = 1; }
        elseif ($reference == '2+' && in_array($reported, ['1+','2+','3+'])) { $awardedScore = 1; }
        elseif ($reference == '3+' && in_array($reported, ['2+','3+'])) { $awardedScore = 1; }
        $calculatedScore = $awardedScore * $sampleScore;
    } else {
        $calculatedScore = 0;
    }
}
```

**Xpert MTB/RIF branch** — two sub-cases depending on whether `drug_resistance_test == 'yes'`:
```php
// without RIF: simple detected/not-detected match
if ($mtbDetected == $referenceMtbDetected) { $calculatedScore = $sampleScore; } else { $calculatedScore = 0; }

// with RIF (both mtb_detected AND rif_resistance considered):
$mtbMatches = $mtbDetected == $referenceMtbDetected;
$rifMatches = $rifResistance == $referenceRifResistance;
if (in_array($mtbDetected, ['invalid','error','no-result'])) { $calculatedScore = $sampleScore * 0.25; }
elseif ($mtbMatches && !$rifMatches) {
    if ($mtbDetected == 'detected' && (in_array($rifResistance, ['indeterminate']) || in_array($referenceRifResistance, ['indeterminate','na']))) {
        $calculatedScore = $sampleScore * 0.5;
    } elseif ($mtbDetected == 'not-detected' && $rifResistance == 'na') {
        $calculatedScore = $sampleScore; // full credit
    }
} elseif ($mtbMatches && $rifMatches) { $calculatedScore = $sampleScore; }
```
`normalizeMTBDetection()` collapses `very-low`/`low`/`medium`/`high`/`trace` → `detected` before comparison — port this exact normalization, it changes which branch above applies.

Passing-score comparison and `FinalResult` mapping is the same shape as EID/Recency (`totalScore >= passingScore ? PASS : FAIL`, `SchemeConfiguration.passingScore` for `tb`, default `100`).

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;

@SpringBootTest
class TbEvaluatorGoldenOutputIT {

    @Autowired
    private TbEvaluator evaluator;

    @Autowired
    private GoldenOutputFixtureLoader fixtureLoader;

    @Autowired
    private ShipmentParticipantMapRepository mapRepository;

    @Autowired
    private ParticipantResultRepository resultRepository;

    @Test
    void reproducesLegacyScoreAcrossMicroscopyAndXpertSamples() {
        List<GoldenOutputFixture> fixtures = fixtureLoader.loadForScheme("tb");
        assertThat(fixtures).isNotEmpty();

        for (GoldenOutputFixture fixture : fixtures) {
            ShipmentParticipantMap map = mapRepository.findById(fixture.shipmentParticipantMapId()).orElseThrow();
            List<ParticipantResult> results = resultRepository.findByShipmentParticipantMap_Id(fixture.shipmentParticipantMapId());

            EvaluationOutcome outcome = evaluator.evaluate(map, results);

            assertThat(outcome.finalResult()).as("id=%d", fixture.shipmentParticipantMapId()).isEqualTo(fixture.legacyFinalResult());
            assertThat(outcome.shipmentScore()).as("id=%d", fixture.shipmentParticipantMapId()).isCloseTo(fixture.legacyShipmentScore(), within(0.01));
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=TbEvaluatorGoldenOutputIT`
Expected: FAIL — `TbEvaluator` does not exist.

- [ ] **Step 3: Write minimal implementation**

Implement `TbEvaluator implements SchemeEvaluator`, translating the microscopy/Xpert branches above directly, keyed off an `assayName`/`drugResistanceTest` attribute — confirm exactly where Phase 4 stored these per-result attributes on `ParticipantResult` (legacy reads them from a JSON `attributes` blob on the response row; check whether Phase 4 normalized these into typed columns or kept a similar JSON blob) before wiring up the branch condition.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=TbEvaluatorGoldenOutputIT`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/TbEvaluator.java src/test/java/zw/org/nmrl/ept/evaluation/TbEvaluatorGoldenOutputIT.java
git commit -m "feat: implement TB evaluator covering microscopy and Xpert MTB/RIF scoring"
```

---

### Task 9: DTS evaluator (`algoUpdatedThreeTests` + `algoRTRI` only)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/DtsAlgorithm.java` (interface — the pluggable-dispatcher shape, only one real implementation registered)
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/UpdatedThreeTestsAlgorithm.java`
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/DtsEvaluator.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/UpdatedThreeTestsAlgorithmTest.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/DtsEvaluatorGoldenOutputIT.java`

This is the highest-risk task in the plan. Do it last, after Tasks 4–8 have established the pattern.

**`algoUpdatedThreeTests` — full logic, read in full from `Dts.php:2761-2800`, reproduced verbatim below (normalize `X`/`N/A`/empty → `-` first via `normalizeAlgoResult`):**
```php
if ($result1 == 'NR' && $reportedResultCode == 'N') {
    if ($result2 == '-' && $result3 == '-' && $repeatResult1 == '-') { $algoResult = 'Pass'; }
    else { /* fail: warningForAlgo */ }
} elseif ($result1 == 'R') {
    if ($result2 == 'R' && $reportedResultCode == 'P' && $repeatResult1 == '-') { $algoResult = 'Pass'; }
    elseif ($result2 == 'NR') {
        if ($repeatResult1 == 'NR' && $reportedResultCode == 'N') { $algoResult = 'Pass'; }
        elseif ($repeatResult1 == 'R' && $reportedResultCode == 'I') { $algoResult = 'Pass'; }
        else { /* fail */ }
    } else { /* fail */ }
} else { /* fail */ }
```

**`algoRTRI` — full logic, read in full from `Dts.php:2983-3012`:**
```php
$r = 'Pass';
if ((empty($control) && empty($verify) && empty($longterm)) || $control === 'absent') $r = 'Fail';
if ($refRes === $possibleResults['N']) { if (!($control=='present' && $verify=='absent' && $longterm=='absent')) $r = 'Fail'; }
elseif ($refRes === $possibleResults['R']) { if (!($control=='present' && $verify=='present' && $longterm=='absent')) $r = 'Fail'; }
elseif ($refRes === $possibleResults['LT']) { if (!($control=='present' && $verify=='present' && $longterm=='present')) $r = 'Fail'; }
```
This is the **same rule** as Task 6's Recency algorithm check — implement `RtriLineInterpretation` once (in Task 6 if it runs first, or here if this task runs first) and have both call it. Do not duplicate the three if/elseif branches a second time.

RTRI only runs when `shipmentAttributes.enableRtri == 'yes'` **and** the specific sample's `dts_rtri_is_editable == 'yes'` — per the correction at the top of this document, confirm real production usage of `enableRtri` before assuming golden-output coverage of the "with RTRI" path is possible from real data.

**Categorical gates, all confirmed by direct read of `Dts.php:evaluateSingleShipment()` (lines 152-460+), each independently produces a failure reason + corrective-action code (join against the migrated `r_dts_corrective_actions` table, action IDs cited below):**

| Gate | Legacy condition | Corrective action ID | Effect |
|---|---|---|---|
| Last-date | `shipmentTestReportDate > shipmentCutoff(response_deadline)`, unless `shipment.response_switch == 'on'` | 1 | `is_excluded = true`, `is_response_late = true` |
| Testkit expired | `testedOn > expiryDate` for any of the 3 test-kit slots | 5 | failure reason added, not auto-excluded |
| Testkit expiry missing | expiry date not reported for a used kit | 6 | `is_excluded = true` |
| No testkit reported | all 3 kit-name fields empty | 7 | `is_excluded = true` |
| Testkit repeated (all 3 same) | 3 kit names identical, and not Myanmar/Vietnam scheme type | 8 | failure reason only |
| Testkit repeated (pairwise) | any 2 of 3 kit names identical, and not Myanmar/Vietnam | 9 | failure reason only |
| Testkit lot missing | lot number blank but a result was reported for that slot | 10 | `is_excluded = true` |
| Non-recommended testkit | kit not in `recommendedTestkits[slot]`, and not Vietnam | 17 | failure reason only |
| Sample not mandatory | `result.mandatory == 0` | — | sample skipped from scoring entirely (`calculated_score = 'N.A.'`), not a fail |

Score composition (per-sample, then summed):
```php
$scorePercentageForAlgorithm = $config['dtsAlgorithmScore'] ?? 0; // fraction of sample_score allotted to "got the algorithm right"
$scoreForAlgorithm = $scorePercentageForAlgorithm * $sample_score;
$scoreForSample = $sample_score - $scoreForAlgorithm;
// awarded only if serology result matches reference AND algorithm passed AND (RTRI passed if checked)
```
For Zimbabwe's real config (`dtsAlgorithmScore` not set in the sampled `scheme_config.dts` row — confirm it's genuinely absent/zero across all real rows, not just the one checked, before hardcoding `0`), `scorePercentageForAlgorithm` defaults to `0`, meaning the algorithm-correctness check is a pure pass/fail gate with no separate score allocation — the full `sample_score` goes to "serology correctness," none to "got the algorithm right." Confirm this against real data in Task 10 rather than assuming.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UpdatedThreeTestsAlgorithmTest {

    private final UpdatedThreeTestsAlgorithm algorithm = new UpdatedThreeTestsAlgorithm();

    @Test
    void allNonReactivePasses() {
        assertThat(algorithm.evaluate("NR", "-", "-", "-", "N").algoResult()).isEqualTo("Pass");
    }

    @Test
    void reactiveThenReactiveWithPositiveConclusionPasses() {
        assertThat(algorithm.evaluate("R", "R", "-", "-", "P").algoResult()).isEqualTo("Pass");
    }

    @Test
    void reactiveNonReactiveRepeatNonReactiveConcludesNegative() {
        assertThat(algorithm.evaluate("R", "NR", "-", "NR", "N").algoResult()).isEqualTo("Pass");
    }

    @Test
    void reactiveNonReactiveRepeatReactiveConcludesIndeterminate() {
        assertThat(algorithm.evaluate("R", "NR", "-", "R", "I").algoResult()).isEqualTo("Pass");
    }

    @Test
    void mismatchedConclusionFails() {
        assertThat(algorithm.evaluate("R", "NR", "-", "NR", "P").algoResult()).isEqualTo("Fail");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=UpdatedThreeTestsAlgorithmTest`
Expected: FAIL — `UpdatedThreeTestsAlgorithm` does not exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

public class UpdatedThreeTestsAlgorithm implements DtsAlgorithm {

    public record Verdict(String algoResult) {}

    public Verdict evaluate(String result1, String result2, String result3, String repeatResult1, String reportedResultCode) {
        String r1 = normalize(result1);
        String r2 = normalize(result2);
        String r3 = normalize(result3);
        String repeat1 = normalize(repeatResult1);

        if (r1.equals("NR") && "N".equals(reportedResultCode)) {
            if (r2.equals("-") && r3.equals("-") && repeat1.equals("-")) return new Verdict("Pass");
            return new Verdict("Fail");
        }
        if (r1.equals("R")) {
            if (r2.equals("R") && "P".equals(reportedResultCode) && repeat1.equals("-")) return new Verdict("Pass");
            if (r2.equals("NR")) {
                if (repeat1.equals("NR") && "N".equals(reportedResultCode)) return new Verdict("Pass");
                if (repeat1.equals("R") && "I".equals(reportedResultCode)) return new Verdict("Pass");
                return new Verdict("Fail");
            }
            return new Verdict("Fail");
        }
        return new Verdict("Fail");
    }

    private String normalize(String result) {
        if (result == null) return "-";
        String trimmedLower = result.trim().toLowerCase();
        return (trimmedLower.isEmpty() || trimmedLower.equals("x") || trimmedLower.equals("n/a")) ? "-" : result;
    }
}
```

Then implement `RtriLineInterpretation` (shared with Task 6, if not already created there) and `DtsEvaluator implements SchemeEvaluator`, wiring in: the 8 categorical gates from the table above (each as its own private method returning a `List<FailureReasonEntry>` plus an exclusion/fail flag — mirror the table's "effect" column exactly, don't collapse them into one combined check), `UpdatedThreeTestsAlgorithm` for the serology verdict, and `RtriLineInterpretation` gated on `enableRtri` + `dts_rtri_is_editable`.

`DtsAlgorithm` is a marker interface with no methods beyond what `UpdatedThreeTestsAlgorithm` defines — its only purpose is documenting where `algoVietnam`/`algoSerial`/etc. would plug in later; don't over-engineer a generic dispatch signature for algorithms that don't exist yet.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=UpdatedThreeTestsAlgorithmTest && ./mvnw -Pprod test -Dtest=DtsEvaluatorGoldenOutputIT`
Expected: PASS for both. The `DtsEvaluatorGoldenOutputIT` test follows the exact same shape as Task 4/6/7/8's golden-output tests (query fixtures for `dts`, load real results, compare) — write it before Step 3, following the pattern established in those tasks' Step 1.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/DtsAlgorithm.java src/main/java/zw/org/nmrl/ept/evaluation/UpdatedThreeTestsAlgorithm.java src/main/java/zw/org/nmrl/ept/evaluation/DtsEvaluator.java src/test/java/zw/org/nmrl/ept/evaluation/UpdatedThreeTestsAlgorithmTest.java src/test/java/zw/org/nmrl/ept/evaluation/DtsEvaluatorGoldenOutputIT.java
git commit -m "feat: implement DTS evaluator (Updated 3 Tests + RTRI), the only algorithm Zimbabwe runs"
```

---

### Task 10: CAPA integration — corrective-action lookup

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/CorrectiveActionLookup.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/CorrectiveActionLookupIT.java`

Port of `Dts.php:getDtsCorrectiveActions()`:
```php
public function getDtsCorrectiveActions()
{
    $res = $this->db->fetchAll($this->db->select()->from('r_dts_corrective_actions'));
    $response = [];
    foreach ($res as $row) { $response[$row['action_id']] = $row['corrective_action']; }
    return $response;
}
```
The legacy `r_dts_corrective_actions` reference table was already migrated into `legacy_record_archive` by the existing pipeline. Confirm whether it was also **typed-promoted** into `CorrectiveAction` (entity already exists) — if not, this task needs to add a promotion SQL script (`src/main/resources/migration/promote/`) following the pattern of the existing ones, keyed by `action_id` → `legacySourceId`, before `DtsEvaluator` (Task 9) can resolve action-ID-to-text at runtime.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CorrectiveActionLookupIT {

    @Autowired
    private CorrectiveActionLookup lookup;

    @Test
    void resolvesEveryActionIdReferencedByDtsGates() {
        // action IDs 1, 5, 6, 7, 8, 9, 10, 17 per the categorical-gate table in this plan's Task 9
        for (int actionId : new int[] { 1, 5, 6, 7, 8, 9, 10, 17 }) {
            assertThat(lookup.textFor(actionId)).as("action id %d", actionId).isNotBlank();
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=CorrectiveActionLookupIT`
Expected: FAIL — `CorrectiveActionLookup` does not exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import org.springframework.stereotype.Component;
import zw.org.nmrl.ept.repository.CorrectiveActionRepository;

@Component
public class CorrectiveActionLookup {

    private final CorrectiveActionRepository repository;

    public CorrectiveActionLookup(CorrectiveActionRepository repository) {
        this.repository = repository;
    }

    public String textFor(int legacyActionId) {
        return repository
            .findByLegacySourceId(String.valueOf(legacyActionId))
            .map(action -> action.getDescription())
            .orElseThrow(() -> new IllegalStateException("No CorrectiveAction promoted for legacy action_id=" + legacyActionId));
    }
}
```

Add `findByLegacySourceId(String)` to `CorrectiveActionRepository` if not already present — check the file first. If `CorrectiveAction` has no `legacySourceId` field (the earlier research pass found it has only `title`/`description`/`scheme`, no explicit legacy-identity column confirmed), either add one following the pattern every other promoted entity uses, or confirm a different stable key (e.g. `title`) is being used instead — don't assume.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=CorrectiveActionLookupIT`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/CorrectiveActionLookup.java src/test/java/zw/org/nmrl/ept/evaluation/CorrectiveActionLookupIT.java
git commit -m "feat: add corrective-action lookup feeding DTS gate failure reasons"
```

---

### Task 11: Scheme dispatcher + full golden-output verification pass

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/evaluation/EvaluationDispatcher.java`
- Test: `src/test/java/zw/org/nmrl/ept/evaluation/EvaluationDispatcherAllSchemesGoldenOutputIT.java`

**Interfaces:**
- Consumes: every `SchemeEvaluator` implementation from Tasks 4–9.
- Produces: `EvaluationDispatcher.evaluatorFor(String schemeCode)` — this is what Phase 3's shipment-evaluation trigger (or a later scheduling task) calls; not built in this plan, but this is the integration point it needs.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;

@SpringBootTest
class EvaluationDispatcherAllSchemesGoldenOutputIT {

    @Autowired
    private EvaluationDispatcher dispatcher;

    @Autowired
    private GoldenOutputFixtureLoader fixtureLoader;

    @Autowired
    private ShipmentParticipantMapRepository mapRepository;

    @Autowired
    private ParticipantResultRepository resultRepository;

    @ParameterizedTest
    @ValueSource(strings = { "dts", "eid", "HBV RDT", "HCV RDT", "mRDT", "recency", "SYPH RDT", "tb", "vl" })
    void everyActiveSchemeReproducesItsLegacyGoldenOutput(String schemeCode) {
        SchemeEvaluator evaluator = dispatcher.evaluatorFor(schemeCode);
        List<GoldenOutputFixture> fixtures = fixtureLoader.loadForScheme(schemeCode);

        assertThat(fixtures).as("scheme=%s must have at least one real migrated case", schemeCode).isNotEmpty();
        assertThat(fixtures).as("scheme=%s must have both a real Pass and a real Fail case", schemeCode)
            .anyMatch(f -> f.legacyFinalResult() == zw.org.nmrl.ept.domain.enumeration.FinalResult.PASS)
            .anyMatch(f -> f.legacyFinalResult() == zw.org.nmrl.ept.domain.enumeration.FinalResult.FAIL);

        for (GoldenOutputFixture fixture : fixtures) {
            ShipmentParticipantMap map = mapRepository.findById(fixture.shipmentParticipantMapId()).orElseThrow();
            List<ParticipantResult> results = resultRepository.findByShipmentParticipantMap_Id(fixture.shipmentParticipantMapId());

            EvaluationOutcome outcome = evaluator.evaluate(map, results);

            assertThat(outcome.finalResult()).as("scheme=%s id=%d", schemeCode, fixture.shipmentParticipantMapId()).isEqualTo(fixture.legacyFinalResult());
            assertThat(outcome.shipmentScore())
                .as("scheme=%s id=%d", schemeCode, fixture.shipmentParticipantMapId())
                .isCloseTo(fixture.legacyShipmentScore(), within(0.01));
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=EvaluationDispatcherAllSchemesGoldenOutputIT`
Expected: FAIL — `EvaluationDispatcher` does not exist.

- [ ] **Step 3: Write minimal implementation**

```java
package zw.org.nmrl.ept.evaluation;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EvaluationDispatcher {

    private final Map<String, SchemeEvaluator> evaluatorsByCode;

    public EvaluationDispatcher(
        EidEvaluator eidEvaluator,
        RecencyEvaluator recencyEvaluator,
        VlEvaluator vlEvaluator,
        TbEvaluator tbEvaluator,
        DtsEvaluator dtsEvaluator,
        GenericConfigDrivenEvaluator genericEvaluator
    ) {
        this.evaluatorsByCode = Map.of(
            "dts",
            dtsEvaluator,
            "eid",
            eidEvaluator,
            "recency",
            recencyEvaluator,
            "vl",
            vlEvaluator,
            "tb",
            tbEvaluator,
            "HBV RDT",
            genericEvaluator,
            "HCV RDT",
            genericEvaluator,
            "mRDT",
            genericEvaluator,
            "SYPH RDT",
            genericEvaluator
        );
    }

    public SchemeEvaluator evaluatorFor(String schemeCode) {
        SchemeEvaluator evaluator = evaluatorsByCode.get(schemeCode);
        if (evaluator == null) {
            throw new IllegalArgumentException(
                "No evaluator registered for scheme '" + schemeCode + "' — confirm this is one of the 9 active schemes in scope for Phase 5."
            );
        }
        return evaluator;
    }
}
```

Confirm the exact scheme-code casing/matching against `Scheme.code` in the database (`scheme_list.scheme_id` values were `HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT` with mixed case and spaces per the real migrated data — match exactly, don't normalize to lowercase and silently break the lookup).

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=EvaluationDispatcherAllSchemesGoldenOutputIT`
Expected: PASS for all 9 schemes. This is the phase's actual exit criteria per `ept_project_plan.md` — do not consider Phase 5 done until this test is green.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/evaluation/EvaluationDispatcher.java src/test/java/zw/org/nmrl/ept/evaluation/EvaluationDispatcherAllSchemesGoldenOutputIT.java
git commit -m "feat: wire up scheme evaluation dispatcher, all 9 active schemes reproduce legacy golden output"
```

---

## Self-review

**Spec coverage:** every task in `ept_project_plan.md`'s Phase 5 section is covered — universal formula (Task 2), `QuantitativeCalculations` port (Task 3), generic engine (Task 5), per-scheme evaluators in ascending complexity (Tasks 4, 6, 7, 8, 9), DTS dispatcher scoped to one algorithm (Task 9), CAPA integration (Task 10), golden-output harness and mandatory coverage across all 9 schemes (Tasks 1, 11).

**Known open risks, not resolved by this plan — resolve before/during implementation, don't skip silently:**
1. **Phase 4 interface dependency**: `referenceResultFor`/`matchesReference` (Tasks 4, 5, 6, 8) all depend on however Phase 4 modeled the `ParticipantResult` ↔ `SampleReferenceResult` lookup, and DTS's 3-test-panel (`result1`/`result2`/`result3`/`repeatResult1`) needs a stable per-sample "test slot" ordering that wasn't found on `ParticipantResult` during this research — confirm both against Phase 4's actual implementation plan/code before Task 4 can compile for real.
2. **Phase 3 interface dependency**: `ShipmentCutoff.of(Shipment)` (deadline/timezone logic) is assigned to Phase 3.5 — check it exists before Task 2.
3. **Phase 1 interface dependency**: `SchemeConfiguration.optionalFields`/`scoringRules` storage shape for `testType` (Task 5) and `documentationScore` (Task 6) needs confirming against Phase 1's actual config editor design.
4. Real-data confirmation still needed (flagged inline in the relevant tasks): whether `enableRtri` is ever `'yes'` on a real Zimbabwe DTS shipment (Task 9), and whether `dtsAlgorithmScore` is genuinely `0`/unset across every real `scheme_config.dts` row, not just the one sampled during research (Task 9).

**Placeholder scan:** the `UnsupportedOperationException` stubs in Tasks 4/5/8 are deliberate, named blockers on cross-phase interfaces this research pass could not resolve (Phase 4's code wasn't available to read) — each names exactly what to check and where. This is different from an unresolved "TBD" — resolve them as the very first action of implementing each task, before writing the rest of that task's code.

**Type consistency:** `EvaluationOutcome`/`FailureReasonEntry` (Task 2) are used with identical shape by every evaluator (Tasks 4–9) and the dispatcher (Task 11); `SchemeEvaluator` (Task 4) is implemented identically by all six evaluator classes; `ZScoreVerdict`/`QuantitativeScoring` (Task 7) is reused by Task 5's quantitative branch rather than redefined.
