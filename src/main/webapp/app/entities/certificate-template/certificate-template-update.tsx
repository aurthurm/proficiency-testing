import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { CertificateType } from 'app/shared/model/enumerations/certificate-type.model';

import { createEntity, getEntity, reset, updateEntity } from './certificate-template.reducer';

export const CertificateTemplateUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const certificateTemplateEntity = useAppSelector(state => state.certificateTemplate.entity);
  const loading = useAppSelector(state => state.certificateTemplate.loading);
  const updating = useAppSelector(state => state.certificateTemplate.updating);
  const updateSuccess = useAppSelector(state => state.certificateTemplate.updateSuccess);
  const certificateTypeValues = Object.keys(CertificateType);

  const handleClose = () => {
    navigate('/certificate-template');
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
      ...certificateTemplateEntity,
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
          certificateType: 'PARTICIPATION',
          ...certificateTemplateEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.certificateTemplate.home.createOrEditLabel" data-cy="CertificateTemplateCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.certificateTemplate.home.createOrEditLabel">
              Create or edit a CertificateTemplate
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
                  id="certificate-template-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateTemplate.certificateType')}
                id="certificate-template-certificateType"
                name="certificateType"
                data-cy="certificateType"
                type="select"
              >
                {certificateTypeValues.map(certificateType => (
                  <option value={certificateType} key={certificateType}>
                    {translate(`proficiencyTestingApp.CertificateType.${certificateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateTemplate.fileRef')}
                id="certificate-template-fileRef"
                name="fileRef"
                data-cy="fileRef"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateTemplate.detectedFields')}
                id="certificate-template-detectedFields"
                name="detectedFields"
                data-cy="detectedFields"
                type="textarea"
              />
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/certificate-template"
                replace
                variant="info"
              >
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

export default CertificateTemplateUpdate;
