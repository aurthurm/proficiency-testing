import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getSchemes } from 'app/entities/scheme/scheme.reducer';

import { createEntity, getEntity, reset, updateEntity } from './report-configuration.reducer';

export const ReportConfigurationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const schemes = useAppSelector(state => state.scheme.entities);
  const reportConfigurationEntity = useAppSelector(state => state.reportConfiguration.entity);
  const loading = useAppSelector(state => state.reportConfiguration.loading);
  const updating = useAppSelector(state => state.reportConfiguration.updating);
  const updateSuccess = useAppSelector(state => state.reportConfiguration.updateSuccess);

  const handleClose = () => {
    navigate('/report-configuration');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSchemes({}));
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
    if (values.topMargin !== undefined && typeof values.topMargin !== 'number') {
      values.topMargin = Number(values.topMargin);
    }

    const entity = {
      ...reportConfigurationEntity,
      ...values,
      scheme: schemes.find(it => it.id.toString() === values.scheme?.toString()),
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
          ...reportConfigurationEntity,
          scheme: reportConfigurationEntity?.scheme?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.reportConfiguration.home.createOrEditLabel" data-cy="ReportConfigurationCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.reportConfiguration.home.createOrEditLabel">
              Create or edit a ReportConfiguration
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
                  id="report-configuration-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.reportHeader')}
                id="report-configuration-reportHeader"
                name="reportHeader"
                data-cy="reportHeader"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.logo')}
                id="report-configuration-logo"
                name="logo"
                data-cy="logo"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.logoRight')}
                id="report-configuration-logoRight"
                name="logoRight"
                data-cy="logoRight"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.layout')}
                id="report-configuration-layout"
                name="layout"
                data-cy="layout"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.format')}
                id="report-configuration-format"
                name="format"
                data-cy="format"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.topMargin')}
                id="report-configuration-topMargin"
                name="topMargin"
                data-cy="topMargin"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.reportConfiguration.instituteAddressPosition')}
                id="report-configuration-instituteAddressPosition"
                name="instituteAddressPosition"
                data-cy="instituteAddressPosition"
                type="text"
              />
              <ValidatedField
                id="report-configuration-scheme"
                name="scheme"
                data-cy="scheme"
                label={translate('proficiencyTestingApp.reportConfiguration.scheme')}
                type="select"
              >
                <option value="" key="0" />
                {schemes
                  ? schemes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/report-configuration"
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

export default ReportConfigurationUpdate;
