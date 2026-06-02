import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './custom-field-definition.reducer';

export const CustomFieldDefinition = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const customFieldDefinitionList = useAppSelector(state => state.customFieldDefinition.entities);
  const loading = useAppSelector(state => state.customFieldDefinition.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="custom-field-definition-heading" data-cy="CustomFieldDefinitionHeading">
        <Translate contentKey="proficiencyTestingApp.customFieldDefinition.home.title">Custom Field Definitions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.customFieldDefinition.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/custom-field-definition/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.customFieldDefinition.home.createLabel">
              Create new Custom Field Definition
            </Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {customFieldDefinitionList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('fieldKey')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.fieldKey">Field Key</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fieldKey')} />
                </th>
                <th className="hand" onClick={sort('label')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.label">Label</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('label')} />
                </th>
                <th className="hand" onClick={sort('fieldType')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.fieldType">Field Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fieldType')} />
                </th>
                <th className="hand" onClick={sort('options')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.options">Options</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('options')} />
                </th>
                <th className="hand" onClick={sort('displayOrder')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.displayOrder">Display Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('displayOrder')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.customFieldDefinition.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {customFieldDefinitionList.map(customFieldDefinition => (
                <tr key={`entity-${customFieldDefinition.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/custom-field-definition/${customFieldDefinition.id}`} variant="link" size="sm">
                      {customFieldDefinition.id}
                    </Button>
                  </td>
                  <td>{customFieldDefinition.fieldKey}</td>
                  <td>{customFieldDefinition.label}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.CustomFieldType.${customFieldDefinition.fieldType}`} />
                  </td>
                  <td>{customFieldDefinition.options}</td>
                  <td>{customFieldDefinition.displayOrder}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.Status.${customFieldDefinition.status}`} />
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/custom-field-definition/${customFieldDefinition.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/custom-field-definition/${customFieldDefinition.id}/edit`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/custom-field-definition/${customFieldDefinition.id}/delete`)}
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="proficiencyTestingApp.customFieldDefinition.home.notFound">
                No Custom Field Definitions found
              </Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default CustomFieldDefinition;
