import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './shipment.reducer';

export const ShipmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const shipmentEntity = useAppSelector(state => state.shipment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="shipmentDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.shipment.detail.title">Shipment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="proficiencyTestingApp.shipment.code">Code</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.code}</dd>
          <dt>
            <span id="shipmentDate">
              <Translate contentKey="proficiencyTestingApp.shipment.shipmentDate">Shipment Date</Translate>
            </span>
          </dt>
          <dd>
            {shipmentEntity.shipmentDate ? (
              <TextFormat value={shipmentEntity.shipmentDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="responseDeadline">
              <Translate contentKey="proficiencyTestingApp.shipment.responseDeadline">Response Deadline</Translate>
            </span>
          </dt>
          <dd>
            {shipmentEntity.responseDeadline ? (
              <TextFormat value={shipmentEntity.responseDeadline} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="responsesOpen">
              <Translate contentKey="proficiencyTestingApp.shipment.responsesOpen">Responses Open</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.responsesOpen ? 'true' : 'false'}</dd>
          <dt>
            <span id="autoCloseAtDeadline">
              <Translate contentKey="proficiencyTestingApp.shipment.autoCloseAtDeadline">Auto Close At Deadline</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.autoCloseAtDeadline ? 'true' : 'false'}</dd>
          <dt>
            <span id="allowEditingResponse">
              <Translate contentKey="proficiencyTestingApp.shipment.allowEditingResponse">Allow Editing Response</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.allowEditingResponse ? 'true' : 'false'}</dd>
          <dt>
            <span id="issuingAuthority">
              <Translate contentKey="proficiencyTestingApp.shipment.issuingAuthority">Issuing Authority</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.issuingAuthority}</dd>
          <dt>
            <span id="coordinatorName">
              <Translate contentKey="proficiencyTestingApp.shipment.coordinatorName">Coordinator Name</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.coordinatorName}</dd>
          <dt>
            <span id="coordinatorEmail">
              <Translate contentKey="proficiencyTestingApp.shipment.coordinatorEmail">Coordinator Email</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.coordinatorEmail}</dd>
          <dt>
            <span id="coordinatorPhone">
              <Translate contentKey="proficiencyTestingApp.shipment.coordinatorPhone">Coordinator Phone</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.coordinatorPhone}</dd>
          <dt>
            <span id="numberOfSamples">
              <Translate contentKey="proficiencyTestingApp.shipment.numberOfSamples">Number Of Samples</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.numberOfSamples}</dd>
          <dt>
            <span id="maxScore">
              <Translate contentKey="proficiencyTestingApp.shipment.maxScore">Max Score</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.maxScore}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.shipment.status">Status</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.status}</dd>
          <dt>
            <span id="attributes">
              <Translate contentKey="proficiencyTestingApp.shipment.attributes">Attributes</Translate>
            </span>
          </dt>
          <dd>{shipmentEntity.attributes}</dd>
          <dt>
            <span id="reportsGeneratedAt">
              <Translate contentKey="proficiencyTestingApp.shipment.reportsGeneratedAt">Reports Generated At</Translate>
            </span>
          </dt>
          <dd>
            {shipmentEntity.reportsGeneratedAt ? (
              <TextFormat value={shipmentEntity.reportsGeneratedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="finalizedAt">
              <Translate contentKey="proficiencyTestingApp.shipment.finalizedAt">Finalized At</Translate>
            </span>
          </dt>
          <dd>
            {shipmentEntity.finalizedAt ? <TextFormat value={shipmentEntity.finalizedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipment.distribution">Distribution</Translate>
          </dt>
          <dd>{shipmentEntity.distribution ? shipmentEntity.distribution.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipment.scheme">Scheme</Translate>
          </dt>
          <dd>{shipmentEntity.scheme ? shipmentEntity.scheme.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipment.certificateBatches">Certificate Batches</Translate>
          </dt>
          <dd>
            {shipmentEntity.certificateBatcheses
              ? shipmentEntity.certificateBatcheses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {shipmentEntity.certificateBatcheses && i === shipmentEntity.certificateBatcheses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/shipment" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/shipment/${shipmentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ShipmentDetail;
