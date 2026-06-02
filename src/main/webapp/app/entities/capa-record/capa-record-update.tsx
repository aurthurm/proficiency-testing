import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCorrectiveActions } from 'app/entities/corrective-action/corrective-action.reducer';
import { getEntities as getShipmentParticipantMaps } from 'app/entities/shipment-participant-map/shipment-participant-map.reducer';
import { Status } from 'app/shared/model/enumerations/status.model';

import { createEntity, getEntity, reset, updateEntity } from './capa-record.reducer';

export const CapaRecordUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const correctiveActions = useAppSelector(state => state.correctiveAction.entities);
  const shipmentParticipantMaps = useAppSelector(state => state.shipmentParticipantMap.entities);
  const capaRecordEntity = useAppSelector(state => state.capaRecord.entity);
  const loading = useAppSelector(state => state.capaRecord.loading);
  const updating = useAppSelector(state => state.capaRecord.updating);
  const updateSuccess = useAppSelector(state => state.capaRecord.updateSuccess);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate(`/capa-record${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCorrectiveActions({}));
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

    const entity = {
      ...capaRecordEntity,
      ...values,
      correctiveAction: correctiveActions.find(it => it.id.toString() === values.correctiveAction?.toString()),
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
          status: 'ACTIVE',
          ...capaRecordEntity,
          correctiveAction: capaRecordEntity?.correctiveAction?.id,
          shipmentParticipantMap: capaRecordEntity?.shipmentParticipantMap?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.capaRecord.home.createOrEditLabel" data-cy="CapaRecordCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.capaRecord.home.createOrEditLabel">Create or edit a CapaRecord</Translate>
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
                  id="capa-record-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.capaRecord.rootCause')}
                id="capa-record-rootCause"
                name="rootCause"
                data-cy="rootCause"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.capaRecord.actionTaken')}
                id="capa-record-actionTaken"
                name="actionTaken"
                data-cy="actionTaken"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.capaRecord.actionDate')}
                id="capa-record-actionDate"
                name="actionDate"
                data-cy="actionDate"
                type="date"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.capaRecord.status')}
                id="capa-record-status"
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
                label={translate('proficiencyTestingApp.capaRecord.followUpDate')}
                id="capa-record-followUpDate"
                name="followUpDate"
                data-cy="followUpDate"
                type="date"
              />
              <ValidatedField
                id="capa-record-correctiveAction"
                name="correctiveAction"
                data-cy="correctiveAction"
                label={translate('proficiencyTestingApp.capaRecord.correctiveAction')}
                type="select"
              >
                <option value="" key="0" />
                {correctiveActions
                  ? correctiveActions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="capa-record-shipmentParticipantMap"
                name="shipmentParticipantMap"
                data-cy="shipmentParticipantMap"
                label={translate('proficiencyTestingApp.capaRecord.shipmentParticipantMap')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/capa-record" replace variant="info">
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

export default CapaRecordUpdate;
