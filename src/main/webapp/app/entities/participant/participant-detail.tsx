import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './participant.reducer';

export const ParticipantDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const participantEntity = useAppSelector(state => state.participant.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="participantDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.participant.detail.title">Participant</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{participantEntity.id}</dd>
          <dt>
            <span id="uniqueIdentifier">
              <Translate contentKey="proficiencyTestingApp.participant.uniqueIdentifier">Unique Identifier</Translate>
            </span>
          </dt>
          <dd>{participantEntity.uniqueIdentifier}</dd>
          <dt>
            <span id="instituteName">
              <Translate contentKey="proficiencyTestingApp.participant.instituteName">Institute Name</Translate>
            </span>
          </dt>
          <dd>{participantEntity.instituteName}</dd>
          <dt>
            <span id="departmentName">
              <Translate contentKey="proficiencyTestingApp.participant.departmentName">Department Name</Translate>
            </span>
          </dt>
          <dd>{participantEntity.departmentName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="proficiencyTestingApp.participant.email">Email</Translate>
            </span>
          </dt>
          <dd>{participantEntity.email}</dd>
          <dt>
            <span id="additionalEmail">
              <Translate contentKey="proficiencyTestingApp.participant.additionalEmail">Additional Email</Translate>
            </span>
          </dt>
          <dd>{participantEntity.additionalEmail}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="proficiencyTestingApp.participant.address">Address</Translate>
            </span>
          </dt>
          <dd>{participantEntity.address}</dd>
          <dt>
            <span id="shippingAddress">
              <Translate contentKey="proficiencyTestingApp.participant.shippingAddress">Shipping Address</Translate>
            </span>
          </dt>
          <dd>{participantEntity.shippingAddress}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="proficiencyTestingApp.participant.city">City</Translate>
            </span>
          </dt>
          <dd>{participantEntity.city}</dd>
          <dt>
            <span id="state">
              <Translate contentKey="proficiencyTestingApp.participant.state">State</Translate>
            </span>
          </dt>
          <dd>{participantEntity.state}</dd>
          <dt>
            <span id="district">
              <Translate contentKey="proficiencyTestingApp.participant.district">District</Translate>
            </span>
          </dt>
          <dd>{participantEntity.district}</dd>
          <dt>
            <span id="zip">
              <Translate contentKey="proficiencyTestingApp.participant.zip">Zip</Translate>
            </span>
          </dt>
          <dd>{participantEntity.zip}</dd>
          <dt>
            <span id="region">
              <Translate contentKey="proficiencyTestingApp.participant.region">Region</Translate>
            </span>
          </dt>
          <dd>{participantEntity.region}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="proficiencyTestingApp.participant.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{participantEntity.phone}</dd>
          <dt>
            <span id="mobile">
              <Translate contentKey="proficiencyTestingApp.participant.mobile">Mobile</Translate>
            </span>
          </dt>
          <dd>{participantEntity.mobile}</dd>
          <dt>
            <span id="affiliation">
              <Translate contentKey="proficiencyTestingApp.participant.affiliation">Affiliation</Translate>
            </span>
          </dt>
          <dd>{participantEntity.affiliation}</dd>
          <dt>
            <span id="networkTier">
              <Translate contentKey="proficiencyTestingApp.participant.networkTier">Network Tier</Translate>
            </span>
          </dt>
          <dd>{participantEntity.networkTier}</dd>
          <dt>
            <span id="siteType">
              <Translate contentKey="proficiencyTestingApp.participant.siteType">Site Type</Translate>
            </span>
          </dt>
          <dd>{participantEntity.siteType}</dd>
          <dt>
            <span id="fundingSource">
              <Translate contentKey="proficiencyTestingApp.participant.fundingSource">Funding Source</Translate>
            </span>
          </dt>
          <dd>{participantEntity.fundingSource}</dd>
          <dt>
            <span id="testingVolume">
              <Translate contentKey="proficiencyTestingApp.participant.testingVolume">Testing Volume</Translate>
            </span>
          </dt>
          <dd>{participantEntity.testingVolume}</dd>
          <dt>
            <span id="pepfarId">
              <Translate contentKey="proficiencyTestingApp.participant.pepfarId">Pepfar Id</Translate>
            </span>
          </dt>
          <dd>{participantEntity.pepfarId}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="proficiencyTestingApp.participant.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{participantEntity.latitude}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="proficiencyTestingApp.participant.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{participantEntity.longitude}</dd>
          <dt>
            <span id="labDirectorName">
              <Translate contentKey="proficiencyTestingApp.participant.labDirectorName">Lab Director Name</Translate>
            </span>
          </dt>
          <dd>{participantEntity.labDirectorName}</dd>
          <dt>
            <span id="labDirectorEmail">
              <Translate contentKey="proficiencyTestingApp.participant.labDirectorEmail">Lab Director Email</Translate>
            </span>
          </dt>
          <dd>{participantEntity.labDirectorEmail}</dd>
          <dt>
            <span id="contactPersonName">
              <Translate contentKey="proficiencyTestingApp.participant.contactPersonName">Contact Person Name</Translate>
            </span>
          </dt>
          <dd>{participantEntity.contactPersonName}</dd>
          <dt>
            <span id="contactPersonEmail">
              <Translate contentKey="proficiencyTestingApp.participant.contactPersonEmail">Contact Person Email</Translate>
            </span>
          </dt>
          <dd>{participantEntity.contactPersonEmail}</dd>
          <dt>
            <span id="contactPersonPhone">
              <Translate contentKey="proficiencyTestingApp.participant.contactPersonPhone">Contact Person Phone</Translate>
            </span>
          </dt>
          <dd>{participantEntity.contactPersonPhone}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.participant.status">Status</Translate>
            </span>
          </dt>
          <dd>{participantEntity.status}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participant.country">Country</Translate>
          </dt>
          <dd>{participantEntity.country ? participantEntity.country.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participant.dataManagers">Data Managers</Translate>
          </dt>
          <dd>
            {participantEntity.dataManagerses
              ? participantEntity.dataManagerses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {participantEntity.dataManagerses && i === participantEntity.dataManagerses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/participant" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/participant/${participantEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParticipantDetail;
