import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './corrective-action.reducer';

export const CorrectiveActionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const correctiveActionEntity = useAppSelector(state => state.correctiveAction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="correctiveActionDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.correctiveAction.detail.title">CorrectiveAction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{correctiveActionEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="proficiencyTestingApp.correctiveAction.title">Title</Translate>
            </span>
          </dt>
          <dd>{correctiveActionEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="proficiencyTestingApp.correctiveAction.description">Description</Translate>
            </span>
          </dt>
          <dd>{correctiveActionEntity.description}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.correctiveAction.scheme">Scheme</Translate>
          </dt>
          <dd>{correctiveActionEntity.scheme ? correctiveActionEntity.scheme.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/corrective-action" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/corrective-action/${correctiveActionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CorrectiveActionDetail;
