import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './announcement.reducer';

export const AnnouncementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const announcementEntity = useAppSelector(state => state.announcement.entity);
  const loading = useAppSelector(state => state.announcement.loading);
  const updating = useAppSelector(state => state.announcement.updating);
  const updateSuccess = useAppSelector(state => state.announcement.updateSuccess);
  const contentStatusValues = Object.keys(ContentStatus);

  const handleClose = () => {
    navigate(`/announcement${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.publishedFrom = convertDateTimeToServer(values.publishedFrom);
    values.publishedTo = convertDateTimeToServer(values.publishedTo);

    const entity = {
      ...announcementEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          publishedFrom: displayDefaultDateTime(),
          publishedTo: displayDefaultDateTime(),
        }
      : {
          status: 'PUBLISHED',
          ...announcementEntity,
          publishedFrom: convertDateTimeFromServer(announcementEntity.publishedFrom),
          publishedTo: convertDateTimeFromServer(announcementEntity.publishedTo),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.announcement.home.createOrEditLabel" data-cy="AnnouncementCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.announcement.home.createOrEditLabel">Create or edit a Announcement</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="announcement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.announcement.title')}
                id="announcement-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.announcement.body')}
                id="announcement-body"
                name="body"
                data-cy="body"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.announcement.status')}
                id="announcement-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {contentStatusValues.map(contentStatus => (
                  <option value={contentStatus} key={contentStatus}>
                    {translate(`proficiencyTestingApp.ContentStatus.${contentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.announcement.publishedFrom')}
                id="announcement-publishedFrom"
                name="publishedFrom"
                data-cy="publishedFrom"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.announcement.publishedTo')}
                id="announcement-publishedTo"
                name="publishedTo"
                data-cy="publishedTo"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/announcement" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AnnouncementUpdate;
