import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getAssays } from 'app/entities/assay/assay.reducer';
import { getEntities as getShipmentParticipantMaps } from 'app/entities/shipment-participant-map/shipment-participant-map.reducer';
import { getEntities as getShipmentSamples } from 'app/entities/shipment-sample/shipment-sample.reducer';
import { getEntities as getTestKits } from 'app/entities/test-kit/test-kit.reducer';

import { createEntity, getEntity, reset, updateEntity } from './participant-result.reducer';

export const ParticipantResultUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const assays = useAppSelector(state => state.assay.entities);
  const testKits = useAppSelector(state => state.testKit.entities);
  const shipmentSamples = useAppSelector(state => state.shipmentSample.entities);
  const shipmentParticipantMaps = useAppSelector(state => state.shipmentParticipantMap.entities);
  const participantResultEntity = useAppSelector(state => state.participantResult.entity);
  const loading = useAppSelector(state => state.participantResult.loading);
  const updating = useAppSelector(state => state.participantResult.updating);
  const updateSuccess = useAppSelector(state => state.participantResult.updateSuccess);

  const handleClose = () => {
    navigate(`/participant-result${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAssays({}));
    dispatch(getTestKits({}));
    dispatch(getShipmentSamples({}));
    dispatch(getShipmentParticipantMaps({}));
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
    if (values.reportedQuantitativeValue !== undefined && typeof values.reportedQuantitativeValue !== 'number') {
      values.reportedQuantitativeValue = Number(values.reportedQuantitativeValue);
    }
    if (values.zScore !== undefined && typeof values.zScore !== 'number') {
      values.zScore = Number(values.zScore);
    }
    if (values.calculatedScore !== undefined && typeof values.calculatedScore !== 'number') {
      values.calculatedScore = Number(values.calculatedScore);
    }

    const entity = {
      ...participantResultEntity,
      ...values,
      assay: assays.find(it => it.id.toString() === values.assay?.toString()),
      testKit: testKits.find(it => it.id.toString() === values.testKit?.toString()),
      sample: shipmentSamples.find(it => it.id.toString() === values.sample?.toString()),
      shipmentParticipantMap: shipmentParticipantMaps.find(it => it.id.toString() === values.shipmentParticipantMap?.toString()),
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
          ...participantResultEntity,
          assay: participantResultEntity?.assay?.id,
          testKit: participantResultEntity?.testKit?.id,
          sample: participantResultEntity?.sample?.id,
          shipmentParticipantMap: participantResultEntity?.shipmentParticipantMap?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.participantResult.home.createOrEditLabel" data-cy="ParticipantResultCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.participantResult.home.createOrEditLabel">
              Create or edit a ParticipantResult
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
                  id="participant-result-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.reportedQualitativeResult')}
                id="participant-result-reportedQualitativeResult"
                name="reportedQualitativeResult"
                data-cy="reportedQualitativeResult"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.reportedQuantitativeValue')}
                id="participant-result-reportedQuantitativeValue"
                name="reportedQuantitativeValue"
                data-cy="reportedQuantitativeValue"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.unit')}
                id="participant-result-unit"
                name="unit"
                data-cy="unit"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.lotNumber')}
                id="participant-result-lotNumber"
                name="lotNumber"
                data-cy="lotNumber"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.expiryDate')}
                id="participant-result-expiryDate"
                name="expiryDate"
                data-cy="expiryDate"
                type="date"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.zScore')}
                id="participant-result-zScore"
                name="zScore"
                data-cy="zScore"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.calculatedScore')}
                id="participant-result-calculatedScore"
                name="calculatedScore"
                data-cy="calculatedScore"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.participantResult.comments')}
                id="participant-result-comments"
                name="comments"
                data-cy="comments"
                type="text"
              />
              <ValidatedField
                id="participant-result-assay"
                name="assay"
                data-cy="assay"
                label={translate('proficiencyTestingApp.participantResult.assay')}
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
                id="participant-result-testKit"
                name="testKit"
                data-cy="testKit"
                label={translate('proficiencyTestingApp.participantResult.testKit')}
                type="select"
              >
                <option value="" key="0" />
                {testKits
                  ? testKits.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="participant-result-sample"
                name="sample"
                data-cy="sample"
                label={translate('proficiencyTestingApp.participantResult.sample')}
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
              <ValidatedField
                id="participant-result-shipmentParticipantMap"
                name="shipmentParticipantMap"
                data-cy="shipmentParticipantMap"
                label={translate('proficiencyTestingApp.participantResult.shipmentParticipantMap')}
                type="select"
                required
              >
                <option value="" key="0" />
                {shipmentParticipantMaps
                  ? shipmentParticipantMaps.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/participant-result" replace variant="info">
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

export default ParticipantResultUpdate;
