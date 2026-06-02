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

describe('MailTemplate e2e test', () => {
  const mailTemplatePageUrl = '/mail-template';
  const mailTemplatePageUrlPattern = new RegExp('/mail-template(\\?.*)?$');
  let username: string;
  let password: string;
  const mailTemplateSample = { code: 'ugh' };

  let mailTemplate;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mail-templates+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mail-templates').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mail-templates/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mailTemplate) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mail-templates/${mailTemplate.id}`,
      }).then(() => {
        mailTemplate = undefined;
      });
    }
  });

  it('MailTemplates menu should load MailTemplates page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mail-template');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MailTemplate').should('exist');
    cy.url().should('match', mailTemplatePageUrlPattern);
  });

  describe('MailTemplate page', () => {
    it('should have translated page title', () => {
      cy.visit(mailTemplatePageUrl);
      cy.getEntityHeading('MailTemplate').should('not.contain', 'proficiencyTestingApp.mailTemplate.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mailTemplatePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MailTemplate page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mail-template/new$'));
        cy.getEntityCreateUpdateHeading('MailTemplate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mailTemplatePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mail-templates',
          body: mailTemplateSample,
        }).then(({ body }) => {
          mailTemplate = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mail-templates+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [mailTemplate],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mailTemplatePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MailTemplate page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mailTemplate');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mailTemplatePageUrlPattern);
      });

      it('edit button click should load edit MailTemplate page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MailTemplate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mailTemplatePageUrlPattern);
      });

      it('edit button click should load edit MailTemplate page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MailTemplate');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mailTemplatePageUrlPattern);
      });

      it('last delete button click should delete instance of MailTemplate', () => {
        cy.intercept('GET', '/api/mail-templates/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('mailTemplate').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mailTemplatePageUrlPattern);

        mailTemplate = undefined;
      });
    });
  });

  describe('new MailTemplate page', () => {
    beforeEach(() => {
      cy.visit(mailTemplatePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MailTemplate');
    });

    it('should create an instance of MailTemplate', () => {
      cy.get(`[data-cy="code"]`).type('ack insistent round');
      cy.get(`[data-cy="code"]`).should('have.value', 'ack insistent round');

      cy.get(`[data-cy="subject"]`).type('hence trusty um');
      cy.get(`[data-cy="subject"]`).should('have.value', 'hence trusty um');

      cy.get(`[data-cy="htmlBody"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="htmlBody"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="status"]`).select('ARCHIVED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mailTemplate = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mailTemplatePageUrlPattern);
    });
  });
});
