import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { CustomFieldType } from 'app/shared/model/enumerations/custom-field-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';

import { createEntity, getEntity, reset, updateEntity } from './custom-field-definition.reducer';

export const CustomFieldDefinitionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const customFieldDefinitionEntity = useAppSelector(state => state.customFieldDefinition.entity);
  const loading = useAppSelector(state => state.customFieldDefinition.loading);
  const updating = useAppSelector(state => state.customFieldDefinition.updating);
  const updateSuccess = useAppSelector(state => state.customFieldDefinition.updateSuccess);
  const customFieldTypeValues = Object.keys(CustomFieldType);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate('/custom-field-definition');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.displayOrder !== undefined && typeof values.displayOrder !== 'number') {
      values.displayOrder = Number(values.displayOrder);
    }

    const entity = {
      ...customFieldDefinitionEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          fieldType: 'TEXT',
          status: 'ACTIVE',
          ...customFieldDefinitionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.customFieldDefinition.home.createOrEditLabel" data-cy="CustomFieldDefinitionCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.customFieldDefinition.home.createOrEditLabel">
              Create or edit a CustomFieldDefinition
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="custom-field-definition-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.customFieldDefinition.fieldKey')}
                id="custom-field-definition-fieldKey"
                name="fieldKey"
                data-cy="fieldKey"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.customFieldDefinition.label')}
                id="custom-field-definition-label"
                name="label"
                data-cy="label"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.customFieldDefinition.fieldType')}
                id="custom-field-definition-fieldType"
                name="fieldType"
                data-cy="fieldType"
                type="select"
              >
                {customFieldTypeValues.map(customFieldType => (
                  <option value={customFieldType} key={customFieldType}>
                    {translate(`proficiencyTestingApp.CustomFieldType.${customFieldType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.customFieldDefinition.options')}
                id="custom-field-definition-options"
                name="options"
                data-cy="options"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.customFieldDefinition.displayOrder')}
                id="custom-field-definition-displayOrder"
                name="displayOrder"
                data-cy="displayOrder"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.customFieldDefinition.status')}
                id="custom-field-definition-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {statusValues.map(status => (
                  <option value={status} key={status}>
                    {translate(`proficiencyTestingApp.Status.${status}`)}
                  </option>
                ))}
              </ValidatedField>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/custom-field-definition"
                replace
                variant="info"
              >
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CustomFieldDefinitionUpdate;
