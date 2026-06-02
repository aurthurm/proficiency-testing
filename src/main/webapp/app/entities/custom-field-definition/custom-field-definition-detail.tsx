import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './custom-field-definition.reducer';

export const CustomFieldDefinitionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const customFieldDefinitionEntity = useAppSelector(state => state.customFieldDefinition.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customFieldDefinitionDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.customFieldDefinition.detail.title">CustomFieldDefinition</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.id}</dd>
          <dt>
            <span id="fieldKey">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.fieldKey">Field Key</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.fieldKey}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.label">Label</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.label}</dd>
          <dt>
            <span id="fieldType">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.fieldType">Field Type</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.fieldType}</dd>
          <dt>
            <span id="options">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.options">Options</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.options}</dd>
          <dt>
            <span id="displayOrder">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.displayOrder">Display Order</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.displayOrder}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.status">Status</Translate>
            </span>
          </dt>
          <dd>{customFieldDefinitionEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/custom-field-definition" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/custom-field-definition/${customFieldDefinitionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CustomFieldDefinitionDetail;
