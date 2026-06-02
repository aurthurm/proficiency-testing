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

describe('ReportConfiguration e2e test', () => {
  const reportConfigurationPageUrl = '/report-configuration';
  const reportConfigurationPageUrlPattern = new RegExp('/report-configuration(\\?.*)?$');
  let username: string;
  let password: string;
  const reportConfigurationSample = {};

  let reportConfiguration;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/report-configurations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/report-configurations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/report-configurations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reportConfiguration) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/report-configurations/${reportConfiguration.id}`,
      }).then(() => {
        reportConfiguration = undefined;
      });
    }
  });

  it('ReportConfigurations menu should load ReportConfigurations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('report-configuration');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ReportConfiguration').should('exist');
    cy.url().should('match', reportConfigurationPageUrlPattern);
  });

  describe('ReportConfiguration page', () => {
    it('should have translated page title', () => {
      cy.visit(reportConfigurationPageUrl);
      cy.getEntityHeading('ReportConfiguration').should('not.contain', 'proficiencyTestingApp.reportConfiguration.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reportConfigurationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ReportConfiguration page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/report-configuration/new$'));
        cy.getEntityCreateUpdateHeading('ReportConfiguration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reportConfigurationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/report-configurations',
          body: reportConfigurationSample,
        }).then(({ body }) => {
          reportConfiguration = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/report-configurations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [reportConfiguration],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(reportConfigurationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ReportConfiguration page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reportConfiguration');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reportConfigurationPageUrlPattern);
      });

      it('edit button click should load edit ReportConfiguration page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReportConfiguration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reportConfigurationPageUrlPattern);
      });

      it('edit button click should load edit ReportConfiguration page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReportConfiguration');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reportConfigurationPageUrlPattern);
      });

      it('last delete button click should delete instance of ReportConfiguration', () => {
        cy.intercept('GET', '/api/report-configurations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('reportConfiguration').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reportConfigurationPageUrlPattern);

        reportConfiguration = undefined;
      });
    });
  });

  describe('new ReportConfiguration page', () => {
    beforeEach(() => {
      cy.visit(reportConfigurationPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ReportConfiguration');
    });

    it('should create an instance of ReportConfiguration', () => {
      cy.get(`[data-cy="reportHeader"]`).type('because formamide sadly');
      cy.get(`[data-cy="reportHeader"]`).should('have.value', 'because formamide sadly');

      cy.get(`[data-cy="logo"]`).type('fiercely deceivingly gah');
      cy.get(`[data-cy="logo"]`).should('have.value', 'fiercely deceivingly gah');

      cy.get(`[data-cy="logoRight"]`).type('ha mousse');
      cy.get(`[data-cy="logoRight"]`).should('have.value', 'ha mousse');

      cy.get(`[data-cy="layout"]`).type('next although');
      cy.get(`[data-cy="layout"]`).should('have.value', 'next although');

      cy.get(`[data-cy="format"]`).type('agile');
      cy.get(`[data-cy="format"]`).should('have.value', 'agile');

      cy.get(`[data-cy="topMargin"]`).type('11927');
      cy.get(`[data-cy="topMargin"]`).should('have.value', '11927');

      cy.get(`[data-cy="instituteAddressPosition"]`).type('subtle');
      cy.get(`[data-cy="instituteAddressPosition"]`).should('have.value', 'subtle');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        reportConfiguration = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', reportConfigurationPageUrlPattern);
    });
  });
});
