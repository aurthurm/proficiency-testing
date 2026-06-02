import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('AuditLog e2e test', () => {
  const auditLogPageUrl = '/audit-log';
  const auditLogPageUrlPattern = new RegExp('/audit-log(\\?.*)?$');
  let username: string;
  let password: string;
  const auditLogSample = { action: 'IMPERSONATE', statement: 'uh-huh', performedOn: '2026-06-02T16:29:14.653Z' };

  let auditLog;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/audit-logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/audit-logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/audit-logs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (auditLog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/audit-logs/${auditLog.id}`,
      }).then(() => {
        auditLog = undefined;
      });
    }
  });

  it('AuditLogs menu should load AuditLogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AuditLog').should('exist');
    cy.url().should('match', auditLogPageUrlPattern);
  });

  describe('AuditLog page', () => {
    it('should have translated page title', () => {
      cy.visit(auditLogPageUrl);
      cy.getEntityHeading('AuditLog').should('not.contain', 'proficiencyTestingApp.auditLog.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(auditLogPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AuditLog page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/audit-log/new$'));
        cy.getEntityCreateUpdateHeading('AuditLog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditLogPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/audit-logs',
          body: auditLogSample,
        }).then(({ body }) => {
          auditLog = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/audit-logs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/audit-logs?page=0&size=20>; rel="last",<http://localhost/api/audit-logs?page=0&size=20>; rel="first"',
              },
              body: [auditLog],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(auditLogPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AuditLog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('auditLog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditLogPageUrlPattern);
      });

      it('edit button click should load edit AuditLog page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AuditLog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditLogPageUrlPattern);
      });

      it('edit button click should load edit AuditLog page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AuditLog');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditLogPageUrlPattern);
      });

      it('last delete button click should delete instance of AuditLog', () => {
        cy.intercept('GET', '/api/audit-logs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('auditLog').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditLogPageUrlPattern);

        auditLog = undefined;
      });
    });
  });

  describe('new AuditLog page', () => {
    beforeEach(() => {
      cy.visit(auditLogPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AuditLog');
    });

    it('should create an instance of AuditLog', () => {
      cy.get(`[data-cy="action"]`).select('FINALIZE');

      cy.get(`[data-cy="statement"]`).type('perfection porter polite');
      cy.get(`[data-cy="statement"]`).should('have.value', 'perfection porter polite');

      cy.get(`[data-cy="performedBy"]`).type('task irresponsible');
      cy.get(`[data-cy="performedBy"]`).should('have.value', 'task irresponsible');

      cy.get(`[data-cy="performedByRole"]`).type('meh');
      cy.get(`[data-cy="performedByRole"]`).should('have.value', 'meh');

      cy.get(`[data-cy="performedOn"]`).type('2026-06-02T15:55');
      cy.get(`[data-cy="performedOn"]`).blur();
      cy.get(`[data-cy="performedOn"]`).should('have.value', '2026-06-02T15:55');

      cy.get(`[data-cy="ipAddress"]`).type('fooey');
      cy.get(`[data-cy="ipAddress"]`).should('have.value', 'fooey');

      cy.get(`[data-cy="userAgent"]`).type('uh-huh without');
      cy.get(`[data-cy="userAgent"]`).should('have.value', 'uh-huh without');

      cy.get(`[data-cy="sessionHash"]`).type('burdensome blah');
      cy.get(`[data-cy="sessionHash"]`).should('have.value', 'burdensome blah');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        auditLog = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', auditLogPageUrlPattern);
    });
  });
});
