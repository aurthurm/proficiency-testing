import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { JobStatus } from 'app/shared/model/enumerations/job-status.model';
import { JobType } from 'app/shared/model/enumerations/job-type.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './scheduled-job.reducer';

export const ScheduledJobUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const scheduledJobEntity = useAppSelector(state => state.scheduledJob.entity);
  const loading = useAppSelector(state => state.scheduledJob.loading);
  const updating = useAppSelector(state => state.scheduledJob.updating);
  const updateSuccess = useAppSelector(state => state.scheduledJob.updateSuccess);
  const jobTypeValues = Object.keys(JobType);
  const jobStatusValues = Object.keys(JobStatus);

  const handleClose = () => {
    navigate(`/scheduled-job${location.search}`);
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
    values.requestedOn = convertDateTimeToServer(values.requestedOn);
    values.startedAt = convertDateTimeToServer(values.startedAt);
    values.lastHeartbeat = convertDateTimeToServer(values.lastHeartbeat);
    values.completedAt = convertDateTimeToServer(values.completedAt);
    if (values.progressCompleted !== undefined && typeof values.progressCompleted !== 'number') {
      values.progressCompleted = Number(values.progressCompleted);
    }
    if (values.progressTotal !== undefined && typeof values.progressTotal !== 'number') {
      values.progressTotal = Number(values.progressTotal);
    }

    const entity = {
      ...scheduledJobEntity,
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
      ? {
          requestedOn: displayDefaultDateTime(),
          startedAt: displayDefaultDateTime(),
          lastHeartbeat: displayDefaultDateTime(),
          completedAt: displayDefaultDateTime(),
        }
      : {
          jobType: 'EVALUATION',
          status: 'PENDING',
          ...scheduledJobEntity,
          requestedOn: convertDateTimeFromServer(scheduledJobEntity.requestedOn),
          startedAt: convertDateTimeFromServer(scheduledJobEntity.startedAt),
          lastHeartbeat: convertDateTimeFromServer(scheduledJobEntity.lastHeartbeat),
          completedAt: convertDateTimeFromServer(scheduledJobEntity.completedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.scheduledJob.home.createOrEditLabel" data-cy="ScheduledJobCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.scheduledJob.home.createOrEditLabel">Create or edit a ScheduledJob</Translate>
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
                  id="scheduled-job-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.jobType')}
                id="scheduled-job-jobType"
                name="jobType"
                data-cy="jobType"
                type="select"
              >
                {jobTypeValues.map(jobType => (
                  <option value={jobType} key={jobType}>
                    {translate(`proficiencyTestingApp.JobType.${jobType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.status')}
                id="scheduled-job-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {jobStatusValues.map(jobStatus => (
                  <option value={jobStatus} key={jobStatus}>
                    {translate(`proficiencyTestingApp.JobStatus.${jobStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.requestedBy')}
                id="scheduled-job-requestedBy"
                name="requestedBy"
                data-cy="requestedBy"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.requestedOn')}
                id="scheduled-job-requestedOn"
                name="requestedOn"
                data-cy="requestedOn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.startedAt')}
                id="scheduled-job-startedAt"
                name="startedAt"
                data-cy="startedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.lastHeartbeat')}
                id="scheduled-job-lastHeartbeat"
                name="lastHeartbeat"
                data-cy="lastHeartbeat"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.completedAt')}
                id="scheduled-job-completedAt"
                name="completedAt"
                data-cy="completedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.progressCompleted')}
                id="scheduled-job-progressCompleted"
                name="progressCompleted"
                data-cy="progressCompleted"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.progressTotal')}
                id="scheduled-job-progressTotal"
                name="progressTotal"
                data-cy="progressTotal"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheduledJob.summary')}
                id="scheduled-job-summary"
                name="summary"
                data-cy="summary"
                type="textarea"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/scheduled-job" replace variant="info">
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

export default ScheduledJobUpdate;
