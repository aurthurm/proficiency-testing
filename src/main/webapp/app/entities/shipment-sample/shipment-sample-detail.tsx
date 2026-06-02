import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './shipment-sample.reducer';

export const ShipmentSampleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const shipmentSampleEntity = useAppSelector(state => state.shipmentSample.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="shipmentSampleDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.shipmentSample.detail.title">ShipmentSample</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{shipmentSampleEntity.id}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="proficiencyTestingApp.shipmentSample.label">Label</Translate>
            </span>
          </dt>
          <dd>{shipmentSampleEntity.label}</dd>
          <dt>
            <span id="displayOrder">
              <Translate contentKey="proficiencyTestingApp.shipmentSample.displayOrder">Display Order</Translate>
            </span>
          </dt>
          <dd>{shipmentSampleEntity.displayOrder}</dd>
          <dt>
            <span id="isControl">
              <Translate contentKey="proficiencyTestingApp.shipmentSample.isControl">Is Control</Translate>
            </span>
          </dt>
          <dd>{shipmentSampleEntity.isControl ? 'true' : 'false'}</dd>
          <dt>
            <span id="isMandatory">
              <Translate contentKey="proficiencyTestingApp.shipmentSample.isMandatory">Is Mandatory</Translate>
            </span>
          </dt>
          <dd>{shipmentSampleEntity.isMandatory ? 'true' : 'false'}</dd>
          <dt>
            <span id="sampleScore">
              <Translate contentKey="proficiencyTestingApp.shipmentSample.sampleScore">Sample Score</Translate>
            </span>
          </dt>
          <dd>{shipmentSampleEntity.sampleScore}</dd>
          <dt>
            <span id="preparationDate">
              <Translate contentKey="proficiencyTestingApp.shipmentSample.preparationDate">Preparation Date</Translate>
            </span>
          </dt>
          <dd>
            {shipmentSampleEntity.preparationDate ? (
              <TextFormat value={shipmentSampleEntity.preparationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipmentSample.shipment">Shipment</Translate>
          </dt>
          <dd>{shipmentSampleEntity.shipment ? shipmentSampleEntity.shipment.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/shipment-sample" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/shipment-sample/${shipmentSampleEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ShipmentSampleDetail;
