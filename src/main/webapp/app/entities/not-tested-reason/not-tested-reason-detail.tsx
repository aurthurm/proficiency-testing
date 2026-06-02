import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './not-tested-reason.reducer';

export const NotTestedReasonDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const notTestedReasonEntity = useAppSelector(state => state.notTestedReason.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="notTestedReasonDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.notTestedReason.detail.title">NotTestedReason</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{notTestedReasonEntity.id}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="proficiencyTestingApp.notTestedReason.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{notTestedReasonEntity.reason}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.notTestedReason.status">Status</Translate>
            </span>
          </dt>
          <dd>{notTestedReasonEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/not-tested-reason" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/not-tested-reason/${notTestedReasonEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NotTestedReasonDetail;
