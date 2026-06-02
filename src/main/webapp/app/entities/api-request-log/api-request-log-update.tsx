import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './api-request-log.reducer';

export const ApiRequestLogUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const apiRequestLogEntity = useAppSelector(state => state.apiRequestLog.entity);
  const loading = useAppSelector(state => state.apiRequestLog.loading);
  const updating = useAppSelector(state => state.apiRequestLog.updating);
  const updateSuccess = useAppSelector(state => state.apiRequestLog.updateSuccess);

  const handleClose = () => {
    navigate(`/api-request-log${location.search}`);
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
    if (values.numberOfRecords !== undefined && typeof values.numberOfRecords !== 'number') {
      values.numberOfRecords = Number(values.numberOfRecords);
    }

    const entity = {
      ...apiRequestLogEntity,
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
        }
      : {
          ...apiRequestLogEntity,
          requestedOn: convertDateTimeFromServer(apiRequestLogEntity.requestedOn),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.apiRequestLog.home.createOrEditLabel" data-cy="ApiRequestLogCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.apiRequestLog.home.createOrEditLabel">Create or edit a ApiRequestLog</Translate>
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
                  id="api-request-log-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.transactionId')}
                id="api-request-log-transactionId"
                name="transactionId"
                data-cy="transactionId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.requestedBy')}
                id="api-request-log-requestedBy"
                name="requestedBy"
                data-cy="requestedBy"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.requestedOn')}
                id="api-request-log-requestedOn"
                name="requestedOn"
                data-cy="requestedOn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.numberOfRecords')}
                id="api-request-log-numberOfRecords"
                name="numberOfRecords"
                data-cy="numberOfRecords"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.requestType')}
                id="api-request-log-requestType"
                name="requestType"
                data-cy="requestType"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.testType')}
                id="api-request-log-testType"
                name="testType"
                data-cy="testType"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.apiUrl')}
                id="api-request-log-apiUrl"
                name="apiUrl"
                data-cy="apiUrl"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.apiRequestLog.dataFormat')}
                id="api-request-log-dataFormat"
                name="dataFormat"
                data-cy="dataFormat"
                type="text"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/api-request-log" replace variant="info">
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

export default ApiRequestLogUpdate;
