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

describe('ContactMessage e2e test', () => {
  const contactMessagePageUrl = '/contact-message';
  const contactMessagePageUrlPattern = new RegExp('/contact-message(\\?.*)?$');
  let username: string;
  let password: string;
  const contactMessageSample = { name: 'reboot puppet flawed', email: '_]w@M.`~', message: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=' };

  let contactMessage;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/contact-messages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/contact-messages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/contact-messages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (contactMessage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/contact-messages/${contactMessage.id}`,
      }).then(() => {
        contactMessage = undefined;
      });
    }
  });

  it('ContactMessages menu should load ContactMessages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('contact-message');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ContactMessage').should('exist');
    cy.url().should('match', contactMessagePageUrlPattern);
  });

  describe('ContactMessage page', () => {
    it('should have translated page title', () => {
      cy.visit(contactMessagePageUrl);
      cy.getEntityHeading('ContactMessage').should('not.contain', 'proficiencyTestingApp.contactMessage.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(contactMessagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ContactMessage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/contact-message/new$'));
        cy.getEntityCreateUpdateHeading('ContactMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contactMessagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/contact-messages',
          body: contactMessageSample,
        }).then(({ body }) => {
          contactMessage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/contact-messages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/contact-messages?page=0&size=20>; rel="last",<http://localhost/api/contact-messages?page=0&size=20>; rel="first"',
              },
              body: [contactMessage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(contactMessagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ContactMessage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('contactMessage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contactMessagePageUrlPattern);
      });

      it('edit button click should load edit ContactMessage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ContactMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contactMessagePageUrlPattern);
      });

      it('edit button click should load edit ContactMessage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ContactMessage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contactMessagePageUrlPattern);
      });

      it('last delete button click should delete instance of ContactMessage', () => {
        cy.intercept('GET', '/api/contact-messages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('contactMessage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contactMessagePageUrlPattern);

        contactMessage = undefined;
      });
    });
  });

  describe('new ContactMessage page', () => {
    beforeEach(() => {
      cy.visit(contactMessagePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ContactMessage');
    });

    it('should create an instance of ContactMessage', () => {
      cy.get(`[data-cy="name"]`).type('so huzzah');
      cy.get(`[data-cy="name"]`).should('have.value', 'so huzzah');

      cy.get(`[data-cy="email"]`).type('//V-@,T)#.`jO5');
      cy.get(`[data-cy="email"]`).should('have.value', '//V-@,T)#.`jO5');

      cy.get(`[data-cy="subject"]`).type('positively graffiti likewise');
      cy.get(`[data-cy="subject"]`).should('have.value', 'positively graffiti likewise');

      cy.get(`[data-cy="message"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="message"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="ipAddress"]`).type('lest internal');
      cy.get(`[data-cy="ipAddress"]`).should('have.value', 'lest internal');

      cy.get(`[data-cy="submittedAt"]`).type('2026-06-02T07:36');
      cy.get(`[data-cy="submittedAt"]`).blur();
      cy.get(`[data-cy="submittedAt"]`).should('have.value', '2026-06-02T07:36');

      cy.get(`[data-cy="isHandled"]`).should('not.be.checked');
      cy.get(`[data-cy="isHandled"]`).click();
      cy.get(`[data-cy="isHandled"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        contactMessage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', contactMessagePageUrlPattern);
    });
  });
});
