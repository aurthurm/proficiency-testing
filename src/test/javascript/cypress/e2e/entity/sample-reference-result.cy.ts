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

describe('SampleReferenceResult e2e test', () => {
  const sampleReferenceResultPageUrl = '/sample-reference-result';
  const sampleReferenceResultPageUrlPattern = new RegExp('/sample-reference-result(\\?.*)?$');
  let username: string;
  let password: string;
  // const sampleReferenceResultSample = {};

  let sampleReferenceResult;
  // let shipmentSample;

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
      body: {"label":"apropos apropos lest","displayOrder":528,"isControl":true,"isMandatory":true,"sampleScore":26655.12,"preparationDate":"2026-06-02"},
    }).then(({ body }) => {
      shipmentSample = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/sample-reference-results+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sample-reference-results').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sample-reference-results/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/assays', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipment-samples', {
      statusCode: 200,
      body: [shipmentSample],
    });

  });
   */

  afterEach(() => {
    if (sampleReferenceResult) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sample-reference-results/${sampleReferenceResult.id}`,
      }).then(() => {
        sampleReferenceResult = undefined;
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
  });
   */

  it('SampleReferenceResults menu should load SampleReferenceResults page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sample-reference-result');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SampleReferenceResult').should('exist');
    cy.url().should('match', sampleReferenceResultPageUrlPattern);
  });

  describe('SampleReferenceResult page', () => {
    it('should have translated page title', () => {
      cy.visit(sampleReferenceResultPageUrl);
      cy.getEntityHeading('SampleReferenceResult').should('not.contain', 'proficiencyTestingApp.sampleReferenceResult.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(sampleReferenceResultPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SampleReferenceResult page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sample-reference-result/new$'));
        cy.getEntityCreateUpdateHeading('SampleReferenceResult');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sampleReferenceResultPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sample-reference-results',
          body: {
            ...sampleReferenceResultSample,
            sample: shipmentSample,
          },
        }).then(({ body }) => {
          sampleReferenceResult = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sample-reference-results+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/sample-reference-results?page=0&size=20>; rel="last",<http://localhost/api/sample-reference-results?page=0&size=20>; rel="first"',
              },
              body: [sampleReferenceResult],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(sampleReferenceResultPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(sampleReferenceResultPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details SampleReferenceResult page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sampleReferenceResult');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sampleReferenceResultPageUrlPattern);
      });

      it('edit button click should load edit SampleReferenceResult page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SampleReferenceResult');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sampleReferenceResultPageUrlPattern);
      });

      it('edit button click should load edit SampleReferenceResult page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SampleReferenceResult');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sampleReferenceResultPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of SampleReferenceResult', () => {
        cy.intercept('GET', '/api/sample-reference-results/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('sampleReferenceResult').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sampleReferenceResultPageUrlPattern);

        sampleReferenceResult = undefined;
      });
    });
  });

  describe('new SampleReferenceResult page', () => {
    beforeEach(() => {
      cy.visit(sampleReferenceResultPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SampleReferenceResult');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of SampleReferenceResult', () => {
      cy.get(`[data-cy="qualitativeResult"]`).type('depend forenenst');
      cy.get(`[data-cy="qualitativeResult"]`).should('have.value', 'depend forenenst');

      cy.get(`[data-cy="quantitativeValue"]`).type('31008.74');
      cy.get(`[data-cy="quantitativeValue"]`).should('have.value', '31008.74');

      cy.get(`[data-cy="unit"]`).type('adrenalin wrongly');
      cy.get(`[data-cy="unit"]`).should('have.value', 'adrenalin wrongly');

      cy.get(`[data-cy="lowerLimit"]`).type('15743.28');
      cy.get(`[data-cy="lowerLimit"]`).should('have.value', '15743.28');

      cy.get(`[data-cy="upperLimit"]`).type('12485.88');
      cy.get(`[data-cy="upperLimit"]`).should('have.value', '12485.88');

      cy.get(`[data-cy="isControlExpected"]`).should('not.be.checked');
      cy.get(`[data-cy="isControlExpected"]`).click();
      cy.get(`[data-cy="isControlExpected"]`).should('be.checked');

      cy.get(`[data-cy="sample"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        sampleReferenceResult = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', sampleReferenceResultPageUrlPattern);
    });
  });
});
