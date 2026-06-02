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

describe('Scheme e2e test', () => {
  const schemePageUrl = '/scheme';
  const schemePageUrlPattern = new RegExp('/scheme(\\?.*)?$');
  let username: string;
  let password: string;
  const schemeSample = {
    code: 'kissingly unnecessarily',
    name: 'yum behind',
    schemeType: 'GENERIC',
    modality: 'QUANTITATIVE',
    status: 'INACTIVE',
  };

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
    cy.intercept('GET', '/api/schemes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/schemes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/schemes/*').as('deleteEntityRequest');
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

  it('Schemes menu should load Schemes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('scheme');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Scheme').should('exist');
    cy.url().should('match', schemePageUrlPattern);
  });

  describe('Scheme page', () => {
    it('should have translated page title', () => {
      cy.visit(schemePageUrl);
      cy.getEntityHeading('Scheme').should('not.contain', 'proficiencyTestingApp.scheme.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(schemePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Scheme page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/scheme/new$'));
        cy.getEntityCreateUpdateHeading('Scheme');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/schemes',
          body: schemeSample,
        }).then(({ body }) => {
          scheme = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/schemes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/schemes?page=0&size=20>; rel="last",<http://localhost/api/schemes?page=0&size=20>; rel="first"',
              },
              body: [scheme],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(schemePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Scheme page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('scheme');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemePageUrlPattern);
      });

      it('edit button click should load edit Scheme page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Scheme');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemePageUrlPattern);
      });

      it('edit button click should load edit Scheme page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Scheme');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemePageUrlPattern);
      });

      it('last delete button click should delete instance of Scheme', () => {
        cy.intercept('GET', '/api/schemes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('scheme').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemePageUrlPattern);

        scheme = undefined;
      });
    });
  });

  describe('new Scheme page', () => {
    beforeEach(() => {
      cy.visit(schemePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Scheme');
    });

    it('should create an instance of Scheme', () => {
      cy.get(`[data-cy="code"]`).type('scrape');
      cy.get(`[data-cy="code"]`).should('have.value', 'scrape');

      cy.get(`[data-cy="name"]`).type('split for');
      cy.get(`[data-cy="name"]`).should('have.value', 'split for');

      cy.get(`[data-cy="schemeType"]`).select('GENERIC');

      cy.get(`[data-cy="modality"]`).select('QUANTITATIVE');

      cy.get(`[data-cy="status"]`).select('INACTIVE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        scheme = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', schemePageUrlPattern);
    });
  });
});
