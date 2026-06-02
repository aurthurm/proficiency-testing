import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './announcement.reducer';

export const AnnouncementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const announcementEntity = useAppSelector(state => state.announcement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="announcementDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.announcement.detail.title">Announcement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{announcementEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="proficiencyTestingApp.announcement.title">Title</Translate>
            </span>
          </dt>
          <dd>{announcementEntity.title}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="proficiencyTestingApp.announcement.body">Body</Translate>
            </span>
          </dt>
          <dd>{announcementEntity.body}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.announcement.status">Status</Translate>
            </span>
          </dt>
          <dd>{announcementEntity.status}</dd>
          <dt>
            <span id="publishedFrom">
              <Translate contentKey="proficiencyTestingApp.announcement.publishedFrom">Published From</Translate>
            </span>
          </dt>
          <dd>
            {announcementEntity.publishedFrom ? (
              <TextFormat value={announcementEntity.publishedFrom} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="publishedTo">
              <Translate contentKey="proficiencyTestingApp.announcement.publishedTo">Published To</Translate>
            </span>
          </dt>
          <dd>
            {announcementEntity.publishedTo ? (
              <TextFormat value={announcementEntity.publishedTo} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/announcement" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/announcement/${announcementEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AnnouncementDetail;
