import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './data-manager.reducer';

export const DataManagerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const dataManagerEntity = useAppSelector(state => state.dataManager.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dataManagerDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.dataManager.detail.title">DataManager</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="proficiencyTestingApp.dataManager.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="proficiencyTestingApp.dataManager.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.lastName}</dd>
          <dt>
            <span id="institute">
              <Translate contentKey="proficiencyTestingApp.dataManager.institute">Institute</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.institute}</dd>
          <dt>
            <span id="primaryEmail">
              <Translate contentKey="proficiencyTestingApp.dataManager.primaryEmail">Primary Email</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.primaryEmail}</dd>
          <dt>
            <span id="secondaryEmail">
              <Translate contentKey="proficiencyTestingApp.dataManager.secondaryEmail">Secondary Email</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.secondaryEmail}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="proficiencyTestingApp.dataManager.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.phone}</dd>
          <dt>
            <span id="mobile">
              <Translate contentKey="proficiencyTestingApp.dataManager.mobile">Mobile</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.mobile}</dd>
          <dt>
            <span id="language">
              <Translate contentKey="proficiencyTestingApp.dataManager.language">Language</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.language}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="proficiencyTestingApp.dataManager.role">Role</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.role}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.dataManager.status">Status</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.status}</dd>
          <dt>
            <span id="qcAccess">
              <Translate contentKey="proficiencyTestingApp.dataManager.qcAccess">Qc Access</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.qcAccess ? 'true' : 'false'}</dd>
          <dt>
            <span id="viewOnlyAccess">
              <Translate contentKey="proficiencyTestingApp.dataManager.viewOnlyAccess">View Only Access</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.viewOnlyAccess ? 'true' : 'false'}</dd>
          <dt>
            <span id="enableTestResponseDate">
              <Translate contentKey="proficiencyTestingApp.dataManager.enableTestResponseDate">Enable Test Response Date</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.enableTestResponseDate ? 'true' : 'false'}</dd>
          <dt>
            <span id="enableModeOfReceipt">
              <Translate contentKey="proficiencyTestingApp.dataManager.enableModeOfReceipt">Enable Mode Of Receipt</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.enableModeOfReceipt ? 'true' : 'false'}</dd>
          <dt>
            <span id="forceProfileCheck">
              <Translate contentKey="proficiencyTestingApp.dataManager.forceProfileCheck">Force Profile Check</Translate>
            </span>
          </dt>
          <dd>{dataManagerEntity.forceProfileCheck ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.dataManager.user">User</Translate>
          </dt>
          <dd>{dataManagerEntity.user ? dataManagerEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.dataManager.country">Country</Translate>
          </dt>
          <dd>{dataManagerEntity.country ? dataManagerEntity.country.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.dataManager.participants">Participants</Translate>
          </dt>
          <dd>
            {dataManagerEntity.participantses
              ? dataManagerEntity.participantses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {dataManagerEntity.participantses && i === dataManagerEntity.participantses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/data-manager" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/data-manager/${dataManagerEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DataManagerDetail;
