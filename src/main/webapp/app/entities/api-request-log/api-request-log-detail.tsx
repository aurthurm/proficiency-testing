import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './api-request-log.reducer';

export const ApiRequestLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const apiRequestLogEntity = useAppSelector(state => state.apiRequestLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="apiRequestLogDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.apiRequestLog.detail.title">ApiRequestLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.id}</dd>
          <dt>
            <span id="transactionId">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.transactionId">Transaction Id</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.transactionId}</dd>
          <dt>
            <span id="requestedBy">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.requestedBy">Requested By</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.requestedBy}</dd>
          <dt>
            <span id="requestedOn">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.requestedOn">Requested On</Translate>
            </span>
          </dt>
          <dd>
            {apiRequestLogEntity.requestedOn ? (
              <TextFormat value={apiRequestLogEntity.requestedOn} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="numberOfRecords">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.numberOfRecords">Number Of Records</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.numberOfRecords}</dd>
          <dt>
            <span id="requestType">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.requestType">Request Type</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.requestType}</dd>
          <dt>
            <span id="testType">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.testType">Test Type</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.testType}</dd>
          <dt>
            <span id="apiUrl">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.apiUrl">Api Url</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.apiUrl}</dd>
          <dt>
            <span id="dataFormat">
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.dataFormat">Data Format</Translate>
            </span>
          </dt>
          <dd>{apiRequestLogEntity.dataFormat}</dd>
        </dl>
        <Button as={Link as any} to="/api-request-log" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/api-request-log/${apiRequestLogEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ApiRequestLogDetail;
