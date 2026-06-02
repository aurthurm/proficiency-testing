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

import { getEntities } from './user-login-history.reducer';

export const UserLoginHistory = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const userLoginHistoryList = useAppSelector(state => state.userLoginHistory.entities);
  const loading = useAppSelector(state => state.userLoginHistory.loading);
  const totalItems = useAppSelector(state => state.userLoginHistory.totalItems);

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
      <h2 id="user-login-history-heading" data-cy="UserLoginHistoryHeading">
        <Translate contentKey="proficiencyTestingApp.userLoginHistory.home.title">User Login Histories</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.userLoginHistory.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/user-login-history/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.userLoginHistory.home.createLabel">Create new User Login History</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {userLoginHistoryList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('loginId')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.loginId">Login Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('loginId')} />
                </th>
                <th className="hand" onClick={sort('loginContext')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.loginContext">Login Context</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('loginContext')} />
                </th>
                <th className="hand" onClick={sort('loginStatus')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.loginStatus">Login Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('loginStatus')} />
                </th>
                <th className="hand" onClick={sort('attemptedAt')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.attemptedAt">Attempted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attemptedAt')} />
                </th>
                <th className="hand" onClick={sort('ipAddress')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.ipAddress">Ip Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('ipAddress')} />
                </th>
                <th className="hand" onClick={sort('browser')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.browser">Browser</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('browser')} />
                </th>
                <th className="hand" onClick={sort('operatingSystem')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.operatingSystem">Operating System</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('operatingSystem')} />
                </th>
                <th className="hand" onClick={sort('sessionHash')}>
                  <Translate contentKey="proficiencyTestingApp.userLoginHistory.sessionHash">Session Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sessionHash')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {userLoginHistoryList.map(userLoginHistory => (
                <tr key={`entity-${userLoginHistory.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/user-login-history/${userLoginHistory.id}`} variant="link" size="sm">
                      {userLoginHistory.id}
                    </Button>
                  </td>
                  <td>{userLoginHistory.loginId}</td>
                  <td>{userLoginHistory.loginContext}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.LoginStatus.${userLoginHistory.loginStatus}`} />
                  </td>
                  <td>
                    {userLoginHistory.attemptedAt ? (
                      <TextFormat type="date" value={userLoginHistory.attemptedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{userLoginHistory.ipAddress}</td>
                  <td>{userLoginHistory.browser}</td>
                  <td>{userLoginHistory.operatingSystem}</td>
                  <td>{userLoginHistory.sessionHash}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/user-login-history/${userLoginHistory.id}`}
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
                        to={`/user-login-history/${userLoginHistory.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/user-login-history/${userLoginHistory.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.userLoginHistory.home.notFound">No User Login Histories found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={userLoginHistoryList && userLoginHistoryList.length > 0 ? '' : 'd-none'}>
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

export default UserLoginHistory;
