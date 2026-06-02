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

import { getEntities } from './shipment-sample.reducer';

export const ShipmentSample = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const shipmentSampleList = useAppSelector(state => state.shipmentSample.entities);
  const loading = useAppSelector(state => state.shipmentSample.loading);
  const totalItems = useAppSelector(state => state.shipmentSample.totalItems);

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
      <h2 id="shipment-sample-heading" data-cy="ShipmentSampleHeading">
        <Translate contentKey="proficiencyTestingApp.shipmentSample.home.title">Shipment Samples</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.shipmentSample.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/shipment-sample/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.shipmentSample.home.createLabel">Create new Shipment Sample</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {shipmentSampleList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('label')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.label">Label</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('label')} />
                </th>
                <th className="hand" onClick={sort('displayOrder')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.displayOrder">Display Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('displayOrder')} />
                </th>
                <th className="hand" onClick={sort('isControl')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.isControl">Is Control</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isControl')} />
                </th>
                <th className="hand" onClick={sort('isMandatory')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.isMandatory">Is Mandatory</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isMandatory')} />
                </th>
                <th className="hand" onClick={sort('sampleScore')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.sampleScore">Sample Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sampleScore')} />
                </th>
                <th className="hand" onClick={sort('preparationDate')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.preparationDate">Preparation Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('preparationDate')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipmentSample.shipment">Shipment</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {shipmentSampleList.map(shipmentSample => (
                <tr key={`entity-${shipmentSample.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/shipment-sample/${shipmentSample.id}`} variant="link" size="sm">
                      {shipmentSample.id}
                    </Button>
                  </td>
                  <td>{shipmentSample.label}</td>
                  <td>{shipmentSample.displayOrder}</td>
                  <td>{shipmentSample.isControl ? 'true' : 'false'}</td>
                  <td>{shipmentSample.isMandatory ? 'true' : 'false'}</td>
                  <td>{shipmentSample.sampleScore}</td>
                  <td>
                    {shipmentSample.preparationDate ? (
                      <TextFormat type="date" value={shipmentSample.preparationDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {shipmentSample.shipment ? (
                      <Link to={`/shipment/${shipmentSample.shipment.id}`}>{shipmentSample.shipment.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/shipment-sample/${shipmentSample.id}`}
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
                        to={`/shipment-sample/${shipmentSample.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/shipment-sample/${shipmentSample.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.shipmentSample.home.notFound">No Shipment Samples found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={shipmentSampleList && shipmentSampleList.length > 0 ? '' : 'd-none'}>
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

export default ShipmentSample;
