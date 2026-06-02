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

describe('HomePageSection e2e test', () => {
  const homePageSectionPageUrl = '/home-page-section';
  const homePageSectionPageUrlPattern = new RegExp('/home-page-section(\\?.*)?$');
  let username: string;
  let password: string;
  const homePageSectionSample = { section: 'if meanwhile', status: 'ARCHIVED' };

  let homePageSection;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/home-page-sections+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/home-page-sections').as('postEntityRequest');
    cy.intercept('DELETE', '/api/home-page-sections/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (homePageSection) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/home-page-sections/${homePageSection.id}`,
      }).then(() => {
        homePageSection = undefined;
      });
    }
  });

  it('HomePageSections menu should load HomePageSections page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('home-page-section');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HomePageSection').should('exist');
    cy.url().should('match', homePageSectionPageUrlPattern);
  });

  describe('HomePageSection page', () => {
    it('should have translated page title', () => {
      cy.visit(homePageSectionPageUrl);
      cy.getEntityHeading('HomePageSection').should('not.contain', 'proficiencyTestingApp.homePageSection.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(homePageSectionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create HomePageSection page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/home-page-section/new$'));
        cy.getEntityCreateUpdateHeading('HomePageSection');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', homePageSectionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/home-page-sections',
          body: homePageSectionSample,
        }).then(({ body }) => {
          homePageSection = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/home-page-sections+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [homePageSection],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(homePageSectionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details HomePageSection page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('homePageSection');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', homePageSectionPageUrlPattern);
      });

      it('edit button click should load edit HomePageSection page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HomePageSection');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', homePageSectionPageUrlPattern);
      });

      it('edit button click should load edit HomePageSection page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HomePageSection');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', homePageSectionPageUrlPattern);
      });

      it('last delete button click should delete instance of HomePageSection', () => {
        cy.intercept('GET', '/api/home-page-sections/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('homePageSection').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', homePageSectionPageUrlPattern);

        homePageSection = undefined;
      });
    });
  });

  describe('new HomePageSection page', () => {
    beforeEach(() => {
      cy.visit(homePageSectionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('HomePageSection');
    });

    it('should create an instance of HomePageSection', () => {
      cy.get(`[data-cy="section"]`).type('gasp geez ick');
      cy.get(`[data-cy="section"]`).should('have.value', 'gasp geez ick');

      cy.get(`[data-cy="type"]`).type('testimonial brr creak');
      cy.get(`[data-cy="type"]`).should('have.value', 'testimonial brr creak');

      cy.get(`[data-cy="title"]`).type('lox');
      cy.get(`[data-cy="title"]`).should('have.value', 'lox');

      cy.get(`[data-cy="text"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="text"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="link"]`).type('ready accompanist venom');
      cy.get(`[data-cy="link"]`).should('have.value', 'ready accompanist venom');

      cy.get(`[data-cy="fileRef"]`).type('case');
      cy.get(`[data-cy="fileRef"]`).should('have.value', 'case');

      cy.get(`[data-cy="icon"]`).type('ouch scare till');
      cy.get(`[data-cy="icon"]`).should('have.value', 'ouch scare till');

      cy.get(`[data-cy="displayOrder"]`).type('29699');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '29699');

      cy.get(`[data-cy="status"]`).select('PUBLISHED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        homePageSection = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', homePageSectionPageUrlPattern);
    });
  });
});
