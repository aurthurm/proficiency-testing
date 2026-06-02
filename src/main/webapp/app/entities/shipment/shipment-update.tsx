import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCertificateBatches } from 'app/entities/certificate-batch/certificate-batch.reducer';
import { getEntities as getDistributions } from 'app/entities/distribution/distribution.reducer';
import { getEntities as getSchemes } from 'app/entities/scheme/scheme.reducer';
import { ShipmentStatus } from 'app/shared/model/enumerations/shipment-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './shipment.reducer';

export const ShipmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const distributions = useAppSelector(state => state.distribution.entities);
  const schemes = useAppSelector(state => state.scheme.entities);
  const certificateBatches = useAppSelector(state => state.certificateBatch.entities);
  const shipmentEntity = useAppSelector(state => state.shipment.entity);
  const loading = useAppSelector(state => state.shipment.loading);
  const updating = useAppSelector(state => state.shipment.updating);
  const updateSuccess = useAppSelector(state => state.shipment.updateSuccess);
  const shipmentStatusValues = Object.keys(ShipmentStatus);

  const handleClose = () => {
    navigate(`/shipment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDistributions({}));
    dispatch(getSchemes({}));
    dispatch(getCertificateBatches({}));
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
    values.responseDeadline = convertDateTimeToServer(values.responseDeadline);
    if (values.numberOfSamples !== undefined && typeof values.numberOfSamples !== 'number') {
      values.numberOfSamples = Number(values.numberOfSamples);
    }
    if (values.maxScore !== undefined && typeof values.maxScore !== 'number') {
      values.maxScore = Number(values.maxScore);
    }
    values.reportsGeneratedAt = convertDateTimeToServer(values.reportsGeneratedAt);
    values.finalizedAt = convertDateTimeToServer(values.finalizedAt);

    const entity = {
      ...shipmentEntity,
      ...values,
      distribution: distributions.find(it => it.id.toString() === values.distribution?.toString()),
      scheme: schemes.find(it => it.id.toString() === values.scheme?.toString()),
      certificateBatcheses: mapIdList(values.certificateBatcheses),
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
          responseDeadline: displayDefaultDateTime(),
          reportsGeneratedAt: displayDefaultDateTime(),
          finalizedAt: displayDefaultDateTime(),
        }
      : {
          status: 'DRAFT',
          ...shipmentEntity,
          responseDeadline: convertDateTimeFromServer(shipmentEntity.responseDeadline),
          reportsGeneratedAt: convertDateTimeFromServer(shipmentEntity.reportsGeneratedAt),
          finalizedAt: convertDateTimeFromServer(shipmentEntity.finalizedAt),
          distribution: shipmentEntity?.distribution?.id,
          scheme: shipmentEntity?.scheme?.id,
          certificateBatcheses: shipmentEntity?.certificateBatcheses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.shipment.home.createOrEditLabel" data-cy="ShipmentCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.shipment.home.createOrEditLabel">Create or edit a Shipment</Translate>
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
                  id="shipment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.code')}
                id="shipment-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.shipmentDate')}
                id="shipment-shipmentDate"
                name="shipmentDate"
                data-cy="shipmentDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.responseDeadline')}
                id="shipment-responseDeadline"
                name="responseDeadline"
                data-cy="responseDeadline"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.responsesOpen')}
                id="shipment-responsesOpen"
                name="responsesOpen"
                data-cy="responsesOpen"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.autoCloseAtDeadline')}
                id="shipment-autoCloseAtDeadline"
                name="autoCloseAtDeadline"
                data-cy="autoCloseAtDeadline"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.allowEditingResponse')}
                id="shipment-allowEditingResponse"
                name="allowEditingResponse"
                data-cy="allowEditingResponse"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.issuingAuthority')}
                id="shipment-issuingAuthority"
                name="issuingAuthority"
                data-cy="issuingAuthority"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.coordinatorName')}
                id="shipment-coordinatorName"
                name="coordinatorName"
                data-cy="coordinatorName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.coordinatorEmail')}
                id="shipment-coordinatorEmail"
                name="coordinatorEmail"
                data-cy="coordinatorEmail"
                type="text"
                validate={{
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.coordinatorPhone')}
                id="shipment-coordinatorPhone"
                name="coordinatorPhone"
                data-cy="coordinatorPhone"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.numberOfSamples')}
                id="shipment-numberOfSamples"
                name="numberOfSamples"
                data-cy="numberOfSamples"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.maxScore')}
                id="shipment-maxScore"
                name="maxScore"
                data-cy="maxScore"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.status')}
                id="shipment-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {shipmentStatusValues.map(shipmentStatus => (
                  <option value={shipmentStatus} key={shipmentStatus}>
                    {translate(`proficiencyTestingApp.ShipmentStatus.${shipmentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.attributes')}
                id="shipment-attributes"
                name="attributes"
                data-cy="attributes"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.reportsGeneratedAt')}
                id="shipment-reportsGeneratedAt"
                name="reportsGeneratedAt"
                data-cy="reportsGeneratedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.finalizedAt')}
                id="shipment-finalizedAt"
                name="finalizedAt"
                data-cy="finalizedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="shipment-distribution"
                name="distribution"
                data-cy="distribution"
                label={translate('proficiencyTestingApp.shipment.distribution')}
                type="select"
              >
                <option value="" key="0" />
                {distributions
                  ? distributions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="shipment-scheme"
                name="scheme"
                data-cy="scheme"
                label={translate('proficiencyTestingApp.shipment.scheme')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                label={translate('proficiencyTestingApp.shipment.certificateBatches')}
                id="shipment-certificateBatches"
                data-cy="certificateBatches"
                type="select"
                multiple
                name="certificateBatcheses"
              >
                <option value="" key="0" />
                {certificateBatches
                  ? certificateBatches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/shipment" replace variant="info">
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

export default ShipmentUpdate;
