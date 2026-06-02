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

describe('GlobalConfiguration e2e test', () => {
  const globalConfigurationPageUrl = '/global-configuration';
  const globalConfigurationPageUrlPattern = new RegExp('/global-configuration(\\?.*)?$');
  let username: string;
  let password: string;
  const globalConfigurationSample = { configKey: 'like accessorise' };

  let globalConfiguration;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/global-configurations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/global-configurations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/global-configurations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (globalConfiguration) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/global-configurations/${globalConfiguration.id}`,
      }).then(() => {
        globalConfiguration = undefined;
      });
    }
  });

  it('GlobalConfigurations menu should load GlobalConfigurations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('global-configuration');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('GlobalConfiguration').should('exist');
    cy.url().should('match', globalConfigurationPageUrlPattern);
  });

  describe('GlobalConfiguration page', () => {
    it('should have translated page title', () => {
      cy.visit(globalConfigurationPageUrl);
      cy.getEntityHeading('GlobalConfiguration').should('not.contain', 'proficiencyTestingApp.globalConfiguration.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(globalConfigurationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create GlobalConfiguration page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/global-configuration/new$'));
        cy.getEntityCreateUpdateHeading('GlobalConfiguration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', globalConfigurationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/global-configurations',
          body: globalConfigurationSample,
        }).then(({ body }) => {
          globalConfiguration = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/global-configurations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [globalConfiguration],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(globalConfigurationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details GlobalConfiguration page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('globalConfiguration');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', globalConfigurationPageUrlPattern);
      });

      it('edit button click should load edit GlobalConfiguration page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('GlobalConfiguration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', globalConfigurationPageUrlPattern);
      });

      it('edit button click should load edit GlobalConfiguration page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('GlobalConfiguration');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', globalConfigurationPageUrlPattern);
      });

      it('last delete button click should delete instance of GlobalConfiguration', () => {
        cy.intercept('GET', '/api/global-configurations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('globalConfiguration').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', globalConfigurationPageUrlPattern);

        globalConfiguration = undefined;
      });
    });
  });

  describe('new GlobalConfiguration page', () => {
    beforeEach(() => {
      cy.visit(globalConfigurationPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('GlobalConfiguration');
    });

    it('should create an instance of GlobalConfiguration', () => {
      cy.get(`[data-cy="configKey"]`).type('pfft modulo');
      cy.get(`[data-cy="configKey"]`).should('have.value', 'pfft modulo');

      cy.get(`[data-cy="configValue"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="configValue"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="description"]`).type('meh meaningfully');
      cy.get(`[data-cy="description"]`).should('have.value', 'meh meaningfully');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        globalConfiguration = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', globalConfigurationPageUrlPattern);
    });
  });
});
