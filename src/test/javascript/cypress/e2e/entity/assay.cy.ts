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

describe('Assay e2e test', () => {
  const assayPageUrl = '/assay';
  const assayPageUrlPattern = new RegExp('/assay(\\?.*)?$');
  let username: string;
  let password: string;
  const assaySample = { name: 'supposing tuxedo stranger', assayType: 'COVID19_GENE', status: 'PENDING' };

  let assay;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/assays+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/assays').as('postEntityRequest');
    cy.intercept('DELETE', '/api/assays/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (assay) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/assays/${assay.id}`,
      }).then(() => {
        assay = undefined;
      });
    }
  });

  it('Assays menu should load Assays page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('assay');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Assay').should('exist');
    cy.url().should('match', assayPageUrlPattern);
  });

  describe('Assay page', () => {
    it('should have translated page title', () => {
      cy.visit(assayPageUrl);
      cy.getEntityHeading('Assay').should('not.contain', 'proficiencyTestingApp.assay.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(assayPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Assay page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/assay/new$'));
        cy.getEntityCreateUpdateHeading('Assay');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assayPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/assays',
          body: assaySample,
        }).then(({ body }) => {
          assay = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/assays+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/assays?page=0&size=20>; rel="last",<http://localhost/api/assays?page=0&size=20>; rel="first"',
              },
              body: [assay],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(assayPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Assay page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('assay');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assayPageUrlPattern);
      });

      it('edit button click should load edit Assay page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Assay');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assayPageUrlPattern);
      });

      it('edit button click should load edit Assay page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Assay');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assayPageUrlPattern);
      });

      it('last delete button click should delete instance of Assay', () => {
        cy.intercept('GET', '/api/assays/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('assay').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assayPageUrlPattern);

        assay = undefined;
      });
    });
  });

  describe('new Assay page', () => {
    beforeEach(() => {
      cy.visit(assayPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Assay');
    });

    it('should create an instance of Assay', () => {
      cy.get(`[data-cy="name"]`).type('terribly orientate sturdy');
      cy.get(`[data-cy="name"]`).should('have.value', 'terribly orientate sturdy');

      cy.get(`[data-cy="assayType"]`).select('EID_EXTRACTION_ASSAY');

      cy.get(`[data-cy="manufacturer"]`).type('politely');
      cy.get(`[data-cy="manufacturer"]`).should('have.value', 'politely');

      cy.get(`[data-cy="status"]`).select('ACTIVE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        assay = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', assayPageUrlPattern);
    });
  });
});
