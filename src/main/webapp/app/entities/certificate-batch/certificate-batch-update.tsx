import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getShipments } from 'app/entities/shipment/shipment.reducer';
import { CertificateBatchStatus } from 'app/shared/model/enumerations/certificate-batch-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './certificate-batch.reducer';

export const CertificateBatchUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const shipments = useAppSelector(state => state.shipment.entities);
  const certificateBatchEntity = useAppSelector(state => state.certificateBatch.entity);
  const loading = useAppSelector(state => state.certificateBatch.loading);
  const updating = useAppSelector(state => state.certificateBatch.updating);
  const updateSuccess = useAppSelector(state => state.certificateBatch.updateSuccess);
  const certificateBatchStatusValues = Object.keys(CertificateBatchStatus);

  const handleClose = () => {
    navigate(`/certificate-batch${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getShipments({}));
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
    if (values.excellenceCount !== undefined && typeof values.excellenceCount !== 'number') {
      values.excellenceCount = Number(values.excellenceCount);
    }
    if (values.participationCount !== undefined && typeof values.participationCount !== 'number') {
      values.participationCount = Number(values.participationCount);
    }
    if (values.skippedCount !== undefined && typeof values.skippedCount !== 'number') {
      values.skippedCount = Number(values.skippedCount);
    }
    values.approvedOn = convertDateTimeToServer(values.approvedOn);

    const entity = {
      ...certificateBatchEntity,
      ...values,
      shipmentses: mapIdList(values.shipmentses),
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
          approvedOn: displayDefaultDateTime(),
        }
      : {
          status: 'PENDING',
          ...certificateBatchEntity,
          approvedOn: convertDateTimeFromServer(certificateBatchEntity.approvedOn),
          shipmentses: certificateBatchEntity?.shipmentses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="proficiencyTestingApp.certificateBatch.home.createOrEditLabel" data-cy="CertificateBatchCreateUpdateHeading">
            <Translate contentKey="proficiencyTestingApp.certificateBatch.home.createOrEditLabel">
              Create or edit a CertificateBatch
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
                  id="certificate-batch-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.name')}
                id="certificate-batch-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.status')}
                id="certificate-batch-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {certificateBatchStatusValues.map(certificateBatchStatus => (
                  <option value={certificateBatchStatus} key={certificateBatchStatus}>
                    {translate(`proficiencyTestingApp.CertificateBatchStatus.${certificateBatchStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.excellenceCount')}
                id="certificate-batch-excellenceCount"
                name="excellenceCount"
                data-cy="excellenceCount"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.participationCount')}
                id="certificate-batch-participationCount"
                name="participationCount"
                data-cy="participationCount"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.skippedCount')}
                id="certificate-batch-skippedCount"
                name="skippedCount"
                data-cy="skippedCount"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.downloadUrl')}
                id="certificate-batch-downloadUrl"
                name="downloadUrl"
                data-cy="downloadUrl"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.errorMessage')}
                id="certificate-batch-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.approvedBy')}
                id="certificate-batch-approvedBy"
                name="approvedBy"
                data-cy="approvedBy"
                type="text"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.approvedOn')}
                id="certificate-batch-approvedOn"
                name="approvedOn"
                data-cy="approvedOn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('proficiencyTestingApp.certificateBatch.shipments')}
                id="certificate-batch-shipments"
                data-cy="shipments"
                type="select"
                multiple
                name="shipmentses"
              >
                <option value="" key="0" />
                {shipments
                  ? shipments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/certificate-batch" replace variant="info">
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

export default CertificateBatchUpdate;
