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

describe('CertificateTemplate e2e test', () => {
  const certificateTemplatePageUrl = '/certificate-template';
  const certificateTemplatePageUrlPattern = new RegExp('/certificate-template(\\?.*)?$');
  let username: string;
  let password: string;
  const certificateTemplateSample = { certificateType: 'PARTICIPATION' };

  let certificateTemplate;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/certificate-templates+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/certificate-templates').as('postEntityRequest');
    cy.intercept('DELETE', '/api/certificate-templates/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (certificateTemplate) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/certificate-templates/${certificateTemplate.id}`,
      }).then(() => {
        certificateTemplate = undefined;
      });
    }
  });

  it('CertificateTemplates menu should load CertificateTemplates page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('certificate-template');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CertificateTemplate').should('exist');
    cy.url().should('match', certificateTemplatePageUrlPattern);
  });

  describe('CertificateTemplate page', () => {
    it('should have translated page title', () => {
      cy.visit(certificateTemplatePageUrl);
      cy.getEntityHeading('CertificateTemplate').should('not.contain', 'proficiencyTestingApp.certificateTemplate.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(certificateTemplatePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CertificateTemplate page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/certificate-template/new$'));
        cy.getEntityCreateUpdateHeading('CertificateTemplate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateTemplatePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/certificate-templates',
          body: certificateTemplateSample,
        }).then(({ body }) => {
          certificateTemplate = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/certificate-templates+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [certificateTemplate],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(certificateTemplatePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CertificateTemplate page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('certificateTemplate');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateTemplatePageUrlPattern);
      });

      it('edit button click should load edit CertificateTemplate page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CertificateTemplate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateTemplatePageUrlPattern);
      });

      it('edit button click should load edit CertificateTemplate page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CertificateTemplate');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateTemplatePageUrlPattern);
      });

      it('last delete button click should delete instance of CertificateTemplate', () => {
        cy.intercept('GET', '/api/certificate-templates/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('certificateTemplate').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', certificateTemplatePageUrlPattern);

        certificateTemplate = undefined;
      });
    });
  });

  describe('new CertificateTemplate page', () => {
    beforeEach(() => {
      cy.visit(certificateTemplatePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CertificateTemplate');
    });

    it('should create an instance of CertificateTemplate', () => {
      cy.get(`[data-cy="certificateType"]`).select('EXCELLENCE');

      cy.get(`[data-cy="fileRef"]`).type('while soon');
      cy.get(`[data-cy="fileRef"]`).should('have.value', 'while soon');

      cy.get(`[data-cy="detectedFields"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="detectedFields"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        certificateTemplate = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', certificateTemplatePageUrlPattern);
    });
  });
});
