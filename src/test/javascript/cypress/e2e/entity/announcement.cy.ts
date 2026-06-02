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

describe('Announcement e2e test', () => {
  const announcementPageUrl = '/announcement';
  const announcementPageUrlPattern = new RegExp('/announcement(\\?.*)?$');
  let username: string;
  let password: string;
  const announcementSample = { title: 'amidst', status: 'PUBLISHED' };

  let announcement;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/announcements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/announcements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/announcements/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (announcement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/announcements/${announcement.id}`,
      }).then(() => {
        announcement = undefined;
      });
    }
  });

  it('Announcements menu should load Announcements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('announcement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Announcement').should('exist');
    cy.url().should('match', announcementPageUrlPattern);
  });

  describe('Announcement page', () => {
    it('should have translated page title', () => {
      cy.visit(announcementPageUrl);
      cy.getEntityHeading('Announcement').should('not.contain', 'proficiencyTestingApp.announcement.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(announcementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Announcement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/announcement/new$'));
        cy.getEntityCreateUpdateHeading('Announcement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', announcementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/announcements',
          body: announcementSample,
        }).then(({ body }) => {
          announcement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/announcements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/announcements?page=0&size=20>; rel="last",<http://localhost/api/announcements?page=0&size=20>; rel="first"',
              },
              body: [announcement],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(announcementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Announcement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('announcement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', announcementPageUrlPattern);
      });

      it('edit button click should load edit Announcement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Announcement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', announcementPageUrlPattern);
      });

      it('edit button click should load edit Announcement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Announcement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', announcementPageUrlPattern);
      });

      it('last delete button click should delete instance of Announcement', () => {
        cy.intercept('GET', '/api/announcements/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('announcement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', announcementPageUrlPattern);

        announcement = undefined;
      });
    });
  });

  describe('new Announcement page', () => {
    beforeEach(() => {
      cy.visit(announcementPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Announcement');
    });

    it('should create an instance of Announcement', () => {
      cy.get(`[data-cy="title"]`).type('multicolored babushka');
      cy.get(`[data-cy="title"]`).should('have.value', 'multicolored babushka');

      cy.get(`[data-cy="body"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="body"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="status"]`).select('ARCHIVED');

      cy.get(`[data-cy="publishedFrom"]`).type('2026-06-02T10:50');
      cy.get(`[data-cy="publishedFrom"]`).blur();
      cy.get(`[data-cy="publishedFrom"]`).should('have.value', '2026-06-02T10:50');

      cy.get(`[data-cy="publishedTo"]`).type('2026-06-02T08:02');
      cy.get(`[data-cy="publishedTo"]`).blur();
      cy.get(`[data-cy="publishedTo"]`).should('have.value', '2026-06-02T08:02');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        announcement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', announcementPageUrlPattern);
    });
  });
});
