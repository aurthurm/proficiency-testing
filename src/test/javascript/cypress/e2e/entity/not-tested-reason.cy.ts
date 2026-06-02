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

describe('NotTestedReason e2e test', () => {
  const notTestedReasonPageUrl = '/not-tested-reason';
  const notTestedReasonPageUrlPattern = new RegExp('/not-tested-reason(\\?.*)?$');
  let username: string;
  let password: string;
  const notTestedReasonSample = { reason: 'concerning', status: 'ACTIVE' };

  let notTestedReason;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/not-tested-reasons+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/not-tested-reasons').as('postEntityRequest');
    cy.intercept('DELETE', '/api/not-tested-reasons/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (notTestedReason) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/not-tested-reasons/${notTestedReason.id}`,
      }).then(() => {
        notTestedReason = undefined;
      });
    }
  });

  it('NotTestedReasons menu should load NotTestedReasons page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('not-tested-reason');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('NotTestedReason').should('exist');
    cy.url().should('match', notTestedReasonPageUrlPattern);
  });

  describe('NotTestedReason page', () => {
    it('should have translated page title', () => {
      cy.visit(notTestedReasonPageUrl);
      cy.getEntityHeading('NotTestedReason').should('not.contain', 'proficiencyTestingApp.notTestedReason.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notTestedReasonPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create NotTestedReason page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/not-tested-reason/new$'));
        cy.getEntityCreateUpdateHeading('NotTestedReason');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notTestedReasonPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/not-tested-reasons',
          body: notTestedReasonSample,
        }).then(({ body }) => {
          notTestedReason = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/not-tested-reasons+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [notTestedReason],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(notTestedReasonPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details NotTestedReason page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('notTestedReason');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notTestedReasonPageUrlPattern);
      });

      it('edit button click should load edit NotTestedReason page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NotTestedReason');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notTestedReasonPageUrlPattern);
      });

      it('edit button click should load edit NotTestedReason page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NotTestedReason');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notTestedReasonPageUrlPattern);
      });

      it('last delete button click should delete instance of NotTestedReason', () => {
        cy.intercept('GET', '/api/not-tested-reasons/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('notTestedReason').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notTestedReasonPageUrlPattern);

        notTestedReason = undefined;
      });
    });
  });

  describe('new NotTestedReason page', () => {
    beforeEach(() => {
      cy.visit(notTestedReasonPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('NotTestedReason');
    });

    it('should create an instance of NotTestedReason', () => {
      cy.get(`[data-cy="reason"]`).type('destock bah');
      cy.get(`[data-cy="reason"]`).should('have.value', 'destock bah');

      cy.get(`[data-cy="status"]`).select('INACTIVE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        notTestedReason = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', notTestedReasonPageUrlPattern);
    });
  });
});
