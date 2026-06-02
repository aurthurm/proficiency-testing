import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { LoginStatus } from 'app/shared/model/enumerations/login-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './user-login-history.reducer';

export const UserLoginHistoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const userLoginHistoryEntity = useAppSelector(state => state.userLoginHistory.entity);
  const loading = useAppSelector(state => state.userLoginHistory.loading);
  const updating = useAppSelector(state => state.userLoginHistory.updating);
  const updateSuccess = useAppSelector(state => state.userLoginHistory.updateSuccess);
  const loginStatusValues = Object.keys(LoginStatus);

  const handleClose = () => {
    navigate(`/user-login-history${location.search}`);
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
    values.attemptedAt = convertDateTimeToServer(values.attemptedAt);

    const entity = {
      ...userLoginHistoryEntity,
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
          attemptedAt: displayDefaultDateTime(),
        }
      : {
          loginStatus: 'SUCCESS',
          ...userLoginHistoryEntity,
          attemptedAt: convertDateTimeFromServer(userLoginHistoryEntity.attemptedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.userLoginHistory.home.createOrEditLabel" data-cy="UserLoginHistoryCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.userLoginHistory.home.createOrEditLabel">
              Create or edit a UserLoginHistory
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
                  id="user-login-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.loginId')}
                id="user-login-history-loginId"
                name="loginId"
                data-cy="loginId"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.loginContext')}
                id="user-login-history-loginContext"
                name="loginContext"
                data-cy="loginContext"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.loginStatus')}
                id="user-login-history-loginStatus"
                name="loginStatus"
                data-cy="loginStatus"
                type="select"
              >
                {loginStatusValues.map(loginStatus => (
                  <option value={loginStatus} key={loginStatus}>
                    {translate(`proficiencyTestingApp.LoginStatus.${loginStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.attemptedAt')}
                id="user-login-history-attemptedAt"
                name="attemptedAt"
                data-cy="attemptedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.ipAddress')}
                id="user-login-history-ipAddress"
                name="ipAddress"
                data-cy="ipAddress"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.browser')}
                id="user-login-history-browser"
                name="browser"
                data-cy="browser"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.operatingSystem')}
                id="user-login-history-operatingSystem"
                name="operatingSystem"
                data-cy="operatingSystem"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.userLoginHistory.sessionHash')}
                id="user-login-history-sessionHash"
                name="sessionHash"
                data-cy="sessionHash"
                type="text"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-login-history" replace variant="info">
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

export default UserLoginHistoryUpdate;
