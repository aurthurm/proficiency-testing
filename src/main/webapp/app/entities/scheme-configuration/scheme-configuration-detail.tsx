import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './scheme-configuration.reducer';

export const SchemeConfigurationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const schemeConfigurationEntity = useAppSelector(state => state.schemeConfiguration.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="schemeConfigurationDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.schemeConfiguration.detail.title">SchemeConfiguration</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.id}</dd>
          <dt>
            <span id="version">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.version">Version</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.version}</dd>
          <dt>
            <span id="effectiveDate">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.effectiveDate">Effective Date</Translate>
            </span>
          </dt>
          <dd>
            {schemeConfigurationEntity.effectiveDate ? (
              <TextFormat value={schemeConfigurationEntity.effectiveDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="passingScore">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.passingScore">Passing Score</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.passingScore}</dd>
          <dt>
            <span id="documentationWeight">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.documentationWeight">Documentation Weight</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.documentationWeight}</dd>
          <dt>
            <span id="allowLateResponse">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.allowLateResponse">Allow Late Response</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.allowLateResponse ? 'true' : 'false'}</dd>
          <dt>
            <span id="optionalFields">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.optionalFields">Optional Fields</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.optionalFields}</dd>
          <dt>
            <span id="scoringRules">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.scoringRules">Scoring Rules</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.scoringRules}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{schemeConfigurationEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.schemeConfiguration.scheme">Scheme</Translate>
          </dt>
          <dd>{schemeConfigurationEntity.scheme ? schemeConfigurationEntity.scheme.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/scheme-configuration" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/scheme-configuration/${schemeConfigurationEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SchemeConfigurationDetail;
