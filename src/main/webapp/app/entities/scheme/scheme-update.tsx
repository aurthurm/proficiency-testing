import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCertificateTemplates } from 'app/entities/certificate-template/certificate-template.reducer';
import { ResultModality } from 'app/shared/model/enumerations/result-modality.model';
import { SchemeType } from 'app/shared/model/enumerations/scheme-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';

import { createEntity, getEntity, reset, updateEntity } from './scheme.reducer';

export const SchemeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const certificateTemplates = useAppSelector(state => state.certificateTemplate.entities);
  const schemeEntity = useAppSelector(state => state.scheme.entity);
  const loading = useAppSelector(state => state.scheme.loading);
  const updating = useAppSelector(state => state.scheme.updating);
  const updateSuccess = useAppSelector(state => state.scheme.updateSuccess);
  const schemeTypeValues = Object.keys(SchemeType);
  const resultModalityValues = Object.keys(ResultModality);
  const statusValues = Object.keys(Status);

  const handleClose = () => {
    navigate(`/scheme${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCertificateTemplates({}));
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
      ...schemeEntity,
      ...values,
      certificateTemplate: certificateTemplates.find(it => it.id.toString() === values.certificateTemplate?.toString()),
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
          schemeType: 'DTS',
          modality: 'QUALITATIVE',
          status: 'ACTIVE',
          ...schemeEntity,
          certificateTemplate: schemeEntity?.certificateTemplate?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.scheme.home.createOrEditLabel" data-cy="SchemeCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.scheme.home.createOrEditLabel">Create or edit a Scheme</Translate>
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
                  id="scheme-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.scheme.code')}
                id="scheme-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheme.name')}
                id="scheme-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.scheme.schemeType')}
                id="scheme-schemeType"
                name="schemeType"
                data-cy="schemeType"
                type="select"
              >
                {schemeTypeValues.map(schemeType => (
                  <option value={schemeType} key={schemeType}>
                    {translate(`proficiencyTestingApp.SchemeType.${schemeType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.scheme.modality')}
                id="scheme-modality"
                name="modality"
                data-cy="modality"
                type="select"
              >
                {resultModalityValues.map(resultModality => (
                  <option value={resultModality} key={resultModality}>
                    {translate(`proficiencyTestingApp.ResultModality.${resultModality}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.scheme.status')}
                id="scheme-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {statusValues.map(status => (
                  <option value={status} key={status}>
                    {translate(`proficiencyTestingApp.Status.${status}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="scheme-certificateTemplate"
                name="certificateTemplate"
                data-cy="certificateTemplate"
                label={translate('proficiencyTestingApp.scheme.certificateTemplate')}
                type="select"
              >
                <option value="" key="0" />
                {certificateTemplates
                  ? certificateTemplates.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/scheme" replace variant="info">
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

export default SchemeUpdate;
