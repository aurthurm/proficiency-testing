import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './not-tested-reason.reducer';

export const NotTestedReasonDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id!));
    setLoadModal(true);
  }, []);

  const notTestedReasonEntity = useAppSelector(state => state.notTestedReason.entity);
  const updateSuccess = useAppSelector(state => state.notTestedReason.updateSuccess);

  const handleClose = () => {
    navigate('/not-tested-reason');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(notTestedReasonEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="notTestedReasonDeleteDialogHeading" closeButton>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="proficiencyTestingApp.notTestedReason.delete.question">
        <Translate contentKey="proficiencyTestingApp.notTestedReason.delete.question" interpolate={{ id: notTestedReasonEntity.id }}>
          Are you sure you want to delete this NotTestedReason?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-notTestedReason" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default NotTestedReasonDeleteDialog;
