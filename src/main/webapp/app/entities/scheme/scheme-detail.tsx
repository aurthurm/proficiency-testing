import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './scheme.reducer';

export const SchemeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const schemeEntity = useAppSelector(state => state.scheme.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="schemeDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.scheme.detail.title">Scheme</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{schemeEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="proficiencyTestingApp.scheme.code">Code</Translate>
            </span>
          </dt>
          <dd>{schemeEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.scheme.name">Name</Translate>
            </span>
          </dt>
          <dd>{schemeEntity.name}</dd>
          <dt>
            <span id="schemeType">
              <Translate contentKey="proficiencyTestingApp.scheme.schemeType">Scheme Type</Translate>
            </span>
          </dt>
          <dd>{schemeEntity.schemeType}</dd>
          <dt>
            <span id="modality">
              <Translate contentKey="proficiencyTestingApp.scheme.modality">Modality</Translate>
            </span>
          </dt>
          <dd>{schemeEntity.modality}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.scheme.status">Status</Translate>
            </span>
          </dt>
          <dd>{schemeEntity.status}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.scheme.certificateTemplate">Certificate Template</Translate>
          </dt>
          <dd>{schemeEntity.certificateTemplate ? schemeEntity.certificateTemplate.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/scheme" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/scheme/${schemeEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SchemeDetail;
