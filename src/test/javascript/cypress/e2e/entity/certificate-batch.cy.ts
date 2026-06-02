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

describe('CertificateBatch e2e test', () => {
  const certificateBatchPageUrl = '/certificate-batch';
  const certificateBatchPageUrlPattern = new RegExp('/certificate-batch(\\?.*)?$');
  let username: string;
  let password: string;
  const certificateBatchSample = { name: 'gown', status: 'PENDING' };

  let certificateBatch;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/certificate-batches+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/certificate-batches').as('postEntityRequest');
    cy.intercept('DELETE', '/api/certificate-batches/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (certificateBatch) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/certificate-batches/${certificateBatch.id}`,
      }).then(() => {
        certificateBatch = undefined;
      });
    }
  });

  it('CertificateBatches menu should load CertificateBatches page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('certificate-batch');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CertificateBatch').should('exist');
    cy.url().should('match', certificateBatchPageUrlPattern);
  });

  describe('CertificateBatch page', () => {
    it('should have translated page title', () => {
      cy.visit(certificateBatchPageUrl);
      cy.getEntityHeading('CertificateBatch').should('not.contain', 'proficiencyTestingApp.certificateBatch.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(certificateBatchPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CertificateBatch page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/certificate-batch/new$'));
        cy.getEntityCreateUpdateHeading('CertificateBatch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateBatchPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/certificate-batches',
          body: certificateBatchSample,
        }).then(({ body }) => {
          certificateBatch = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/certificate-batches+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/certificate-batches?page=0&size=20>; rel="last",<http://localhost/api/certificate-batches?page=0&size=20>; rel="first"',
              },
              body: [certificateBatch],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(certificateBatchPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CertificateBatch page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('certificateBatch');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateBatchPageUrlPattern);
      });

      it('edit button click should load edit CertificateBatch page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CertificateBatch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateBatchPageUrlPattern);
      });

      it('edit button click should load edit CertificateBatch page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CertificateBatch');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateBatchPageUrlPattern);
      });

      it('last delete button click should delete instance of CertificateBatch', () => {
        cy.intercept('GET', '/api/certificate-batches/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('certificateBatch').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateBatchPageUrlPattern);

        certificateBatch = undefined;
      });
    });
  });

  describe('new CertificateBatch page', () => {
    beforeEach(() => {
      cy.visit(certificateBatchPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CertificateBatch');
    });

    it('should create an instance of CertificateBatch', () => {
      cy.get(`[data-cy="name"]`).type('for savour under');
      cy.get(`[data-cy="name"]`).should('have.value', 'for savour under');

      cy.get(`[data-cy="status"]`).select('DISTRIBUTING');

      cy.get(`[data-cy="excellenceCount"]`).type('5942');
      cy.get(`[data-cy="excellenceCount"]`).should('have.value', '5942');

      cy.get(`[data-cy="participationCount"]`).type('14452');
      cy.get(`[data-cy="participationCount"]`).should('have.value', '14452');

      cy.get(`[data-cy="skippedCount"]`).type('7878');
      cy.get(`[data-cy="skippedCount"]`).should('have.value', '7878');

      cy.get(`[data-cy="downloadUrl"]`).type('coliseum unearth silent');
      cy.get(`[data-cy="downloadUrl"]`).should('have.value', 'coliseum unearth silent');

      cy.get(`[data-cy="errorMessage"]`).type('provided frizzy');
      cy.get(`[data-cy="errorMessage"]`).should('have.value', 'provided frizzy');

      cy.get(`[data-cy="approvedBy"]`).type('deficient blue');
      cy.get(`[data-cy="approvedBy"]`).should('have.value', 'deficient blue');

      cy.get(`[data-cy="approvedOn"]`).type('2026-06-02T15:55');
      cy.get(`[data-cy="approvedOn"]`).blur();
      cy.get(`[data-cy="approvedOn"]`).should('have.value', '2026-06-02T15:55');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        certificateBatch = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', certificateBatchPageUrlPattern);
    });
  });
});
