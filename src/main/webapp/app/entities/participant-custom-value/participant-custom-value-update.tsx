import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCustomFieldDefinitions } from 'app/entities/custom-field-definition/custom-field-definition.reducer';
import { getEntities as getParticipants } from 'app/entities/participant/participant.reducer';

import { createEntity, getEntity, reset, updateEntity } from './participant-custom-value.reducer';

export const ParticipantCustomValueUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const participants = useAppSelector(state => state.participant.entities);
  const customFieldDefinitions = useAppSelector(state => state.customFieldDefinition.entities);
  const participantCustomValueEntity = useAppSelector(state => state.participantCustomValue.entity);
  const loading = useAppSelector(state => state.participantCustomValue.loading);
  const updating = useAppSelector(state => state.participantCustomValue.updating);
  const updateSuccess = useAppSelector(state => state.participantCustomValue.updateSuccess);

  const handleClose = () => {
    navigate('/participant-custom-value');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getParticipants({}));
    dispatch(getCustomFieldDefinitions({}));
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
      ...participantCustomValueEntity,
      ...values,
      participant: participants.find(it => it.id.toString() === values.participant?.toString()),
      definition: customFieldDefinitions.find(it => it.id.toString() === values.definition?.toString()),
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
          ...participantCustomValueEntity,
          participant: participantCustomValueEntity?.participant?.id,
          definition: participantCustomValueEntity?.definition?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.participantCustomValue.home.createOrEditLabel" data-cy="ParticipantCustomValueCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.participantCustomValue.home.createOrEditLabel">
              Create or edit a ParticipantCustomValue
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
                  id="participant-custom-value-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.participantCustomValue.value')}
                id="participant-custom-value-value"
                name="value"
                data-cy="value"
                type="text"
              />
              <ValidatedField
                id="participant-custom-value-participant"
                name="participant"
                data-cy="participant"
                label={translate('proficiencyTestingApp.participantCustomValue.participant')}
                type="select"
                required
              >
                <option value="" key="0" />
                {participants
                  ? participants.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="participant-custom-value-definition"
                name="definition"
                data-cy="definition"
                label={translate('proficiencyTestingApp.participantCustomValue.definition')}
                type="select"
                required
              >
                <option value="" key="0" />
                {customFieldDefinitions
                  ? customFieldDefinitions.map(otherEntity => (
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
                to="/participant-custom-value"
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

export default ParticipantCustomValueUpdate;
