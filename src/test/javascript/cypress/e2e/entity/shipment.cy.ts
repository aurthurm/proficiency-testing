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

describe('Shipment e2e test', () => {
  const shipmentPageUrl = '/shipment';
  const shipmentPageUrlPattern = new RegExp('/shipment(\\?.*)?$');
  let username: string;
  let password: string;
  const shipmentSample = {
    code: 'jaunty besides',
    shipmentDate: '2026-06-02',
    responseDeadline: '2026-06-01T21:29:48.665Z',
    status: 'FINALIZED',
  };

  let shipment;
  let scheme;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/schemes',
      body: { code: 'hungrily quirkily claw', name: 'until quarrel', schemeType: 'VL', modality: 'QUANTITATIVE', status: 'PENDING' },
    }).then(({ body }) => {
      scheme = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/shipments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shipments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shipments/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/shipment-samples', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipment-participant-maps', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/distributions', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/schemes', {
      statusCode: 200,
      body: [scheme],
    });

    cy.intercept('GET', '/api/certificate-batches', {
      statusCode: 200,
      body: [],
    });
  });

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

  afterEach(() => {
    if (scheme) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/schemes/${scheme.id}`,
      }).then(() => {
        scheme = undefined;
      });
    }
  });

  it('Shipments menu should load Shipments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shipment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Shipment').should('exist');
    cy.url().should('match', shipmentPageUrlPattern);
  });

  describe('Shipment page', () => {
    it('should have translated page title', () => {
      cy.visit(shipmentPageUrl);
      cy.getEntityHeading('Shipment').should('not.contain', 'proficiencyTestingApp.shipment.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shipmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Shipment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shipment/new$'));
        cy.getEntityCreateUpdateHeading('Shipment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shipments',
          body: {
            ...shipmentSample,
            scheme,
          },
        }).then(({ body }) => {
          shipment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shipments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/shipments?page=0&size=20>; rel="last",<http://localhost/api/shipments?page=0&size=20>; rel="first"',
              },
              body: [shipment],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(shipmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Shipment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shipment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentPageUrlPattern);
      });

      it('edit button click should load edit Shipment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Shipment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentPageUrlPattern);
      });

      it('edit button click should load edit Shipment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Shipment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentPageUrlPattern);
      });

      it('last delete button click should delete instance of Shipment', () => {
        cy.intercept('GET', '/api/shipments/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('shipment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentPageUrlPattern);

        shipment = undefined;
      });
    });
  });

  describe('new Shipment page', () => {
    beforeEach(() => {
      cy.visit(shipmentPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Shipment');
    });

    it('should create an instance of Shipment', () => {
      cy.get(`[data-cy="code"]`).type('meanwhile behind');
      cy.get(`[data-cy="code"]`).should('have.value', 'meanwhile behind');

      cy.get(`[data-cy="shipmentDate"]`).type('2026-06-02');
      cy.get(`[data-cy="shipmentDate"]`).blur();
      cy.get(`[data-cy="shipmentDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="responseDeadline"]`).type('2026-06-02T07:04');
      cy.get(`[data-cy="responseDeadline"]`).blur();
      cy.get(`[data-cy="responseDeadline"]`).should('have.value', '2026-06-02T07:04');

      cy.get(`[data-cy="responsesOpen"]`).should('not.be.checked');
      cy.get(`[data-cy="responsesOpen"]`).click();
      cy.get(`[data-cy="responsesOpen"]`).should('be.checked');

      cy.get(`[data-cy="autoCloseAtDeadline"]`).should('not.be.checked');
      cy.get(`[data-cy="autoCloseAtDeadline"]`).click();
      cy.get(`[data-cy="autoCloseAtDeadline"]`).should('be.checked');

      cy.get(`[data-cy="allowEditingResponse"]`).should('not.be.checked');
      cy.get(`[data-cy="allowEditingResponse"]`).click();
      cy.get(`[data-cy="allowEditingResponse"]`).should('be.checked');

      cy.get(`[data-cy="issuingAuthority"]`).type('hm productive');
      cy.get(`[data-cy="issuingAuthority"]`).should('have.value', 'hm productive');

      cy.get(`[data-cy="coordinatorName"]`).type('unto cavernous');
      cy.get(`[data-cy="coordinatorName"]`).should('have.value', 'unto cavernous');

      cy.get(`[data-cy="coordinatorEmail"]`).type('v5_cRm@q3i.P+zB');
      cy.get(`[data-cy="coordinatorEmail"]`).should('have.value', 'v5_cRm@q3i.P+zB');

      cy.get(`[data-cy="coordinatorPhone"]`).type('acceptable far');
      cy.get(`[data-cy="coordinatorPhone"]`).should('have.value', 'acceptable far');

      cy.get(`[data-cy="numberOfSamples"]`).type('10978');
      cy.get(`[data-cy="numberOfSamples"]`).should('have.value', '10978');

      cy.get(`[data-cy="maxScore"]`).type('15837');
      cy.get(`[data-cy="maxScore"]`).should('have.value', '15837');

      cy.get(`[data-cy="status"]`).select('QUEUED');

      cy.get(`[data-cy="attributes"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="attributes"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="reportsGeneratedAt"]`).type('2026-06-02T14:48');
      cy.get(`[data-cy="reportsGeneratedAt"]`).blur();
      cy.get(`[data-cy="reportsGeneratedAt"]`).should('have.value', '2026-06-02T14:48');

      cy.get(`[data-cy="finalizedAt"]`).type('2026-06-02T08:55');
      cy.get(`[data-cy="finalizedAt"]`).blur();
      cy.get(`[data-cy="finalizedAt"]`).should('have.value', '2026-06-02T08:55');

      cy.get(`[data-cy="scheme"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shipment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shipmentPageUrlPattern);
    });
  });
});
