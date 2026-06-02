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

describe('ApiRequestLog e2e test', () => {
  const apiRequestLogPageUrl = '/api-request-log';
  const apiRequestLogPageUrlPattern = new RegExp('/api-request-log(\\?.*)?$');
  let username: string;
  let password: string;
  const apiRequestLogSample = { transactionId: 'dividend incidentally' };

  let apiRequestLog;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/api-request-logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/api-request-logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/api-request-logs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (apiRequestLog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/api-request-logs/${apiRequestLog.id}`,
      }).then(() => {
        apiRequestLog = undefined;
      });
    }
  });

  it('ApiRequestLogs menu should load ApiRequestLogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('api-request-log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ApiRequestLog').should('exist');
    cy.url().should('match', apiRequestLogPageUrlPattern);
  });

  describe('ApiRequestLog page', () => {
    it('should have translated page title', () => {
      cy.visit(apiRequestLogPageUrl);
      cy.getEntityHeading('ApiRequestLog').should('not.contain', 'proficiencyTestingApp.apiRequestLog.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(apiRequestLogPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ApiRequestLog page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/api-request-log/new$'));
        cy.getEntityCreateUpdateHeading('ApiRequestLog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiRequestLogPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/api-request-logs',
          body: apiRequestLogSample,
        }).then(({ body }) => {
          apiRequestLog = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/api-request-logs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/api-request-logs?page=0&size=20>; rel="last",<http://localhost/api/api-request-logs?page=0&size=20>; rel="first"',
              },
              body: [apiRequestLog],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(apiRequestLogPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ApiRequestLog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('apiRequestLog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiRequestLogPageUrlPattern);
      });

      it('edit button click should load edit ApiRequestLog page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ApiRequestLog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiRequestLogPageUrlPattern);
      });

      it('edit button click should load edit ApiRequestLog page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ApiRequestLog');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiRequestLogPageUrlPattern);
      });

      it('last delete button click should delete instance of ApiRequestLog', () => {
        cy.intercept('GET', '/api/api-request-logs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('apiRequestLog').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiRequestLogPageUrlPattern);

        apiRequestLog = undefined;
      });
    });
  });

  describe('new ApiRequestLog page', () => {
    beforeEach(() => {
      cy.visit(apiRequestLogPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ApiRequestLog');
    });

    it('should create an instance of ApiRequestLog', () => {
      cy.get(`[data-cy="transactionId"]`).type('prejudge wash premise');
      cy.get(`[data-cy="transactionId"]`).should('have.value', 'prejudge wash premise');

      cy.get(`[data-cy="requestedBy"]`).type('platypus');
      cy.get(`[data-cy="requestedBy"]`).should('have.value', 'platypus');

      cy.get(`[data-cy="requestedOn"]`).type('2026-06-02T16:40');
      cy.get(`[data-cy="requestedOn"]`).blur();
      cy.get(`[data-cy="requestedOn"]`).should('have.value', '2026-06-02T16:40');

      cy.get(`[data-cy="numberOfRecords"]`).type('30801');
      cy.get(`[data-cy="numberOfRecords"]`).should('have.value', '30801');

      cy.get(`[data-cy="requestType"]`).type('mmm');
      cy.get(`[data-cy="requestType"]`).should('have.value', 'mmm');

      cy.get(`[data-cy="testType"]`).type('honorable against');
      cy.get(`[data-cy="testType"]`).should('have.value', 'honorable against');

      cy.get(`[data-cy="apiUrl"]`).type('hm tricky apud');
      cy.get(`[data-cy="apiUrl"]`).should('have.value', 'hm tricky apud');

      cy.get(`[data-cy="dataFormat"]`).type('stool meh');
      cy.get(`[data-cy="dataFormat"]`).should('have.value', 'stool meh');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        apiRequestLog = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', apiRequestLogPageUrlPattern);
    });
  });
});
