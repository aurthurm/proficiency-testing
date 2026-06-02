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

describe('CustomFieldDefinition e2e test', () => {
  const customFieldDefinitionPageUrl = '/custom-field-definition';
  const customFieldDefinitionPageUrlPattern = new RegExp('/custom-field-definition(\\?.*)?$');
  let username: string;
  let password: string;
  const customFieldDefinitionSample = { fieldKey: 'pluck', label: 'hence', fieldType: 'BOOLEAN', status: 'PENDING' };

  let customFieldDefinition;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/custom-field-definitions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/custom-field-definitions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/custom-field-definitions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (customFieldDefinition) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/custom-field-definitions/${customFieldDefinition.id}`,
      }).then(() => {
        customFieldDefinition = undefined;
      });
    }
  });

  it('CustomFieldDefinitions menu should load CustomFieldDefinitions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('custom-field-definition');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CustomFieldDefinition').should('exist');
    cy.url().should('match', customFieldDefinitionPageUrlPattern);
  });

  describe('CustomFieldDefinition page', () => {
    it('should have translated page title', () => {
      cy.visit(customFieldDefinitionPageUrl);
      cy.getEntityHeading('CustomFieldDefinition').should('not.contain', 'proficiencyTestingApp.customFieldDefinition.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(customFieldDefinitionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CustomFieldDefinition page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/custom-field-definition/new$'));
        cy.getEntityCreateUpdateHeading('CustomFieldDefinition');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customFieldDefinitionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/custom-field-definitions',
          body: customFieldDefinitionSample,
        }).then(({ body }) => {
          customFieldDefinition = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/custom-field-definitions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [customFieldDefinition],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(customFieldDefinitionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CustomFieldDefinition page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('customFieldDefinition');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customFieldDefinitionPageUrlPattern);
      });

      it('edit button click should load edit CustomFieldDefinition page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CustomFieldDefinition');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customFieldDefinitionPageUrlPattern);
      });

      it('edit button click should load edit CustomFieldDefinition page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CustomFieldDefinition');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customFieldDefinitionPageUrlPattern);
      });

      it('last delete button click should delete instance of CustomFieldDefinition', () => {
        cy.intercept('GET', '/api/custom-field-definitions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('customFieldDefinition').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', customFieldDefinitionPageUrlPattern);

        customFieldDefinition = undefined;
      });
    });
  });

  describe('new CustomFieldDefinition page', () => {
    beforeEach(() => {
      cy.visit(customFieldDefinitionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CustomFieldDefinition');
    });

    it('should create an instance of CustomFieldDefinition', () => {
      cy.get(`[data-cy="fieldKey"]`).type('oof instead humidity');
      cy.get(`[data-cy="fieldKey"]`).should('have.value', 'oof instead humidity');

      cy.get(`[data-cy="label"]`).type('huzzah phew');
      cy.get(`[data-cy="label"]`).should('have.value', 'huzzah phew');

      cy.get(`[data-cy="fieldType"]`).select('BOOLEAN');

      cy.get(`[data-cy="options"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="options"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="displayOrder"]`).type('9582');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '9582');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        customFieldDefinition = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', customFieldDefinitionPageUrlPattern);
    });
  });
});
