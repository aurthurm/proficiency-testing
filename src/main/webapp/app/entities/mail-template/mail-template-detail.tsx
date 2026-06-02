import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './mail-template.reducer';

export const MailTemplateDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const mailTemplateEntity = useAppSelector(state => state.mailTemplate.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="mailTemplateDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.mailTemplate.detail.title">MailTemplate</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{mailTemplateEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="proficiencyTestingApp.mailTemplate.code">Code</Translate>
            </span>
          </dt>
          <dd>{mailTemplateEntity.code}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="proficiencyTestingApp.mailTemplate.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{mailTemplateEntity.subject}</dd>
          <dt>
            <span id="htmlBody">
              <Translate contentKey="proficiencyTestingApp.mailTemplate.htmlBody">Html Body</Translate>
            </span>
          </dt>
          <dd>{mailTemplateEntity.htmlBody}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.mailTemplate.status">Status</Translate>
            </span>
          </dt>
          <dd>{mailTemplateEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/mail-template" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/mail-template/${mailTemplateEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MailTemplateDetail;
