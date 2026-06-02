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

describe('CapaRecord e2e test', () => {
  const capaRecordPageUrl = '/capa-record';
  const capaRecordPageUrlPattern = new RegExp('/capa-record(\\?.*)?$');
  let username: string;
  let password: string;
  // const capaRecordSample = {};

  let capaRecord;
  // let shipmentParticipantMap;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/shipment-participant-maps',
      body: {"responseStatus":"LATE","shipmentReceiptDate":"2026-06-02","shipmentTestDate":"2026-06-02","shipmentTestReportDate":"2026-06-02T13:55:24.097Z","submittedAt":"2026-06-02T07:41:16.359Z","evaluatedAt":"2026-06-02T03:20:07.459Z","isExcluded":false,"isResponseLate":true,"isPtTestNotPerformed":false,"ptTestNotPerformedComments":"brr inexperienced","supervisorApproved":true,"participantSupervisor":"huff but","userComment":"wilderness in","shipmentScore":5174.6,"documentationScore":11473.2,"finalResult":"NOT_EVALUATED","failureReason":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","evaluationComment":"napkin vice deselect","isFollowup":true,"manualOverride":false,"qcStatus":"FAILED","qcDate":"2026-06-02","qcDoneBy":"zowie peppery","syncedToMobile":false,"syncedOn":"2026-06-02T01:25:25.700Z"},
    }).then(({ body }) => {
      shipmentParticipantMap = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/capa-records+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/capa-records').as('postEntityRequest');
    cy.intercept('DELETE', '/api/capa-records/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corrective-actions', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipment-participant-maps', {
      statusCode: 200,
      body: [shipmentParticipantMap],
    });

  });
   */

  afterEach(() => {
    if (capaRecord) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/capa-records/${capaRecord.id}`,
      }).then(() => {
        capaRecord = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (shipmentParticipantMap) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-participant-maps/${shipmentParticipantMap.id}`,
      }).then(() => {
        shipmentParticipantMap = undefined;
      });
    }
  });
   */

  it('CapaRecords menu should load CapaRecords page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('capa-record');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CapaRecord').should('exist');
    cy.url().should('match', capaRecordPageUrlPattern);
  });

  describe('CapaRecord page', () => {
    it('should have translated page title', () => {
      cy.visit(capaRecordPageUrl);
      cy.getEntityHeading('CapaRecord').should('not.contain', 'proficiencyTestingApp.capaRecord.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(capaRecordPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CapaRecord page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/capa-record/new$'));
        cy.getEntityCreateUpdateHeading('CapaRecord');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', capaRecordPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/capa-records',
          body: {
            ...capaRecordSample,
            shipmentParticipantMap: shipmentParticipantMap,
          },
        }).then(({ body }) => {
          capaRecord = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/capa-records+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/capa-records?page=0&size=20>; rel="last",<http://localhost/api/capa-records?page=0&size=20>; rel="first"',
              },
              body: [capaRecord],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(capaRecordPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(capaRecordPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details CapaRecord page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('capaRecord');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', capaRecordPageUrlPattern);
      });

      it('edit button click should load edit CapaRecord page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CapaRecord');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', capaRecordPageUrlPattern);
      });

      it('edit button click should load edit CapaRecord page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CapaRecord');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', capaRecordPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of CapaRecord', () => {
        cy.intercept('GET', '/api/capa-records/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('capaRecord').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', capaRecordPageUrlPattern);

        capaRecord = undefined;
      });
    });
  });

  describe('new CapaRecord page', () => {
    beforeEach(() => {
      cy.visit(capaRecordPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CapaRecord');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of CapaRecord', () => {
      cy.get(`[data-cy="rootCause"]`).type('chilly vulgarise clinking');
      cy.get(`[data-cy="rootCause"]`).should('have.value', 'chilly vulgarise clinking');

      cy.get(`[data-cy="actionTaken"]`).type('upon splosh till');
      cy.get(`[data-cy="actionTaken"]`).should('have.value', 'upon splosh till');

      cy.get(`[data-cy="actionDate"]`).type('2026-06-02');
      cy.get(`[data-cy="actionDate"]`).blur();
      cy.get(`[data-cy="actionDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="followUpDate"]`).type('2026-06-02');
      cy.get(`[data-cy="followUpDate"]`).blur();
      cy.get(`[data-cy="followUpDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="shipmentParticipantMap"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        capaRecord = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', capaRecordPageUrlPattern);
    });
  });
});
