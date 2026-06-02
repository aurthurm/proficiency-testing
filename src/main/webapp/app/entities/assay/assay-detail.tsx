import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './assay.reducer';

export const AssayDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const assayEntity = useAppSelector(state => state.assay.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assayDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.assay.detail.title">Assay</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assayEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.assay.name">Name</Translate>
            </span>
          </dt>
          <dd>{assayEntity.name}</dd>
          <dt>
            <span id="assayType">
              <Translate contentKey="proficiencyTestingApp.assay.assayType">Assay Type</Translate>
            </span>
          </dt>
          <dd>{assayEntity.assayType}</dd>
          <dt>
            <span id="manufacturer">
              <Translate contentKey="proficiencyTestingApp.assay.manufacturer">Manufacturer</Translate>
            </span>
          </dt>
          <dd>{assayEntity.manufacturer}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.assay.status">Status</Translate>
            </span>
          </dt>
          <dd>{assayEntity.status}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.assay.scheme">Scheme</Translate>
          </dt>
          <dd>{assayEntity.scheme ? assayEntity.scheme.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/assay" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/assay/${assayEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssayDetail;
