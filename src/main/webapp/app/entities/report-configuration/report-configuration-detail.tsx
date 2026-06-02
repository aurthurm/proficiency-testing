import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './report-configuration.reducer';

export const ReportConfigurationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const reportConfigurationEntity = useAppSelector(state => state.reportConfiguration.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="reportConfigurationDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.reportConfiguration.detail.title">ReportConfiguration</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.id}</dd>
          <dt>
            <span id="reportHeader">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.reportHeader">Report Header</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.reportHeader}</dd>
          <dt>
            <span id="logo">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.logo">Logo</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.logo}</dd>
          <dt>
            <span id="logoRight">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.logoRight">Logo Right</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.logoRight}</dd>
          <dt>
            <span id="layout">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.layout">Layout</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.layout}</dd>
          <dt>
            <span id="format">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.format">Format</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.format}</dd>
          <dt>
            <span id="topMargin">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.topMargin">Top Margin</Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.topMargin}</dd>
          <dt>
            <span id="instituteAddressPosition">
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.instituteAddressPosition">
                Institute Address Position
              </Translate>
            </span>
          </dt>
          <dd>{reportConfigurationEntity.instituteAddressPosition}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.reportConfiguration.scheme">Scheme</Translate>
          </dt>
          <dd>{reportConfigurationEntity.scheme ? reportConfigurationEntity.scheme.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/report-configuration" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/report-configuration/${reportConfigurationEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReportConfigurationDetail;
