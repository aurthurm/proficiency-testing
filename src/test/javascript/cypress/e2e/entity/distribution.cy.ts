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

describe('Distribution e2e test', () => {
  const distributionPageUrl = '/distribution';
  const distributionPageUrlPattern = new RegExp('/distribution(\\?.*)?$');
  let username: string;
  let password: string;
  const distributionSample = { code: 'inspection hoof', distributionDate: '2026-06-02', status: 'CLOSED' };

  let distribution;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/distributions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/distributions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/distributions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (distribution) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/distributions/${distribution.id}`,
      }).then(() => {
        distribution = undefined;
      });
    }
  });

  it('Distributions menu should load Distributions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('distribution');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Distribution').should('exist');
    cy.url().should('match', distributionPageUrlPattern);
  });

  describe('Distribution page', () => {
    it('should have translated page title', () => {
      cy.visit(distributionPageUrl);
      cy.getEntityHeading('Distribution').should('not.contain', 'proficiencyTestingApp.distribution.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(distributionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Distribution page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/distribution/new$'));
        cy.getEntityCreateUpdateHeading('Distribution');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', distributionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/distributions',
          body: distributionSample,
        }).then(({ body }) => {
          distribution = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/distributions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/distributions?page=0&size=20>; rel="last",<http://localhost/api/distributions?page=0&size=20>; rel="first"',
              },
              body: [distribution],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(distributionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Distribution page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('distribution');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', distributionPageUrlPattern);
      });

      it('edit button click should load edit Distribution page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Distribution');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', distributionPageUrlPattern);
      });

      it('edit button click should load edit Distribution page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Distribution');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', distributionPageUrlPattern);
      });

      it('last delete button click should delete instance of Distribution', () => {
        cy.intercept('GET', '/api/distributions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('distribution').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', distributionPageUrlPattern);

        distribution = undefined;
      });
    });
  });

  describe('new Distribution page', () => {
    beforeEach(() => {
      cy.visit(distributionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Distribution');
    });

    it('should create an instance of Distribution', () => {
      cy.get(`[data-cy="code"]`).type('rigidly');
      cy.get(`[data-cy="code"]`).should('have.value', 'rigidly');

      cy.get(`[data-cy="distributionDate"]`).type('2026-06-01');
      cy.get(`[data-cy="distributionDate"]`).blur();
      cy.get(`[data-cy="distributionDate"]`).should('have.value', '2026-06-01');

      cy.get(`[data-cy="status"]`).select('SHIPPED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        distribution = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', distributionPageUrlPattern);
    });
  });
});
