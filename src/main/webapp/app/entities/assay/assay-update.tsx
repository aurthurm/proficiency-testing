import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getSchemes } from 'app/entities/scheme/scheme.reducer';
import { AssayType } from 'app/shared/model/enumerations/assay-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';

import { createEntity, getEntity, reset, updateEntity } from './assay.reducer';

export const AssayUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const schemes = useAppSelector(state => state.scheme.entities);
  const assayEntity = useAppSelector(state => state.assay.entity);
  const loading = useAppSelector(state => state.assay.loading);
  const updating = useAppSelector(state => state.assay.updating);
  const updateSuccess = useAppSelector(state => state.assay.updateSuccess);
  const assayTypeValues = Object.keys(AssayType);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate(`/assay${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
      ...assayEntity,
      ...values,
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
          assayType: 'DTS_ALGORITHM',
          status: 'ACTIVE',
          ...assayEntity,
          scheme: assayEntity?.scheme?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.assay.home.createOrEditLabel" data-cy="AssayCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.assay.home.createOrEditLabel">Create or edit a Assay</Translate>
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
                  id="assay-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.assay.name')}
                id="assay-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.assay.assayType')}
                id="assay-assayType"
                name="assayType"
                data-cy="assayType"
                type="select"
              >
                {assayTypeValues.map(assayType => (
                  <option value={assayType} key={assayType}>
                    {translate(`proficiencyTestingApp.AssayType.${assayType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.assay.manufacturer')}
                id="assay-manufacturer"
                name="manufacturer"
                data-cy="manufacturer"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.assay.status')}
                id="assay-status"
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
                id="assay-scheme"
                name="scheme"
                data-cy="scheme"
                label={translate('proficiencyTestingApp.assay.scheme')}
                type="select"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/assay" replace variant="info">
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

export default AssayUpdate;
