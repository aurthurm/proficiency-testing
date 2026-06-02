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

describe('SchemeConfiguration e2e test', () => {
  const schemeConfigurationPageUrl = '/scheme-configuration';
  const schemeConfigurationPageUrlPattern = new RegExp('/scheme-configuration(\\?.*)?$');
  let username: string;
  let password: string;
  const schemeConfigurationSample = { version: 30536, effectiveDate: '2026-06-02', passingScore: 12969.81 };

  let schemeConfiguration;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/schemes',
      body: { code: 'per atop', name: 'bah', schemeType: 'TB', modality: 'QUANTITATIVE', status: 'PENDING' },
    }).then(({ body }) => {
      scheme = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/scheme-configurations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/scheme-configurations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/scheme-configurations/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/schemes', {
      statusCode: 200,
      body: [scheme],
    });
  });

  afterEach(() => {
    if (schemeConfiguration) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/scheme-configurations/${schemeConfiguration.id}`,
      }).then(() => {
        schemeConfiguration = undefined;
      });
    }
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

  it('SchemeConfigurations menu should load SchemeConfigurations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('scheme-configuration');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SchemeConfiguration').should('exist');
    cy.url().should('match', schemeConfigurationPageUrlPattern);
  });

  describe('SchemeConfiguration page', () => {
    it('should have translated page title', () => {
      cy.visit(schemeConfigurationPageUrl);
      cy.getEntityHeading('SchemeConfiguration').should('not.contain', 'proficiencyTestingApp.schemeConfiguration.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(schemeConfigurationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SchemeConfiguration page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/scheme-configuration/new$'));
        cy.getEntityCreateUpdateHeading('SchemeConfiguration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemeConfigurationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/scheme-configurations',
          body: {
            ...schemeConfigurationSample,
            scheme,
          },
        }).then(({ body }) => {
          schemeConfiguration = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/scheme-configurations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [schemeConfiguration],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(schemeConfigurationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SchemeConfiguration page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('schemeConfiguration');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemeConfigurationPageUrlPattern);
      });

      it('edit button click should load edit SchemeConfiguration page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SchemeConfiguration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemeConfigurationPageUrlPattern);
      });

      it('edit button click should load edit SchemeConfiguration page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SchemeConfiguration');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemeConfigurationPageUrlPattern);
      });

      it('last delete button click should delete instance of SchemeConfiguration', () => {
        cy.intercept('GET', '/api/scheme-configurations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('schemeConfiguration').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schemeConfigurationPageUrlPattern);

        schemeConfiguration = undefined;
      });
    });
  });

  describe('new SchemeConfiguration page', () => {
    beforeEach(() => {
      cy.visit(schemeConfigurationPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SchemeConfiguration');
    });

    it('should create an instance of SchemeConfiguration', () => {
      cy.get(`[data-cy="version"]`).type('13452');
      cy.get(`[data-cy="version"]`).should('have.value', '13452');

      cy.get(`[data-cy="effectiveDate"]`).type('2026-06-01');
      cy.get(`[data-cy="effectiveDate"]`).blur();
      cy.get(`[data-cy="effectiveDate"]`).should('have.value', '2026-06-01');

      cy.get(`[data-cy="passingScore"]`).type('8677.92');
      cy.get(`[data-cy="passingScore"]`).should('have.value', '8677.92');

      cy.get(`[data-cy="documentationWeight"]`).type('27560.84');
      cy.get(`[data-cy="documentationWeight"]`).should('have.value', '27560.84');

      cy.get(`[data-cy="allowLateResponse"]`).should('not.be.checked');
      cy.get(`[data-cy="allowLateResponse"]`).click();
      cy.get(`[data-cy="allowLateResponse"]`).should('be.checked');

      cy.get(`[data-cy="optionalFields"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="optionalFields"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="scoringRules"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="scoringRules"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="scheme"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        schemeConfiguration = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', schemeConfigurationPageUrlPattern);
    });
  });
});
