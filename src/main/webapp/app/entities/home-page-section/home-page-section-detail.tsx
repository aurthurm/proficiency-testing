import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './home-page-section.reducer';

export const HomePageSectionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const homePageSectionEntity = useAppSelector(state => state.homePageSection.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="homePageSectionDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.homePageSection.detail.title">HomePageSection</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.id}</dd>
          <dt>
            <span id="section">
              <Translate contentKey="proficiencyTestingApp.homePageSection.section">Section</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.section}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="proficiencyTestingApp.homePageSection.type">Type</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.type}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="proficiencyTestingApp.homePageSection.title">Title</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.title}</dd>
          <dt>
            <span id="text">
              <Translate contentKey="proficiencyTestingApp.homePageSection.text">Text</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.text}</dd>
          <dt>
            <span id="link">
              <Translate contentKey="proficiencyTestingApp.homePageSection.link">Link</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.link}</dd>
          <dt>
            <span id="fileRef">
              <Translate contentKey="proficiencyTestingApp.homePageSection.fileRef">File Ref</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.fileRef}</dd>
          <dt>
            <span id="icon">
              <Translate contentKey="proficiencyTestingApp.homePageSection.icon">Icon</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.icon}</dd>
          <dt>
            <span id="displayOrder">
              <Translate contentKey="proficiencyTestingApp.homePageSection.displayOrder">Display Order</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.displayOrder}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.homePageSection.status">Status</Translate>
            </span>
          </dt>
          <dd>{homePageSectionEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/home-page-section" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/home-page-section/${homePageSectionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HomePageSectionDetail;
