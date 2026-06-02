import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getMailTemplates } from 'app/entities/mail-template/mail-template.reducer';
import { EmailStatus } from 'app/shared/model/enumerations/email-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './email-message.reducer';

export const EmailMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const mailTemplates = useAppSelector(state => state.mailTemplate.entities);
  const emailMessageEntity = useAppSelector(state => state.emailMessage.entity);
  const loading = useAppSelector(state => state.emailMessage.loading);
  const updating = useAppSelector(state => state.emailMessage.updating);
  const updateSuccess = useAppSelector(state => state.emailMessage.updateSuccess);
  const emailStatusValues = Object.keys(EmailStatus);

  const handleClose = () => {
    navigate(`/email-message${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMailTemplates({}));
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
    values.queuedOn = convertDateTimeToServer(values.queuedOn);
    values.sentAt = convertDateTimeToServer(values.sentAt);
    if (values.retryCount !== undefined && typeof values.retryCount !== 'number') {
      values.retryCount = Number(values.retryCount);
    }

    const entity = {
      ...emailMessageEntity,
      ...values,
      template: mailTemplates.find(it => it.id.toString() === values.template?.toString()),
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
          queuedOn: displayDefaultDateTime(),
          sentAt: displayDefaultDateTime(),
        }
      : {
          status: 'PENDING',
          ...emailMessageEntity,
          queuedOn: convertDateTimeFromServer(emailMessageEntity.queuedOn),
          sentAt: convertDateTimeFromServer(emailMessageEntity.sentAt),
          template: emailMessageEntity?.template?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.emailMessage.home.createOrEditLabel" data-cy="EmailMessageCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.emailMessage.home.createOrEditLabel">Create or edit a EmailMessage</Translate>
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
                  id="email-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.fromEmail')}
                id="email-message-fromEmail"
                name="fromEmail"
                data-cy="fromEmail"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.fromName')}
                id="email-message-fromName"
                name="fromName"
                data-cy="fromName"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.replyTo')}
                id="email-message-replyTo"
                name="replyTo"
                data-cy="replyTo"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.toEmail')}
                id="email-message-toEmail"
                name="toEmail"
                data-cy="toEmail"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.cc')}
                id="email-message-cc"
                name="cc"
                data-cy="cc"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.bcc')}
                id="email-message-bcc"
                name="bcc"
                data-cy="bcc"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.subject')}
                id="email-message-subject"
                name="subject"
                data-cy="subject"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.body')}
                id="email-message-body"
                name="body"
                data-cy="body"
                type="textarea"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.attachmentRef')}
                id="email-message-attachmentRef"
                name="attachmentRef"
                data-cy="attachmentRef"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.status')}
                id="email-message-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {emailStatusValues.map(emailStatus => (
                  <option value={emailStatus} key={emailStatus}>
                    {translate(`proficiencyTestingApp.EmailStatus.${emailStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.failureType')}
                id="email-message-failureType"
                name="failureType"
                data-cy="failureType"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.failureReason')}
                id="email-message-failureReason"
                name="failureReason"
                data-cy="failureReason"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.queuedOn')}
                id="email-message-queuedOn"
                name="queuedOn"
                data-cy="queuedOn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.sentAt')}
                id="email-message-sentAt"
                name="sentAt"
                data-cy="sentAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.emailMessage.retryCount')}
                id="email-message-retryCount"
                name="retryCount"
                data-cy="retryCount"
                type="text"
              />
              <ValidatedField
                id="email-message-template"
                name="template"
                data-cy="template"
                label={translate('proficiencyTestingApp.emailMessage.template')}
                type="select"
              >
                <option value="" key="0" />
                {mailTemplates
                  ? mailTemplates.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/email-message" replace variant="info">
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

export default EmailMessageUpdate;
