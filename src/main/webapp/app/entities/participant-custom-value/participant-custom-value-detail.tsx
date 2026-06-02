import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './participant-custom-value.reducer';

export const ParticipantCustomValueDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const participantCustomValueEntity = useAppSelector(state => state.participantCustomValue.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="participantCustomValueDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.participantCustomValue.detail.title">ParticipantCustomValue</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{participantCustomValueEntity.id}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="proficiencyTestingApp.participantCustomValue.value">Value</Translate>
            </span>
          </dt>
          <dd>{participantCustomValueEntity.value}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantCustomValue.participant">Participant</Translate>
          </dt>
          <dd>{participantCustomValueEntity.participant ? participantCustomValueEntity.participant.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantCustomValue.definition">Definition</Translate>
          </dt>
          <dd>{participantCustomValueEntity.definition ? participantCustomValueEntity.definition.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/participant-custom-value" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/participant-custom-value/${participantCustomValueEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParticipantCustomValueDetail;
