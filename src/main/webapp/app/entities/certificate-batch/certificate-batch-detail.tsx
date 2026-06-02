import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './certificate-batch.reducer';

export const CertificateBatchDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const certificateBatchEntity = useAppSelector(state => state.certificateBatch.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="certificateBatchDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.certificateBatch.detail.title">CertificateBatch</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.name">Name</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.status">Status</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.status}</dd>
          <dt>
            <span id="excellenceCount">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.excellenceCount">Excellence Count</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.excellenceCount}</dd>
          <dt>
            <span id="participationCount">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.participationCount">Participation Count</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.participationCount}</dd>
          <dt>
            <span id="skippedCount">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.skippedCount">Skipped Count</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.skippedCount}</dd>
          <dt>
            <span id="downloadUrl">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.downloadUrl">Download Url</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.downloadUrl}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.errorMessage}</dd>
          <dt>
            <span id="approvedBy">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.approvedBy">Approved By</Translate>
            </span>
          </dt>
          <dd>{certificateBatchEntity.approvedBy}</dd>
          <dt>
            <span id="approvedOn">
              <Translate contentKey="proficiencyTestingApp.certificateBatch.approvedOn">Approved On</Translate>
            </span>
          </dt>
          <dd>
            {certificateBatchEntity.approvedOn ? (
              <TextFormat value={certificateBatchEntity.approvedOn} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="proficiencyTestingApp.certificateBatch.shipments">Shipments</Translate>
          </dt>
          <dd>
            {certificateBatchEntity.shipmentses
              ? certificateBatchEntity.shipmentses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {certificateBatchEntity.shipmentses && i === certificateBatchEntity.shipmentses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/certificate-batch" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/certificate-batch/${certificateBatchEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CertificateBatchDetail;
