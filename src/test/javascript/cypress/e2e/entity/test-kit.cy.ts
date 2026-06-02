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

describe('TestKit e2e test', () => {
  const testKitPageUrl = '/test-kit';
  const testKitPageUrlPattern = new RegExp('/test-kit(\\?.*)?$');
  let username: string;
  let password: string;
  const testKitSample = { name: 'spherical volleyball', status: 'PENDING' };

  let testKit;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/test-kits+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/test-kits').as('postEntityRequest');
    cy.intercept('DELETE', '/api/test-kits/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (testKit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/test-kits/${testKit.id}`,
      }).then(() => {
        testKit = undefined;
      });
    }
  });

  it('TestKits menu should load TestKits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('test-kit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TestKit').should('exist');
    cy.url().should('match', testKitPageUrlPattern);
  });

  describe('TestKit page', () => {
    it('should have translated page title', () => {
      cy.visit(testKitPageUrl);
      cy.getEntityHeading('TestKit').should('not.contain', 'proficiencyTestingApp.testKit.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(testKitPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TestKit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/test-kit/new$'));
        cy.getEntityCreateUpdateHeading('TestKit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testKitPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/test-kits',
          body: testKitSample,
        }).then(({ body }) => {
          testKit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/test-kits+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/test-kits?page=0&size=20>; rel="last",<http://localhost/api/test-kits?page=0&size=20>; rel="first"',
              },
              body: [testKit],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(testKitPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TestKit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('testKit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testKitPageUrlPattern);
      });

      it('edit button click should load edit TestKit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TestKit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testKitPageUrlPattern);
      });

      it('edit button click should load edit TestKit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TestKit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testKitPageUrlPattern);
      });

      it('last delete button click should delete instance of TestKit', () => {
        cy.intercept('GET', '/api/test-kits/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('testKit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testKitPageUrlPattern);

        testKit = undefined;
      });
    });
  });

  describe('new TestKit page', () => {
    beforeEach(() => {
      cy.visit(testKitPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TestKit');
    });

    it('should create an instance of TestKit', () => {
      cy.get(`[data-cy="name"]`).type('famously who happy-go-lucky');
      cy.get(`[data-cy="name"]`).should('have.value', 'famously who happy-go-lucky');

      cy.get(`[data-cy="manufacturer"]`).type('monumental');
      cy.get(`[data-cy="manufacturer"]`).should('have.value', 'monumental');

      cy.get(`[data-cy="status"]`).select('INACTIVE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        testKit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', testKitPageUrlPattern);
    });
  });
});
