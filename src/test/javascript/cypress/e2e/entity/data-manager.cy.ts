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

describe('DataManager e2e test', () => {
  const dataManagerPageUrl = '/data-manager';
  const dataManagerPageUrlPattern = new RegExp('/data-manager(\\?.*)?$');
  let username: string;
  let password: string;
  const dataManagerSample = { primaryEmail: 'yZM{D@5{Enf.kd]%C', role: 'PTCC', status: 'INACTIVE' };

  let dataManager;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/data-managers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/data-managers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/data-managers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (dataManager) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/data-managers/${dataManager.id}`,
      }).then(() => {
        dataManager = undefined;
      });
    }
  });

  it('DataManagers menu should load DataManagers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('data-manager');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DataManager').should('exist');
    cy.url().should('match', dataManagerPageUrlPattern);
  });

  describe('DataManager page', () => {
    it('should have translated page title', () => {
      cy.visit(dataManagerPageUrl);
      cy.getEntityHeading('DataManager').should('not.contain', 'proficiencyTestingApp.dataManager.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(dataManagerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DataManager page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/data-manager/new$'));
        cy.getEntityCreateUpdateHeading('DataManager');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', dataManagerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/data-managers',
          body: dataManagerSample,
        }).then(({ body }) => {
          dataManager = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/data-managers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/data-managers?page=0&size=20>; rel="last",<http://localhost/api/data-managers?page=0&size=20>; rel="first"',
              },
              body: [dataManager],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(dataManagerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DataManager page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('dataManager');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', dataManagerPageUrlPattern);
      });

      it('edit button click should load edit DataManager page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DataManager');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', dataManagerPageUrlPattern);
      });

      it('edit button click should load edit DataManager page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DataManager');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', dataManagerPageUrlPattern);
      });

      it('last delete button click should delete instance of DataManager', () => {
        cy.intercept('GET', '/api/data-managers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('dataManager').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', dataManagerPageUrlPattern);

        dataManager = undefined;
      });
    });
  });

  describe('new DataManager page', () => {
    beforeEach(() => {
      cy.visit(dataManagerPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DataManager');
    });

    it('should create an instance of DataManager', () => {
      cy.get(`[data-cy="firstName"]`).type('Tim');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Tim');

      cy.get(`[data-cy="lastName"]`).type('Wyman');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Wyman');

      cy.get(`[data-cy="institute"]`).type('nor');
      cy.get(`[data-cy="institute"]`).should('have.value', 'nor');

      cy.get(`[data-cy="primaryEmail"]`).type('rI|/@h0.Y(?uU');
      cy.get(`[data-cy="primaryEmail"]`).should('have.value', 'rI|/@h0.Y(?uU');

      cy.get(`[data-cy="secondaryEmail"]`).type('K)@DfHk.=b$');
      cy.get(`[data-cy="secondaryEmail"]`).should('have.value', 'K)@DfHk.=b$');

      cy.get(`[data-cy="phone"]`).type('290-312-4631');
      cy.get(`[data-cy="phone"]`).should('have.value', '290-312-4631');

      cy.get(`[data-cy="mobile"]`).type('scholarship mmm');
      cy.get(`[data-cy="mobile"]`).should('have.value', 'scholarship mmm');

      cy.get(`[data-cy="language"]`).type('mutate content');
      cy.get(`[data-cy="language"]`).should('have.value', 'mutate content');

      cy.get(`[data-cy="role"]`).select('MANAGER');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="qcAccess"]`).should('not.be.checked');
      cy.get(`[data-cy="qcAccess"]`).click();
      cy.get(`[data-cy="qcAccess"]`).should('be.checked');

      cy.get(`[data-cy="viewOnlyAccess"]`).should('not.be.checked');
      cy.get(`[data-cy="viewOnlyAccess"]`).click();
      cy.get(`[data-cy="viewOnlyAccess"]`).should('be.checked');

      cy.get(`[data-cy="enableTestResponseDate"]`).should('not.be.checked');
      cy.get(`[data-cy="enableTestResponseDate"]`).click();
      cy.get(`[data-cy="enableTestResponseDate"]`).should('be.checked');

      cy.get(`[data-cy="enableModeOfReceipt"]`).should('not.be.checked');
      cy.get(`[data-cy="enableModeOfReceipt"]`).click();
      cy.get(`[data-cy="enableModeOfReceipt"]`).should('be.checked');

      cy.get(`[data-cy="forceProfileCheck"]`).should('not.be.checked');
      cy.get(`[data-cy="forceProfileCheck"]`).click();
      cy.get(`[data-cy="forceProfileCheck"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        dataManager = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', dataManagerPageUrlPattern);
    });
  });
});
