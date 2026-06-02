import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

import { createEntity, getEntity, reset, updateEntity } from './mail-template.reducer';

export const MailTemplateUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const mailTemplateEntity = useAppSelector(state => state.mailTemplate.entity);
  const loading = useAppSelector(state => state.mailTemplate.loading);
  const updating = useAppSelector(state => state.mailTemplate.updating);
  const updateSuccess = useAppSelector(state => state.mailTemplate.updateSuccess);
  const contentStatusValues = Object.keys(ContentStatus);

  const handleClose = () => {
    navigate('/mail-template');
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

    const entity = {
      ...mailTemplateEntity,
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
          ...mailTemplateEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.mailTemplate.home.createOrEditLabel" data-cy="MailTemplateCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.mailTemplate.home.createOrEditLabel">Create or edit a MailTemplate</Translate>
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
                  id="mail-template-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.mailTemplate.code')}
                id="mail-template-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.mailTemplate.subject')}
                id="mail-template-subject"
                name="subject"
                data-cy="subject"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.mailTemplate.htmlBody')}
                id="mail-template-htmlBody"
                name="htmlBody"
                data-cy="htmlBody"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.mailTemplate.status')}
                id="mail-template-status"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/mail-template" replace variant="info">
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

export default MailTemplateUpdate;
