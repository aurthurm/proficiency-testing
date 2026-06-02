import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './api-request-log.reducer';

export const ApiRequestLog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const apiRequestLogList = useAppSelector(state => state.apiRequestLog.entities);
  const loading = useAppSelector(state => state.apiRequestLog.loading);
  const totalItems = useAppSelector(state => state.apiRequestLog.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="api-request-log-heading" data-cy="ApiRequestLogHeading">
        <Translate contentKey="proficiencyTestingApp.apiRequestLog.home.title">Api Request Logs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.apiRequestLog.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/api-request-log/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.apiRequestLog.home.createLabel">Create new Api Request Log</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {apiRequestLogList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('transactionId')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.transactionId">Transaction Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('transactionId')} />
                </th>
                <th className="hand" onClick={sort('requestedBy')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.requestedBy">Requested By</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedBy')} />
                </th>
                <th className="hand" onClick={sort('requestedOn')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.requestedOn">Requested On</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedOn')} />
                </th>
                <th className="hand" onClick={sort('numberOfRecords')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.numberOfRecords">Number Of Records</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('numberOfRecords')} />
                </th>
                <th className="hand" onClick={sort('requestType')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.requestType">Request Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestType')} />
                </th>
                <th className="hand" onClick={sort('testType')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.testType">Test Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('testType')} />
                </th>
                <th className="hand" onClick={sort('apiUrl')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.apiUrl">Api Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('apiUrl')} />
                </th>
                <th className="hand" onClick={sort('dataFormat')}>
                  <Translate contentKey="proficiencyTestingApp.apiRequestLog.dataFormat">Data Format</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dataFormat')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {apiRequestLogList.map(apiRequestLog => (
                <tr key={`entity-${apiRequestLog.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/api-request-log/${apiRequestLog.id}`} variant="link" size="sm">
                      {apiRequestLog.id}
                    </Button>
                  </td>
                  <td>{apiRequestLog.transactionId}</td>
                  <td>{apiRequestLog.requestedBy}</td>
                  <td>
                    {apiRequestLog.requestedOn ? (
                      <TextFormat type="date" value={apiRequestLog.requestedOn} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{apiRequestLog.numberOfRecords}</td>
                  <td>{apiRequestLog.requestType}</td>
                  <td>{apiRequestLog.testType}</td>
                  <td>{apiRequestLog.apiUrl}</td>
                  <td>{apiRequestLog.dataFormat}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/api-request-log/${apiRequestLog.id}`}
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
                        to={`/api-request-log/${apiRequestLog.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                        onClick={() =>
                          (globalThis.location.href = `/api-request-log/${apiRequestLog.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
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
              <Translate contentKey="proficiencyTestingApp.apiRequestLog.home.notFound">No Api Request Logs found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={apiRequestLogList && apiRequestLogList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default ApiRequestLog;
