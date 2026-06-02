import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './data-manager.reducer';

export const DataManager = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const dataManagerList = useAppSelector(state => state.dataManager.entities);
  const loading = useAppSelector(state => state.dataManager.loading);
  const totalItems = useAppSelector(state => state.dataManager.totalItems);

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
      <h2 id="data-manager-heading" data-cy="DataManagerHeading">
        <Translate contentKey="proficiencyTestingApp.dataManager.home.title">Data Managers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.dataManager.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/data-manager/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.dataManager.home.createLabel">Create new Data Manager</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {dataManagerList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('firstName')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.firstName">First Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('firstName')} />
                </th>
                <th className="hand" onClick={sort('lastName')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.lastName">Last Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastName')} />
                </th>
                <th className="hand" onClick={sort('institute')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.institute">Institute</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('institute')} />
                </th>
                <th className="hand" onClick={sort('primaryEmail')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.primaryEmail">Primary Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('primaryEmail')} />
                </th>
                <th className="hand" onClick={sort('secondaryEmail')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.secondaryEmail">Secondary Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('secondaryEmail')} />
                </th>
                <th className="hand" onClick={sort('phone')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.phone">Phone</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phone')} />
                </th>
                <th className="hand" onClick={sort('mobile')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.mobile">Mobile</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mobile')} />
                </th>
                <th className="hand" onClick={sort('language')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.language">Language</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('language')} />
                </th>
                <th className="hand" onClick={sort('role')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.role">Role</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('role')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('qcAccess')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.qcAccess">Qc Access</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('qcAccess')} />
                </th>
                <th className="hand" onClick={sort('viewOnlyAccess')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.viewOnlyAccess">View Only Access</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('viewOnlyAccess')} />
                </th>
                <th className="hand" onClick={sort('enableTestResponseDate')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.enableTestResponseDate">Enable Test Response Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('enableTestResponseDate')} />
                </th>
                <th className="hand" onClick={sort('enableModeOfReceipt')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.enableModeOfReceipt">Enable Mode Of Receipt</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('enableModeOfReceipt')} />
                </th>
                <th className="hand" onClick={sort('forceProfileCheck')}>
                  <Translate contentKey="proficiencyTestingApp.dataManager.forceProfileCheck">Force Profile Check</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('forceProfileCheck')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.dataManager.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.dataManager.country">Country</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {dataManagerList.map(dataManager => (
                <tr key={`entity-${dataManager.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/data-manager/${dataManager.id}`} variant="link" size="sm">
                      {dataManager.id}
                    </Button>
                  </td>
                  <td>{dataManager.firstName}</td>
                  <td>{dataManager.lastName}</td>
                  <td>{dataManager.institute}</td>
                  <td>{dataManager.primaryEmail}</td>
                  <td>{dataManager.secondaryEmail}</td>
                  <td>{dataManager.phone}</td>
                  <td>{dataManager.mobile}</td>
                  <td>{dataManager.language}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.DataManagerRole.${dataManager.role}`} />
                  </td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.Status.${dataManager.status}`} />
                  </td>
                  <td>{dataManager.qcAccess ? 'true' : 'false'}</td>
                  <td>{dataManager.viewOnlyAccess ? 'true' : 'false'}</td>
                  <td>{dataManager.enableTestResponseDate ? 'true' : 'false'}</td>
                  <td>{dataManager.enableModeOfReceipt ? 'true' : 'false'}</td>
                  <td>{dataManager.forceProfileCheck ? 'true' : 'false'}</td>
                  <td>{dataManager.user ? dataManager.user.id : ''}</td>
                  <td>{dataManager.country ? <Link to={`/country/${dataManager.country.id}`}>{dataManager.country.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/data-manager/${dataManager.id}`}
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
                        to={`/data-manager/${dataManager.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/data-manager/${dataManager.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.dataManager.home.notFound">No Data Managers found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={dataManagerList && dataManagerList.length > 0 ? '' : 'd-none'}>
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

export default DataManager;
