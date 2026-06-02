import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './participant-message.reducer';

export const ParticipantMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const participantMessageEntity = useAppSelector(state => state.participantMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="participantMessageDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.participantMessage.detail.title">ParticipantMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{participantMessageEntity.id}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="proficiencyTestingApp.participantMessage.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{participantMessageEntity.subject}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="proficiencyTestingApp.participantMessage.body">Body</Translate>
            </span>
          </dt>
          <dd>{participantMessageEntity.body}</dd>
          <dt>
            <span id="isRead">
              <Translate contentKey="proficiencyTestingApp.participantMessage.isRead">Is Read</Translate>
            </span>
          </dt>
          <dd>{participantMessageEntity.isRead ? 'true' : 'false'}</dd>
          <dt>
            <span id="sentAt">
              <Translate contentKey="proficiencyTestingApp.participantMessage.sentAt">Sent At</Translate>
            </span>
          </dt>
          <dd>
            {participantMessageEntity.sentAt ? (
              <TextFormat value={participantMessageEntity.sentAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantMessage.participant">Participant</Translate>
          </dt>
          <dd>{participantMessageEntity.participant ? participantMessageEntity.participant.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/participant-message" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/participant-message/${participantMessageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParticipantMessageDetail;
