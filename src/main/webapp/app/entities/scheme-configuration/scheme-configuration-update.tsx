import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getSchemes } from 'app/entities/scheme/scheme.reducer';

import { createEntity, getEntity, reset, updateEntity } from './scheme-configuration.reducer';

export const SchemeConfigurationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const schemes = useAppSelector(state => state.scheme.entities);
  const schemeConfigurationEntity = useAppSelector(state => state.schemeConfiguration.entity);
  const loading = useAppSelector(state => state.schemeConfiguration.loading);
  const updating = useAppSelector(state => state.schemeConfiguration.updating);
  const updateSuccess = useAppSelector(state => state.schemeConfiguration.updateSuccess);

  const handleClose = () => {
    navigate('/scheme-configuration');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSchemes({}));
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
    if (values.version !== undefined && typeof values.version !== 'number') {
      values.version = Number(values.version);
    }
    if (values.passingScore !== undefined && typeof values.passingScore !== 'number') {
      values.passingScore = Number(values.passingScore);
    }
    if (values.documentationWeight !== undefined && typeof values.documentationWeight !== 'number') {
      values.documentationWeight = Number(values.documentationWeight);
    }

    const entity = {
      ...schemeConfigurationEntity,
      ...values,
      scheme: schemes.find(it => it.id.toString() === values.scheme?.toString()),
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
          ...schemeConfigurationEntity,
          scheme: schemeConfigurationEntity?.scheme?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.schemeConfiguration.home.createOrEditLabel" data-cy="SchemeConfigurationCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.schemeConfiguration.home.createOrEditLabel">
              Create or edit a SchemeConfiguration
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
                  id="scheme-configuration-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.version')}
                id="scheme-configuration-version"
                name="version"
                data-cy="version"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.effectiveDate')}
                id="scheme-configuration-effectiveDate"
                name="effectiveDate"
                data-cy="effectiveDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.passingScore')}
                id="scheme-configuration-passingScore"
                name="passingScore"
                data-cy="passingScore"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.documentationWeight')}
                id="scheme-configuration-documentationWeight"
                name="documentationWeight"
                data-cy="documentationWeight"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.allowLateResponse')}
                id="scheme-configuration-allowLateResponse"
                name="allowLateResponse"
                data-cy="allowLateResponse"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.optionalFields')}
                id="scheme-configuration-optionalFields"
                name="optionalFields"
                data-cy="optionalFields"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.scoringRules')}
                id="scheme-configuration-scoringRules"
                name="scoringRules"
                data-cy="scoringRules"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.schemeConfiguration.isActive')}
                id="scheme-configuration-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                id="scheme-configuration-scheme"
                name="scheme"
                data-cy="scheme"
                label={translate('proficiencyTestingApp.schemeConfiguration.scheme')}
                type="select"
                required
              >
                <option value="" key="0" />
                {schemes
                  ? schemes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/scheme-configuration"
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

export default SchemeConfigurationUpdate;
