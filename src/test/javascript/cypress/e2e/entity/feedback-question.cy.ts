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

describe('FeedbackQuestion e2e test', () => {
  const feedbackQuestionPageUrl = '/feedback-question';
  const feedbackQuestionPageUrlPattern = new RegExp('/feedback-question(\\?.*)?$');
  let username: string;
  let password: string;
  const feedbackQuestionSample = { questionText: 'though', status: 'DRAFT' };

  let feedbackQuestion;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/feedback-questions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/feedback-questions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/feedback-questions/*').as('deleteEntityRequest');
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
  });

  it('FeedbackQuestions menu should load FeedbackQuestions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('feedback-question');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FeedbackQuestion').should('exist');
    cy.url().should('match', feedbackQuestionPageUrlPattern);
  });

  describe('FeedbackQuestion page', () => {
    it('should have translated page title', () => {
      cy.visit(feedbackQuestionPageUrl);
      cy.getEntityHeading('FeedbackQuestion').should('not.contain', 'proficiencyTestingApp.feedbackQuestion.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(feedbackQuestionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FeedbackQuestion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/feedback-question/new$'));
        cy.getEntityCreateUpdateHeading('FeedbackQuestion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', feedbackQuestionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/feedback-questions',
          body: feedbackQuestionSample,
        }).then(({ body }) => {
          feedbackQuestion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/feedback-questions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [feedbackQuestion],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(feedbackQuestionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FeedbackQuestion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('feedbackQuestion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', feedbackQuestionPageUrlPattern);
      });

      it('edit button click should load edit FeedbackQuestion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FeedbackQuestion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', feedbackQuestionPageUrlPattern);
      });

      it('edit button click should load edit FeedbackQuestion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FeedbackQuestion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', feedbackQuestionPageUrlPattern);
      });

      it('last delete button click should delete instance of FeedbackQuestion', () => {
        cy.intercept('GET', '/api/feedback-questions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('feedbackQuestion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', feedbackQuestionPageUrlPattern);

        feedbackQuestion = undefined;
      });
    });
  });

  describe('new FeedbackQuestion page', () => {
    beforeEach(() => {
      cy.visit(feedbackQuestionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FeedbackQuestion');
    });

    it('should create an instance of FeedbackQuestion', () => {
      cy.get(`[data-cy="questionText"]`).type('aggressive');
      cy.get(`[data-cy="questionText"]`).should('have.value', 'aggressive');

      cy.get(`[data-cy="displayOrder"]`).type('13177');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '13177');

      cy.get(`[data-cy="status"]`).select('ARCHIVED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        feedbackQuestion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', feedbackQuestionPageUrlPattern);
    });
  });
});
