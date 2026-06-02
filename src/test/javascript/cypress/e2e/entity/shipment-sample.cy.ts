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

describe('ShipmentSample e2e test', () => {
  const shipmentSamplePageUrl = '/shipment-sample';
  const shipmentSamplePageUrlPattern = new RegExp('/shipment-sample(\\?.*)?$');
  let username: string;
  let password: string;
  // const shipmentSampleSample = {"label":"partially"};

  let shipmentSample;
  // let shipment;

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
      url: '/api/shipments',
      body: {"code":"gaseous notwithstanding","shipmentDate":"2026-06-01","responseDeadline":"2026-06-01T21:59:29.644Z","responsesOpen":false,"autoCloseAtDeadline":true,"allowEditingResponse":true,"issuingAuthority":"within","coordinatorName":"the yippee boastfully","coordinatorEmail":"V!u]F@{UHGx1.Ps2","coordinatorPhone":"grandiose fiercely","numberOfSamples":27940,"maxScore":7644,"status":"FINALIZED","attributes":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","reportsGeneratedAt":"2026-06-02T04:18:06.584Z","finalizedAt":"2026-06-02T04:37:27.892Z"},
    }).then(({ body }) => {
      shipment = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/shipment-samples+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shipment-samples').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shipment-samples/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/sample-reference-results', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/participant-results', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [shipment],
    });

  });
   */

  afterEach(() => {
    if (shipmentSample) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-samples/${shipmentSample.id}`,
      }).then(() => {
        shipmentSample = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (shipment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipments/${shipment.id}`,
      }).then(() => {
        shipment = undefined;
      });
    }
  });
   */

  it('ShipmentSamples menu should load ShipmentSamples page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shipment-sample');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShipmentSample').should('exist');
    cy.url().should('match', shipmentSamplePageUrlPattern);
  });

  describe('ShipmentSample page', () => {
    it('should have translated page title', () => {
      cy.visit(shipmentSamplePageUrl);
      cy.getEntityHeading('ShipmentSample').should('not.contain', 'proficiencyTestingApp.shipmentSample.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shipmentSamplePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShipmentSample page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shipment-sample/new$'));
        cy.getEntityCreateUpdateHeading('ShipmentSample');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentSamplePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shipment-samples',
          body: {
            ...shipmentSampleSample,
            shipment: shipment,
          },
        }).then(({ body }) => {
          shipmentSample = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shipment-samples+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/shipment-samples?page=0&size=20>; rel="last",<http://localhost/api/shipment-samples?page=0&size=20>; rel="first"',
              },
              body: [shipmentSample],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(shipmentSamplePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(shipmentSamplePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ShipmentSample page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shipmentSample');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentSamplePageUrlPattern);
      });

      it('edit button click should load edit ShipmentSample page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentSample');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentSamplePageUrlPattern);
      });

      it('edit button click should load edit ShipmentSample page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentSample');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentSamplePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ShipmentSample', () => {
        cy.intercept('GET', '/api/shipment-samples/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('shipmentSample').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentSamplePageUrlPattern);

        shipmentSample = undefined;
      });
    });
  });

  describe('new ShipmentSample page', () => {
    beforeEach(() => {
      cy.visit(shipmentSamplePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShipmentSample');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ShipmentSample', () => {
      cy.get(`[data-cy="label"]`).type('meh brr unlike');
      cy.get(`[data-cy="label"]`).should('have.value', 'meh brr unlike');

      cy.get(`[data-cy="displayOrder"]`).type('22420');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '22420');

      cy.get(`[data-cy="isControl"]`).should('not.be.checked');
      cy.get(`[data-cy="isControl"]`).click();
      cy.get(`[data-cy="isControl"]`).should('be.checked');

      cy.get(`[data-cy="isMandatory"]`).should('not.be.checked');
      cy.get(`[data-cy="isMandatory"]`).click();
      cy.get(`[data-cy="isMandatory"]`).should('be.checked');

      cy.get(`[data-cy="sampleScore"]`).type('14035.45');
      cy.get(`[data-cy="sampleScore"]`).should('have.value', '14035.45');

      cy.get(`[data-cy="preparationDate"]`).type('2026-06-02');
      cy.get(`[data-cy="preparationDate"]`).blur();
      cy.get(`[data-cy="preparationDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="shipment"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shipmentSample = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shipmentSamplePageUrlPattern);
    });
  });
});
