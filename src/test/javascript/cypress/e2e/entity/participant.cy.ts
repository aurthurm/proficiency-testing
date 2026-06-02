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

describe('Participant e2e test', () => {
  const participantPageUrl = '/participant';
  const participantPageUrlPattern = new RegExp('/participant(\\?.*)?$');
  let username: string;
  let password: string;
  const participantSample = {
    uniqueIdentifier: 'far testing creaking',
    instituteName: 'pfft provided frenetically',
    email: 'edR@\\5./!.;',
    affiliation: 'ugh rue',
    status: 'ACTIVE',
  };

  let participant;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/participants+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/participants').as('postEntityRequest');
    cy.intercept('DELETE', '/api/participants/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (participant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participants/${participant.id}`,
      }).then(() => {
        participant = undefined;
      });
    }
  });

  it('Participants menu should load Participants page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('participant');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Participant').should('exist');
    cy.url().should('match', participantPageUrlPattern);
  });

  describe('Participant page', () => {
    it('should have translated page title', () => {
      cy.visit(participantPageUrl);
      cy.getEntityHeading('Participant').should('not.contain', 'proficiencyTestingApp.participant.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(participantPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Participant page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/participant/new$'));
        cy.getEntityCreateUpdateHeading('Participant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/participants',
          body: participantSample,
        }).then(({ body }) => {
          participant = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/participants+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/participants?page=0&size=20>; rel="last",<http://localhost/api/participants?page=0&size=20>; rel="first"',
              },
              body: [participant],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(participantPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Participant page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('participant');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });

      it('edit button click should load edit Participant page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Participant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });

      it('edit button click should load edit Participant page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Participant');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });

      it('last delete button click should delete instance of Participant', () => {
        cy.intercept('GET', '/api/participants/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('participant').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);

        participant = undefined;
      });
    });
  });

  describe('new Participant page', () => {
    beforeEach(() => {
      cy.visit(participantPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Participant');
    });

    it('should create an instance of Participant', () => {
      cy.get(`[data-cy="uniqueIdentifier"]`).type('joyfully');
      cy.get(`[data-cy="uniqueIdentifier"]`).should('have.value', 'joyfully');

      cy.get(`[data-cy="instituteName"]`).type('bravely impassioned');
      cy.get(`[data-cy="instituteName"]`).should('have.value', 'bravely impassioned');

      cy.get(`[data-cy="departmentName"]`).type('um debit');
      cy.get(`[data-cy="departmentName"]`).should('have.value', 'um debit');

      cy.get(`[data-cy="email"]`).type('&P@4C.$');
      cy.get(`[data-cy="email"]`).should('have.value', '&P@4C.$');

      cy.get(`[data-cy="additionalEmail"]`).type('9az3@rPm.ir`');
      cy.get(`[data-cy="additionalEmail"]`).should('have.value', '9az3@rPm.ir`');

      cy.get(`[data-cy="address"]`).type('phooey true enchanting');
      cy.get(`[data-cy="address"]`).should('have.value', 'phooey true enchanting');

      cy.get(`[data-cy="shippingAddress"]`).type('usually usefully merry');
      cy.get(`[data-cy="shippingAddress"]`).should('have.value', 'usually usefully merry');

      cy.get(`[data-cy="city"]`).type('Kundeworth');
      cy.get(`[data-cy="city"]`).should('have.value', 'Kundeworth');

      cy.get(`[data-cy="state"]`).type('tight brood');
      cy.get(`[data-cy="state"]`).should('have.value', 'tight brood');

      cy.get(`[data-cy="district"]`).type('colligate');
      cy.get(`[data-cy="district"]`).should('have.value', 'colligate');

      cy.get(`[data-cy="zip"]`).type('once');
      cy.get(`[data-cy="zip"]`).should('have.value', 'once');

      cy.get(`[data-cy="region"]`).type('shanghai but');
      cy.get(`[data-cy="region"]`).should('have.value', 'shanghai but');

      cy.get(`[data-cy="phone"]`).type('980-910-3448 x9455');
      cy.get(`[data-cy="phone"]`).should('have.value', '980-910-3448 x9455');

      cy.get(`[data-cy="mobile"]`).type('whoa for');
      cy.get(`[data-cy="mobile"]`).should('have.value', 'whoa for');

      cy.get(`[data-cy="affiliation"]`).type('inside');
      cy.get(`[data-cy="affiliation"]`).should('have.value', 'inside');

      cy.get(`[data-cy="networkTier"]`).type('ew headline woot');
      cy.get(`[data-cy="networkTier"]`).should('have.value', 'ew headline woot');

      cy.get(`[data-cy="siteType"]`).type('reflecting roughly stool');
      cy.get(`[data-cy="siteType"]`).should('have.value', 'reflecting roughly stool');

      cy.get(`[data-cy="fundingSource"]`).type('progress electric optimal');
      cy.get(`[data-cy="fundingSource"]`).should('have.value', 'progress electric optimal');

      cy.get(`[data-cy="testingVolume"]`).type('9023');
      cy.get(`[data-cy="testingVolume"]`).should('have.value', '9023');

      cy.get(`[data-cy="pepfarId"]`).type('and');
      cy.get(`[data-cy="pepfarId"]`).should('have.value', 'and');

      cy.get(`[data-cy="latitude"]`).type('2715.8');
      cy.get(`[data-cy="latitude"]`).should('have.value', '2715.8');

      cy.get(`[data-cy="longitude"]`).type('27664.31');
      cy.get(`[data-cy="longitude"]`).should('have.value', '27664.31');

      cy.get(`[data-cy="labDirectorName"]`).type('flashy thongs');
      cy.get(`[data-cy="labDirectorName"]`).should('have.value', 'flashy thongs');

      cy.get(`[data-cy="labDirectorEmail"]`).type('u-<@Y-wEpf.bJ^');
      cy.get(`[data-cy="labDirectorEmail"]`).should('have.value', 'u-<@Y-wEpf.bJ^');

      cy.get(`[data-cy="contactPersonName"]`).type('narrate');
      cy.get(`[data-cy="contactPersonName"]`).should('have.value', 'narrate');

      cy.get(`[data-cy="contactPersonEmail"]`).type("v@Xs'q~.^'Gp");
      cy.get(`[data-cy="contactPersonEmail"]`).should('have.value', "v@Xs'q~.^'Gp");

      cy.get(`[data-cy="contactPersonPhone"]`).type('intensely from anguished');
      cy.get(`[data-cy="contactPersonPhone"]`).should('have.value', 'intensely from anguished');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        participant = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', participantPageUrlPattern);
    });
  });
});
