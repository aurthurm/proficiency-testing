import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { useLocation, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './shipment-participant-map.reducer';

export const ShipmentParticipantMapDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id!));
    setLoadModal(true);
  }, []);

  const shipmentParticipantMapEntity = useAppSelector(state => state.shipmentParticipantMap.entity);
  const updateSuccess = useAppSelector(state => state.shipmentParticipantMap.updateSuccess);

  const handleClose = () => {
    navigate(`/shipment-participant-map${pageLocation.search}`);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(shipmentParticipantMapEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="shipmentParticipantMapDeleteDialogHeading" closeButton>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="proficiencyTestingApp.shipmentParticipantMap.delete.question">
        <Translate
          contentKey="proficiencyTestingApp.shipmentParticipantMap.delete.question"
          interpolate={{ id: shipmentParticipantMapEntity.id }}
        >
          Are you sure you want to delete this ShipmentParticipantMap?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-shipmentParticipantMap" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ShipmentParticipantMapDeleteDialog;
