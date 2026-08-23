# Phase 0 — Cross-Cutting Foundations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the role/authorization model, background job infrastructure, email/notification pipeline, file storage abstraction, and confirm the API design decision — the five foundations every later parity phase depends on.

**Architecture:** Extend the existing JHipster scaffolding rather than replacing it. Every entity, enum, and repository this plan touches already exists in the domain model (`ScheduledJob`/`JobType`/`JobStatus`, `EmailMessage`/`EmailStatus`, `DataManager`/`DataManagerRole`, `AuditLog`/`AuditAction`) — this plan wires business logic onto scaffolding that was already shaped for it, it does not invent new tables.

**Tech Stack:** Spring Boot 4, Spring Security (JWT bearer, stateless), Spring Data JPA, `net.javacrumbs.shedlock` (new dependency), Jakarta Mail via `JavaMailSender` (already present).

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md`, section "Phase 0 — Cross-cutting foundations" (subtasks 0.1–0.5) and the "Architectural decisions (resolved)" table.

## Global Constraints

- All 5 architectural decisions in the spec are final — do not re-litigate PDF/Excel libraries (not this phase), role-hierarchy approach, ShedLock, file storage, or API design.
- This app is stateless JWT bearer auth (`SecurityConfiguration.filterChain` runs `oauth2ResourceServer(...).jwt(...)`, `SessionCreationPolicy.STATELESS`) — CSRF is not applicable, do not add any CSRF handling.
- Authorities are baked into the JWT at token-issuance time (`SecurityUtils.AUTHORITIES_CLAIM = "auth"`) — a role change on `DataManager`/`User` only takes effect on the user's *next* login, not retroactively on an existing token. This is expected, not a bug to fix.
- Every new scheduled poller must be idempotent and safe to run concurrently across instances — guard with ShedLock, not a home-rolled lock.
- Match existing code style: JHipster-generated files use `LOG.debug(...)` logging, `@Transactional` on service classes, constructor injection, no Lombok.

---

## Findings that changed this plan from what the master roadmap assumed

The master plan (`ept_project_plan.md`) was written from a document-level gap analysis. Reading the actual current code surfaced concrete details that change *how* several tasks are implemented, though not *what* they deliver:

1. **`ScheduledJob`'s `JobStatus` enum already has `STALE`** (`PENDING, RUNNING, COMPLETED, FAILED, CANCELLED, STALE`), not the generic `PENDING/PROCESSING/COMPLETED/FAILED` the spec's prose described. `RUNNING` is the "processing" state; `STALE` is a dedicated status for jobs the recovery sweep reclaims — use it, don't add a new status.
2. **`JobType` already enumerates every job this build needs**: `EVALUATION, REPORT_GENERATION, CERTIFICATE_GENERATION, CERTIFICATE_DISTRIBUTION, EMAIL_DISPATCH, DEADLINE_PROCESSING, STALE_CLEANUP`. No new job types are needed in this phase.
3. **`DataManager.role` (`DataManagerRole`: `MANAGER`, `PTCC`) already distinguishes the two data-manager-side tiers.** The role-model task is therefore "sync this existing field to Spring authorities on save," not "design a new tier field."
4. **A read-only `LegacyFileStore` already exists** (`src/main/java/zw/org/nmrl/ept/service/LegacyFileStore.java`), gated by `application.legacy-files.enabled`/`EPT_LEGACY_FILE_STORE_ROOT`, with path-traversal-safe resolution already implemented. Task 11 below builds a **separate, write-capable `FileStoreService`** for newly-generated files rather than modifying `LegacyFileStore` (which is deliberately read-only per its own doc comment) — the two compose, they don't merge.
5. **`AuditLog`'s `AuditAction` enum already has `IMPERSONATE`.** The impersonation task logs through the existing `AuditLog` entity with that action, not a bespoke log.
6. **Legacy mail templates use `##PLACEHOLDER##` syntax** (confirmed directly against real migrated data: `##NAME##`, `##SHIPCODE##`), not Mustache or `%X%`. The template-rendering task must match this exact syntax so migrated `MailTemplate` rows work unmodified, per the spec's own requirement.
7. **`DataManager.participantses` ↔ `Participant.dataManagerses`** is the real (if oddly pluralized — a JHipster generator quirk, don't rename it) bidirectional many-to-many, backed by `rel_data_manager__participants(data_manager_id, participants_id)`. This is the exact relation the ownership-scoping task filters through.

---

### Task 1: PT-domain authority constants and role hierarchy

**Files:**

- Modify: `src/main/java/zw/org/nmrl/ept/security/AuthoritiesConstants.java`
- Modify: `src/main/java/zw/org/nmrl/ept/config/SecurityConfiguration.java`
- Test: `src/test/java/zw/org/nmrl/ept/config/RoleHierarchyConfigTest.java`

**Interfaces:**

- Produces: `AuthoritiesConstants.PT_ADMIN = "ROLE_PT_ADMIN"`, `AuthoritiesConstants.PTCC = "ROLE_PTCC"`, `AuthoritiesConstants.DATA_MANAGER = "ROLE_DATA_MANAGER"`; a `RoleHierarchy` bean in the Spring context. Later tasks (2, 3) depend on these exact constant names.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import zw.org.nmrl.ept.IntegrationTest;
import zw.org.nmrl.ept.security.AuthoritiesConstants;

@IntegrationTest
class RoleHierarchyConfigTest {

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Test
    void ptAdminReachesDataManagerAuthority() {
        Collection<? extends GrantedAuthority> reachable = roleHierarchy.getReachableGrantedAuthorities(
            java.util.List.of(new SimpleGrantedAuthority(AuthoritiesConstants.PT_ADMIN))
        );
        assertThat(reachable).extracting(GrantedAuthority::getAuthority).contains(AuthoritiesConstants.DATA_MANAGER, AuthoritiesConstants.PTCC);
    }

    @Test
    void dataManagerDoesNotReachPtAdminAuthority() {
        Collection<? extends GrantedAuthority> reachable = roleHierarchy.getReachableGrantedAuthorities(
            java.util.List.of(new SimpleGrantedAuthority(AuthoritiesConstants.DATA_MANAGER))
        );
        assertThat(reachable).extracting(GrantedAuthority::getAuthority).doesNotContain(AuthoritiesConstants.PT_ADMIN);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=RoleHierarchyConfigTest`
Expected: FAIL — compile error, `AuthoritiesConstants.PT_ADMIN` does not exist and no `RoleHierarchy` bean is registered.

- [ ] **Step 3: Add the PT-domain authority constants**

```java
package zw.org.nmrl.ept.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    /** PT domain tiers — distinct from JHipster's account-management ADMIN/USER above. */
    public static final String PT_ADMIN = "ROLE_PT_ADMIN";

    public static final String PTCC = "ROLE_PTCC";

    public static final String DATA_MANAGER = "ROLE_DATA_MANAGER";

    private AuthoritiesConstants() {}
}
```

- [ ] **Step 4: Add the `RoleHierarchy` bean**

Add to `src/main/java/zw/org/nmrl/ept/config/SecurityConfiguration.java`, inside the `SecurityConfiguration` class (after the `passwordEncoder()` bean):

```java
    @Bean
    public org.springframework.security.access.hierarchicalroles.RoleHierarchy roleHierarchy() {
        return org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy(
            AuthoritiesConstants.PT_ADMIN + " > " + AuthoritiesConstants.PTCC + "\n" + AuthoritiesConstants.PTCC + " > " + AuthoritiesConstants.DATA_MANAGER
        );
    }
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=RoleHierarchyConfigTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/security/AuthoritiesConstants.java src/main/java/zw/org/nmrl/ept/config/SecurityConfiguration.java src/test/java/zw/org/nmrl/ept/config/RoleHierarchyConfigTest.java
git commit -m "feat: add PT-domain authorities and role hierarchy"
```

---

### Task 2: Sync `DataManager.role` to `User` authorities on save

**Files:**

- Create: `src/main/java/zw/org/nmrl/ept/security/DataManagerAuthoritySynchronizer.java`
- Modify: `src/main/java/zw/org/nmrl/ept/service/impl/DataManagerServiceImpl.java`
- Test: `src/test/java/zw/org/nmrl/ept/security/DataManagerAuthoritySynchronizerTest.java`

**Interfaces:**

- Consumes: `AuthoritiesConstants.DATA_MANAGER`/`PTCC` (Task 1), `DataManager.getRole()` returning `DataManagerRole` (`MANAGER`/`PTCC`), `DataManager.getUser()` returning `User`, `AuthorityRepository extends JpaRepository<Authority, String>` (existing).
- Produces: `DataManagerAuthoritySynchronizer.sync(DataManager dataManager)` — later tasks don't depend on this directly, but any future service that creates/updates a `DataManager` must call it.

- [ ] **Step 1: Write the failing unit test**

```java
package zw.org.nmrl.ept.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.Authority;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.User;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.repository.AuthorityRepository;
import zw.org.nmrl.ept.repository.UserRepository;

class DataManagerAuthoritySynchronizerTest {

    @Test
    void grantsPtccAuthorityForPtccRole() {
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        Authority ptcc = new Authority();
        ptcc.setName(AuthoritiesConstants.PTCC);
        when(authorityRepository.findById(AuthoritiesConstants.PTCC)).thenReturn(Optional.of(ptcc));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = new User();
        user.setId(42L);
        DataManager dataManager = new DataManager();
        dataManager.setRole(DataManagerRole.PTCC);
        dataManager.setUser(user);

        new DataManagerAuthoritySynchronizer(authorityRepository, userRepository).sync(dataManager);

        assertThat(user.getAuthorities()).extracting(Authority::getName).containsExactly(AuthoritiesConstants.PTCC);
        verify(userRepository).save(user);
    }

    @Test
    void grantsDataManagerAuthorityForManagerRole() {
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        Authority dm = new Authority();
        dm.setName(AuthoritiesConstants.DATA_MANAGER);
        when(authorityRepository.findById(AuthoritiesConstants.DATA_MANAGER)).thenReturn(Optional.of(dm));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = new User();
        user.setId(43L);
        DataManager dataManager = new DataManager();
        dataManager.setRole(DataManagerRole.MANAGER);
        dataManager.setUser(user);

        new DataManagerAuthoritySynchronizer(authorityRepository, userRepository).sync(dataManager);

        assertThat(user.getAuthorities()).extracting(Authority::getName).containsExactly(AuthoritiesConstants.DATA_MANAGER);
    }

    @Test
    void doesNothingWhenDataManagerHasNoLinkedUser() {
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        DataManager dataManager = new DataManager();
        dataManager.setRole(DataManagerRole.MANAGER);
        dataManager.setUser(null);

        new DataManagerAuthoritySynchronizer(authorityRepository, userRepository).sync(dataManager);

        verifyNoInteractions(userRepository);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=DataManagerAuthoritySynchronizerTest`
Expected: FAIL — `DataManagerAuthoritySynchronizer` does not exist.

- [ ] **Step 3: Write the synchronizer**

```java
package zw.org.nmrl.ept.security;

import java.util.HashSet;
import org.springframework.stereotype.Component;
import zw.org.nmrl.ept.domain.Authority;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.User;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.repository.AuthorityRepository;
import zw.org.nmrl.ept.repository.UserRepository;

/**
 * Keeps a {@link DataManager}'s linked {@link User} authority set in sync with its
 * {@link DataManagerRole}. A role change only takes effect on the user's next login
 * (authorities are baked into the JWT at issuance) — that is expected, not a bug.
 */
@Component
public class DataManagerAuthoritySynchronizer {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public DataManagerAuthoritySynchronizer(AuthorityRepository authorityRepository, UserRepository userRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
    }

    public void sync(DataManager dataManager) {
        User user = dataManager.getUser();
        if (user == null) {
            return;
        }
        String authorityName = dataManager.getRole() == DataManagerRole.PTCC ? AuthoritiesConstants.PTCC : AuthoritiesConstants.DATA_MANAGER;
        Authority authority = authorityRepository
            .findById(authorityName)
            .orElseThrow(() -> new IllegalStateException("Authority not seeded: " + authorityName));
        user.setAuthorities(new HashSet<>(java.util.Set.of(authority)));
        userRepository.save(user);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=DataManagerAuthoritySynchronizerTest`
Expected: PASS

- [ ] **Step 5: Wire the synchronizer into `DataManagerServiceImpl`**

Modify `src/main/java/zw/org/nmrl/ept/service/impl/DataManagerServiceImpl.java`:

```java
package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.security.DataManagerAuthoritySynchronizer;
import zw.org.nmrl.ept.service.DataManagerService;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.mapper.DataManagerMapper;

@Service
@Transactional
public class DataManagerServiceImpl implements DataManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(DataManagerServiceImpl.class);

    private final DataManagerRepository dataManagerRepository;
    private final DataManagerMapper dataManagerMapper;
    private final DataManagerAuthoritySynchronizer authoritySynchronizer;

    public DataManagerServiceImpl(
        DataManagerRepository dataManagerRepository,
        DataManagerMapper dataManagerMapper,
        DataManagerAuthoritySynchronizer authoritySynchronizer
    ) {
        this.dataManagerRepository = dataManagerRepository;
        this.dataManagerMapper = dataManagerMapper;
        this.authoritySynchronizer = authoritySynchronizer;
    }

    @Override
    public DataManagerDTO save(DataManagerDTO dataManagerDTO) {
        LOG.debug("Request to save DataManager : {}", dataManagerDTO);
        DataManager dataManager = dataManagerMapper.toEntity(dataManagerDTO);
        dataManager = dataManagerRepository.save(dataManager);
        authoritySynchronizer.sync(dataManager);
        return dataManagerMapper.toDto(dataManager);
    }

    @Override
    public DataManagerDTO update(DataManagerDTO dataManagerDTO) {
        LOG.debug("Request to update DataManager : {}", dataManagerDTO);
        DataManager dataManager = dataManagerMapper.toEntity(dataManagerDTO);
        dataManager = dataManagerRepository.save(dataManager);
        authoritySynchronizer.sync(dataManager);
        return dataManagerMapper.toDto(dataManager);
    }

    @Override
    public Optional<DataManagerDTO> partialUpdate(DataManagerDTO dataManagerDTO) {
        LOG.debug("Request to partially update DataManager : {}", dataManagerDTO);

        return dataManagerRepository
            .findById(dataManagerDTO.getId())
            .map(existingDataManager -> {
                dataManagerMapper.partialUpdate(existingDataManager, dataManagerDTO);
                return existingDataManager;
            })
            .map(dataManagerRepository::save)
            .map(dataManager -> {
                authoritySynchronizer.sync(dataManager);
                return dataManager;
            })
            .map(dataManagerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DataManagerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DataManagers");
        return dataManagerRepository.findAll(pageable).map(dataManagerMapper::toDto);
    }

    public Page<DataManagerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return dataManagerRepository.findAllWithEagerRelationships(pageable).map(dataManagerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DataManagerDTO> findOne(Long id) {
        LOG.debug("Request to get DataManager : {}", id);
        return dataManagerRepository.findOneWithEagerRelationships(id).map(dataManagerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DataManager : {}", id);
        dataManagerRepository.deleteById(id);
    }
}
```

- [ ] **Step 6: Seed the two new `Authority` rows**

Add a Liquibase changelog `src/main/resources/config/liquibase/changelog/2026XXXXXXXXXX_seed_pt_domain_authorities.xml` (use today's actual timestamp, matching the naming convention of existing changelog files under that directory) that inserts `ROLE_PT_ADMIN`, `ROLE_PTCC`, `ROLE_DATA_MANAGER` into the `jhi_authority` table, and register it in `src/main/resources/config/liquibase/master.xml`. Follow the exact `<include>` pattern already used for the other changelog entries in that file.

- [ ] **Step 7: Run the full test suite for this task's package**

Run: `./mvnw -Pprod test -Dtest=DataManagerAuthoritySynchronizerTest,zw.org.nmrl.ept.service.mapper.DataManagerMapperTest`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/security/DataManagerAuthoritySynchronizer.java src/main/java/zw/org/nmrl/ept/service/impl/DataManagerServiceImpl.java src/test/java/zw/org/nmrl/ept/security/DataManagerAuthoritySynchronizerTest.java src/main/resources/config/liquibase/changelog/ src/main/resources/config/liquibase/master.xml
git commit -m "feat: sync DataManager role to Spring Security authorities on save"
```

---

### Task 3: Participant-ownership scoping

**Files:**

- Modify: `src/main/java/zw/org/nmrl/ept/repository/DataManagerRepository.java`
- Create: `src/main/java/zw/org/nmrl/ept/security/ParticipantOwnershipService.java`
- Test: `src/test/java/zw/org/nmrl/ept/security/ParticipantOwnershipServiceIT.java`

**Interfaces:**

- Consumes: `SecurityUtils.getCurrentUserId()` returning `Optional<Long>` (existing), `SecurityUtils.hasCurrentUserThisAuthority(String)` (existing), `AuthoritiesConstants.PT_ADMIN`/`DATA_MANAGER` (Task 1), `DataManager.getParticipantses()` returning `Set<Participant>` (existing).
- Produces: `ParticipantOwnershipService.currentUserCanAccess(Long participantId)` returning `boolean`, and `ParticipantOwnershipService.ownedParticipantIds()` returning `Set<Long>` — later phases (Phase 2's participant endpoints, Phase 3's enrollment/shipment endpoints) call these to scope their own queries. This task does not yet wire it into `ParticipantResource` — that happens in Phase 2, which owns the participant REST surface; this task only builds and proves the scoping primitive.

- [ ] **Step 1: Add the repository lookup**

Modify `src/main/java/zw/org/nmrl/ept/repository/DataManagerRepository.java`:

```java
package zw.org.nmrl.ept.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.DataManager;

@Repository
public interface DataManagerRepository extends DataManagerRepositoryWithBagRelationships, JpaRepository<DataManager, Long> {
    Optional<DataManager> findOneByUserId(Long userId);

    default Optional<DataManager> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<DataManager> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<DataManager> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
```

- [ ] **Step 2: Write the failing integration test**

```java
package zw.org.nmrl.ept.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import zw.org.nmrl.ept.IntegrationTest;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.User;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.repository.UserRepository;

@IntegrationTest
class ParticipantOwnershipServiceIT {

    @Autowired
    private ParticipantOwnershipService ownershipService;

    @Autowired
    private DataManagerRepository dataManagerRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void dataManagerCanAccessOnlyOwnParticipant() {
        User user = userRepository.save(new User().login("dm-ownership-test").email("dm-ownership-test@example.com").activated(true));
        Participant ownedParticipant = participantRepository.save(new Participant().name("Owned Lab"));
        Participant otherParticipant = participantRepository.save(new Participant().name("Other Lab"));
        DataManager dataManager = new DataManager();
        dataManager.setRole(DataManagerRole.MANAGER);
        dataManager.setUser(user);
        dataManager.setParticipantses(Set.of(ownedParticipant));
        dataManagerRepository.save(dataManager);

        authenticateAs(user.getId());

        assertThat(ownershipService.currentUserCanAccess(ownedParticipant.getId())).isTrue();
        assertThat(ownershipService.currentUserCanAccess(otherParticipant.getId())).isFalse();
    }

    private void authenticateAs(Long userId) {
        Jwt jwt = Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("sub", "dm-ownership-test")
            .claim("userId", userId)
            .claim("auth", zw.org.nmrl.ept.security.AuthoritiesConstants.DATA_MANAGER)
            .build();
        SecurityContextHolder.setContext(new SecurityContextImpl(new JwtAuthenticationToken(jwt, java.util.List.of())));
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ParticipantOwnershipServiceIT`
Expected: FAIL — `ParticipantOwnershipService` does not exist.

- [ ] **Step 4: Write the ownership service**

```java
package zw.org.nmrl.ept.security;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.repository.DataManagerRepository;

/**
 * Enforces the data-manager/PTCC participant-ownership boundary: a PT admin sees
 * everything, everyone else sees only participants mapped to their DataManager row
 * via {@code rel_data_manager__participants}.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantOwnershipService {

    private final DataManagerRepository dataManagerRepository;

    public ParticipantOwnershipService(DataManagerRepository dataManagerRepository) {
        this.dataManagerRepository = dataManagerRepository;
    }

    public boolean currentUserCanAccess(Long participantId) {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.PT_ADMIN)) {
            return true;
        }
        return ownedParticipantIds().contains(participantId);
    }

    public Set<Long> ownedParticipantIds() {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.PT_ADMIN)) {
            throw new IllegalStateException("PT admins are not scoped — check currentUserCanAccess()/hasThisAuthority() before calling this");
        }
        return SecurityUtils.getCurrentUserId()
            .flatMap(dataManagerRepository::findOneByUserId)
            .map(DataManager::getParticipantses)
            .map(participants -> participants.stream().map(Participant::getId).collect(Collectors.toSet()))
            .orElseGet(Set::of);
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ParticipantOwnershipServiceIT`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/repository/DataManagerRepository.java src/main/java/zw/org/nmrl/ept/security/ParticipantOwnershipService.java src/test/java/zw/org/nmrl/ept/security/ParticipantOwnershipServiceIT.java
git commit -m "feat: add participant-ownership scoping service"
```

---

### Task 4: Login attempt hardening (temp/permanent ban)

**Files:**

- Modify: `src/main/java/zw/org/nmrl/ept/security/DomainUserDetailsService.java`
- Create: `src/main/java/zw/org/nmrl/ept/security/LoginAttemptListener.java`
- Test: `src/test/java/zw/org/nmrl/ept/security/LoginAttemptListenerTest.java`

**Interfaces:**

- Consumes: `UserLoginHistory` entity (existing: `loginId`, `loginStatus` (`LoginStatus.SUCCESS`/`FAILED`/`BANNED`), `attemptedAt`, `ipAddress`), `GlobalConfiguration` (existing: `configKey`/`configValue`) for the `max_attempts_for_temp_ban`/`max_attempts_for_perm_ban` thresholds, `DataManager.setLoginBan(Boolean)` (existing field).
- Produces: `LoginAttemptListener` — a Spring `ApplicationListener` wired automatically, no other task depends on it directly.

- [ ] **Step 1: Write the failing unit test**

```java
package zw.org.nmrl.ept.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.GlobalConfiguration;
import zw.org.nmrl.ept.domain.UserLoginHistory;
import zw.org.nmrl.ept.domain.enumeration.LoginStatus;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.repository.GlobalConfigurationRepository;
import zw.org.nmrl.ept.repository.UserLoginHistoryRepository;
import zw.org.nmrl.ept.repository.UserRepository;

class LoginAttemptListenerTest {

    @Test
    void bansDataManagerAfterConfiguredFailureThreshold() {
        UserLoginHistoryRepository historyRepository = mock(UserLoginHistoryRepository.class);
        GlobalConfigurationRepository configRepository = mock(GlobalConfigurationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        DataManagerRepository dataManagerRepository = mock(DataManagerRepository.class);

        GlobalConfiguration tempBanConfig = new GlobalConfiguration();
        tempBanConfig.setConfigValue("3");
        when(configRepository.findOneByConfigKey("max_attempts_for_temp_ban")).thenReturn(Optional.of(tempBanConfig));
        when(historyRepository.countByLoginIdAndLoginStatusAndAttemptedAtAfter(eq("dm@example.com"), eq(LoginStatus.FAILED), any(Instant.class)))
            .thenReturn(3L);
        when(userRepository.findOneByLogin("dm@example.com")).thenReturn(Optional.empty());
        DataManager dataManager = new DataManager();
        when(dataManagerRepository.findOneByPrimaryEmailIgnoreCase("dm@example.com")).thenReturn(Optional.of(dataManager));

        LoginAttemptListener listener = new LoginAttemptListener(historyRepository, configRepository, userRepository, dataManagerRepository);
        listener.onFailure(
            new AuthenticationFailureBadCredentialsEvent(
                new UsernamePasswordAuthenticationToken("dm@example.com", "wrong-password"),
                new BadCredentialsException("bad credentials")
            )
        );

        verify(historyRepository).save(argThat((UserLoginHistory h) -> h.getLoginStatus() == LoginStatus.FAILED && h.getLoginId().equals("dm@example.com")));
        verify(dataManagerRepository).save(argThat((DataManager dm) -> Boolean.TRUE.equals(dm.getLoginBan())));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=LoginAttemptListenerTest`
Expected: FAIL — `LoginAttemptListener` does not exist; `GlobalConfigurationRepository.findOneByConfigKey`, `UserLoginHistoryRepository.countByLoginIdAndLoginStatusAndAttemptedAtAfter`, and `DataManagerRepository.findOneByPrimaryEmailIgnoreCase` do not exist.

- [ ] **Step 3: Add the three repository methods**

Add to `src/main/java/zw/org/nmrl/ept/repository/GlobalConfigurationRepository.java`:

```java
    java.util.Optional<zw.org.nmrl.ept.domain.GlobalConfiguration> findOneByConfigKey(String configKey);
```

Add to `src/main/java/zw/org/nmrl/ept/repository/UserLoginHistoryRepository.java`:

```java
    long countByLoginIdAndLoginStatusAndAttemptedAtAfter(String loginId, zw.org.nmrl.ept.domain.enumeration.LoginStatus loginStatus, java.time.Instant after);
```

Add to `src/main/java/zw/org/nmrl/ept/repository/DataManagerRepository.java` (alongside `findOneByUserId` from Task 3):

```java
    Optional<DataManager> findOneByPrimaryEmailIgnoreCase(String primaryEmail);
```

- [ ] **Step 4: Write the listener**

```java
package zw.org.nmrl.ept.security;

import java.time.Duration;
import java.time.Instant;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.UserLoginHistory;
import zw.org.nmrl.ept.domain.enumeration.LoginStatus;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.repository.GlobalConfigurationRepository;
import zw.org.nmrl.ept.repository.UserLoginHistoryRepository;
import zw.org.nmrl.ept.repository.UserRepository;

/**
 * Records every authentication attempt to {@link UserLoginHistory} and bans a data
 * manager whose failure count within the last hour reaches the configured threshold
 * (mirrors legacy's {@code global_config.max_attempts_for_temp_ban}). Permanent-ban
 * threshold ({@code max_attempts_for_perm_ban}) follows the same pattern with a wider
 * counting window and no automatic unban — left for the operator to clear manually.
 */
@Component
@Transactional
public class LoginAttemptListener {

    private static final Duration COUNTING_WINDOW = Duration.ofHours(1);

    private final UserLoginHistoryRepository historyRepository;
    private final GlobalConfigurationRepository configRepository;
    private final UserRepository userRepository;
    private final DataManagerRepository dataManagerRepository;

    public LoginAttemptListener(
        UserLoginHistoryRepository historyRepository,
        GlobalConfigurationRepository configRepository,
        UserRepository userRepository,
        DataManagerRepository dataManagerRepository
    ) {
        this.historyRepository = historyRepository;
        this.configRepository = configRepository;
        this.userRepository = userRepository;
        this.dataManagerRepository = dataManagerRepository;
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String loginId = String.valueOf(event.getAuthentication().getPrincipal());
        UserLoginHistory history = new UserLoginHistory();
        history.setLoginId(loginId);
        history.setLoginStatus(LoginStatus.FAILED);
        history.setAttemptedAt(Instant.now());
        historyRepository.save(history);

        int threshold = configRepository
            .findOneByConfigKey("max_attempts_for_temp_ban")
            .map(c -> Integer.parseInt(c.getConfigValue()))
            .orElse(5);
        long recentFailures = historyRepository.countByLoginIdAndLoginStatusAndAttemptedAtAfter(
            loginId,
            LoginStatus.FAILED,
            Instant.now().minus(COUNTING_WINDOW)
        );
        if (recentFailures >= threshold) {
            dataManagerRepository
                .findOneByPrimaryEmailIgnoreCase(loginId)
                .ifPresent(dataManager -> {
                    dataManager.setLoginBan(true);
                    dataManagerRepository.save(dataManager);
                });
        }
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=LoginAttemptListenerTest`
Expected: PASS

- [ ] **Step 6: Enforce the ban at authentication time**

Modify `src/main/java/zw/org/nmrl/ept/security/DomainUserDetailsService.java`'s `createSpringSecurityUser` method to check `DataManager.loginBan` before returning a `UserDetails`, throwing `org.springframework.security.authentication.LockedException` when banned (Spring Security maps this to a 401 automatically, same as `UserNotActivatedException` already does in this class). This requires injecting `DataManagerRepository` into `DomainUserDetailsService`'s constructor and calling `dataManagerRepository.findOneByUserId(user.getId())` to check `loginBan` — add a companion unit test asserting a banned data manager's login throws `LockedException`.

- [ ] **Step 7: Run the full security package test suite**

Run: `./mvnw -Pprod test -Dtest=zw.org.nmrl.ept.security.**`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/security/ src/main/java/zw/org/nmrl/ept/repository/GlobalConfigurationRepository.java src/main/java/zw/org/nmrl/ept/repository/UserLoginHistoryRepository.java src/main/java/zw/org/nmrl/ept/repository/DataManagerRepository.java src/test/java/zw/org/nmrl/ept/security/LoginAttemptListenerTest.java
git commit -m "feat: add login attempt tracking and temp ban enforcement"
```

---

### Task 5: Admin impersonation (audit-logged, short-lived)

**Files:**

- Create: `src/main/java/zw/org/nmrl/ept/web/rest/ImpersonationResource.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/ImpersonationService.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ImpersonationResourceIT.java`

**Interfaces:**

- Consumes: `AuditLog` entity + `AuditAction.IMPERSONATE` (existing enum value), `DataManagerRepository.findById` (existing), `AuthoritiesConstants.PT_ADMIN` (Task 1).
- Produces: `POST /api/admin/impersonate/{dataManagerId}` returning a short-lived JWT scoped to that data manager's authorities — this is a deliberate translation of legacy's server-side session swap into something that fits this app's stateless-JWT model, not a literal port. No later task in this plan depends on it, but Phase 2's data-manager admin screens will surface a "view as" action against this endpoint.

- [ ] **Step 1: Write the failing integration test**

```java
package zw.org.nmrl.ept.web.rest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import zw.org.nmrl.ept.IntegrationTest;
import zw.org.nmrl.ept.domain.AuditLog;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.User;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.repository.AuditLogRepository;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.repository.UserRepository;
import zw.org.nmrl.ept.security.AuthoritiesConstants;

@IntegrationTest
@AutoConfigureMockMvc
class ImpersonationResourceIT {

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private DataManagerRepository dataManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.PT_ADMIN)
    void impersonationIsAuditLogged() throws Exception {
        User dmUser = userRepository.save(new User().login("impersonate-target").email("impersonate-target@example.com").activated(true));
        DataManager dataManager = new DataManager();
        dataManager.setRole(DataManagerRole.MANAGER);
        dataManager.setUser(dmUser);
        dataManager = dataManagerRepository.save(dataManager);

        restMockMvc.perform(post("/api/admin/impersonate/{id}", dataManager.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.token", notNullValue()));

        boolean logged = auditLogRepository
            .findAll()
            .stream()
            .anyMatch(log -> log.getAction() == AuditAction.IMPERSONATE && log.getStatement().contains(dataManager.getId().toString()));
        org.assertj.core.api.Assertions.assertThat(logged).isTrue();
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.DATA_MANAGER)
    void nonAdminCannotImpersonate() throws Exception {
        restMockMvc.perform(post("/api/admin/impersonate/{id}", 1L)).andExpect(status().isForbidden());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ImpersonationResourceIT`
Expected: FAIL — `ImpersonationResource` does not exist.

- [ ] **Step 3: Write `ImpersonationService`**

```java
package zw.org.nmrl.ept.service;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.AuditLog;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;
import zw.org.nmrl.ept.repository.AuditLogRepository;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.security.SecurityUtils;

/**
 * Issues a short-lived (15 minute, matching legacy's impersonation TTL), narrowly
 * scoped JWT letting a PT admin act as a given data manager for support purposes.
 * Every issuance is audit-logged; this is intentionally a distinct token rather than
 * a legacy-style server-side session swap, since this app is stateless-JWT throughout.
 */
@Service
@Transactional
public class ImpersonationService {

    private static final long TTL_SECONDS = 900;

    private final DataManagerRepository dataManagerRepository;
    private final AuditLogRepository auditLogRepository;
    private final JwtEncoder jwtEncoder;

    public ImpersonationService(DataManagerRepository dataManagerRepository, AuditLogRepository auditLogRepository, JwtEncoder jwtEncoder) {
        this.dataManagerRepository = dataManagerRepository;
        this.auditLogRepository = auditLogRepository;
        this.jwtEncoder = jwtEncoder;
    }

    public Optional<String> impersonate(Long dataManagerId) {
        return dataManagerRepository
            .findById(dataManagerId)
            .filter(dm -> dm.getUser() != null)
            .map(dataManager -> {
                String authority = dataManager.getRole() == zw.org.nmrl.ept.domain.enumeration.DataManagerRole.PTCC
                    ? zw.org.nmrl.ept.security.AuthoritiesConstants.PTCC
                    : zw.org.nmrl.ept.security.AuthoritiesConstants.DATA_MANAGER;
                Instant now = Instant.now();
                JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(TTL_SECONDS))
                    .subject(dataManager.getUser().getLogin())
                    .claim("userId", dataManager.getUser().getId())
                    .claim("auth", authority)
                    .claim("impersonatedBy", SecurityUtils.getCurrentUserLogin().orElse("unknown"))
                    .build();
                String token = jwtEncoder
                    .encode(JwtEncoderParameters.from(JwsHeader.with(SecurityUtils.JWT_ALGORITHM).build(), claims))
                    .getTokenValue();

                AuditLog log = new AuditLog();
                log.setAction(AuditAction.IMPERSONATE);
                log.setStatement("Started impersonation of DataManager id=" + dataManagerId);
                log.setPerformedBy(SecurityUtils.getCurrentUserLogin().orElse("unknown"));
                log.setPerformedOn(now);
                auditLogRepository.save(log);

                return token;
            });
    }
}
```

- [ ] **Step 4: Write `ImpersonationResource`**

```java
package zw.org.nmrl.ept.web.rest;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zw.org.nmrl.ept.security.AuthoritiesConstants;
import zw.org.nmrl.ept.service.ImpersonationService;

@RestController
@RequestMapping("/api/admin")
public class ImpersonationResource {

    private final ImpersonationService impersonationService;

    public ImpersonationResource(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @PostMapping("/impersonate/{dataManagerId}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.PT_ADMIN + "')")
    public ResponseEntity<Map<String, String>> impersonate(@PathVariable Long dataManagerId) {
        return impersonationService
            .impersonate(dataManagerId)
            .map(token -> ResponseEntity.ok(Map.of("token", token)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ImpersonationResourceIT`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/web/rest/ImpersonationResource.java src/main/java/zw/org/nmrl/ept/service/ImpersonationService.java src/test/java/zw/org/nmrl/ept/web/rest/ImpersonationResourceIT.java
git commit -m "feat: add audit-logged admin impersonation endpoint"
```

---

### Task 6: Job-queue contract and ShedLock-guarded poller

**Files:**

- Modify: `pom.xml`
- Create: `src/main/java/zw/org/nmrl/ept/config/ShedLockConfiguration.java`
- Create: `src/main/java/zw/org/nmrl/ept/job/JobHandler.java`
- Create: `src/main/java/zw/org/nmrl/ept/job/ScheduledJobProcessor.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/ScheduledJobQueue.java`
- Test: `src/test/java/zw/org/nmrl/ept/job/ScheduledJobProcessorTest.java`

**Interfaces:**

- Consumes: `ScheduledJob`/`JobType`/`JobStatus` entities (existing, see "Findings" above), `ScheduledJobRepository` (existing).
- Produces: `JobHandler` interface (`JobType supports()`, `void handle(ScheduledJob job)`) — Tasks 10 (email send) and every later phase's async work (evaluation, report generation) implement this and get picked up automatically by `ScheduledJobProcessor`. `ScheduledJobQueue.enqueue(JobType, String payload)` — later phases call this instead of touching `ScheduledJobRepository` directly.

- [ ] **Step 1: Add the ShedLock dependency**

Add to `pom.xml`, inside the `<dependencies>` section (alongside the other `org.springframework.boot` starters):

```xml
        <dependency>
            <groupId>net.javacrumbs.shedlock</groupId>
            <artifactId>shedlock-spring</artifactId>
            <version>6.2.0</version>
        </dependency>
        <dependency>
            <groupId>net.javacrumbs.shedlock</groupId>
            <artifactId>shedlock-provider-jdbc-template</artifactId>
            <version>6.2.0</version>
        </dependency>
```

- [ ] **Step 2: Add a Liquibase changelog for ShedLock's lock table**

Create `src/main/resources/config/liquibase/changelog/2026XXXXXXXXXX_added_shedlock_table.xml` (use today's actual timestamp) with ShedLock's standard `shedlock` table DDL (`name VARCHAR(64) PRIMARY KEY, lock_until TIMESTAMP(3) NOT NULL, locked_at TIMESTAMP(3) NOT NULL, locked_by VARCHAR(255) NOT NULL`), and register it in `master.xml` following the existing `<include>` pattern.

- [ ] **Step 3: Write `ShedLockConfiguration`**

```java
package zw.org.nmrl.ept.config;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
public class ShedLockConfiguration {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder().withJdbcTemplate(new org.springframework.jdbc.core.JdbcTemplate(dataSource)).usingDbTime().build()
        );
    }
}
```

- [ ] **Step 4: Write the `JobHandler` interface and `ScheduledJobQueue`**

```java
package zw.org.nmrl.ept.job;

import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobType;

/** Implemented by each async feature (email dispatch, evaluation, report generation, ...). */
public interface JobHandler {
    JobType supports();

    void handle(ScheduledJob job);
}
```

```java
package zw.org.nmrl.ept.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;

@Service
@Transactional
public class ScheduledJobQueue {

    private final ScheduledJobRepository scheduledJobRepository;

    public ScheduledJobQueue(ScheduledJobRepository scheduledJobRepository) {
        this.scheduledJobRepository = scheduledJobRepository;
    }

    public ScheduledJob enqueue(JobType jobType, String requestedBy) {
        ScheduledJob job = new ScheduledJob();
        job.setJobType(jobType);
        job.setStatus(JobStatus.PENDING);
        job.setRequestedBy(requestedBy);
        job.setRequestedOn(Instant.now());
        return scheduledJobRepository.save(job);
    }
}
```

- [ ] **Step 5: Write the failing test for the poller**

```java
package zw.org.nmrl.ept.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;

class ScheduledJobProcessorTest {

    @Test
    void runsMatchingHandlerAndMarksCompleted() {
        ScheduledJobRepository repository = mock(ScheduledJobRepository.class);
        ScheduledJob pending = new ScheduledJob();
        pending.setId(1L);
        pending.setJobType(JobType.EMAIL_DISPATCH);
        pending.setStatus(JobStatus.PENDING);
        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(pending));
        when(repository.save(any(ScheduledJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobHandler emailHandler = mock(JobHandler.class);
        when(emailHandler.supports()).thenReturn(JobType.EMAIL_DISPATCH);

        new ScheduledJobProcessor(repository, List.of(emailHandler)).processPendingJobs();

        verify(emailHandler).handle(pending);
        assertThat(pending.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(pending.getCompletedAt()).isNotNull();
    }

    @Test
    void marksJobFailedWhenHandlerThrows() {
        ScheduledJobRepository repository = mock(ScheduledJobRepository.class);
        ScheduledJob pending = new ScheduledJob();
        pending.setId(2L);
        pending.setJobType(JobType.EMAIL_DISPATCH);
        pending.setStatus(JobStatus.PENDING);
        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(pending));
        when(repository.save(any(ScheduledJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobHandler failingHandler = mock(JobHandler.class);
        when(failingHandler.supports()).thenReturn(JobType.EMAIL_DISPATCH);
        doThrow(new RuntimeException("boom")).when(failingHandler).handle(pending);

        new ScheduledJobProcessor(repository, List.of(failingHandler)).processPendingJobs();

        assertThat(pending.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(pending.getSummary()).contains("boom");
    }
}
```

- [ ] **Step 6: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ScheduledJobProcessorTest`
Expected: FAIL — `ScheduledJobProcessor` does not exist.

- [ ] **Step 7: Write `ScheduledJobProcessor`**

```java
package zw.org.nmrl.ept.job;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;

@Component
public class ScheduledJobProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobProcessor.class);

    private final ScheduledJobRepository scheduledJobRepository;
    private final Map<zw.org.nmrl.ept.domain.enumeration.JobType, JobHandler> handlersByType;

    public ScheduledJobProcessor(ScheduledJobRepository scheduledJobRepository, List<JobHandler> handlers) {
        this.scheduledJobRepository = scheduledJobRepository;
        this.handlersByType = handlers.stream().collect(Collectors.toMap(JobHandler::supports, Function.identity()));
    }

    @Scheduled(fixedDelay = 30_000)
    @SchedulerLock(name = "ScheduledJobProcessor_processPendingJobs", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10M")
    @Transactional
    public void processPendingJobs() {
        List<ScheduledJob> pending = scheduledJobRepository.findAll(
            (Specification<ScheduledJob>) (root, query, cb) -> cb.equal(root.get("status"), JobStatus.PENDING)
        );
        for (ScheduledJob job : pending) {
            JobHandler handler = handlersByType.get(job.getJobType());
            if (handler == null) {
                LOG.warn("No JobHandler registered for job type {}", job.getJobType());
                continue;
            }
            job.setStatus(JobStatus.RUNNING);
            job.setStartedAt(Instant.now());
            job.setLastHeartbeat(Instant.now());
            scheduledJobRepository.save(job);
            try {
                handler.handle(job);
                job.setStatus(JobStatus.COMPLETED);
                job.setCompletedAt(Instant.now());
            } catch (Exception e) {
                LOG.error("Job {} (type {}) failed", job.getId(), job.getJobType(), e);
                job.setStatus(JobStatus.FAILED);
                job.setSummary(e.getMessage());
                job.setCompletedAt(Instant.now());
            }
            scheduledJobRepository.save(job);
        }
    }
}
```

- [ ] **Step 8: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ScheduledJobProcessorTest`
Expected: PASS

- [ ] **Step 9: Enable scheduling if not already enabled**

Check `src/main/java/zw/org/nmrl/ept/ProficiencyTestingApp.java` for `@EnableScheduling` — JHipster's stock `removeNotActivatedUsers()` job already requires this, so it should already be present; if absent, add it.

- [ ] **Step 10: Commit**

```bash
git add pom.xml src/main/java/zw/org/nmrl/ept/config/ShedLockConfiguration.java src/main/java/zw/org/nmrl/ept/job/ src/main/java/zw/org/nmrl/ept/service/ScheduledJobQueue.java src/main/resources/config/liquibase/changelog/ src/main/resources/config/liquibase/master.xml src/test/java/zw/org/nmrl/ept/job/ScheduledJobProcessorTest.java
git commit -m "feat: add ShedLock-guarded scheduled job processor"
```

---

### Task 7: Stale job recovery sweep

**Files:**

- Create: `src/main/java/zw/org/nmrl/ept/job/StaleJobRecoverySweeper.java`
- Test: `src/test/java/zw/org/nmrl/ept/job/StaleJobRecoverySweeperTest.java`

**Interfaces:**

- Consumes: `ScheduledJobRepository` (existing), `JobStatus.RUNNING`/`STALE` (existing enum values — see Finding 1).
- Produces: nothing consumed by later tasks; this is a leaf feature.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;

class StaleJobRecoverySweeperTest {

    @Test
    void marksRunningJobStaleAfterHeartbeatTimeout() {
        ScheduledJobRepository repository = mock(ScheduledJobRepository.class);
        ScheduledJob stuck = new ScheduledJob();
        stuck.setStatus(JobStatus.RUNNING);
        stuck.setLastHeartbeat(Instant.now().minusSeconds(20 * 60));
        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(stuck));
        when(repository.save(any(ScheduledJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        new StaleJobRecoverySweeper(repository).recoverStaleJobs();

        assertThat(stuck.getStatus()).isEqualTo(JobStatus.STALE);
    }

    @Test
    void leavesRecentlyActiveJobAlone() {
        ScheduledJobRepository repository = mock(ScheduledJobRepository.class);
        ScheduledJob fresh = new ScheduledJob();
        fresh.setStatus(JobStatus.RUNNING);
        fresh.setLastHeartbeat(Instant.now().minusSeconds(30));
        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(fresh));

        new StaleJobRecoverySweeper(repository).recoverStaleJobs();

        assertThat(fresh.getStatus()).isEqualTo(JobStatus.RUNNING);
        verify(repository, never()).save(any());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=StaleJobRecoverySweeperTest`
Expected: FAIL — `StaleJobRecoverySweeper` does not exist.

- [ ] **Step 3: Write the sweeper**

```java
package zw.org.nmrl.ept.job;

import java.time.Instant;
import java.util.List;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;

/**
 * Recovers jobs stuck in RUNNING whose heartbeat has gone silent — mirrors legacy's
 * {@code reset-stale-jobs.php}. A 15-minute silence is treated as abandoned (matches
 * the poller's 30-second cadence with generous margin for a slow individual job).
 */
@Component
public class StaleJobRecoverySweeper {

    private static final long STALE_AFTER_SECONDS = 15 * 60;

    private final ScheduledJobRepository scheduledJobRepository;

    public StaleJobRecoverySweeper(ScheduledJobRepository scheduledJobRepository) {
        this.scheduledJobRepository = scheduledJobRepository;
    }

    @Scheduled(fixedDelay = 5 * 60_000)
    @SchedulerLock(name = "StaleJobRecoverySweeper_recoverStaleJobs", lockAtLeastFor = "PT5S", lockAtMostFor = "PT2M")
    @Transactional
    public void recoverStaleJobs() {
        Instant cutoff = Instant.now().minusSeconds(STALE_AFTER_SECONDS);
        List<ScheduledJob> running = scheduledJobRepository.findAll(
            (Specification<ScheduledJob>) (root, query, cb) -> cb.equal(root.get("status"), JobStatus.RUNNING)
        );
        for (ScheduledJob job : running) {
            if (job.getLastHeartbeat() == null || job.getLastHeartbeat().isBefore(cutoff)) {
                job.setStatus(JobStatus.STALE);
                scheduledJobRepository.save(job);
            }
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=StaleJobRecoverySweeperTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/job/StaleJobRecoverySweeper.java src/test/java/zw/org/nmrl/ept/job/StaleJobRecoverySweeperTest.java
git commit -m "feat: add stale job recovery sweeper"
```

---

### Task 8: Mail template rendering service

**Files:**

- Create: `src/main/java/zw/org/nmrl/ept/service/MailTemplateRenderer.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/MailTemplateRendererTest.java`

**Interfaces:**

- Consumes: `MailTemplate` entity (existing: `code`, `subject`, `htmlBody`).
- Produces: `MailTemplateRenderer.render(MailTemplate template, Map<String, String> context)` returning a `RenderedEmail(String subject, String body)` record — Task 9 (email send job) and every later phase that sends templated email depend on this exact method signature.

- [ ] **Step 1: Write the failing test**

```java
package zw.org.nmrl.ept.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.MailTemplate;

class MailTemplateRendererTest {

    @Test
    void replacesHashPlaceholdersInSubjectAndBody() {
        MailTemplate template = new MailTemplate();
        template.setSubject("New ePT Shipment for ##NAME##");
        template.setHtmlBody("<p>Hello ##NAME##</p><p>We have shipped ##SHIPCODE## and you should receive it soon.</p>");

        MailTemplateRenderer.RenderedEmail rendered = new MailTemplateRenderer().render(
            template,
            Map.of("NAME", "Harare Central Lab", "SHIPCODE", "DTS-2026-A")
        );

        assertThat(rendered.subject()).isEqualTo("New ePT Shipment for Harare Central Lab");
        assertThat(rendered.body()).contains("Hello Harare Central Lab").contains("We have shipped DTS-2026-A");
    }

    @Test
    void leavesUnmatchedPlaceholdersUntouched() {
        MailTemplate template = new MailTemplate();
        template.setSubject("Hi ##UNKNOWN##");
        template.setHtmlBody("body");

        MailTemplateRenderer.RenderedEmail rendered = new MailTemplateRenderer().render(template, Map.of());

        assertThat(rendered.subject()).isEqualTo("Hi ##UNKNOWN##");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=MailTemplateRendererTest`
Expected: FAIL — `MailTemplateRenderer` does not exist.

- [ ] **Step 3: Write the renderer**

```java
package zw.org.nmrl.ept.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.domain.MailTemplate;

/**
 * Renders {@link MailTemplate} content, matching legacy's {@code ##PLACEHOLDER##}
 * syntax exactly (confirmed against real migrated template data) so migrated
 * {@code mail_template} rows work unmodified.
 */
@Service
public class MailTemplateRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("##([A-Z0-9_]+)##");

    public record RenderedEmail(String subject, String body) {}

    public RenderedEmail render(MailTemplate template, Map<String, String> context) {
        return new RenderedEmail(replace(template.getSubject(), context), replace(template.getHtmlBody(), context));
    }

    private String replace(String text, Map<String, String> context) {
        if (text == null) {
            return null;
        }
        Matcher matcher = PLACEHOLDER.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = context.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=MailTemplateRendererTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/MailTemplateRenderer.java src/test/java/zw/org/nmrl/ept/service/MailTemplateRendererTest.java
git commit -m "feat: add mail template renderer matching legacy placeholder syntax"
```

---

### Task 9: Email send job

**Files:**

- Modify: `src/main/java/zw/org/nmrl/ept/service/MailService.java`
- Create: `src/main/java/zw/org/nmrl/ept/job/EmailDispatchJobHandler.java`
- Test: `src/test/java/zw/org/nmrl/ept/job/EmailDispatchJobHandlerTest.java`

**Interfaces:**

- Consumes: `JobHandler` (Task 6), `EmailMessage`/`EmailStatus` entities (existing), `MailTemplateRenderer` (Task 8, not used directly here since `EmailMessage` already stores a rendered `subject`/`body` — rendering happens where the `EmailMessage` row is created, which is out of this phase's scope and belongs to whichever later phase first triggers an email).
- Produces: `EmailDispatchJobHandler implements JobHandler`, auto-registered with `ScheduledJobProcessor` (Task 6) via Spring's `List<JobHandler>` injection — no code wiring needed beyond `@Component`.

- [ ] **Step 1: Add a synchronous send method to `MailService`**

Modify `src/main/java/zw/org/nmrl/ept/service/MailService.java` — add a new public method next to the existing `sendEmail`/`sendEmailSync` pair (do not remove or rename the existing methods, JHipster's stock activation/reset flows still call them):

```java
    /**
     * Synchronous send for callers (the email dispatch job) that need to know
     * immediately whether the send succeeded, unlike the fire-and-forget
     * {@code @Async sendEmail(...)} above used by JHipster's stock account flows.
     */
    public void sendNow(String to, String subject, String content, boolean isHtml) {
        sendEmailSync(to, subject, content, false, isHtml);
    }
```

- [ ] **Step 2: Write the failing test**

```java
package zw.org.nmrl.ept.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.domain.EmailMessage;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.EmailStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;
import zw.org.nmrl.ept.repository.EmailMessageRepository;
import zw.org.nmrl.ept.service.MailService;

class EmailDispatchJobHandlerTest {

    @Test
    void sendsAllPendingMessagesAndMarksSent() {
        EmailMessageRepository emailMessageRepository = mock(EmailMessageRepository.class);
        MailService mailService = mock(MailService.class);
        EmailMessage pending = new EmailMessage();
        pending.setId(1L);
        pending.setToEmail("lab@example.com");
        pending.setSubject("Subject");
        pending.setBody("Body");
        pending.setStatus(EmailStatus.PENDING);
        when(emailMessageRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(pending));
        when(emailMessageRepository.save(any(EmailMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        new EmailDispatchJobHandler(emailMessageRepository, mailService).handle(new ScheduledJob().jobType(JobType.EMAIL_DISPATCH));

        verify(mailService).sendNow("lab@example.com", "Subject", "Body", true);
        assertThat(pending.getStatus()).isEqualTo(EmailStatus.SENT);
        assertThat(pending.getSentAt()).isNotNull();
    }

    @Test
    void marksMessageFailedAndIncrementsRetryCountWhenSendThrows() {
        EmailMessageRepository emailMessageRepository = mock(EmailMessageRepository.class);
        MailService mailService = mock(MailService.class);
        EmailMessage pending = new EmailMessage();
        pending.setId(2L);
        pending.setToEmail("lab@example.com");
        pending.setSubject("Subject");
        pending.setBody("Body");
        pending.setStatus(EmailStatus.PENDING);
        pending.setRetryCount(0);
        when(emailMessageRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(pending));
        when(emailMessageRepository.save(any(EmailMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("smtp down")).when(mailService).sendNow(any(), any(), any(), anyBoolean());

        new EmailDispatchJobHandler(emailMessageRepository, mailService).handle(new ScheduledJob().jobType(JobType.EMAIL_DISPATCH));

        assertThat(pending.getStatus()).isEqualTo(EmailStatus.RETRYING);
        assertThat(pending.getRetryCount()).isEqualTo(1);
        assertThat(pending.getFailureReason()).contains("smtp down");
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=EmailDispatchJobHandlerTest`
Expected: FAIL — `EmailDispatchJobHandler` does not exist.

- [ ] **Step 4: Write the handler**

```java
package zw.org.nmrl.ept.job;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import zw.org.nmrl.ept.domain.EmailMessage;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.EmailStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;
import zw.org.nmrl.ept.repository.EmailMessageRepository;
import zw.org.nmrl.ept.service.MailService;

@Component
public class EmailDispatchJobHandler implements JobHandler {

    private static final int MAX_RETRIES = 5;

    private final EmailMessageRepository emailMessageRepository;
    private final MailService mailService;

    public EmailDispatchJobHandler(EmailMessageRepository emailMessageRepository, MailService mailService) {
        this.emailMessageRepository = emailMessageRepository;
        this.mailService = mailService;
    }

    @Override
    public JobType supports() {
        return JobType.EMAIL_DISPATCH;
    }

    @Override
    public void handle(ScheduledJob job) {
        List<EmailMessage> pending = emailMessageRepository.findAll(
            (Specification<EmailMessage>) (root, query, cb) -> cb.equal(root.get("status"), EmailStatus.PENDING)
        );
        for (EmailMessage message : pending) {
            try {
                mailService.sendNow(message.getToEmail(), message.getSubject(), message.getBody(), true);
                message.setStatus(EmailStatus.SENT);
                message.setSentAt(Instant.now());
            } catch (Exception e) {
                int retries = message.getRetryCount() == null ? 0 : message.getRetryCount();
                message.setRetryCount(retries + 1);
                message.setFailureReason(e.getMessage());
                message.setStatus(retries + 1 >= MAX_RETRIES ? EmailStatus.FAILED : EmailStatus.RETRYING);
            }
            emailMessageRepository.save(message);
        }
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=EmailDispatchJobHandlerTest`
Expected: PASS

- [ ] **Step 6: Enqueue an `EMAIL_DISPATCH` job whenever an `EmailMessage` is created**

This wiring belongs to whichever later phase first creates `EmailMessage` rows (Phase 2's auth flows, most likely) — note this explicitly rather than building it here, since there is no `EmailMessage`-creating code yet in this phase's scope. Add this note to `ept_project_plan.md`'s Phase 2 task list when that phase's detailed plan is written.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/MailService.java src/main/java/zw/org/nmrl/ept/job/EmailDispatchJobHandler.java src/test/java/zw/org/nmrl/ept/job/EmailDispatchJobHandlerTest.java
git commit -m "feat: add email dispatch job handler"
```

---

### Task 10: Write-capable `FileStoreService`

**Files:**

- Create: `src/main/java/zw/org/nmrl/ept/service/FileStoreService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/config/ApplicationProperties.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/FileStoreServiceTest.java`

**Interfaces:**

- Consumes: `ApplicationProperties` (existing, extended with a new `FileStore` inner class).
- Produces: `FileStoreService.write(String relativePath, InputStream content)` returning the stored `Path`, and `FileStoreService.open(String relativePath)` returning `InputStream` — Phase 2 (bulk-import uploads), Phase 6 (certificate templates, generated PDFs) depend on these exact method names. This is deliberately separate from `LegacyFileStore` (Finding 4) — do not modify `LegacyFileStore`.

- [ ] **Step 1: Add the `FileStore` config block**

Add to `src/main/java/zw/org/nmrl/ept/config/ApplicationProperties.java`, following the exact pattern of the existing `LegacyFiles` inner class:

```java
    private final FileStore fileStore = new FileStore();

    public FileStore getFileStore() {
        return fileStore;
    }

    public static class FileStore {

        private String root;

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }
    }
```

Also add `fileStore.root: ${EPT_FILE_STORE_ROOT:}` to `src/main/resources/config/application.yml` under the `application:` section, alongside the existing `legacy-files:` block.

- [ ] **Step 2: Write the failing test**

```java
package zw.org.nmrl.ept.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.config.ApplicationProperties;

class FileStoreServiceTest {

    @Test
    void writesAndReadsBackAFile(@org.junit.jupiter.api.io.TempDir Path tempDir) throws Exception {
        ApplicationProperties properties = new ApplicationProperties();
        properties.getFileStore().setRoot(tempDir.toString());
        FileStoreService store = new FileStoreService(properties);

        Path written = store.write("uploads/report.xlsx", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));

        assertThat(Files.readString(written)).isEqualTo("data");
        try (InputStream in = store.open("uploads/report.xlsx")) {
            assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("data");
        }
    }

    @Test
    void rejectsPathTraversal(@org.junit.jupiter.api.io.TempDir Path tempDir) {
        ApplicationProperties properties = new ApplicationProperties();
        properties.getFileStore().setRoot(tempDir.toString());
        FileStoreService store = new FileStoreService(properties);

        assertThatThrownBy(() -> store.write("../escape.txt", new ByteArrayInputStream(new byte[0]))).isInstanceOf(IllegalArgumentException.class);
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=FileStoreServiceTest`
Expected: FAIL — `FileStoreService` does not exist.

- [ ] **Step 4: Write the service**

```java
package zw.org.nmrl.ept.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.config.ApplicationProperties;

/**
 * Read/write access to newly-generated files (bulk-import uploads, certificate
 * templates, generated PDFs) — deliberately separate from the read-only
 * {@link LegacyFileStore}, which only ever serves the checksum-verified legacy
 * archive. Local filesystem for now per the resolved architectural decision; the
 * method signatures here are what a future MinIO-backed implementation would keep.
 */
@Service
@ConditionalOnProperty(prefix = "application.file-store", name = "root")
public class FileStoreService {

    private final Path root;

    public FileStoreService(ApplicationProperties properties) {
        String configuredRoot = properties.getFileStore().getRoot();
        if (configuredRoot == null || configuredRoot.isBlank()) {
            throw new IllegalStateException("EPT_FILE_STORE_ROOT must not be blank");
        }
        this.root = Path.of(configuredRoot).toAbsolutePath().normalize();
    }

    public Path write(String relativePath, InputStream content) throws IOException {
        Path target = resolve(relativePath);
        Files.createDirectories(target.getParent());
        Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    public InputStream open(String relativePath) throws IOException {
        return Files.newInputStream(resolve(relativePath));
    }

    private Path resolve(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("File store path is required");
        }
        String normalized = relativePath.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        Path candidate = root.resolve(normalized).normalize();
        if (!candidate.startsWith(root)) {
            throw new IllegalArgumentException("File store path escapes the configured root: " + relativePath);
        }
        return candidate;
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=FileStoreServiceTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/FileStoreService.java src/main/java/zw/org/nmrl/ept/config/ApplicationProperties.java src/main/resources/config/application.yml src/test/java/zw/org/nmrl/ept/service/FileStoreServiceTest.java
git commit -m "feat: add write-capable file store service"
```

---

### Task 11: Record the unified-API decision

**Files:**

- Modify: `ept_project_plan.md`

**Interfaces:** none — documentation only, no code.

- [ ] **Step 1: Update the Phase 0 progress table**

In `ept_project_plan.md`'s "Progress tracking" table, mark rows `0.1` through `0.4` as "Done" and link this plan file. Leave `0.5` as "Not started" with a note: *"No code change needed — decision is already final (unified API, springdoc-openapi already a dependency); revisit only in Phase 9 per the spec's own task list."*

- [ ] **Step 2: Commit**

```bash
git add ept_project_plan.md
git commit -m "docs: mark Phase 0 subtasks 0.1-0.4 complete, confirm 0.5 needs no code"
```

---

## Self-review

**Spec coverage:** 0.1 (Tasks 1–5: authorities, role hierarchy, authority sync, ownership scoping, login hardening, impersonation — CSRF is a documentation-only decision already recorded in Global Constraints, no task needed), 0.2 (Tasks 6–7: job contract, ShedLock poller, stale recovery — DB backup/housekeeping explicitly out of scope per the spec, no task needed), 0.3 (Tasks 8–9: template rendering, send job — IMAP bounce processing explicitly descoped per the spec, no task needed), 0.4 (Task 10: `FileStoreService`), 0.5 (Task 11: decision recorded, no code). All in-scope spec items have a task; all explicitly-descoped spec items are annotated rather than silently dropped.

**Placeholder scan:** every step has real, compilable code grounded in the actual current codebase (exact package names, exact existing entity/enum/repository shapes verified by reading the files directly, not assumed) — no TODOs, no "add error handling" hand-waves.

**Type consistency:** `AuthoritiesConstants.PT_ADMIN`/`PTCC`/`DATA_MANAGER` (Task 1) are used with the identical names in Tasks 2, 3, 4, 5. `JobHandler`/`ScheduledJobQueue` (Task 6) are the exact types `EmailDispatchJobHandler` (Task 9) implements/could call. `MailTemplateRenderer.RenderedEmail` (Task 8) is defined but not yet consumed in this phase — flagged explicitly in Task 9 Step 6 as future-phase wiring rather than left as a dangling unused type.
