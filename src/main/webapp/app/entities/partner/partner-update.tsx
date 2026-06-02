import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

import { createEntity, getEntity, reset, updateEntity } from './partner.reducer';

export const PartnerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const partnerEntity = useAppSelector(state => state.partner.entity);
  const loading = useAppSelector(state => state.partner.loading);
  const updating = useAppSelector(state => state.partner.updating);
  const updateSuccess = useAppSelector(state => state.partner.updateSuccess);
  const contentStatusValues = Object.keys(ContentStatus);

  const handleClose = () => {
    navigate('/partner');
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
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }

    const entity = {
      ...partnerEntity,
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
          ...partnerEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.partner.home.createOrEditLabel" data-cy="PartnerCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.partner.home.createOrEditLabel">Create or edit a Partner</Translate>
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
                  id="partner-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.partner.name')}
                id="partner-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.partner.link')}
                id="partner-link"
                name="link"
                data-cy="link"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.partner.logoRef')}
                id="partner-logoRef"
                name="logoRef"
                data-cy="logoRef"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.partner.sortOrder')}
                id="partner-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.partner.status')}
                id="partner-status"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/partner" replace variant="info">
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

export default PartnerUpdate;
