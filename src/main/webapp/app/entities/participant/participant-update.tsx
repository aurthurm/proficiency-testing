import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCountries } from 'app/entities/country/country.reducer';
import { getEntities as getDataManagers } from 'app/entities/data-manager/data-manager.reducer';
import { Status } from 'app/shared/model/enumerations/status.model';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './participant.reducer';

export const ParticipantUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const countries = useAppSelector(state => state.country.entities);
  const dataManagers = useAppSelector(state => state.dataManager.entities);
  const participantEntity = useAppSelector(state => state.participant.entity);
  const loading = useAppSelector(state => state.participant.loading);
  const updating = useAppSelector(state => state.participant.updating);
  const updateSuccess = useAppSelector(state => state.participant.updateSuccess);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate(`/participant${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCountries({}));
    dispatch(getDataManagers({}));
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
    if (values.testingVolume !== undefined && typeof values.testingVolume !== 'number') {
      values.testingVolume = Number(values.testingVolume);
    }
    if (values.latitude !== undefined && typeof values.latitude !== 'number') {
      values.latitude = Number(values.latitude);
    }
    if (values.longitude !== undefined && typeof values.longitude !== 'number') {
      values.longitude = Number(values.longitude);
    }

    const entity = {
      ...participantEntity,
      ...values,
      country: countries.find(it => it.id.toString() === values.country?.toString()),
      dataManagerses: mapIdList(values.dataManagerses),
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
          status: 'ACTIVE',
          ...participantEntity,
          country: participantEntity?.country?.id,
          dataManagerses: participantEntity?.dataManagerses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.participant.home.createOrEditLabel" data-cy="ParticipantCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.participant.home.createOrEditLabel">Create or edit a Participant</Translate>
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
                  id="participant-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.uniqueIdentifier')}
                id="participant-uniqueIdentifier"
                name="uniqueIdentifier"
                data-cy="uniqueIdentifier"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.instituteName')}
                id="participant-instituteName"
                name="instituteName"
                data-cy="instituteName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.departmentName')}
                id="participant-departmentName"
                name="departmentName"
                data-cy="departmentName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.email')}
                id="participant-email"
                name="email"
                data-cy="email"
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
                label={translate('proficiencyTestingApp.participant.additionalEmail')}
                id="participant-additionalEmail"
                name="additionalEmail"
                data-cy="additionalEmail"
                type="text"
                validate={{
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.address')}
                id="participant-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.shippingAddress')}
                id="participant-shippingAddress"
                name="shippingAddress"
                data-cy="shippingAddress"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.city')}
                id="participant-city"
                name="city"
                data-cy="city"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.state')}
                id="participant-state"
                name="state"
                data-cy="state"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.district')}
                id="participant-district"
                name="district"
                data-cy="district"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.zip')}
                id="participant-zip"
                name="zip"
                data-cy="zip"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.region')}
                id="participant-region"
                name="region"
                data-cy="region"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.phone')}
                id="participant-phone"
                name="phone"
                data-cy="phone"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.mobile')}
                id="participant-mobile"
                name="mobile"
                data-cy="mobile"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.affiliation')}
                id="participant-affiliation"
                name="affiliation"
                data-cy="affiliation"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.networkTier')}
                id="participant-networkTier"
                name="networkTier"
                data-cy="networkTier"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.siteType')}
                id="participant-siteType"
                name="siteType"
                data-cy="siteType"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.fundingSource')}
                id="participant-fundingSource"
                name="fundingSource"
                data-cy="fundingSource"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.testingVolume')}
                id="participant-testingVolume"
                name="testingVolume"
                data-cy="testingVolume"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.pepfarId')}
                id="participant-pepfarId"
                name="pepfarId"
                data-cy="pepfarId"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.latitude')}
                id="participant-latitude"
                name="latitude"
                data-cy="latitude"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.longitude')}
                id="participant-longitude"
                name="longitude"
                data-cy="longitude"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.labDirectorName')}
                id="participant-labDirectorName"
                name="labDirectorName"
                data-cy="labDirectorName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.labDirectorEmail')}
                id="participant-labDirectorEmail"
                name="labDirectorEmail"
                data-cy="labDirectorEmail"
                type="text"
                validate={{
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.contactPersonName')}
                id="participant-contactPersonName"
                name="contactPersonName"
                data-cy="contactPersonName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.contactPersonEmail')}
                id="participant-contactPersonEmail"
                name="contactPersonEmail"
                data-cy="contactPersonEmail"
                type="text"
                validate={{
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.contactPersonPhone')}
                id="participant-contactPersonPhone"
                name="contactPersonPhone"
                data-cy="contactPersonPhone"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participant.status')}
                id="participant-status"
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
                id="participant-country"
                name="country"
                data-cy="country"
                label={translate('proficiencyTestingApp.participant.country')}
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
                label={translate('proficiencyTestingApp.participant.dataManagers')}
                id="participant-dataManagers"
                data-cy="dataManagers"
                type="select"
                multiple
                name="dataManagerses"
              >
                <option value="" key="0" />
                {dataManagers
                  ? dataManagers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/participant" replace variant="info">
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

export default ParticipantUpdate;
