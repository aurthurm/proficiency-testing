import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './shipment-participant-map.reducer';

export const ShipmentParticipantMapDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const shipmentParticipantMapEntity = useAppSelector(state => state.shipmentParticipantMap.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="shipmentParticipantMapDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.detail.title">ShipmentParticipantMap</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.id}</dd>
          <dt>
            <span id="responseStatus">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.responseStatus">Response Status</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.responseStatus}</dd>
          <dt>
            <span id="shipmentReceiptDate">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentReceiptDate">Shipment Receipt Date</Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.shipmentReceiptDate ? (
              <TextFormat value={shipmentParticipantMapEntity.shipmentReceiptDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="shipmentTestDate">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentTestDate">Shipment Test Date</Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.shipmentTestDate ? (
              <TextFormat value={shipmentParticipantMapEntity.shipmentTestDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="shipmentTestReportDate">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentTestReportDate">
                Shipment Test Report Date
              </Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.shipmentTestReportDate ? (
              <TextFormat value={shipmentParticipantMapEntity.shipmentTestReportDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.submittedAt ? (
              <TextFormat value={shipmentParticipantMapEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="evaluatedAt">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.evaluatedAt">Evaluated At</Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.evaluatedAt ? (
              <TextFormat value={shipmentParticipantMapEntity.evaluatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="isExcluded">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isExcluded">Is Excluded</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.isExcluded ? 'true' : 'false'}</dd>
          <dt>
            <span id="isResponseLate">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isResponseLate">Is Response Late</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.isResponseLate ? 'true' : 'false'}</dd>
          <dt>
            <span id="isPtTestNotPerformed">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isPtTestNotPerformed">Is Pt Test Not Performed</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.isPtTestNotPerformed ? 'true' : 'false'}</dd>
          <dt>
            <span id="ptTestNotPerformedComments">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.ptTestNotPerformedComments">
                Pt Test Not Performed Comments
              </Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.ptTestNotPerformedComments}</dd>
          <dt>
            <span id="supervisorApproved">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.supervisorApproved">Supervisor Approved</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.supervisorApproved ? 'true' : 'false'}</dd>
          <dt>
            <span id="participantSupervisor">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.participantSupervisor">Participant Supervisor</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.participantSupervisor}</dd>
          <dt>
            <span id="userComment">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.userComment">User Comment</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.userComment}</dd>
          <dt>
            <span id="shipmentScore">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentScore">Shipment Score</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.shipmentScore}</dd>
          <dt>
            <span id="documentationScore">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.documentationScore">Documentation Score</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.documentationScore}</dd>
          <dt>
            <span id="finalResult">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.finalResult">Final Result</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.finalResult}</dd>
          <dt>
            <span id="failureReason">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.failureReason">Failure Reason</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.failureReason}</dd>
          <dt>
            <span id="evaluationComment">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.evaluationComment">Evaluation Comment</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.evaluationComment}</dd>
          <dt>
            <span id="isFollowup">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isFollowup">Is Followup</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.isFollowup ? 'true' : 'false'}</dd>
          <dt>
            <span id="manualOverride">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.manualOverride">Manual Override</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.manualOverride ? 'true' : 'false'}</dd>
          <dt>
            <span id="qcStatus">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.qcStatus">Qc Status</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.qcStatus}</dd>
          <dt>
            <span id="qcDate">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.qcDate">Qc Date</Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.qcDate ? (
              <TextFormat value={shipmentParticipantMapEntity.qcDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="qcDoneBy">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.qcDoneBy">Qc Done By</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.qcDoneBy}</dd>
          <dt>
            <span id="syncedToMobile">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.syncedToMobile">Synced To Mobile</Translate>
            </span>
          </dt>
          <dd>{shipmentParticipantMapEntity.syncedToMobile ? 'true' : 'false'}</dd>
          <dt>
            <span id="syncedOn">
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.syncedOn">Synced On</Translate>
            </span>
          </dt>
          <dd>
            {shipmentParticipantMapEntity.syncedOn ? (
              <TextFormat value={shipmentParticipantMapEntity.syncedOn} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.modeOfReceipt">Mode Of Receipt</Translate>
          </dt>
          <dd>{shipmentParticipantMapEntity.modeOfReceipt ? shipmentParticipantMapEntity.modeOfReceipt.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.notTestedReason">Not Tested Reason</Translate>
          </dt>
          <dd>{shipmentParticipantMapEntity.notTestedReason ? shipmentParticipantMapEntity.notTestedReason.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipment">Shipment</Translate>
          </dt>
          <dd>{shipmentParticipantMapEntity.shipment ? shipmentParticipantMapEntity.shipment.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.participant">Participant</Translate>
          </dt>
          <dd>{shipmentParticipantMapEntity.participant ? shipmentParticipantMapEntity.participant.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/shipment-participant-map" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/shipment-participant-map/${shipmentParticipantMapEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ShipmentParticipantMapDetail;
