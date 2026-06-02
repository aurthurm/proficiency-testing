import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './scheme-configuration.reducer';

export const SchemeConfiguration = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const schemeConfigurationList = useAppSelector(state => state.schemeConfiguration.entities);
  const loading = useAppSelector(state => state.schemeConfiguration.loading);

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
      <h2 id="scheme-configuration-heading" data-cy="SchemeConfigurationHeading">
        <Translate contentKey="proficiencyTestingApp.schemeConfiguration.home.title">Scheme Configurations</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.schemeConfiguration.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/scheme-configuration/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.schemeConfiguration.home.createLabel">Create new Scheme Configuration</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {schemeConfigurationList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('version')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.version">Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('version')} />
                </th>
                <th className="hand" onClick={sort('effectiveDate')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.effectiveDate">Effective Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('effectiveDate')} />
                </th>
                <th className="hand" onClick={sort('passingScore')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.passingScore">Passing Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('passingScore')} />
                </th>
                <th className="hand" onClick={sort('documentationWeight')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.documentationWeight">Documentation Weight</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('documentationWeight')} />
                </th>
                <th className="hand" onClick={sort('allowLateResponse')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.allowLateResponse">Allow Late Response</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('allowLateResponse')} />
                </th>
                <th className="hand" onClick={sort('optionalFields')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.optionalFields">Optional Fields</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('optionalFields')} />
                </th>
                <th className="hand" onClick={sort('scoringRules')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.scoringRules">Scoring Rules</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('scoringRules')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.schemeConfiguration.scheme">Scheme</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {schemeConfigurationList.map(schemeConfiguration => (
                <tr key={`entity-${schemeConfiguration.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/scheme-configuration/${schemeConfiguration.id}`} variant="link" size="sm">
                      {schemeConfiguration.id}
                    </Button>
                  </td>
                  <td>{schemeConfiguration.version}</td>
                  <td>
                    {schemeConfiguration.effectiveDate ? (
                      <TextFormat type="date" value={schemeConfiguration.effectiveDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{schemeConfiguration.passingScore}</td>
                  <td>{schemeConfiguration.documentationWeight}</td>
                  <td>{schemeConfiguration.allowLateResponse ? 'true' : 'false'}</td>
                  <td>{schemeConfiguration.optionalFields}</td>
                  <td>{schemeConfiguration.scoringRules}</td>
                  <td>{schemeConfiguration.isActive ? 'true' : 'false'}</td>
                  <td>
                    {schemeConfiguration.scheme ? (
                      <Link to={`/scheme/${schemeConfiguration.scheme.id}`}>{schemeConfiguration.scheme.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/scheme-configuration/${schemeConfiguration.id}`}
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
                        to={`/scheme-configuration/${schemeConfiguration.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/scheme-configuration/${schemeConfiguration.id}/delete`)}
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
              <Translate contentKey="proficiencyTestingApp.schemeConfiguration.home.notFound">No Scheme Configurations found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default SchemeConfiguration;
