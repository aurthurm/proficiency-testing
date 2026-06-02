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

describe('EmailMessage e2e test', () => {
  const emailMessagePageUrl = '/email-message';
  const emailMessagePageUrlPattern = new RegExp('/email-message(\\?.*)?$');
  let username: string;
  let password: string;
  const emailMessageSample = { toEmail: 'now bah lock', status: 'RETRYING' };

  let emailMessage;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/email-messages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/email-messages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/email-messages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (emailMessage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/email-messages/${emailMessage.id}`,
      }).then(() => {
        emailMessage = undefined;
      });
    }
  });

  it('EmailMessages menu should load EmailMessages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('email-message');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EmailMessage').should('exist');
    cy.url().should('match', emailMessagePageUrlPattern);
  });

  describe('EmailMessage page', () => {
    it('should have translated page title', () => {
      cy.visit(emailMessagePageUrl);
      cy.getEntityHeading('EmailMessage').should('not.contain', 'proficiencyTestingApp.emailMessage.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(emailMessagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EmailMessage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/email-message/new$'));
        cy.getEntityCreateUpdateHeading('EmailMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', emailMessagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/email-messages',
          body: emailMessageSample,
        }).then(({ body }) => {
          emailMessage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/email-messages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/email-messages?page=0&size=20>; rel="last",<http://localhost/api/email-messages?page=0&size=20>; rel="first"',
              },
              body: [emailMessage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(emailMessagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details EmailMessage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('emailMessage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', emailMessagePageUrlPattern);
      });

      it('edit button click should load edit EmailMessage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EmailMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', emailMessagePageUrlPattern);
      });

      it('edit button click should load edit EmailMessage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EmailMessage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', emailMessagePageUrlPattern);
      });

      it('last delete button click should delete instance of EmailMessage', () => {
        cy.intercept('GET', '/api/email-messages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('emailMessage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', emailMessagePageUrlPattern);

        emailMessage = undefined;
      });
    });
  });

  describe('new EmailMessage page', () => {
    beforeEach(() => {
      cy.visit(emailMessagePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EmailMessage');
    });

    it('should create an instance of EmailMessage', () => {
      cy.get(`[data-cy="fromEmail"]`).type('except reassemble duh');
      cy.get(`[data-cy="fromEmail"]`).should('have.value', 'except reassemble duh');

      cy.get(`[data-cy="fromName"]`).type('license');
      cy.get(`[data-cy="fromName"]`).should('have.value', 'license');

      cy.get(`[data-cy="replyTo"]`).type('kowtow');
      cy.get(`[data-cy="replyTo"]`).should('have.value', 'kowtow');

      cy.get(`[data-cy="toEmail"]`).type('eek meaningfully equally');
      cy.get(`[data-cy="toEmail"]`).should('have.value', 'eek meaningfully equally');

      cy.get(`[data-cy="cc"]`).type('fondly');
      cy.get(`[data-cy="cc"]`).should('have.value', 'fondly');

      cy.get(`[data-cy="bcc"]`).type('why dream');
      cy.get(`[data-cy="bcc"]`).should('have.value', 'why dream');

      cy.get(`[data-cy="subject"]`).type('pish');
      cy.get(`[data-cy="subject"]`).should('have.value', 'pish');

      cy.get(`[data-cy="body"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="body"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="attachmentRef"]`).type('whereas');
      cy.get(`[data-cy="attachmentRef"]`).should('have.value', 'whereas');

      cy.get(`[data-cy="status"]`).select('SENT');

      cy.get(`[data-cy="failureType"]`).type('elastic density');
      cy.get(`[data-cy="failureType"]`).should('have.value', 'elastic density');

      cy.get(`[data-cy="failureReason"]`).type('waver red');
      cy.get(`[data-cy="failureReason"]`).should('have.value', 'waver red');

      cy.get(`[data-cy="queuedOn"]`).type('2026-06-02T00:47');
      cy.get(`[data-cy="queuedOn"]`).blur();
      cy.get(`[data-cy="queuedOn"]`).should('have.value', '2026-06-02T00:47');

      cy.get(`[data-cy="sentAt"]`).type('2026-06-02T13:50');
      cy.get(`[data-cy="sentAt"]`).blur();
      cy.get(`[data-cy="sentAt"]`).should('have.value', '2026-06-02T13:50');

      cy.get(`[data-cy="retryCount"]`).type('31095');
      cy.get(`[data-cy="retryCount"]`).should('have.value', '31095');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        emailMessage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', emailMessagePageUrlPattern);
    });
  });
});
