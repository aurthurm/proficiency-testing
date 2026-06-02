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

describe('ParticipantResult e2e test', () => {
  const participantResultPageUrl = '/participant-result';
  const participantResultPageUrlPattern = new RegExp('/participant-result(\\?.*)?$');
  let username: string;
  let password: string;
  // const participantResultSample = {};

  let participantResult;
  // let shipmentSample;
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
      url: '/api/shipment-samples',
      body: {"label":"ew sun","displayOrder":31179,"isControl":false,"isMandatory":false,"sampleScore":6013.74,"preparationDate":"2026-06-02"},
    }).then(({ body }) => {
      shipmentSample = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/shipment-participant-maps',
      body: {"responseStatus":"NO_RESPONSE","shipmentReceiptDate":"2026-06-02","shipmentTestDate":"2026-06-02","shipmentTestReportDate":"2026-06-02T18:17:16.268Z","submittedAt":"2026-06-02T15:11:25.034Z","evaluatedAt":"2026-06-02T18:31:38.886Z","isExcluded":false,"isResponseLate":true,"isPtTestNotPerformed":true,"ptTestNotPerformedComments":"knottily daily terribly","supervisorApproved":true,"participantSupervisor":"yogurt","userComment":"certification knavishly","shipmentScore":29523.59,"documentationScore":7378.22,"finalResult":"NOT_EVALUATED","failureReason":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","evaluationComment":"fooey unimpressively bah","isFollowup":true,"manualOverride":true,"qcStatus":"PENDING","qcDate":"2026-06-02","qcDoneBy":"paltry tremendously","syncedToMobile":false,"syncedOn":"2026-06-02T12:13:52.815Z"},
    }).then(({ body }) => {
      shipmentParticipantMap = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/participant-results+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/participant-results').as('postEntityRequest');
    cy.intercept('DELETE', '/api/participant-results/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/assays', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/test-kits', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipment-samples', {
      statusCode: 200,
      body: [shipmentSample],
    });

    cy.intercept('GET', '/api/shipment-participant-maps', {
      statusCode: 200,
      body: [shipmentParticipantMap],
    });

  });
   */

  afterEach(() => {
    if (participantResult) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participant-results/${participantResult.id}`,
      }).then(() => {
        participantResult = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (shipmentSample) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-samples/${shipmentSample.id}`,
      }).then(() => {
        shipmentSample = undefined;
      });
    }
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

  it('ParticipantResults menu should load ParticipantResults page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('participant-result');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ParticipantResult').should('exist');
    cy.url().should('match', participantResultPageUrlPattern);
  });

  describe('ParticipantResult page', () => {
    it('should have translated page title', () => {
      cy.visit(participantResultPageUrl);
      cy.getEntityHeading('ParticipantResult').should('not.contain', 'proficiencyTestingApp.participantResult.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(participantResultPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ParticipantResult page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/participant-result/new$'));
        cy.getEntityCreateUpdateHeading('ParticipantResult');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantResultPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/participant-results',
          body: {
            ...participantResultSample,
            sample: shipmentSample,
            shipmentParticipantMap: shipmentParticipantMap,
          },
        }).then(({ body }) => {
          participantResult = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/participant-results+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/participant-results?page=0&size=20>; rel="last",<http://localhost/api/participant-results?page=0&size=20>; rel="first"',
              },
              body: [participantResult],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(participantResultPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(participantResultPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ParticipantResult page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('participantResult');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantResultPageUrlPattern);
      });

      it('edit button click should load edit ParticipantResult page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantResult');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantResultPageUrlPattern);
      });

      it('edit button click should load edit ParticipantResult page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantResult');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantResultPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ParticipantResult', () => {
        cy.intercept('GET', '/api/participant-results/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('participantResult').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantResultPageUrlPattern);

        participantResult = undefined;
      });
    });
  });

  describe('new ParticipantResult page', () => {
    beforeEach(() => {
      cy.visit(participantResultPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ParticipantResult');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ParticipantResult', () => {
      cy.get(`[data-cy="reportedQualitativeResult"]`).type('crossly');
      cy.get(`[data-cy="reportedQualitativeResult"]`).should('have.value', 'crossly');

      cy.get(`[data-cy="reportedQuantitativeValue"]`).type('5597.59');
      cy.get(`[data-cy="reportedQuantitativeValue"]`).should('have.value', '5597.59');

      cy.get(`[data-cy="unit"]`).type('supposing');
      cy.get(`[data-cy="unit"]`).should('have.value', 'supposing');

      cy.get(`[data-cy="lotNumber"]`).type('dearly elderly noisily');
      cy.get(`[data-cy="lotNumber"]`).should('have.value', 'dearly elderly noisily');

      cy.get(`[data-cy="expiryDate"]`).type('2026-06-02');
      cy.get(`[data-cy="expiryDate"]`).blur();
      cy.get(`[data-cy="expiryDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="zScore"]`).type('5097.25');
      cy.get(`[data-cy="zScore"]`).should('have.value', '5097.25');

      cy.get(`[data-cy="calculatedScore"]`).type('7047.97');
      cy.get(`[data-cy="calculatedScore"]`).should('have.value', '7047.97');

      cy.get(`[data-cy="comments"]`).type('circular');
      cy.get(`[data-cy="comments"]`).should('have.value', 'circular');

      cy.get(`[data-cy="sample"]`).select(1);
      cy.get(`[data-cy="shipmentParticipantMap"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        participantResult = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', participantResultPageUrlPattern);
    });
  });
});
