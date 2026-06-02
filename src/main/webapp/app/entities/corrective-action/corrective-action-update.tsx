import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getSchemes } from 'app/entities/scheme/scheme.reducer';

import { createEntity, getEntity, reset, updateEntity } from './corrective-action.reducer';

export const CorrectiveActionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const schemes = useAppSelector(state => state.scheme.entities);
  const correctiveActionEntity = useAppSelector(state => state.correctiveAction.entity);
  const loading = useAppSelector(state => state.correctiveAction.loading);
  const updating = useAppSelector(state => state.correctiveAction.updating);
  const updateSuccess = useAppSelector(state => state.correctiveAction.updateSuccess);

  const handleClose = () => {
    navigate('/corrective-action');
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

    const entity = {
      ...correctiveActionEntity,
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
          ...correctiveActionEntity,
          scheme: correctiveActionEntity?.scheme?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.correctiveAction.home.createOrEditLabel" data-cy="CorrectiveActionCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.correctiveAction.home.createOrEditLabel">
              Create or edit a CorrectiveAction
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
                  id="corrective-action-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.correctiveAction.title')}
                id="corrective-action-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.correctiveAction.description')}
                id="corrective-action-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                id="corrective-action-scheme"
                name="scheme"
                data-cy="scheme"
                label={translate('proficiencyTestingApp.correctiveAction.scheme')}
                type="select"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/corrective-action" replace variant="info">
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

export default CorrectiveActionUpdate;
