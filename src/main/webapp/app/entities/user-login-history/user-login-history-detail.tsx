import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-login-history.reducer';

export const UserLoginHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const userLoginHistoryEntity = useAppSelector(state => state.userLoginHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userLoginHistoryDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.userLoginHistory.detail.title">UserLoginHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.id}</dd>
          <dt>
            <span id="loginId">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.loginId">Login Id</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.loginId}</dd>
          <dt>
            <span id="loginContext">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.loginContext">Login Context</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.loginContext}</dd>
          <dt>
            <span id="loginStatus">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.loginStatus">Login Status</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.loginStatus}</dd>
          <dt>
            <span id="attemptedAt">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.attemptedAt">Attempted At</Translate>
            </span>
          </dt>
          <dd>
            {userLoginHistoryEntity.attemptedAt ? (
              <TextFormat value={userLoginHistoryEntity.attemptedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.ipAddress}</dd>
          <dt>
            <span id="browser">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.browser">Browser</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.browser}</dd>
          <dt>
            <span id="operatingSystem">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.operatingSystem">Operating System</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.operatingSystem}</dd>
          <dt>
            <span id="sessionHash">
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.sessionHash">Session Hash</Translate>
            </span>
          </dt>
          <dd>{userLoginHistoryEntity.sessionHash}</dd>
        </dl>
        <Button as={Link as any} to="/user-login-history" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/user-login-history/${userLoginHistoryEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserLoginHistoryDetail;
