import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './mode-of-receipt.reducer';

export const ModeOfReceiptDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const modeOfReceiptEntity = useAppSelector(state => state.modeOfReceipt.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="modeOfReceiptDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.modeOfReceipt.detail.title">ModeOfReceipt</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{modeOfReceiptEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="proficiencyTestingApp.modeOfReceipt.name">Name</Translate>
            </span>
          </dt>
          <dd>{modeOfReceiptEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.modeOfReceipt.status">Status</Translate>
            </span>
          </dt>
          <dd>{modeOfReceiptEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/mode-of-receipt" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/mode-of-receipt/${modeOfReceiptEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ModeOfReceiptDetail;
