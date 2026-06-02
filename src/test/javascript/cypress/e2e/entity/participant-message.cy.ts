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

describe('ParticipantMessage e2e test', () => {
  const participantMessagePageUrl = '/participant-message';
  const participantMessagePageUrlPattern = new RegExp('/participant-message(\\?.*)?$');
  let username: string;
  let password: string;
  const participantMessageSample = {};

  let participantMessage;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/participants',
      body: {
        uniqueIdentifier: 'boldly blindly',
        instituteName: 'yowza through',
        departmentName: 'tool doodle',
        email: ':F5@RNRG!.xu7w',
        additionalEmail: 'y@?E,~}V.-',
        address: 'carefree under',
        shippingAddress: 'oof major',
        city: 'East Abigayleside',
        state: 'galvanize',
        district: 'scarily',
        zip: 'avalanche',
        region: 'kindheartedly until hold',
        phone: '1-428-760-6154 x84220',
        mobile: 'calmly woot above',
        affiliation: 'divert',
        networkTier: 'only',
        siteType: 'ack',
        fundingSource: 'adviser',
        testingVolume: 13866,
        pepfarId: 'aggressive majestically ick',
        latitude: 5191.53,
        longitude: 9038.74,
        labDirectorName: 'after uh-huh about',
        labDirectorEmail: 'azs^d@0..VSW-E',
        contactPersonName: 'um',
        contactPersonEmail: 'AI@#hFIb{._H',
        contactPersonPhone: 'boo chilly',
        status: 'INACTIVE',
      },
    }).then(({ body }) => {
      participant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/participant-messages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/participant-messages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/participant-messages/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/participants', {
      statusCode: 200,
      body: [participant],
    });
  });

  afterEach(() => {
    if (participantMessage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participant-messages/${participantMessage.id}`,
      }).then(() => {
        participantMessage = undefined;
      });
    }
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

  it('ParticipantMessages menu should load ParticipantMessages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('participant-message');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ParticipantMessage').should('exist');
    cy.url().should('match', participantMessagePageUrlPattern);
  });

  describe('ParticipantMessage page', () => {
    it('should have translated page title', () => {
      cy.visit(participantMessagePageUrl);
      cy.getEntityHeading('ParticipantMessage').should('not.contain', 'proficiencyTestingApp.participantMessage.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(participantMessagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ParticipantMessage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/participant-message/new$'));
        cy.getEntityCreateUpdateHeading('ParticipantMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantMessagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/participant-messages',
          body: {
            ...participantMessageSample,
            participant,
          },
        }).then(({ body }) => {
          participantMessage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/participant-messages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/participant-messages?page=0&size=20>; rel="last",<http://localhost/api/participant-messages?page=0&size=20>; rel="first"',
              },
              body: [participantMessage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(participantMessagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ParticipantMessage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('participantMessage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantMessagePageUrlPattern);
      });

      it('edit button click should load edit ParticipantMessage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantMessagePageUrlPattern);
      });

      it('edit button click should load edit ParticipantMessage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantMessage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantMessagePageUrlPattern);
      });

      it('last delete button click should delete instance of ParticipantMessage', () => {
        cy.intercept('GET', '/api/participant-messages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('participantMessage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantMessagePageUrlPattern);

        participantMessage = undefined;
      });
    });
  });

  describe('new ParticipantMessage page', () => {
    beforeEach(() => {
      cy.visit(participantMessagePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ParticipantMessage');
    });

    it('should create an instance of ParticipantMessage', () => {
      cy.get(`[data-cy="subject"]`).type('emotional out as');
      cy.get(`[data-cy="subject"]`).should('have.value', 'emotional out as');

      cy.get(`[data-cy="body"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="body"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="isRead"]`).should('not.be.checked');
      cy.get(`[data-cy="isRead"]`).click();
      cy.get(`[data-cy="isRead"]`).should('be.checked');

      cy.get(`[data-cy="sentAt"]`).type('2026-06-02T13:11');
      cy.get(`[data-cy="sentAt"]`).blur();
      cy.get(`[data-cy="sentAt"]`).should('have.value', '2026-06-02T13:11');

      cy.get(`[data-cy="participant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        participantMessage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', participantMessagePageUrlPattern);
    });
  });
});
