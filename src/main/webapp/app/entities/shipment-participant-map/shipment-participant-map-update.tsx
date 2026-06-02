import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getModeOfReceipts } from 'app/entities/mode-of-receipt/mode-of-receipt.reducer';
import { getEntities as getNotTestedReasons } from 'app/entities/not-tested-reason/not-tested-reason.reducer';
import { getEntities as getParticipants } from 'app/entities/participant/participant.reducer';
import { getEntities as getShipments } from 'app/entities/shipment/shipment.reducer';
import { FinalResult } from 'app/shared/model/enumerations/final-result.model';
import { QcStatus } from 'app/shared/model/enumerations/qc-status.model';
import { ResponseStatus } from 'app/shared/model/enumerations/response-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './shipment-participant-map.reducer';

export const ShipmentParticipantMapUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const modeOfReceipts = useAppSelector(state => state.modeOfReceipt.entities);
  const notTestedReasons = useAppSelector(state => state.notTestedReason.entities);
  const shipments = useAppSelector(state => state.shipment.entities);
  const participants = useAppSelector(state => state.participant.entities);
  const shipmentParticipantMapEntity = useAppSelector(state => state.shipmentParticipantMap.entity);
  const loading = useAppSelector(state => state.shipmentParticipantMap.loading);
  const updating = useAppSelector(state => state.shipmentParticipantMap.updating);
  const updateSuccess = useAppSelector(state => state.shipmentParticipantMap.updateSuccess);
  const responseStatusValues = Object.keys(ResponseStatus);
  const finalResultValues = Object.keys(FinalResult);
  const qcStatusValues = Object.keys(QcStatus);

  const handleClose = () => {
    navigate(`/shipment-participant-map${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getModeOfReceipts({}));
    dispatch(getNotTestedReasons({}));
    dispatch(getShipments({}));
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
    values.shipmentTestReportDate = convertDateTimeToServer(values.shipmentTestReportDate);
    values.submittedAt = convertDateTimeToServer(values.submittedAt);
    values.evaluatedAt = convertDateTimeToServer(values.evaluatedAt);
    if (values.shipmentScore !== undefined && typeof values.shipmentScore !== 'number') {
      values.shipmentScore = Number(values.shipmentScore);
    }
    if (values.documentationScore !== undefined && typeof values.documentationScore !== 'number') {
      values.documentationScore = Number(values.documentationScore);
    }
    values.syncedOn = convertDateTimeToServer(values.syncedOn);

    const entity = {
      ...shipmentParticipantMapEntity,
      ...values,
      modeOfReceipt: modeOfReceipts.find(it => it.id.toString() === values.modeOfReceipt?.toString()),
      notTestedReason: notTestedReasons.find(it => it.id.toString() === values.notTestedReason?.toString()),
      shipment: shipments.find(it => it.id.toString() === values.shipment?.toString()),
      participant: participants.find(it => it.id.toString() === values.participant?.toString()),
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
          shipmentTestReportDate: displayDefaultDateTime(),
          submittedAt: displayDefaultDateTime(),
          evaluatedAt: displayDefaultDateTime(),
          syncedOn: displayDefaultDateTime(),
        }
      : {
          responseStatus: 'NOT_STARTED',
          finalResult: 'PASS',
          qcStatus: 'PENDING',
          ...shipmentParticipantMapEntity,
          shipmentTestReportDate: convertDateTimeFromServer(shipmentParticipantMapEntity.shipmentTestReportDate),
          submittedAt: convertDateTimeFromServer(shipmentParticipantMapEntity.submittedAt),
          evaluatedAt: convertDateTimeFromServer(shipmentParticipantMapEntity.evaluatedAt),
          syncedOn: convertDateTimeFromServer(shipmentParticipantMapEntity.syncedOn),
          modeOfReceipt: shipmentParticipantMapEntity?.modeOfReceipt?.id,
          notTestedReason: shipmentParticipantMapEntity?.notTestedReason?.id,
          shipment: shipmentParticipantMapEntity?.shipment?.id,
          participant: shipmentParticipantMapEntity?.participant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.shipmentParticipantMap.home.createOrEditLabel" data-cy="ShipmentParticipantMapCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.home.createOrEditLabel">
              Create or edit a ShipmentParticipantMap
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
                  id="shipment-participant-map-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.responseStatus')}
                id="shipment-participant-map-responseStatus"
                name="responseStatus"
                data-cy="responseStatus"
                type="select"
              >
                {responseStatusValues.map(responseStatus => (
                  <option value={responseStatus} key={responseStatus}>
                    {translate(`proficiencyTestingApp.ResponseStatus.${responseStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.shipmentReceiptDate')}
                id="shipment-participant-map-shipmentReceiptDate"
                name="shipmentReceiptDate"
                data-cy="shipmentReceiptDate"
                type="date"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.shipmentTestDate')}
                id="shipment-participant-map-shipmentTestDate"
                name="shipmentTestDate"
                data-cy="shipmentTestDate"
                type="date"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.shipmentTestReportDate')}
                id="shipment-participant-map-shipmentTestReportDate"
                name="shipmentTestReportDate"
                data-cy="shipmentTestReportDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.submittedAt')}
                id="shipment-participant-map-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.evaluatedAt')}
                id="shipment-participant-map-evaluatedAt"
                name="evaluatedAt"
                data-cy="evaluatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.isExcluded')}
                id="shipment-participant-map-isExcluded"
                name="isExcluded"
                data-cy="isExcluded"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.isResponseLate')}
                id="shipment-participant-map-isResponseLate"
                name="isResponseLate"
                data-cy="isResponseLate"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.isPtTestNotPerformed')}
                id="shipment-participant-map-isPtTestNotPerformed"
                name="isPtTestNotPerformed"
                data-cy="isPtTestNotPerformed"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.ptTestNotPerformedComments')}
                id="shipment-participant-map-ptTestNotPerformedComments"
                name="ptTestNotPerformedComments"
                data-cy="ptTestNotPerformedComments"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.supervisorApproved')}
                id="shipment-participant-map-supervisorApproved"
                name="supervisorApproved"
                data-cy="supervisorApproved"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.participantSupervisor')}
                id="shipment-participant-map-participantSupervisor"
                name="participantSupervisor"
                data-cy="participantSupervisor"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.userComment')}
                id="shipment-participant-map-userComment"
                name="userComment"
                data-cy="userComment"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.shipmentScore')}
                id="shipment-participant-map-shipmentScore"
                name="shipmentScore"
                data-cy="shipmentScore"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.documentationScore')}
                id="shipment-participant-map-documentationScore"
                name="documentationScore"
                data-cy="documentationScore"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.finalResult')}
                id="shipment-participant-map-finalResult"
                name="finalResult"
                data-cy="finalResult"
                type="select"
              >
                {finalResultValues.map(finalResult => (
                  <option value={finalResult} key={finalResult}>
                    {translate(`proficiencyTestingApp.FinalResult.${finalResult}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.failureReason')}
                id="shipment-participant-map-failureReason"
                name="failureReason"
                data-cy="failureReason"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.evaluationComment')}
                id="shipment-participant-map-evaluationComment"
                name="evaluationComment"
                data-cy="evaluationComment"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.isFollowup')}
                id="shipment-participant-map-isFollowup"
                name="isFollowup"
                data-cy="isFollowup"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.manualOverride')}
                id="shipment-participant-map-manualOverride"
                name="manualOverride"
                data-cy="manualOverride"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.qcStatus')}
                id="shipment-participant-map-qcStatus"
                name="qcStatus"
                data-cy="qcStatus"
                type="select"
              >
                {qcStatusValues.map(qcStatus => (
                  <option value={qcStatus} key={qcStatus}>
                    {translate(`proficiencyTestingApp.QcStatus.${qcStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.qcDate')}
                id="shipment-participant-map-qcDate"
                name="qcDate"
                data-cy="qcDate"
                type="date"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.qcDoneBy')}
                id="shipment-participant-map-qcDoneBy"
                name="qcDoneBy"
                data-cy="qcDoneBy"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.syncedToMobile')}
                id="shipment-participant-map-syncedToMobile"
                name="syncedToMobile"
                data-cy="syncedToMobile"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.shipmentParticipantMap.syncedOn')}
                id="shipment-participant-map-syncedOn"
                name="syncedOn"
                data-cy="syncedOn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="shipment-participant-map-modeOfReceipt"
                name="modeOfReceipt"
                data-cy="modeOfReceipt"
                label={translate('proficiencyTestingApp.shipmentParticipantMap.modeOfReceipt')}
                type="select"
              >
                <option value="" key="0" />
                {modeOfReceipts
                  ? modeOfReceipts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="shipment-participant-map-notTestedReason"
                name="notTestedReason"
                data-cy="notTestedReason"
                label={translate('proficiencyTestingApp.shipmentParticipantMap.notTestedReason')}
                type="select"
              >
                <option value="" key="0" />
                {notTestedReasons
                  ? notTestedReasons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="shipment-participant-map-shipment"
                name="shipment"
                data-cy="shipment"
                label={translate('proficiencyTestingApp.shipmentParticipantMap.shipment')}
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
              <ValidatedField
                id="shipment-participant-map-participant"
                name="participant"
                data-cy="participant"
                label={translate('proficiencyTestingApp.shipmentParticipantMap.participant')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/shipment-participant-map"
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

export default ShipmentParticipantMapUpdate;
