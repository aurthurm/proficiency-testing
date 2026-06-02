import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './enrollment.reducer';

export const EnrollmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const enrollmentEntity = useAppSelector(state => state.enrollment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="enrollmentDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.enrollment.detail.title">Enrollment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.enrollment.status">Status</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.status}</dd>
          <dt>
            <span id="enrolledOn">
              <Translate contentKey="proficiencyTestingApp.enrollment.enrolledOn">Enrolled On</Translate>
            </span>
          </dt>
          <dd>
            {enrollmentEntity.enrolledOn ? (
              <TextFormat value={enrollmentEntity.enrolledOn} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="withdrawnOn">
              <Translate contentKey="proficiencyTestingApp.enrollment.withdrawnOn">Withdrawn On</Translate>
            </span>
          </dt>
          <dd>
            {enrollmentEntity.withdrawnOn ? (
              <TextFormat value={enrollmentEntity.withdrawnOn} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.enrollment.participant">Participant</Translate>
          </dt>
          <dd>{enrollmentEntity.participant ? enrollmentEntity.participant.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.enrollment.scheme">Scheme</Translate>
          </dt>
          <dd>{enrollmentEntity.scheme ? enrollmentEntity.scheme.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/enrollment" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/enrollment/${enrollmentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnrollmentDetail;
