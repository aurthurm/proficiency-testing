import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './contact-message.reducer';

export const ContactMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const contactMessageEntity = useAppSelector(state => state.contactMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="contactMessageDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.contactMessage.detail.title">ContactMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.contactMessage.name">Name</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.name}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="proficiencyTestingApp.contactMessage.email">Email</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.email}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="proficiencyTestingApp.contactMessage.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.subject}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="proficiencyTestingApp.contactMessage.message">Message</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.message}</dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="proficiencyTestingApp.contactMessage.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.ipAddress}</dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="proficiencyTestingApp.contactMessage.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {contactMessageEntity.submittedAt ? (
              <TextFormat value={contactMessageEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="isHandled">
              <Translate contentKey="proficiencyTestingApp.contactMessage.isHandled">Is Handled</Translate>
            </span>
          </dt>
          <dd>{contactMessageEntity.isHandled ? 'true' : 'false'}</dd>
        </dl>
        <Button as={Link as any} to="/contact-message" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/contact-message/${contactMessageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ContactMessageDetail;
