import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './audit-log.reducer';

export const AuditLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const auditLogEntity = useAppSelector(state => state.auditLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditLogDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.auditLog.detail.title">AuditLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.id}</dd>
          <dt>
            <span id="action">
              <Translate contentKey="proficiencyTestingApp.auditLog.action">Action</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.action}</dd>
          <dt>
            <span id="statement">
              <Translate contentKey="proficiencyTestingApp.auditLog.statement">Statement</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.statement}</dd>
          <dt>
            <span id="performedBy">
              <Translate contentKey="proficiencyTestingApp.auditLog.performedBy">Performed By</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.performedBy}</dd>
          <dt>
            <span id="performedByRole">
              <Translate contentKey="proficiencyTestingApp.auditLog.performedByRole">Performed By Role</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.performedByRole}</dd>
          <dt>
            <span id="performedOn">
              <Translate contentKey="proficiencyTestingApp.auditLog.performedOn">Performed On</Translate>
            </span>
          </dt>
          <dd>
            {auditLogEntity.performedOn ? <TextFormat value={auditLogEntity.performedOn} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="proficiencyTestingApp.auditLog.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.ipAddress}</dd>
          <dt>
            <span id="userAgent">
              <Translate contentKey="proficiencyTestingApp.auditLog.userAgent">User Agent</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.userAgent}</dd>
          <dt>
            <span id="sessionHash">
              <Translate contentKey="proficiencyTestingApp.auditLog.sessionHash">Session Hash</Translate>
            </span>
          </dt>
          <dd>{auditLogEntity.sessionHash}</dd>
        </dl>
        <Button as={Link as any} to="/audit-log" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/audit-log/${auditLogEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuditLogDetail;
