import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCountries } from 'app/entities/country/country.reducer';
import { getEntities as getParticipants } from 'app/entities/participant/participant.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { DataManagerRole } from 'app/shared/model/enumerations/data-manager-role.model';
import { Status } from 'app/shared/model/enumerations/status.model';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './data-manager.reducer';

export const DataManagerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const countries = useAppSelector(state => state.country.entities);
  const participants = useAppSelector(state => state.participant.entities);
  const dataManagerEntity = useAppSelector(state => state.dataManager.entity);
  const loading = useAppSelector(state => state.dataManager.loading);
  const updating = useAppSelector(state => state.dataManager.updating);
  const updateSuccess = useAppSelector(state => state.dataManager.updateSuccess);
  const dataManagerRoleValues = Object.keys(DataManagerRole);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate(`/data-manager${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getCountries({}));
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

    const entity = {
      ...dataManagerEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      country: countries.find(it => it.id.toString() === values.country?.toString()),
      participantses: mapIdList(values.participantses),
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
          role: 'MANAGER',
          status: 'ACTIVE',
          ...dataManagerEntity,
          user: dataManagerEntity?.user?.id,
          country: dataManagerEntity?.country?.id,
          participantses: dataManagerEntity?.participantses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.dataManager.home.createOrEditLabel" data-cy="DataManagerCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.dataManager.home.createOrEditLabel">Create or edit a DataManager</Translate>
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
                  id="data-manager-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.firstName')}
                id="data-manager-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.lastName')}
                id="data-manager-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.institute')}
                id="data-manager-institute"
                name="institute"
                data-cy="institute"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.primaryEmail')}
                id="data-manager-primaryEmail"
                name="primaryEmail"
                data-cy="primaryEmail"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.secondaryEmail')}
                id="data-manager-secondaryEmail"
                name="secondaryEmail"
                data-cy="secondaryEmail"
                type="text"
                validate={{
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.phone')}
                id="data-manager-phone"
                name="phone"
                data-cy="phone"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.mobile')}
                id="data-manager-mobile"
                name="mobile"
                data-cy="mobile"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.language')}
                id="data-manager-language"
                name="language"
                data-cy="language"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.role')}
                id="data-manager-role"
                name="role"
                data-cy="role"
                type="select"
              >
                {dataManagerRoleValues.map(dataManagerRole => (
                  <option value={dataManagerRole} key={dataManagerRole}>
                    {translate(`proficiencyTestingApp.DataManagerRole.${dataManagerRole}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.status')}
                id="data-manager-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {statusValues.map(status => (
                  <option value={status} key={status}>
                    {translate(`proficiencyTestingApp.Status.${status}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.qcAccess')}
                id="data-manager-qcAccess"
                name="qcAccess"
                data-cy="qcAccess"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.viewOnlyAccess')}
                id="data-manager-viewOnlyAccess"
                name="viewOnlyAccess"
                data-cy="viewOnlyAccess"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.enableTestResponseDate')}
                id="data-manager-enableTestResponseDate"
                name="enableTestResponseDate"
                data-cy="enableTestResponseDate"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.enableModeOfReceipt')}
                id="data-manager-enableModeOfReceipt"
                name="enableModeOfReceipt"
                data-cy="enableModeOfReceipt"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.forceProfileCheck')}
                id="data-manager-forceProfileCheck"
                name="forceProfileCheck"
                data-cy="forceProfileCheck"
                check
                type="checkbox"
              />
              <ValidatedField
                id="data-manager-user"
                name="user"
                data-cy="user"
                label={translate('proficiencyTestingApp.dataManager.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="data-manager-country"
                name="country"
                data-cy="country"
                label={translate('proficiencyTestingApp.dataManager.country')}
                type="select"
              >
                <option value="" key="0" />
                {countries
                  ? countries.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.dataManager.participants')}
                id="data-manager-participants"
                data-cy="participants"
                type="select"
                multiple
                name="participantses"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/data-manager" replace variant="info">
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

export default DataManagerUpdate;
