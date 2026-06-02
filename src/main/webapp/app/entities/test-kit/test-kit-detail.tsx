import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './test-kit.reducer';

export const TestKitDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const testKitEntity = useAppSelector(state => state.testKit.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="testKitDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.testKit.detail.title">TestKit</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{testKitEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.testKit.name">Name</Translate>
            </span>
          </dt>
          <dd>{testKitEntity.name}</dd>
          <dt>
            <span id="manufacturer">
              <Translate contentKey="proficiencyTestingApp.testKit.manufacturer">Manufacturer</Translate>
            </span>
          </dt>
          <dd>{testKitEntity.manufacturer}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.testKit.status">Status</Translate>
            </span>
          </dt>
          <dd>{testKitEntity.status}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.testKit.scheme">Scheme</Translate>
          </dt>
          <dd>{testKitEntity.scheme ? testKitEntity.scheme.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/test-kit" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/test-kit/${testKitEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TestKitDetail;
