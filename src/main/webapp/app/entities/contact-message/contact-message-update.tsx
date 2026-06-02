import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './contact-message.reducer';

export const ContactMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const contactMessageEntity = useAppSelector(state => state.contactMessage.entity);
  const loading = useAppSelector(state => state.contactMessage.loading);
  const updating = useAppSelector(state => state.contactMessage.updating);
  const updateSuccess = useAppSelector(state => state.contactMessage.updateSuccess);

  const handleClose = () => {
    navigate(`/contact-message${location.search}`);
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
    values.submittedAt = convertDateTimeToServer(values.submittedAt);

    const entity = {
      ...contactMessageEntity,
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
          submittedAt: displayDefaultDateTime(),
        }
      : {
          ...contactMessageEntity,
          submittedAt: convertDateTimeFromServer(contactMessageEntity.submittedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.contactMessage.home.createOrEditLabel" data-cy="ContactMessageCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.contactMessage.home.createOrEditLabel">Create or edit a ContactMessage</Translate>
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
                  id="contact-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.name')}
                id="contact-message-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.email')}
                id="contact-message-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.subject')}
                id="contact-message-subject"
                name="subject"
                data-cy="subject"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.message')}
                id="contact-message-message"
                name="message"
                data-cy="message"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.ipAddress')}
                id="contact-message-ipAddress"
                name="ipAddress"
                data-cy="ipAddress"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.submittedAt')}
                id="contact-message-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.contactMessage.isHandled')}
                id="contact-message-isHandled"
                name="isHandled"
                data-cy="isHandled"
                check
                type="checkbox"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/contact-message" replace variant="info">
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

export default ContactMessageUpdate;
