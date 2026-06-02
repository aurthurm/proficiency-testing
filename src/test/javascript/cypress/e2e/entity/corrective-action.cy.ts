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

describe('CorrectiveAction e2e test', () => {
  const correctiveActionPageUrl = '/corrective-action';
  const correctiveActionPageUrlPattern = new RegExp('/corrective-action(\\?.*)?$');
  let username: string;
  let password: string;
  const correctiveActionSample = { title: 'as undergo' };

  let correctiveAction;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/corrective-actions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/corrective-actions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/corrective-actions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (correctiveAction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/corrective-actions/${correctiveAction.id}`,
      }).then(() => {
        correctiveAction = undefined;
      });
    }
  });

  it('CorrectiveActions menu should load CorrectiveActions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('corrective-action');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CorrectiveAction').should('exist');
    cy.url().should('match', correctiveActionPageUrlPattern);
  });

  describe('CorrectiveAction page', () => {
    it('should have translated page title', () => {
      cy.visit(correctiveActionPageUrl);
      cy.getEntityHeading('CorrectiveAction').should('not.contain', 'proficiencyTestingApp.correctiveAction.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(correctiveActionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CorrectiveAction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/corrective-action/new$'));
        cy.getEntityCreateUpdateHeading('CorrectiveAction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', correctiveActionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/corrective-actions',
          body: correctiveActionSample,
        }).then(({ body }) => {
          correctiveAction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/corrective-actions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [correctiveAction],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(correctiveActionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CorrectiveAction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('correctiveAction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', correctiveActionPageUrlPattern);
      });

      it('edit button click should load edit CorrectiveAction page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CorrectiveAction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', correctiveActionPageUrlPattern);
      });

      it('edit button click should load edit CorrectiveAction page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CorrectiveAction');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', correctiveActionPageUrlPattern);
      });

      it('last delete button click should delete instance of CorrectiveAction', () => {
        cy.intercept('GET', '/api/corrective-actions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('correctiveAction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', correctiveActionPageUrlPattern);

        correctiveAction = undefined;
      });
    });
  });

  describe('new CorrectiveAction page', () => {
    beforeEach(() => {
      cy.visit(correctiveActionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CorrectiveAction');
    });

    it('should create an instance of CorrectiveAction', () => {
      cy.get(`[data-cy="title"]`).type('ponder lack exploration');
      cy.get(`[data-cy="title"]`).should('have.value', 'ponder lack exploration');

      cy.get(`[data-cy="description"]`).type('scrabble');
      cy.get(`[data-cy="description"]`).should('have.value', 'scrabble');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        correctiveAction = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', correctiveActionPageUrlPattern);
    });
  });
});
