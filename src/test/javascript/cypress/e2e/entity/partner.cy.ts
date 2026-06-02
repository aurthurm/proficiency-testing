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

describe('Partner e2e test', () => {
  const partnerPageUrl = '/partner';
  const partnerPageUrlPattern = new RegExp('/partner(\\?.*)?$');
  let username: string;
  let password: string;
  const partnerSample = { name: 'yahoo contravene', status: 'DRAFT' };

  let partner;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/partners+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/partners').as('postEntityRequest');
    cy.intercept('DELETE', '/api/partners/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (partner) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/partners/${partner.id}`,
      }).then(() => {
        partner = undefined;
      });
    }
  });

  it('Partners menu should load Partners page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('partner');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Partner').should('exist');
    cy.url().should('match', partnerPageUrlPattern);
  });

  describe('Partner page', () => {
    it('should have translated page title', () => {
      cy.visit(partnerPageUrl);
      cy.getEntityHeading('Partner').should('not.contain', 'proficiencyTestingApp.partner.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(partnerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Partner page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/partner/new$'));
        cy.getEntityCreateUpdateHeading('Partner');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', partnerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/partners',
          body: partnerSample,
        }).then(({ body }) => {
          partner = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/partners+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [partner],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(partnerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Partner page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('partner');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', partnerPageUrlPattern);
      });

      it('edit button click should load edit Partner page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Partner');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', partnerPageUrlPattern);
      });

      it('edit button click should load edit Partner page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Partner');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', partnerPageUrlPattern);
      });

      it('last delete button click should delete instance of Partner', () => {
        cy.intercept('GET', '/api/partners/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('partner').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', partnerPageUrlPattern);

        partner = undefined;
      });
    });
  });

  describe('new Partner page', () => {
    beforeEach(() => {
      cy.visit(partnerPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Partner');
    });

    it('should create an instance of Partner', () => {
      cy.get(`[data-cy="name"]`).type('sway quixotic rebel');
      cy.get(`[data-cy="name"]`).should('have.value', 'sway quixotic rebel');

      cy.get(`[data-cy="link"]`).type('specific marten');
      cy.get(`[data-cy="link"]`).should('have.value', 'specific marten');

      cy.get(`[data-cy="logoRef"]`).type('iridescence aha');
      cy.get(`[data-cy="logoRef"]`).should('have.value', 'iridescence aha');

      cy.get(`[data-cy="sortOrder"]`).type('31934');
      cy.get(`[data-cy="sortOrder"]`).should('have.value', '31934');

      cy.get(`[data-cy="status"]`).select('ARCHIVED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        partner = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', partnerPageUrlPattern);
    });
  });
});
