import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './global-configuration.reducer';

export const GlobalConfigurationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const globalConfigurationEntity = useAppSelector(state => state.globalConfiguration.entity);
  const loading = useAppSelector(state => state.globalConfiguration.loading);
  const updating = useAppSelector(state => state.globalConfiguration.updating);
  const updateSuccess = useAppSelector(state => state.globalConfiguration.updateSuccess);

  const handleClose = () => {
    navigate('/global-configuration');
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

    const entity = {
      ...globalConfigurationEntity,
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
          ...globalConfigurationEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.globalConfiguration.home.createOrEditLabel" data-cy="GlobalConfigurationCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.globalConfiguration.home.createOrEditLabel">
              Create or edit a GlobalConfiguration
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
                  id="global-configuration-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.globalConfiguration.configKey')}
                id="global-configuration-configKey"
                name="configKey"
                data-cy="configKey"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.globalConfiguration.configValue')}
                id="global-configuration-configValue"
                name="configValue"
                data-cy="configValue"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.globalConfiguration.description')}
                id="global-configuration-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/global-configuration"
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

export default GlobalConfigurationUpdate;
