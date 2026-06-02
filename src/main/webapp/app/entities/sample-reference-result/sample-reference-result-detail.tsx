import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sample-reference-result.reducer';

export const SampleReferenceResultDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const sampleReferenceResultEntity = useAppSelector(state => state.sampleReferenceResult.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="sampleReferenceResultDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.detail.title">SampleReferenceResult</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.id}</dd>
          <dt>
            <span id="qualitativeResult">
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.qualitativeResult">Qualitative Result</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.qualitativeResult}</dd>
          <dt>
            <span id="quantitativeValue">
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.quantitativeValue">Quantitative Value</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.quantitativeValue}</dd>
          <dt>
            <span id="unit">
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.unit">Unit</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.unit}</dd>
          <dt>
            <span id="lowerLimit">
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.lowerLimit">Lower Limit</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.lowerLimit}</dd>
          <dt>
            <span id="upperLimit">
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.upperLimit">Upper Limit</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.upperLimit}</dd>
          <dt>
            <span id="isControlExpected">
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.isControlExpected">Is Control Expected</Translate>
            </span>
          </dt>
          <dd>{sampleReferenceResultEntity.isControlExpected ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.assay">Assay</Translate>
          </dt>
          <dd>{sampleReferenceResultEntity.assay ? sampleReferenceResultEntity.assay.id : ''}</dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.sample">Sample</Translate>
          </dt>
          <dd>{sampleReferenceResultEntity.sample ? sampleReferenceResultEntity.sample.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/sample-reference-result" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/sample-reference-result/${sampleReferenceResultEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SampleReferenceResultDetail;
