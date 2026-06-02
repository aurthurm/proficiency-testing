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

describe('ModeOfReceipt e2e test', () => {
  const modeOfReceiptPageUrl = '/mode-of-receipt';
  const modeOfReceiptPageUrlPattern = new RegExp('/mode-of-receipt(\\?.*)?$');
  let username: string;
  let password: string;
  const modeOfReceiptSample = { name: 'searchingly puritan', status: 'INACTIVE' };

  let modeOfReceipt;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mode-of-receipts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mode-of-receipts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mode-of-receipts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (modeOfReceipt) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mode-of-receipts/${modeOfReceipt.id}`,
      }).then(() => {
        modeOfReceipt = undefined;
      });
    }
  });

  it('ModeOfReceipts menu should load ModeOfReceipts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mode-of-receipt');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ModeOfReceipt').should('exist');
    cy.url().should('match', modeOfReceiptPageUrlPattern);
  });

  describe('ModeOfReceipt page', () => {
    it('should have translated page title', () => {
      cy.visit(modeOfReceiptPageUrl);
      cy.getEntityHeading('ModeOfReceipt').should('not.contain', 'proficiencyTestingApp.modeOfReceipt.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(modeOfReceiptPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ModeOfReceipt page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mode-of-receipt/new$'));
        cy.getEntityCreateUpdateHeading('ModeOfReceipt');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', modeOfReceiptPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mode-of-receipts',
          body: modeOfReceiptSample,
        }).then(({ body }) => {
          modeOfReceipt = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mode-of-receipts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [modeOfReceipt],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(modeOfReceiptPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ModeOfReceipt page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('modeOfReceipt');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', modeOfReceiptPageUrlPattern);
      });

      it('edit button click should load edit ModeOfReceipt page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ModeOfReceipt');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', modeOfReceiptPageUrlPattern);
      });

      it('edit button click should load edit ModeOfReceipt page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ModeOfReceipt');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', modeOfReceiptPageUrlPattern);
      });

      it('last delete button click should delete instance of ModeOfReceipt', () => {
        cy.intercept('GET', '/api/mode-of-receipts/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('modeOfReceipt').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', modeOfReceiptPageUrlPattern);

        modeOfReceipt = undefined;
      });
    });
  });

  describe('new ModeOfReceipt page', () => {
    beforeEach(() => {
      cy.visit(modeOfReceiptPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ModeOfReceipt');
    });

    it('should create an instance of ModeOfReceipt', () => {
      cy.get(`[data-cy="name"]`).type('community suffice fowl');
      cy.get(`[data-cy="name"]`).should('have.value', 'community suffice fowl');

      cy.get(`[data-cy="status"]`).select('INACTIVE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        modeOfReceipt = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', modeOfReceiptPageUrlPattern);
    });
  });
});
