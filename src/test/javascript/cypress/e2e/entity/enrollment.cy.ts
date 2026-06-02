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

describe('Enrollment e2e test', () => {
  const enrollmentPageUrl = '/enrollment';
  const enrollmentPageUrlPattern = new RegExp('/enrollment(\\?.*)?$');
  let username: string;
  let password: string;
  const enrollmentSample = { status: 'WITHDRAWN', enrolledOn: '2026-06-02' };

  let enrollment;
  let participant;
  let scheme;

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
        uniqueIdentifier: 'nervously ha thoroughly',
        instituteName: 'oh',
        departmentName: 'energetically offensively',
        email: 'Jl@C67$l:.}};',
        additionalEmail: "&.M@l.?B'.",
        address: 'devil',
        shippingAddress: 'impeccable puff',
        city: 'New Roxanne',
        state: 'of',
        district: 'hence',
        zip: 'anesthetize',
        region: 'fall for',
        phone: '(422) 402-9230',
        mobile: 'frozen slowly dark',
        affiliation: 'mid upliftingly',
        networkTier: 'physical',
        siteType: 'delight miserly oof',
        fundingSource: 'bathrobe where unlearn',
        testingVolume: 29285,
        pepfarId: 'bouncy tall scenario',
        latitude: 12795.82,
        longitude: 13977.87,
        labDirectorName: 'worth',
        labDirectorEmail: 's@/C.O',
        contactPersonName: 'councilman sticky',
        contactPersonEmail: 'Dal@n{czMV.=FG$}:',
        contactPersonPhone: 'gosh rawhide yuck',
        status: 'INACTIVE',
      },
    }).then(({ body }) => {
      participant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/schemes',
      body: { code: 'gleefully formamide', name: 'joyously', schemeType: 'RECENCY', modality: 'QUALITATIVE', status: 'INACTIVE' },
    }).then(({ body }) => {
      scheme = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/enrollments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/enrollments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/enrollments/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/participants', {
      statusCode: 200,
      body: [participant],
    });

    cy.intercept('GET', '/api/schemes', {
      statusCode: 200,
      body: [scheme],
    });
  });

  afterEach(() => {
    if (enrollment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/enrollments/${enrollment.id}`,
      }).then(() => {
        enrollment = undefined;
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
    if (scheme) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/schemes/${scheme.id}`,
      }).then(() => {
        scheme = undefined;
      });
    }
  });

  it('Enrollments menu should load Enrollments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('enrollment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Enrollment').should('exist');
    cy.url().should('match', enrollmentPageUrlPattern);
  });

  describe('Enrollment page', () => {
    it('should have translated page title', () => {
      cy.visit(enrollmentPageUrl);
      cy.getEntityHeading('Enrollment').should('not.contain', 'proficiencyTestingApp.enrollment.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(enrollmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Enrollment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/enrollment/new$'));
        cy.getEntityCreateUpdateHeading('Enrollment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', enrollmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/enrollments',
          body: {
            ...enrollmentSample,
            participant,
            scheme,
          },
        }).then(({ body }) => {
          enrollment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/enrollments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/enrollments?page=0&size=20>; rel="last",<http://localhost/api/enrollments?page=0&size=20>; rel="first"',
              },
              body: [enrollment],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(enrollmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Enrollment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('enrollment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', enrollmentPageUrlPattern);
      });

      it('edit button click should load edit Enrollment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Enrollment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', enrollmentPageUrlPattern);
      });

      it('edit button click should load edit Enrollment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Enrollment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', enrollmentPageUrlPattern);
      });

      it('last delete button click should delete instance of Enrollment', () => {
        cy.intercept('GET', '/api/enrollments/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('enrollment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', enrollmentPageUrlPattern);

        enrollment = undefined;
      });
    });
  });

  describe('new Enrollment page', () => {
    beforeEach(() => {
      cy.visit(enrollmentPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Enrollment');
    });

    it('should create an instance of Enrollment', () => {
      cy.get(`[data-cy="status"]`).select('WITHDRAWN');

      cy.get(`[data-cy="enrolledOn"]`).type('2026-06-02');
      cy.get(`[data-cy="enrolledOn"]`).blur();
      cy.get(`[data-cy="enrolledOn"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="withdrawnOn"]`).type('2026-06-02');
      cy.get(`[data-cy="withdrawnOn"]`).blur();
      cy.get(`[data-cy="withdrawnOn"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="participant"]`).select(1);
      cy.get(`[data-cy="scheme"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        enrollment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', enrollmentPageUrlPattern);
    });
  });
});
