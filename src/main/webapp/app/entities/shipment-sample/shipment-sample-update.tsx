import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getShipments } from 'app/entities/shipment/shipment.reducer';

import { createEntity, getEntity, reset, updateEntity } from './shipment-sample.reducer';

export const ShipmentSampleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const shipments = useAppSelector(state => state.shipment.entities);
  const shipmentSampleEntity = useAppSelector(state => state.shipmentSample.entity);
  const loading = useAppSelector(state => state.shipmentSample.loading);
  const updating = useAppSelector(state => state.shipmentSample.updating);
  const updateSuccess = useAppSelector(state => state.shipmentSample.updateSuccess);

  const handleClose = () => {
    navigate(`/shipment-sample${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getShipments({}));
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
    if (values.sampleScore !== undefined && typeof values.sampleScore !== 'number') {
      values.sampleScore = Number(values.sampleScore);
    }

    const entity = {
      ...shipmentSampleEntity,
      ...values,
      shipment: shipments.find(it => it.id.toString() === values.shipment?.toString()),
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
          ...shipmentSampleEntity,
          shipment: shipmentSampleEntity?.shipment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.shipmentSample.home.createOrEditLabel" data-cy="ShipmentSampleCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.shipmentSample.home.createOrEditLabel">Create or edit a ShipmentSample</Translate>
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
                  id="shipment-sample-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentSample.label')}
                id="shipment-sample-label"
                name="label"
                data-cy="label"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentSample.displayOrder')}
                id="shipment-sample-displayOrder"
                name="displayOrder"
                data-cy="displayOrder"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentSample.isControl')}
                id="shipment-sample-isControl"
                name="isControl"
                data-cy="isControl"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentSample.isMandatory')}
                id="shipment-sample-isMandatory"
                name="isMandatory"
                data-cy="isMandatory"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentSample.sampleScore')}
                id="shipment-sample-sampleScore"
                name="sampleScore"
                data-cy="sampleScore"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentSample.preparationDate')}
                id="shipment-sample-preparationDate"
                name="preparationDate"
                data-cy="preparationDate"
                type="date"
              />
              <ValidatedField
                id="shipment-sample-shipment"
                name="shipment"
                data-cy="shipment"
                label={translate('proficiencyTestingApp.shipmentSample.shipment')}
                type="select"
                required
              >
                <option value="" key="0" />
                {shipments
                  ? shipments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/shipment-sample" replace variant="info">
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

export default ShipmentSampleUpdate;
