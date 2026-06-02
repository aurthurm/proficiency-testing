import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './sample-reference-result.reducer';

export const SampleReferenceResult = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const sampleReferenceResultList = useAppSelector(state => state.sampleReferenceResult.entities);
  const loading = useAppSelector(state => state.sampleReferenceResult.loading);
  const totalItems = useAppSelector(state => state.sampleReferenceResult.totalItems);

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
      <h2 id="sample-reference-result-heading" data-cy="SampleReferenceResultHeading">
        <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.home.title">Sample Reference Results</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/sample-reference-result/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.home.createLabel">
              Create new Sample Reference Result
            </Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {sampleReferenceResultList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('qualitativeResult')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.qualitativeResult">Qualitative Result</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('qualitativeResult')} />
                </th>
                <th className="hand" onClick={sort('quantitativeValue')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.quantitativeValue">Quantitative Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('quantitativeValue')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.unit">Unit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('lowerLimit')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.lowerLimit">Lower Limit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lowerLimit')} />
                </th>
                <th className="hand" onClick={sort('upperLimit')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.upperLimit">Upper Limit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('upperLimit')} />
                </th>
                <th className="hand" onClick={sort('isControlExpected')}>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.isControlExpected">Is Control Expected</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isControlExpected')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.assay">Assay</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.sample">Sample</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {sampleReferenceResultList.map(sampleReferenceResult => (
                <tr key={`entity-${sampleReferenceResult.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/sample-reference-result/${sampleReferenceResult.id}`} variant="link" size="sm">
                      {sampleReferenceResult.id}
                    </Button>
                  </td>
                  <td>{sampleReferenceResult.qualitativeResult}</td>
                  <td>{sampleReferenceResult.quantitativeValue}</td>
                  <td>{sampleReferenceResult.unit}</td>
                  <td>{sampleReferenceResult.lowerLimit}</td>
                  <td>{sampleReferenceResult.upperLimit}</td>
                  <td>{sampleReferenceResult.isControlExpected ? 'true' : 'false'}</td>
                  <td>
                    {sampleReferenceResult.assay ? (
                      <Link to={`/assay/${sampleReferenceResult.assay.id}`}>{sampleReferenceResult.assay.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {sampleReferenceResult.sample ? (
                      <Link to={`/shipment-sample/${sampleReferenceResult.sample.id}`}>{sampleReferenceResult.sample.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/sample-reference-result/${sampleReferenceResult.id}`}
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
                        to={`/sample-reference-result/${sampleReferenceResult.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/sample-reference-result/${sampleReferenceResult.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.sampleReferenceResult.home.notFound">
                No Sample Reference Results found
              </Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={sampleReferenceResultList && sampleReferenceResultList.length > 0 ? '' : 'd-none'}>
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

export default SampleReferenceResult;
