import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './report-configuration.reducer';

export const ReportConfiguration = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const reportConfigurationList = useAppSelector(state => state.reportConfiguration.entities);
  const loading = useAppSelector(state => state.reportConfiguration.loading);

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
      <h2 id="report-configuration-heading" data-cy="ReportConfigurationHeading">
        <Translate contentKey="proficiencyTestingApp.reportConfiguration.home.title">Report Configurations</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.reportConfiguration.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/report-configuration/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.reportConfiguration.home.createLabel">Create new Report Configuration</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {reportConfigurationList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('reportHeader')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.reportHeader">Report Header</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportHeader')} />
                </th>
                <th className="hand" onClick={sort('logo')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.logo">Logo</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logo')} />
                </th>
                <th className="hand" onClick={sort('logoRight')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.logoRight">Logo Right</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logoRight')} />
                </th>
                <th className="hand" onClick={sort('layout')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.layout">Layout</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('layout')} />
                </th>
                <th className="hand" onClick={sort('format')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.format">Format</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('format')} />
                </th>
                <th className="hand" onClick={sort('topMargin')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.topMargin">Top Margin</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('topMargin')} />
                </th>
                <th className="hand" onClick={sort('instituteAddressPosition')}>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.instituteAddressPosition">
                    Institute Address Position
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('instituteAddressPosition')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.reportConfiguration.scheme">Scheme</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {reportConfigurationList.map(reportConfiguration => (
                <tr key={`entity-${reportConfiguration.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/report-configuration/${reportConfiguration.id}`} variant="link" size="sm">
                      {reportConfiguration.id}
                    </Button>
                  </td>
                  <td>{reportConfiguration.reportHeader}</td>
                  <td>{reportConfiguration.logo}</td>
                  <td>{reportConfiguration.logoRight}</td>
                  <td>{reportConfiguration.layout}</td>
                  <td>{reportConfiguration.format}</td>
                  <td>{reportConfiguration.topMargin}</td>
                  <td>{reportConfiguration.instituteAddressPosition}</td>
                  <td>
                    {reportConfiguration.scheme ? (
                      <Link to={`/scheme/${reportConfiguration.scheme.id}`}>{reportConfiguration.scheme.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/report-configuration/${reportConfiguration.id}`}
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
                        to={`/report-configuration/${reportConfiguration.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/report-configuration/${reportConfiguration.id}/delete`)}
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
              <Translate contentKey="proficiencyTestingApp.reportConfiguration.home.notFound">No Report Configurations found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ReportConfiguration;
