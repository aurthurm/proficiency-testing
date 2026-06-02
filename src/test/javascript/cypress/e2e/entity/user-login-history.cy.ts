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

describe('UserLoginHistory e2e test', () => {
  const userLoginHistoryPageUrl = '/user-login-history';
  const userLoginHistoryPageUrlPattern = new RegExp('/user-login-history(\\?.*)?$');
  let username: string;
  let password: string;
  const userLoginHistorySample = { loginStatus: 'SUCCESS', attemptedAt: '2026-06-02T04:04:35.447Z' };

  let userLoginHistory;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/user-login-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-login-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-login-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userLoginHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-login-histories/${userLoginHistory.id}`,
      }).then(() => {
        userLoginHistory = undefined;
      });
    }
  });

  it('UserLoginHistories menu should load UserLoginHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-login-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserLoginHistory').should('exist');
    cy.url().should('match', userLoginHistoryPageUrlPattern);
  });

  describe('UserLoginHistory page', () => {
    it('should have translated page title', () => {
      cy.visit(userLoginHistoryPageUrl);
      cy.getEntityHeading('UserLoginHistory').should('not.contain', 'proficiencyTestingApp.userLoginHistory.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userLoginHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserLoginHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-login-history/new$'));
        cy.getEntityCreateUpdateHeading('UserLoginHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userLoginHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-login-histories',
          body: userLoginHistorySample,
        }).then(({ body }) => {
          userLoginHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-login-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/user-login-histories?page=0&size=20>; rel="last",<http://localhost/api/user-login-histories?page=0&size=20>; rel="first"',
              },
              body: [userLoginHistory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(userLoginHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserLoginHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userLoginHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userLoginHistoryPageUrlPattern);
      });

      it('edit button click should load edit UserLoginHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserLoginHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userLoginHistoryPageUrlPattern);
      });

      it('edit button click should load edit UserLoginHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserLoginHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userLoginHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of UserLoginHistory', () => {
        cy.intercept('GET', '/api/user-login-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('userLoginHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userLoginHistoryPageUrlPattern);

        userLoginHistory = undefined;
      });
    });
  });

  describe('new UserLoginHistory page', () => {
    beforeEach(() => {
      cy.visit(userLoginHistoryPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserLoginHistory');
    });

    it('should create an instance of UserLoginHistory', () => {
      cy.get(`[data-cy="loginId"]`).type('intently ah');
      cy.get(`[data-cy="loginId"]`).should('have.value', 'intently ah');

      cy.get(`[data-cy="loginContext"]`).type('ruddy promptly');
      cy.get(`[data-cy="loginContext"]`).should('have.value', 'ruddy promptly');

      cy.get(`[data-cy="loginStatus"]`).select('SUCCESS');

      cy.get(`[data-cy="attemptedAt"]`).type('2026-06-02T10:29');
      cy.get(`[data-cy="attemptedAt"]`).blur();
      cy.get(`[data-cy="attemptedAt"]`).should('have.value', '2026-06-02T10:29');

      cy.get(`[data-cy="ipAddress"]`).type('alongside fervently');
      cy.get(`[data-cy="ipAddress"]`).should('have.value', 'alongside fervently');

      cy.get(`[data-cy="browser"]`).type('hard-to-find easily');
      cy.get(`[data-cy="browser"]`).should('have.value', 'hard-to-find easily');

      cy.get(`[data-cy="operatingSystem"]`).type('likewise fictionalize');
      cy.get(`[data-cy="operatingSystem"]`).should('have.value', 'likewise fictionalize');

      cy.get(`[data-cy="sessionHash"]`).type('howl progress');
      cy.get(`[data-cy="sessionHash"]`).should('have.value', 'howl progress');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        userLoginHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userLoginHistoryPageUrlPattern);
    });
  });
});
