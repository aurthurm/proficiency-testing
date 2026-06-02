import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './participant-feedback.reducer';

export const ParticipantFeedbackDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const participantFeedbackEntity = useAppSelector(state => state.participantFeedback.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="participantFeedbackDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.participantFeedback.detail.title">ParticipantFeedback</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{participantFeedbackEntity.id}</dd>
          <dt>
            <span id="answer">
              <Translate contentKey="proficiencyTestingApp.participantFeedback.answer">Answer</Translate>
            </span>
          </dt>
          <dd>{participantFeedbackEntity.answer}</dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="proficiencyTestingApp.participantFeedback.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {participantFeedbackEntity.submittedAt ? (
              <TextFormat value={participantFeedbackEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantFeedback.question">Question</Translate>
          </dt>
          <dd>{participantFeedbackEntity.question ? participantFeedbackEntity.question.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantFeedback.participant">Participant</Translate>
          </dt>
          <dd>{participantFeedbackEntity.participant ? participantFeedbackEntity.participant.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantFeedback.shipment">Shipment</Translate>
          </dt>
          <dd>{participantFeedbackEntity.shipment ? participantFeedbackEntity.shipment.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/participant-feedback" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/participant-feedback/${participantFeedbackEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParticipantFeedbackDetail;
