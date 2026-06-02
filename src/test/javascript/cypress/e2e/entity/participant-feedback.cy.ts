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

describe('ParticipantFeedback e2e test', () => {
  const participantFeedbackPageUrl = '/participant-feedback';
  const participantFeedbackPageUrlPattern = new RegExp('/participant-feedback(\\?.*)?$');
  let username: string;
  let password: string;
  const participantFeedbackSample = {};

  let participantFeedback;
  let feedbackQuestion;
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
      url: '/api/feedback-questions',
      body: { questionText: 'outrank', displayOrder: 6125, status: 'PUBLISHED' },
    }).then(({ body }) => {
      feedbackQuestion = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/participants',
      body: {
        uniqueIdentifier: 'as bandwidth',
        instituteName: 'weary although as',
        departmentName: 'inasmuch catalyze',
        email: 'o@W%+Q\\.Mj',
        additionalEmail: 'K$@4+x.6{X$<^',
        address: 'league toward',
        shippingAddress: 'past slowly',
        city: 'South Forrest',
        state: 'notwithstanding geez',
        district: 'sophisticated definite overburden',
        zip: 'whoever hunger as',
        region: 'expansion lively',
        phone: '1-596-361-6504',
        mobile: 'boring',
        affiliation: 'unto',
        networkTier: 'tackle',
        siteType: 'though why insignificant',
        fundingSource: 'knowledgeable mmm',
        testingVolume: 28189,
        pepfarId: 'plain through wolf',
        latitude: 12966.49,
        longitude: 22511.73,
        labDirectorName: 'before through',
        labDirectorEmail: 'n@/Ak#p.Eo',
        contactPersonName: 'presell',
        contactPersonEmail: "b@'.pK",
        contactPersonPhone: 'at ick screw',
        status: 'ACTIVE',
      },
    }).then(({ body }) => {
      participant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/participant-feedbacks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/participant-feedbacks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/participant-feedbacks/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/feedback-questions', {
      statusCode: 200,
      body: [feedbackQuestion],
    });

    cy.intercept('GET', '/api/participants', {
      statusCode: 200,
      body: [participant],
    });

    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (participantFeedback) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participant-feedbacks/${participantFeedback.id}`,
      }).then(() => {
        participantFeedback = undefined;
      });
    }
  });

  afterEach(() => {
    if (feedbackQuestion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/feedback-questions/${feedbackQuestion.id}`,
      }).then(() => {
        feedbackQuestion = undefined;
      });
    }
    if (participant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participants/${participant.id}`,
      }).then(() => {
        participant = undefined;
      });
    }
  });

  it('ParticipantFeedbacks menu should load ParticipantFeedbacks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('participant-feedback');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ParticipantFeedback').should('exist');
    cy.url().should('match', participantFeedbackPageUrlPattern);
  });

  describe('ParticipantFeedback page', () => {
    it('should have translated page title', () => {
      cy.visit(participantFeedbackPageUrl);
      cy.getEntityHeading('ParticipantFeedback').should('not.contain', 'proficiencyTestingApp.participantFeedback.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(participantFeedbackPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ParticipantFeedback page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/participant-feedback/new$'));
        cy.getEntityCreateUpdateHeading('ParticipantFeedback');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantFeedbackPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/participant-feedbacks',
          body: {
            ...participantFeedbackSample,
            question: feedbackQuestion,
            participant,
          },
        }).then(({ body }) => {
          participantFeedback = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/participant-feedbacks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [participantFeedback],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(participantFeedbackPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ParticipantFeedback page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('participantFeedback');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantFeedbackPageUrlPattern);
      });

      it('edit button click should load edit ParticipantFeedback page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantFeedback');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantFeedbackPageUrlPattern);
      });

      it('edit button click should load edit ParticipantFeedback page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantFeedback');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantFeedbackPageUrlPattern);
      });

      it('last delete button click should delete instance of ParticipantFeedback', () => {
        cy.intercept('GET', '/api/participant-feedbacks/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('participantFeedback').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantFeedbackPageUrlPattern);

        participantFeedback = undefined;
      });
    });
  });

  describe('new ParticipantFeedback page', () => {
    beforeEach(() => {
      cy.visit(participantFeedbackPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ParticipantFeedback');
    });

    it('should create an instance of ParticipantFeedback', () => {
      cy.get(`[data-cy="answer"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="answer"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="submittedAt"]`).type('2026-06-02T04:46');
      cy.get(`[data-cy="submittedAt"]`).blur();
      cy.get(`[data-cy="submittedAt"]`).should('have.value', '2026-06-02T04:46');

      cy.get(`[data-cy="question"]`).select(1);
      cy.get(`[data-cy="participant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        participantFeedback = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', participantFeedbackPageUrlPattern);
    });
  });
});
