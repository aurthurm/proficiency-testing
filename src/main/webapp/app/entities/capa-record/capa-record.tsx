import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './capa-record.reducer';

export const CapaRecord = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const capaRecordList = useAppSelector(state => state.capaRecord.entities);
  const loading = useAppSelector(state => state.capaRecord.loading);
  const totalItems = useAppSelector(state => state.capaRecord.totalItems);

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
      <h2 id="capa-record-heading" data-cy="CapaRecordHeading">
        <Translate contentKey="proficiencyTestingApp.capaRecord.home.title">Capa Records</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.capaRecord.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/capa-record/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.capaRecord.home.createLabel">Create new Capa Record</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {capaRecordList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('rootCause')}>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.rootCause">Root Cause</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rootCause')} />
                </th>
                <th className="hand" onClick={sort('actionTaken')}>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.actionTaken">Action Taken</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('actionTaken')} />
                </th>
                <th className="hand" onClick={sort('actionDate')}>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.actionDate">Action Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('actionDate')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('followUpDate')}>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.followUpDate">Follow Up Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('followUpDate')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.correctiveAction">Corrective Action</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.capaRecord.shipmentParticipantMap">Shipment Participant Map</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {capaRecordList.map(capaRecord => (
                <tr key={`entity-${capaRecord.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/capa-record/${capaRecord.id}`} variant="link" size="sm">
                      {capaRecord.id}
                    </Button>
                  </td>
                  <td>{capaRecord.rootCause}</td>
                  <td>{capaRecord.actionTaken}</td>
                  <td>
                    {capaRecord.actionDate ? <TextFormat type="date" value={capaRecord.actionDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.Status.${capaRecord.status}`} />
                  </td>
                  <td>
                    {capaRecord.followUpDate ? (
                      <TextFormat type="date" value={capaRecord.followUpDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {capaRecord.correctiveAction ? (
                      <Link to={`/corrective-action/${capaRecord.correctiveAction.id}`}>{capaRecord.correctiveAction.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {capaRecord.shipmentParticipantMap ? (
                      <Link to={`/shipment-participant-map/${capaRecord.shipmentParticipantMap.id}`}>
                        {capaRecord.shipmentParticipantMap.id}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/capa-record/${capaRecord.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/capa-record/${capaRecord.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/capa-record/${capaRecord.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.capaRecord.home.notFound">No Capa Records found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={capaRecordList && capaRecordList.length > 0 ? '' : 'd-none'}>
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

export default CapaRecord;
