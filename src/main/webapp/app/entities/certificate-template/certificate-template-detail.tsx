import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './certificate-template.reducer';

export const CertificateTemplateDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const certificateTemplateEntity = useAppSelector(state => state.certificateTemplate.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="certificateTemplateDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.certificateTemplate.detail.title">CertificateTemplate</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{certificateTemplateEntity.id}</dd>
          <dt>
            <span id="certificateType">
              <Translate contentKey="proficiencyTestingApp.certificateTemplate.certificateType">Certificate Type</Translate>
            </span>
          </dt>
          <dd>{certificateTemplateEntity.certificateType}</dd>
          <dt>
            <span id="fileRef">
              <Translate contentKey="proficiencyTestingApp.certificateTemplate.fileRef">File Ref</Translate>
            </span>
          </dt>
          <dd>{certificateTemplateEntity.fileRef}</dd>
          <dt>
            <span id="detectedFields">
              <Translate contentKey="proficiencyTestingApp.certificateTemplate.detectedFields">Detected Fields</Translate>
            </span>
          </dt>
          <dd>{certificateTemplateEntity.detectedFields}</dd>
        </dl>
        <Button as={Link as any} to="/certificate-template" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/certificate-template/${certificateTemplateEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CertificateTemplateDetail;
