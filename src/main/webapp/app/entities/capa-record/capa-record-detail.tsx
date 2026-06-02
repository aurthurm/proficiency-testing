import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './capa-record.reducer';

export const CapaRecordDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const capaRecordEntity = useAppSelector(state => state.capaRecord.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="capaRecordDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.capaRecord.detail.title">CapaRecord</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{capaRecordEntity.id}</dd>
          <dt>
            <span id="rootCause">
              <Translate contentKey="proficiencyTestingApp.capaRecord.rootCause">Root Cause</Translate>
            </span>
          </dt>
          <dd>{capaRecordEntity.rootCause}</dd>
          <dt>
            <span id="actionTaken">
              <Translate contentKey="proficiencyTestingApp.capaRecord.actionTaken">Action Taken</Translate>
            </span>
          </dt>
          <dd>{capaRecordEntity.actionTaken}</dd>
          <dt>
            <span id="actionDate">
              <Translate contentKey="proficiencyTestingApp.capaRecord.actionDate">Action Date</Translate>
            </span>
          </dt>
          <dd>
            {capaRecordEntity.actionDate ? (
              <TextFormat value={capaRecordEntity.actionDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.capaRecord.status">Status</Translate>
            </span>
          </dt>
          <dd>{capaRecordEntity.status}</dd>
          <dt>
            <span id="followUpDate">
              <Translate contentKey="proficiencyTestingApp.capaRecord.followUpDate">Follow Up Date</Translate>
            </span>
          </dt>
          <dd>
            {capaRecordEntity.followUpDate ? (
              <TextFormat value={capaRecordEntity.followUpDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.capaRecord.correctiveAction">Corrective Action</Translate>
          </dt>
          <dd>{capaRecordEntity.correctiveAction ? capaRecordEntity.correctiveAction.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.capaRecord.shipmentParticipantMap">Shipment Participant Map</Translate>
          </dt>
          <dd>{capaRecordEntity.shipmentParticipantMap ? capaRecordEntity.shipmentParticipantMap.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/capa-record" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/capa-record/${capaRecordEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CapaRecordDetail;
