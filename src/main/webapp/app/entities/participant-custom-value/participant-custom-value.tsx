import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './participant-custom-value.reducer';

export const ParticipantCustomValue = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const participantCustomValueList = useAppSelector(state => state.participantCustomValue.entities);
  const loading = useAppSelector(state => state.participantCustomValue.loading);

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
      <h2 id="participant-custom-value-heading" data-cy="ParticipantCustomValueHeading">
        <Translate contentKey="proficiencyTestingApp.participantCustomValue.home.title">Participant Custom Values</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.participantCustomValue.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/participant-custom-value/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.participantCustomValue.home.createLabel">
              Create new Participant Custom Value
            </Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {participantCustomValueList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.participantCustomValue.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('value')}>
                  <Translate contentKey="proficiencyTestingApp.participantCustomValue.value">Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('value')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participantCustomValue.participant">Participant</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participantCustomValue.definition">Definition</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {participantCustomValueList.map(participantCustomValue => (
                <tr key={`entity-${participantCustomValue.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/participant-custom-value/${participantCustomValue.id}`} variant="link" size="sm">
                      {participantCustomValue.id}
                    </Button>
                  </td>
                  <td>{participantCustomValue.value}</td>
                  <td>
                    {participantCustomValue.participant ? (
                      <Link to={`/participant/${participantCustomValue.participant.id}`}>{participantCustomValue.participant.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {participantCustomValue.definition ? (
                      <Link to={`/custom-field-definition/${participantCustomValue.definition.id}`}>
                        {participantCustomValue.definition.id}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/participant-custom-value/${participantCustomValue.id}`}
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
                        to={`/participant-custom-value/${participantCustomValue.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/participant-custom-value/${participantCustomValue.id}/delete`)}
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
              <Translate contentKey="proficiencyTestingApp.participantCustomValue.home.notFound">
                No Participant Custom Values found
              </Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ParticipantCustomValue;
