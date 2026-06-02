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

describe('ParticipantCustomValue e2e test', () => {
  const participantCustomValuePageUrl = '/participant-custom-value';
  const participantCustomValuePageUrlPattern = new RegExp('/participant-custom-value(\\?.*)?$');
  let username: string;
  let password: string;
  const participantCustomValueSample = {};

  let participantCustomValue;
  let participant;
  let customFieldDefinition;

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
        uniqueIdentifier: 'and yearn a',
        instituteName: 'yowza',
        departmentName: 'pish community so',
        email: 'I_ML@/>.X',
        additionalEmail: '-grq@y<.9',
        address: 'bah',
        shippingAddress: 'although',
        city: 'Marksfield',
        state: 'primary mountain aboard',
        district: 'ah yearly yum',
        zip: 'before phooey',
        region: 'untimely battle abaft',
        phone: '1-346-365-5382 x501',
        mobile: 'before duh',
        affiliation: 'worth',
        networkTier: 'even',
        siteType: 'deprave outlandish',
        fundingSource: 'woot',
        testingVolume: 7033,
        pepfarId: 'printer opposite oval',
        latitude: 26030.06,
        longitude: 12982.64,
        labDirectorName: 'conjecture devastation for',
        labDirectorEmail: '2h0!5@\\."hm.}UN',
        contactPersonName: 'down',
        contactPersonEmail: 'hP>@CB..2c&ZF/',
        contactPersonPhone: 'to overvalue daily',
        status: 'ACTIVE',
      },
    }).then(({ body }) => {
      participant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/custom-field-definitions',
      body: {
        fieldKey: 'downshift brr yowza',
        label: 'eek',
        fieldType: 'BOOLEAN',
        options: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        displayOrder: 24741,
        status: 'INACTIVE',
      },
    }).then(({ body }) => {
      customFieldDefinition = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/participant-custom-values+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/participant-custom-values').as('postEntityRequest');
    cy.intercept('DELETE', '/api/participant-custom-values/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/participants', {
      statusCode: 200,
      body: [participant],
    });

    cy.intercept('GET', '/api/custom-field-definitions', {
      statusCode: 200,
      body: [customFieldDefinition],
    });
  });

  afterEach(() => {
    if (participantCustomValue) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participant-custom-values/${participantCustomValue.id}`,
      }).then(() => {
        participantCustomValue = undefined;
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
    if (customFieldDefinition) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/custom-field-definitions/${customFieldDefinition.id}`,
      }).then(() => {
        customFieldDefinition = undefined;
      });
    }
  });

  it('ParticipantCustomValues menu should load ParticipantCustomValues page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('participant-custom-value');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ParticipantCustomValue').should('exist');
    cy.url().should('match', participantCustomValuePageUrlPattern);
  });

  describe('ParticipantCustomValue page', () => {
    it('should have translated page title', () => {
      cy.visit(participantCustomValuePageUrl);
      cy.getEntityHeading('ParticipantCustomValue').should('not.contain', 'proficiencyTestingApp.participantCustomValue.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(participantCustomValuePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ParticipantCustomValue page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/participant-custom-value/new$'));
        cy.getEntityCreateUpdateHeading('ParticipantCustomValue');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantCustomValuePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/participant-custom-values',
          body: {
            ...participantCustomValueSample,
            participant,
            definition: customFieldDefinition,
          },
        }).then(({ body }) => {
          participantCustomValue = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/participant-custom-values+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [participantCustomValue],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(participantCustomValuePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ParticipantCustomValue page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('participantCustomValue');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantCustomValuePageUrlPattern);
      });

      it('edit button click should load edit ParticipantCustomValue page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantCustomValue');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantCustomValuePageUrlPattern);
      });

      it('edit button click should load edit ParticipantCustomValue page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParticipantCustomValue');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantCustomValuePageUrlPattern);
      });

      it('last delete button click should delete instance of ParticipantCustomValue', () => {
        cy.intercept('GET', '/api/participant-custom-values/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('participantCustomValue').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantCustomValuePageUrlPattern);

        participantCustomValue = undefined;
      });
    });
  });

  describe('new ParticipantCustomValue page', () => {
    beforeEach(() => {
      cy.visit(participantCustomValuePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ParticipantCustomValue');
    });

    it('should create an instance of ParticipantCustomValue', () => {
      cy.get(`[data-cy="value"]`).type('provided funny');
      cy.get(`[data-cy="value"]`).should('have.value', 'provided funny');

      cy.get(`[data-cy="participant"]`).select(1);
      cy.get(`[data-cy="definition"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        participantCustomValue = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', participantCustomValuePageUrlPattern);
    });
  });
});
