import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AuditAction } from 'app/shared/model/enumerations/audit-action.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './audit-log.reducer';

export const AuditLogUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const auditLogEntity = useAppSelector(state => state.auditLog.entity);
  const loading = useAppSelector(state => state.auditLog.loading);
  const updating = useAppSelector(state => state.auditLog.updating);
  const updateSuccess = useAppSelector(state => state.auditLog.updateSuccess);
  const auditActionValues = Object.keys(AuditAction);

  const handleClose = () => {
    navigate(`/audit-log${location.search}`);
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
    values.performedOn = convertDateTimeToServer(values.performedOn);

    const entity = {
      ...auditLogEntity,
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
          performedOn: displayDefaultDateTime(),
        }
      : {
          action: 'LOGIN',
          ...auditLogEntity,
          performedOn: convertDateTimeFromServer(auditLogEntity.performedOn),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.auditLog.home.createOrEditLabel" data-cy="AuditLogCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.auditLog.home.createOrEditLabel">Create or edit a AuditLog</Translate>
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
                  id="audit-log-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.action')}
                id="audit-log-action"
                name="action"
                data-cy="action"
                type="select"
              >
                {auditActionValues.map(auditAction => (
                  <option value={auditAction} key={auditAction}>
                    {translate(`proficiencyTestingApp.AuditAction.${auditAction}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.statement')}
                id="audit-log-statement"
                name="statement"
                data-cy="statement"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.performedBy')}
                id="audit-log-performedBy"
                name="performedBy"
                data-cy="performedBy"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.performedByRole')}
                id="audit-log-performedByRole"
                name="performedByRole"
                data-cy="performedByRole"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.performedOn')}
                id="audit-log-performedOn"
                name="performedOn"
                data-cy="performedOn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.ipAddress')}
                id="audit-log-ipAddress"
                name="ipAddress"
                data-cy="ipAddress"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.userAgent')}
                id="audit-log-userAgent"
                name="userAgent"
                data-cy="userAgent"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.auditLog.sessionHash')}
                id="audit-log-sessionHash"
                name="sessionHash"
                data-cy="sessionHash"
                type="text"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/audit-log" replace variant="info">
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

export default AuditLogUpdate;
