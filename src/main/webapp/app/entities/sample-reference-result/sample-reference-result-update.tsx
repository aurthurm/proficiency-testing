import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getAssays } from 'app/entities/assay/assay.reducer';
import { getEntities as getShipmentSamples } from 'app/entities/shipment-sample/shipment-sample.reducer';

import { createEntity, getEntity, reset, updateEntity } from './sample-reference-result.reducer';

export const SampleReferenceResultUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const assays = useAppSelector(state => state.assay.entities);
  const shipmentSamples = useAppSelector(state => state.shipmentSample.entities);
  const sampleReferenceResultEntity = useAppSelector(state => state.sampleReferenceResult.entity);
  const loading = useAppSelector(state => state.sampleReferenceResult.loading);
  const updating = useAppSelector(state => state.sampleReferenceResult.updating);
  const updateSuccess = useAppSelector(state => state.sampleReferenceResult.updateSuccess);

  const handleClose = () => {
    navigate(`/sample-reference-result${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAssays({}));
    dispatch(getShipmentSamples({}));
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
    if (values.quantitativeValue !== undefined && typeof values.quantitativeValue !== 'number') {
      values.quantitativeValue = Number(values.quantitativeValue);
    }
    if (values.lowerLimit !== undefined && typeof values.lowerLimit !== 'number') {
      values.lowerLimit = Number(values.lowerLimit);
    }
    if (values.upperLimit !== undefined && typeof values.upperLimit !== 'number') {
      values.upperLimit = Number(values.upperLimit);
    }

    const entity = {
      ...sampleReferenceResultEntity,
      ...values,
      assay: assays.find(it => it.id.toString() === values.assay?.toString()),
      sample: shipmentSamples.find(it => it.id.toString() === values.sample?.toString()),
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
          ...sampleReferenceResultEntity,
          assay: sampleReferenceResultEntity?.assay?.id,
          sample: sampleReferenceResultEntity?.sample?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.sampleReferenceResult.home.createOrEditLabel" data-cy="SampleReferenceResultCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.home.createOrEditLabel">
              Create or edit a SampleReferenceResult
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
                  id="sample-reference-result-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.sampleReferenceResult.qualitativeResult')}
                id="sample-reference-result-qualitativeResult"
                name="qualitativeResult"
                data-cy="qualitativeResult"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.sampleReferenceResult.quantitativeValue')}
                id="sample-reference-result-quantitativeValue"
                name="quantitativeValue"
                data-cy="quantitativeValue"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.sampleReferenceResult.unit')}
                id="sample-reference-result-unit"
                name="unit"
                data-cy="unit"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.sampleReferenceResult.lowerLimit')}
                id="sample-reference-result-lowerLimit"
                name="lowerLimit"
                data-cy="lowerLimit"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.sampleReferenceResult.upperLimit')}
                id="sample-reference-result-upperLimit"
                name="upperLimit"
                data-cy="upperLimit"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.sampleReferenceResult.isControlExpected')}
                id="sample-reference-result-isControlExpected"
                name="isControlExpected"
                data-cy="isControlExpected"
                check
                type="checkbox"
              />
              <ValidatedField
                id="sample-reference-result-assay"
                name="assay"
                data-cy="assay"
                label={translate('proficiencyTestingApp.sampleReferenceResult.assay')}
                type="select"
              >
                <option value="" key="0" />
                {assays
                  ? assays.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="sample-reference-result-sample"
                name="sample"
                data-cy="sample"
                label={translate('proficiencyTestingApp.sampleReferenceResult.sample')}
                type="select"
                required
              >
                <option value="" key="0" />
                {shipmentSamples
                  ? shipmentSamples.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/sample-reference-result"
                replace
                variant="info"
              >
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

export default SampleReferenceResultUpdate;
