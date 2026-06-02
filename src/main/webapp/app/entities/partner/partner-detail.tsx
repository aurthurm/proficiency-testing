import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './partner.reducer';

export const PartnerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const partnerEntity = useAppSelector(state => state.partner.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="partnerDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.partner.detail.title">Partner</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{partnerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.partner.name">Name</Translate>
            </span>
          </dt>
          <dd>{partnerEntity.name}</dd>
          <dt>
            <span id="link">
              <Translate contentKey="proficiencyTestingApp.partner.link">Link</Translate>
            </span>
          </dt>
          <dd>{partnerEntity.link}</dd>
          <dt>
            <span id="logoRef">
              <Translate contentKey="proficiencyTestingApp.partner.logoRef">Logo Ref</Translate>
            </span>
          </dt>
          <dd>{partnerEntity.logoRef}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="proficiencyTestingApp.partner.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{partnerEntity.sortOrder}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.partner.status">Status</Translate>
            </span>
          </dt>
          <dd>{partnerEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/partner" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/partner/${partnerEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PartnerDetail;
