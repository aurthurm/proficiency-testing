import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { DistributionStatus } from 'app/shared/model/enumerations/distribution-status.model';

import { createEntity, getEntity, reset, updateEntity } from './distribution.reducer';

export const DistributionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const distributionEntity = useAppSelector(state => state.distribution.entity);
  const loading = useAppSelector(state => state.distribution.loading);
  const updating = useAppSelector(state => state.distribution.updating);
  const updateSuccess = useAppSelector(state => state.distribution.updateSuccess);
  const distributionStatusValues = Object.keys(DistributionStatus);

  const handleClose = () => {
    navigate(`/distribution${location.search}`);
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

    const entity = {
      ...distributionEntity,
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
          status: 'DRAFT',
          ...distributionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.distribution.home.createOrEditLabel" data-cy="DistributionCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.distribution.home.createOrEditLabel">Create or edit a Distribution</Translate>
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
                  id="distribution-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.distribution.code')}
                id="distribution-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.distribution.distributionDate')}
                id="distribution-distributionDate"
                name="distributionDate"
                data-cy="distributionDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.distribution.status')}
                id="distribution-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {distributionStatusValues.map(distributionStatus => (
                  <option value={distributionStatus} key={distributionStatus}>
                    {translate(`proficiencyTestingApp.DistributionStatus.${distributionStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/distribution" replace variant="info">
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

export default DistributionUpdate;
