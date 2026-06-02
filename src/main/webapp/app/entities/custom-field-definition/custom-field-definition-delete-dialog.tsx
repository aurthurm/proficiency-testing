import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './custom-field-definition.reducer';

export const CustomFieldDefinitionDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id!));
    setLoadModal(true);
  }, []);

  const customFieldDefinitionEntity = useAppSelector(state => state.customFieldDefinition.entity);
  const updateSuccess = useAppSelector(state => state.customFieldDefinition.updateSuccess);

  const handleClose = () => {
    navigate('/custom-field-definition');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(customFieldDefinitionEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="customFieldDefinitionDeleteDialogHeading" closeButton>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="proficiencyTestingApp.customFieldDefinition.delete.question">
        <Translate
          contentKey="proficiencyTestingApp.customFieldDefinition.delete.question"
          interpolate={{ id: customFieldDefinitionEntity.id }}
        >
          Are you sure you want to delete this CustomFieldDefinition?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-customFieldDefinition" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default CustomFieldDefinitionDeleteDialog;
