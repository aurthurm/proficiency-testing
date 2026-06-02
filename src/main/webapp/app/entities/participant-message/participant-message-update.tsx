import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getParticipants } from 'app/entities/participant/participant.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './participant-message.reducer';

export const ParticipantMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const participants = useAppSelector(state => state.participant.entities);
  const participantMessageEntity = useAppSelector(state => state.participantMessage.entity);
  const loading = useAppSelector(state => state.participantMessage.loading);
  const updating = useAppSelector(state => state.participantMessage.updating);
  const updateSuccess = useAppSelector(state => state.participantMessage.updateSuccess);

  const handleClose = () => {
    navigate(`/participant-message${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getParticipants({}));
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
    values.sentAt = convertDateTimeToServer(values.sentAt);

    const entity = {
      ...participantMessageEntity,
      ...values,
      participant: participants.find(it => it.id.toString() === values.participant?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          sentAt: displayDefaultDateTime(),
        }
      : {
          ...participantMessageEntity,
          sentAt: convertDateTimeFromServer(participantMessageEntity.sentAt),
          participant: participantMessageEntity?.participant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.participantMessage.home.createOrEditLabel" data-cy="ParticipantMessageCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.participantMessage.home.createOrEditLabel">
              Create or edit a ParticipantMessage
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
                  id="participant-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.participantMessage.subject')}
                id="participant-message-subject"
                name="subject"
                data-cy="subject"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantMessage.body')}
                id="participant-message-body"
                name="body"
                data-cy="body"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantMessage.isRead')}
                id="participant-message-isRead"
                name="isRead"
                data-cy="isRead"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantMessage.sentAt')}
                id="participant-message-sentAt"
                name="sentAt"
                data-cy="sentAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="participant-message-participant"
                name="participant"
                data-cy="participant"
                label={translate('proficiencyTestingApp.participantMessage.participant')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/participant-message" replace variant="info">
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

export default ParticipantMessageUpdate;
