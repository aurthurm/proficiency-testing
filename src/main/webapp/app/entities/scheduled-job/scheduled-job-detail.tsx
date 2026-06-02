import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './scheduled-job.reducer';

export const ScheduledJobDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const scheduledJobEntity = useAppSelector(state => state.scheduledJob.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="scheduledJobDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.scheduledJob.detail.title">ScheduledJob</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.id}</dd>
          <dt>
            <span id="jobType">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.jobType">Job Type</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.jobType}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.status">Status</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.status}</dd>
          <dt>
            <span id="requestedBy">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.requestedBy">Requested By</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.requestedBy}</dd>
          <dt>
            <span id="requestedOn">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.requestedOn">Requested On</Translate>
            </span>
          </dt>
          <dd>
            {scheduledJobEntity.requestedOn ? (
              <TextFormat value={scheduledJobEntity.requestedOn} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="startedAt">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.startedAt">Started At</Translate>
            </span>
          </dt>
          <dd>
            {scheduledJobEntity.startedAt ? <TextFormat value={scheduledJobEntity.startedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastHeartbeat">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.lastHeartbeat">Last Heartbeat</Translate>
            </span>
          </dt>
          <dd>
            {scheduledJobEntity.lastHeartbeat ? (
              <TextFormat value={scheduledJobEntity.lastHeartbeat} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="completedAt">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.completedAt">Completed At</Translate>
            </span>
          </dt>
          <dd>
            {scheduledJobEntity.completedAt ? (
              <TextFormat value={scheduledJobEntity.completedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="progressCompleted">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.progressCompleted">Progress Completed</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.progressCompleted}</dd>
          <dt>
            <span id="progressTotal">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.progressTotal">Progress Total</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.progressTotal}</dd>
          <dt>
            <span id="summary">
              <Translate contentKey="proficiencyTestingApp.scheduledJob.summary">Summary</Translate>
            </span>
          </dt>
          <dd>{scheduledJobEntity.summary}</dd>
        </dl>
        <Button as={Link as any} to="/scheduled-job" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/scheduled-job/${scheduledJobEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ScheduledJobDetail;
