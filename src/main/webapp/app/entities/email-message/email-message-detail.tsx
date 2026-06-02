import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './email-message.reducer';

export const EmailMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const emailMessageEntity = useAppSelector(state => state.emailMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="emailMessageDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.emailMessage.detail.title">EmailMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.id}</dd>
          <dt>
            <span id="fromEmail">
              <Translate contentKey="proficiencyTestingApp.emailMessage.fromEmail">From Email</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.fromEmail}</dd>
          <dt>
            <span id="fromName">
              <Translate contentKey="proficiencyTestingApp.emailMessage.fromName">From Name</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.fromName}</dd>
          <dt>
            <span id="replyTo">
              <Translate contentKey="proficiencyTestingApp.emailMessage.replyTo">Reply To</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.replyTo}</dd>
          <dt>
            <span id="toEmail">
              <Translate contentKey="proficiencyTestingApp.emailMessage.toEmail">To Email</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.toEmail}</dd>
          <dt>
            <span id="cc">
              <Translate contentKey="proficiencyTestingApp.emailMessage.cc">Cc</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.cc}</dd>
          <dt>
            <span id="bcc">
              <Translate contentKey="proficiencyTestingApp.emailMessage.bcc">Bcc</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.bcc}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="proficiencyTestingApp.emailMessage.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.subject}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="proficiencyTestingApp.emailMessage.body">Body</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.body}</dd>
          <dt>
            <span id="attachmentRef">
              <Translate contentKey="proficiencyTestingApp.emailMessage.attachmentRef">Attachment Ref</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.attachmentRef}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.emailMessage.status">Status</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.status}</dd>
          <dt>
            <span id="failureType">
              <Translate contentKey="proficiencyTestingApp.emailMessage.failureType">Failure Type</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.failureType}</dd>
          <dt>
            <span id="failureReason">
              <Translate contentKey="proficiencyTestingApp.emailMessage.failureReason">Failure Reason</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.failureReason}</dd>
          <dt>
            <span id="queuedOn">
              <Translate contentKey="proficiencyTestingApp.emailMessage.queuedOn">Queued On</Translate>
            </span>
          </dt>
          <dd>
            {emailMessageEntity.queuedOn ? <TextFormat value={emailMessageEntity.queuedOn} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="sentAt">
              <Translate contentKey="proficiencyTestingApp.emailMessage.sentAt">Sent At</Translate>
            </span>
          </dt>
          <dd>
            {emailMessageEntity.sentAt ? <TextFormat value={emailMessageEntity.sentAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="retryCount">
              <Translate contentKey="proficiencyTestingApp.emailMessage.retryCount">Retry Count</Translate>
            </span>
          </dt>
          <dd>{emailMessageEntity.retryCount}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.emailMessage.template">Template</Translate>
          </dt>
          <dd>{emailMessageEntity.template ? emailMessageEntity.template.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/email-message" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/email-message/${emailMessageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EmailMessageDetail;
