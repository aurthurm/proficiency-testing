import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './distribution.reducer';

export const DistributionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const distributionEntity = useAppSelector(state => state.distribution.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="distributionDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.distribution.detail.title">Distribution</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{distributionEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="proficiencyTestingApp.distribution.code">Code</Translate>
            </span>
          </dt>
          <dd>{distributionEntity.code}</dd>
          <dt>
            <span id="distributionDate">
              <Translate contentKey="proficiencyTestingApp.distribution.distributionDate">Distribution Date</Translate>
            </span>
          </dt>
          <dd>
            {distributionEntity.distributionDate ? (
              <TextFormat value={distributionEntity.distributionDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.distribution.status">Status</Translate>
            </span>
          </dt>
          <dd>{distributionEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/distribution" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/distribution/${distributionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DistributionDetail;
