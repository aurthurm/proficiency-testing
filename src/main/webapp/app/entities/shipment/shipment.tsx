import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './shipment.reducer';

export const Shipment = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const shipmentList = useAppSelector(state => state.shipment.entities);
  const loading = useAppSelector(state => state.shipment.loading);
  const totalItems = useAppSelector(state => state.shipment.totalItems);

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
      <h2 id="shipment-heading" data-cy="ShipmentHeading">
        <Translate contentKey="proficiencyTestingApp.shipment.home.title">Shipments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.shipment.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/shipment/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.shipment.home.createLabel">Create new Shipment</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {shipmentList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.code">Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('shipmentDate')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.shipmentDate">Shipment Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shipmentDate')} />
                </th>
                <th className="hand" onClick={sort('responseDeadline')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.responseDeadline">Response Deadline</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseDeadline')} />
                </th>
                <th className="hand" onClick={sort('responsesOpen')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.responsesOpen">Responses Open</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responsesOpen')} />
                </th>
                <th className="hand" onClick={sort('autoCloseAtDeadline')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.autoCloseAtDeadline">Auto Close At Deadline</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('autoCloseAtDeadline')} />
                </th>
                <th className="hand" onClick={sort('allowEditingResponse')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.allowEditingResponse">Allow Editing Response</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('allowEditingResponse')} />
                </th>
                <th className="hand" onClick={sort('issuingAuthority')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.issuingAuthority">Issuing Authority</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('issuingAuthority')} />
                </th>
                <th className="hand" onClick={sort('coordinatorName')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.coordinatorName">Coordinator Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('coordinatorName')} />
                </th>
                <th className="hand" onClick={sort('coordinatorEmail')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.coordinatorEmail">Coordinator Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('coordinatorEmail')} />
                </th>
                <th className="hand" onClick={sort('coordinatorPhone')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.coordinatorPhone">Coordinator Phone</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('coordinatorPhone')} />
                </th>
                <th className="hand" onClick={sort('numberOfSamples')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.numberOfSamples">Number Of Samples</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('numberOfSamples')} />
                </th>
                <th className="hand" onClick={sort('maxScore')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.maxScore">Max Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxScore')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('attributes')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.attributes">Attributes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attributes')} />
                </th>
                <th className="hand" onClick={sort('reportsGeneratedAt')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.reportsGeneratedAt">Reports Generated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportsGeneratedAt')} />
                </th>
                <th className="hand" onClick={sort('finalizedAt')}>
                  <Translate contentKey="proficiencyTestingApp.shipment.finalizedAt">Finalized At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('finalizedAt')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipment.distribution">Distribution</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipment.scheme">Scheme</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {shipmentList.map(shipment => (
                <tr key={`entity-${shipment.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/shipment/${shipment.id}`} variant="link" size="sm">
                      {shipment.id}
                    </Button>
                  </td>
                  <td>{shipment.code}</td>
                  <td>
                    {shipment.shipmentDate ? <TextFormat type="date" value={shipment.shipmentDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {shipment.responseDeadline ? (
                      <TextFormat type="date" value={shipment.responseDeadline} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{shipment.responsesOpen ? 'true' : 'false'}</td>
                  <td>{shipment.autoCloseAtDeadline ? 'true' : 'false'}</td>
                  <td>{shipment.allowEditingResponse ? 'true' : 'false'}</td>
                  <td>{shipment.issuingAuthority}</td>
                  <td>{shipment.coordinatorName}</td>
                  <td>{shipment.coordinatorEmail}</td>
                  <td>{shipment.coordinatorPhone}</td>
                  <td>{shipment.numberOfSamples}</td>
                  <td>{shipment.maxScore}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.ShipmentStatus.${shipment.status}`} />
                  </td>
                  <td>{shipment.attributes}</td>
                  <td>
                    {shipment.reportsGeneratedAt ? (
                      <TextFormat type="date" value={shipment.reportsGeneratedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{shipment.finalizedAt ? <TextFormat type="date" value={shipment.finalizedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {shipment.distribution ? <Link to={`/distribution/${shipment.distribution.id}`}>{shipment.distribution.id}</Link> : ''}
                  </td>
                  <td>{shipment.scheme ? <Link to={`/scheme/${shipment.scheme.id}`}>{shipment.scheme.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/shipment/${shipment.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/shipment/${shipment.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/shipment/${shipment.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.shipment.home.notFound">No Shipments found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={shipmentList && shipmentList.length > 0 ? '' : 'd-none'}>
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

export default Shipment;
