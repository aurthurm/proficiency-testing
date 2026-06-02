import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

import { createEntity, getEntity, reset, updateEntity } from './home-page-section.reducer';

export const HomePageSectionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const homePageSectionEntity = useAppSelector(state => state.homePageSection.entity);
  const loading = useAppSelector(state => state.homePageSection.loading);
  const updating = useAppSelector(state => state.homePageSection.updating);
  const updateSuccess = useAppSelector(state => state.homePageSection.updateSuccess);
  const contentStatusValues = Object.keys(ContentStatus);

  const handleClose = () => {
    navigate('/home-page-section');
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
    if (values.displayOrder !== undefined && typeof values.displayOrder !== 'number') {
      values.displayOrder = Number(values.displayOrder);
    }

    const entity = {
      ...homePageSectionEntity,
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
      ? {}
      : {
          status: 'PUBLISHED',
          ...homePageSectionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.homePageSection.home.createOrEditLabel" data-cy="HomePageSectionCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.homePageSection.home.createOrEditLabel">
              Create or edit a HomePageSection
            </Translate>
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
                  id="home-page-section-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.section')}
                id="home-page-section-section"
                name="section"
                data-cy="section"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.type')}
                id="home-page-section-type"
                name="type"
                data-cy="type"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.title')}
                id="home-page-section-title"
                name="title"
                data-cy="title"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.text')}
                id="home-page-section-text"
                name="text"
                data-cy="text"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.link')}
                id="home-page-section-link"
                name="link"
                data-cy="link"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.fileRef')}
                id="home-page-section-fileRef"
                name="fileRef"
                data-cy="fileRef"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.icon')}
                id="home-page-section-icon"
                name="icon"
                data-cy="icon"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.displayOrder')}
                id="home-page-section-displayOrder"
                name="displayOrder"
                data-cy="displayOrder"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.homePageSection.status')}
                id="home-page-section-status"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/home-page-section" replace variant="info">
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

export default HomePageSectionUpdate;
