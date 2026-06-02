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

describe('ShipmentParticipantMap e2e test', () => {
  const shipmentParticipantMapPageUrl = '/shipment-participant-map';
  const shipmentParticipantMapPageUrlPattern = new RegExp('/shipment-participant-map(\\?.*)?$');
  let username: string;
  let password: string;
  // const shipmentParticipantMapSample = {};

  let shipmentParticipantMap;
  // let shipment;
  // let participant;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/shipments',
      body: {"code":"boohoo","shipmentDate":"2026-06-01","responseDeadline":"2026-06-02T13:00:21.587Z","responsesOpen":false,"autoCloseAtDeadline":true,"allowEditingResponse":true,"issuingAuthority":"polite upon","coordinatorName":"finally","coordinatorEmail":"5@&t!b=.?","coordinatorPhone":"solemnly","numberOfSamples":7047,"maxScore":13265,"status":"RESPONSES_CLOSED","attributes":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","reportsGeneratedAt":"2026-06-01T23:00:28.615Z","finalizedAt":"2026-06-02T03:38:30.766Z"},
    }).then(({ body }) => {
      shipment = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/participants',
      body: {"uniqueIdentifier":"who shrill","instituteName":"manner","departmentName":"scratchy scramble meh","email":"4QoPJ2@mkd'.]&","additionalEmail":"txu@$]]%Pq.}u!i3-","address":"whoa","shippingAddress":"acquaintance","city":"Sierracester","state":"after immediately","district":"onto","zip":"transcend motionless impostor","region":"like idle","phone":"394-753-6612 x336","mobile":"for beneath shakily","affiliation":"not terraform","networkTier":"um","siteType":"cheerful expansion","fundingSource":"nephew","testingVolume":27579,"pepfarId":"compromise because which","latitude":28686.15,"longitude":5405.71,"labDirectorName":"whether generally","labDirectorEmail":"1W4[@fm.1mI","contactPersonName":"jagged","contactPersonEmail":"s}$%@L>.xXk>","contactPersonPhone":"signature unto","status":"PENDING"},
    }).then(({ body }) => {
      participant = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/shipment-participant-maps+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shipment-participant-maps').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shipment-participant-maps/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/participant-results', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/capa-records', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/mode-of-receipts', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/not-tested-reasons', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [shipment],
    });

    cy.intercept('GET', '/api/participants', {
      statusCode: 200,
      body: [participant],
    });

  });
   */

  afterEach(() => {
    if (shipmentParticipantMap) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-participant-maps/${shipmentParticipantMap.id}`,
      }).then(() => {
        shipmentParticipantMap = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (shipment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipments/${shipment.id}`,
      }).then(() => {
        shipment = undefined;
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
   */

  it('ShipmentParticipantMaps menu should load ShipmentParticipantMaps page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shipment-participant-map');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShipmentParticipantMap').should('exist');
    cy.url().should('match', shipmentParticipantMapPageUrlPattern);
  });

  describe('ShipmentParticipantMap page', () => {
    it('should have translated page title', () => {
      cy.visit(shipmentParticipantMapPageUrl);
      cy.getEntityHeading('ShipmentParticipantMap').should('not.contain', 'proficiencyTestingApp.shipmentParticipantMap.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shipmentParticipantMapPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShipmentParticipantMap page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shipment-participant-map/new$'));
        cy.getEntityCreateUpdateHeading('ShipmentParticipantMap');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentParticipantMapPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shipment-participant-maps',
          body: {
            ...shipmentParticipantMapSample,
            shipment: shipment,
            participant: participant,
          },
        }).then(({ body }) => {
          shipmentParticipantMap = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shipment-participant-maps+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/shipment-participant-maps?page=0&size=20>; rel="last",<http://localhost/api/shipment-participant-maps?page=0&size=20>; rel="first"',
              },
              body: [shipmentParticipantMap],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(shipmentParticipantMapPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(shipmentParticipantMapPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ShipmentParticipantMap page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shipmentParticipantMap');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentParticipantMapPageUrlPattern);
      });

      it('edit button click should load edit ShipmentParticipantMap page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentParticipantMap');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentParticipantMapPageUrlPattern);
      });

      it('edit button click should load edit ShipmentParticipantMap page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentParticipantMap');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentParticipantMapPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ShipmentParticipantMap', () => {
        cy.intercept('GET', '/api/shipment-participant-maps/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('shipmentParticipantMap').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentParticipantMapPageUrlPattern);

        shipmentParticipantMap = undefined;
      });
    });
  });

  describe('new ShipmentParticipantMap page', () => {
    beforeEach(() => {
      cy.visit(shipmentParticipantMapPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShipmentParticipantMap');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ShipmentParticipantMap', () => {
      cy.get(`[data-cy="responseStatus"]`).select('LATE');

      cy.get(`[data-cy="shipmentReceiptDate"]`).type('2026-06-02');
      cy.get(`[data-cy="shipmentReceiptDate"]`).blur();
      cy.get(`[data-cy="shipmentReceiptDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="shipmentTestDate"]`).type('2026-06-02');
      cy.get(`[data-cy="shipmentTestDate"]`).blur();
      cy.get(`[data-cy="shipmentTestDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="shipmentTestReportDate"]`).type('2026-06-02T15:58');
      cy.get(`[data-cy="shipmentTestReportDate"]`).blur();
      cy.get(`[data-cy="shipmentTestReportDate"]`).should('have.value', '2026-06-02T15:58');

      cy.get(`[data-cy="submittedAt"]`).type('2026-06-02T00:40');
      cy.get(`[data-cy="submittedAt"]`).blur();
      cy.get(`[data-cy="submittedAt"]`).should('have.value', '2026-06-02T00:40');

      cy.get(`[data-cy="evaluatedAt"]`).type('2026-06-02T05:58');
      cy.get(`[data-cy="evaluatedAt"]`).blur();
      cy.get(`[data-cy="evaluatedAt"]`).should('have.value', '2026-06-02T05:58');

      cy.get(`[data-cy="isExcluded"]`).should('not.be.checked');
      cy.get(`[data-cy="isExcluded"]`).click();
      cy.get(`[data-cy="isExcluded"]`).should('be.checked');

      cy.get(`[data-cy="isResponseLate"]`).should('not.be.checked');
      cy.get(`[data-cy="isResponseLate"]`).click();
      cy.get(`[data-cy="isResponseLate"]`).should('be.checked');

      cy.get(`[data-cy="isPtTestNotPerformed"]`).should('not.be.checked');
      cy.get(`[data-cy="isPtTestNotPerformed"]`).click();
      cy.get(`[data-cy="isPtTestNotPerformed"]`).should('be.checked');

      cy.get(`[data-cy="ptTestNotPerformedComments"]`).type('imaginary for broadly');
      cy.get(`[data-cy="ptTestNotPerformedComments"]`).should('have.value', 'imaginary for broadly');

      cy.get(`[data-cy="supervisorApproved"]`).should('not.be.checked');
      cy.get(`[data-cy="supervisorApproved"]`).click();
      cy.get(`[data-cy="supervisorApproved"]`).should('be.checked');

      cy.get(`[data-cy="participantSupervisor"]`).type('commodity incidentally foolhardy');
      cy.get(`[data-cy="participantSupervisor"]`).should('have.value', 'commodity incidentally foolhardy');

      cy.get(`[data-cy="userComment"]`).type('nectarine');
      cy.get(`[data-cy="userComment"]`).should('have.value', 'nectarine');

      cy.get(`[data-cy="shipmentScore"]`).type('3325.04');
      cy.get(`[data-cy="shipmentScore"]`).should('have.value', '3325.04');

      cy.get(`[data-cy="documentationScore"]`).type('30567.39');
      cy.get(`[data-cy="documentationScore"]`).should('have.value', '30567.39');

      cy.get(`[data-cy="finalResult"]`).select('FAIL');

      cy.get(`[data-cy="failureReason"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="failureReason"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="evaluationComment"]`).type('crackle into wherever');
      cy.get(`[data-cy="evaluationComment"]`).should('have.value', 'crackle into wherever');

      cy.get(`[data-cy="isFollowup"]`).should('not.be.checked');
      cy.get(`[data-cy="isFollowup"]`).click();
      cy.get(`[data-cy="isFollowup"]`).should('be.checked');

      cy.get(`[data-cy="manualOverride"]`).should('not.be.checked');
      cy.get(`[data-cy="manualOverride"]`).click();
      cy.get(`[data-cy="manualOverride"]`).should('be.checked');

      cy.get(`[data-cy="qcStatus"]`).select('PASSED');

      cy.get(`[data-cy="qcDate"]`).type('2026-06-02');
      cy.get(`[data-cy="qcDate"]`).blur();
      cy.get(`[data-cy="qcDate"]`).should('have.value', '2026-06-02');

      cy.get(`[data-cy="qcDoneBy"]`).type('oh source');
      cy.get(`[data-cy="qcDoneBy"]`).should('have.value', 'oh source');

      cy.get(`[data-cy="syncedToMobile"]`).should('not.be.checked');
      cy.get(`[data-cy="syncedToMobile"]`).click();
      cy.get(`[data-cy="syncedToMobile"]`).should('be.checked');

      cy.get(`[data-cy="syncedOn"]`).type('2026-06-02T03:56');
      cy.get(`[data-cy="syncedOn"]`).blur();
      cy.get(`[data-cy="syncedOn"]`).should('have.value', '2026-06-02T03:56');

      cy.get(`[data-cy="shipment"]`).select(1);
      cy.get(`[data-cy="participant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shipmentParticipantMap = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shipmentParticipantMapPageUrlPattern);
    });
  });
});
