import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getFeedbackQuestions } from 'app/entities/feedback-question/feedback-question.reducer';
import { getEntities as getParticipants } from 'app/entities/participant/participant.reducer';
import { getEntities as getShipments } from 'app/entities/shipment/shipment.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './participant-feedback.reducer';

export const ParticipantFeedbackUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const feedbackQuestions = useAppSelector(state => state.feedbackQuestion.entities);
  const participants = useAppSelector(state => state.participant.entities);
  const shipments = useAppSelector(state => state.shipment.entities);
  const participantFeedbackEntity = useAppSelector(state => state.participantFeedback.entity);
  const loading = useAppSelector(state => state.participantFeedback.loading);
  const updating = useAppSelector(state => state.participantFeedback.updating);
  const updateSuccess = useAppSelector(state => state.participantFeedback.updateSuccess);

  const handleClose = () => {
    navigate('/participant-feedback');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFeedbackQuestions({}));
    dispatch(getParticipants({}));
    dispatch(getShipments({}));
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
    values.submittedAt = convertDateTimeToServer(values.submittedAt);

    const entity = {
      ...participantFeedbackEntity,
      ...values,
      question: feedbackQuestions.find(it => it.id.toString() === values.question?.toString()),
      participant: participants.find(it => it.id.toString() === values.participant?.toString()),
      shipment: shipments.find(it => it.id.toString() === values.shipment?.toString()),
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
          submittedAt: displayDefaultDateTime(),
        }
      : {
          ...participantFeedbackEntity,
          submittedAt: convertDateTimeFromServer(participantFeedbackEntity.submittedAt),
          question: participantFeedbackEntity?.question?.id,
          participant: participantFeedbackEntity?.participant?.id,
          shipment: participantFeedbackEntity?.shipment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.participantFeedback.home.createOrEditLabel" data-cy="ParticipantFeedbackCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.participantFeedback.home.createOrEditLabel">
              Create or edit a ParticipantFeedback
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
                  id="participant-feedback-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.participantFeedback.answer')}
                id="participant-feedback-answer"
                name="answer"
                data-cy="answer"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantFeedback.submittedAt')}
                id="participant-feedback-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="participant-feedback-question"
                name="question"
                data-cy="question"
                label={translate('proficiencyTestingApp.participantFeedback.question')}
                type="select"
                required
              >
                <option value="" key="0" />
                {feedbackQuestions
                  ? feedbackQuestions.map(otherEntity => (
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
                id="participant-feedback-participant"
                name="participant"
                data-cy="participant"
                label={translate('proficiencyTestingApp.participantFeedback.participant')}
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
                id="participant-feedback-shipment"
                name="shipment"
                data-cy="shipment"
                label={translate('proficiencyTestingApp.participantFeedback.shipment')}
                type="select"
              >
                <option value="" key="0" />
                {shipments
                  ? shipments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/participant-feedback"
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

export default ParticipantFeedbackUpdate;
