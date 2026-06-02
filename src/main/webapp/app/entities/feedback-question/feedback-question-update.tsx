import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

import { createEntity, getEntity, reset, updateEntity } from './feedback-question.reducer';

export const FeedbackQuestionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const feedbackQuestionEntity = useAppSelector(state => state.feedbackQuestion.entity);
  const loading = useAppSelector(state => state.feedbackQuestion.loading);
  const updating = useAppSelector(state => state.feedbackQuestion.updating);
  const updateSuccess = useAppSelector(state => state.feedbackQuestion.updateSuccess);
  const contentStatusValues = Object.keys(ContentStatus);

  const handleClose = () => {
    navigate('/feedback-question');
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
      ...feedbackQuestionEntity,
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
          status: 'PUBLISHED',
          ...feedbackQuestionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.feedbackQuestion.home.createOrEditLabel" data-cy="FeedbackQuestionCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.feedbackQuestion.home.createOrEditLabel">
              Create or edit a FeedbackQuestion
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
                  id="feedback-question-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.feedbackQuestion.questionText')}
                id="feedback-question-questionText"
                name="questionText"
                data-cy="questionText"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.feedbackQuestion.displayOrder')}
                id="feedback-question-displayOrder"
                name="displayOrder"
                data-cy="displayOrder"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.feedbackQuestion.status')}
                id="feedback-question-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {contentStatusValues.map(contentStatus => (
                  <option value={contentStatus} key={contentStatus}>
                    {translate(`proficiencyTestingApp.ContentStatus.${contentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/feedback-question" replace variant="info">
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

export default FeedbackQuestionUpdate;
