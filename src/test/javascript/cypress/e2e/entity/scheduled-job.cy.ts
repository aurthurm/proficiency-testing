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

describe('ScheduledJob e2e test', () => {
  const scheduledJobPageUrl = '/scheduled-job';
  const scheduledJobPageUrlPattern = new RegExp('/scheduled-job(\\?.*)?$');
  let username: string;
  let password: string;
  const scheduledJobSample = { jobType: 'EVALUATION', status: 'COMPLETED' };

  let scheduledJob;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/scheduled-jobs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/scheduled-jobs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/scheduled-jobs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (scheduledJob) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/scheduled-jobs/${scheduledJob.id}`,
      }).then(() => {
        scheduledJob = undefined;
      });
    }
  });

  it('ScheduledJobs menu should load ScheduledJobs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('scheduled-job');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ScheduledJob').should('exist');
    cy.url().should('match', scheduledJobPageUrlPattern);
  });

  describe('ScheduledJob page', () => {
    it('should have translated page title', () => {
      cy.visit(scheduledJobPageUrl);
      cy.getEntityHeading('ScheduledJob').should('not.contain', 'proficiencyTestingApp.scheduledJob.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(scheduledJobPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ScheduledJob page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/scheduled-job/new$'));
        cy.getEntityCreateUpdateHeading('ScheduledJob');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', scheduledJobPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/scheduled-jobs',
          body: scheduledJobSample,
        }).then(({ body }) => {
          scheduledJob = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/scheduled-jobs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/scheduled-jobs?page=0&size=20>; rel="last",<http://localhost/api/scheduled-jobs?page=0&size=20>; rel="first"',
              },
              body: [scheduledJob],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(scheduledJobPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ScheduledJob page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('scheduledJob');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', scheduledJobPageUrlPattern);
      });

      it('edit button click should load edit ScheduledJob page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ScheduledJob');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', scheduledJobPageUrlPattern);
      });

      it('edit button click should load edit ScheduledJob page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ScheduledJob');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', scheduledJobPageUrlPattern);
      });

      it('last delete button click should delete instance of ScheduledJob', () => {
        cy.intercept('GET', '/api/scheduled-jobs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('scheduledJob').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', scheduledJobPageUrlPattern);

        scheduledJob = undefined;
      });
    });
  });

  describe('new ScheduledJob page', () => {
    beforeEach(() => {
      cy.visit(scheduledJobPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ScheduledJob');
    });

    it('should create an instance of ScheduledJob', () => {
      cy.get(`[data-cy="jobType"]`).select('CERTIFICATE_GENERATION');

      cy.get(`[data-cy="status"]`).select('CANCELLED');

      cy.get(`[data-cy="requestedBy"]`).type('spring wee');
      cy.get(`[data-cy="requestedBy"]`).should('have.value', 'spring wee');

      cy.get(`[data-cy="requestedOn"]`).type('2026-06-02T06:07');
      cy.get(`[data-cy="requestedOn"]`).blur();
      cy.get(`[data-cy="requestedOn"]`).should('have.value', '2026-06-02T06:07');

      cy.get(`[data-cy="startedAt"]`).type('2026-06-02T06:29');
      cy.get(`[data-cy="startedAt"]`).blur();
      cy.get(`[data-cy="startedAt"]`).should('have.value', '2026-06-02T06:29');

      cy.get(`[data-cy="lastHeartbeat"]`).type('2026-06-02T12:28');
      cy.get(`[data-cy="lastHeartbeat"]`).blur();
      cy.get(`[data-cy="lastHeartbeat"]`).should('have.value', '2026-06-02T12:28');

      cy.get(`[data-cy="completedAt"]`).type('2026-06-02T00:33');
      cy.get(`[data-cy="completedAt"]`).blur();
      cy.get(`[data-cy="completedAt"]`).should('have.value', '2026-06-02T00:33');

      cy.get(`[data-cy="progressCompleted"]`).type('25530');
      cy.get(`[data-cy="progressCompleted"]`).should('have.value', '25530');

      cy.get(`[data-cy="progressTotal"]`).type('30023');
      cy.get(`[data-cy="progressTotal"]`).should('have.value', '30023');

      cy.get(`[data-cy="summary"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="summary"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        scheduledJob = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', scheduledJobPageUrlPattern);
    });
  });
});
