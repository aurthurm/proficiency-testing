import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './participant-result.reducer';

export const ParticipantResultDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const participantResultEntity = useAppSelector(state => state.participantResult.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="participantResultDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.participantResult.detail.title">ParticipantResult</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.id}</dd>
          <dt>
            <span id="reportedQualitativeResult">
              <Translate contentKey="proficiencyTestingApp.participantResult.reportedQualitativeResult">
                Reported Qualitative Result
              </Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.reportedQualitativeResult}</dd>
          <dt>
            <span id="reportedQuantitativeValue">
              <Translate contentKey="proficiencyTestingApp.participantResult.reportedQuantitativeValue">
                Reported Quantitative Value
              </Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.reportedQuantitativeValue}</dd>
          <dt>
            <span id="unit">
              <Translate contentKey="proficiencyTestingApp.participantResult.unit">Unit</Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.unit}</dd>
          <dt>
            <span id="lotNumber">
              <Translate contentKey="proficiencyTestingApp.participantResult.lotNumber">Lot Number</Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.lotNumber}</dd>
          <dt>
            <span id="expiryDate">
              <Translate contentKey="proficiencyTestingApp.participantResult.expiryDate">Expiry Date</Translate>
            </span>
          </dt>
          <dd>
            {participantResultEntity.expiryDate ? (
              <TextFormat value={participantResultEntity.expiryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="zScore">
              <Translate contentKey="proficiencyTestingApp.participantResult.zScore">Z Score</Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.zScore}</dd>
          <dt>
            <span id="calculatedScore">
              <Translate contentKey="proficiencyTestingApp.participantResult.calculatedScore">Calculated Score</Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.calculatedScore}</dd>
          <dt>
            <span id="comments">
              <Translate contentKey="proficiencyTestingApp.participantResult.comments">Comments</Translate>
            </span>
          </dt>
          <dd>{participantResultEntity.comments}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantResult.assay">Assay</Translate>
          </dt>
          <dd>{participantResultEntity.assay ? participantResultEntity.assay.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantResult.testKit">Test Kit</Translate>
          </dt>
          <dd>{participantResultEntity.testKit ? participantResultEntity.testKit.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantResult.sample">Sample</Translate>
          </dt>
          <dd>{participantResultEntity.sample ? participantResultEntity.sample.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.participantResult.shipmentParticipantMap">Shipment Participant Map</Translate>
          </dt>
          <dd>{participantResultEntity.shipmentParticipantMap ? participantResultEntity.shipmentParticipantMap.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/participant-result" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/participant-result/${participantResultEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParticipantResultDetail;
