import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getParticipants } from 'app/entities/participant/participant.reducer';
import { getEntities as getSchemes } from 'app/entities/scheme/scheme.reducer';
import { EnrollmentStatus } from 'app/shared/model/enumerations/enrollment-status.model';

import { createEntity, getEntity, reset, updateEntity } from './enrollment.reducer';

export const EnrollmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const participants = useAppSelector(state => state.participant.entities);
  const schemes = useAppSelector(state => state.scheme.entities);
  const enrollmentEntity = useAppSelector(state => state.enrollment.entity);
  const loading = useAppSelector(state => state.enrollment.loading);
  const updating = useAppSelector(state => state.enrollment.updating);
  const updateSuccess = useAppSelector(state => state.enrollment.updateSuccess);
  const enrollmentStatusValues = Object.keys(EnrollmentStatus);

  const handleClose = () => {
    navigate(`/enrollment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getParticipants({}));
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
      ...enrollmentEntity,
      ...values,
      participant: participants.find(it => it.id.toString() === values.participant?.toString()),
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
          status: 'ENROLLED',
          ...enrollmentEntity,
          participant: enrollmentEntity?.participant?.id,
          scheme: enrollmentEntity?.scheme?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.enrollment.home.createOrEditLabel" data-cy="EnrollmentCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.enrollment.home.createOrEditLabel">Create or edit a Enrollment</Translate>
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
                  id="enrollment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.enrollment.status')}
                id="enrollment-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {enrollmentStatusValues.map(enrollmentStatus => (
                  <option value={enrollmentStatus} key={enrollmentStatus}>
                    {translate(`proficiencyTestingApp.EnrollmentStatus.${enrollmentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.enrollment.enrolledOn')}
                id="enrollment-enrolledOn"
                name="enrolledOn"
                data-cy="enrolledOn"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.enrollment.withdrawnOn')}
                id="enrollment-withdrawnOn"
                name="withdrawnOn"
                data-cy="withdrawnOn"
                type="date"
              />
              <ValidatedField
                id="enrollment-participant"
                name="participant"
                data-cy="participant"
                label={translate('proficiencyTestingApp.enrollment.participant')}
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
                id="enrollment-scheme"
                name="scheme"
                data-cy="scheme"
                label={translate('proficiencyTestingApp.enrollment.scheme')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/enrollment" replace variant="info">
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

export default EnrollmentUpdate;
