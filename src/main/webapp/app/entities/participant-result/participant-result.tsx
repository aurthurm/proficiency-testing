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

import { getEntities } from './participant-result.reducer';

export const ParticipantResult = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const participantResultList = useAppSelector(state => state.participantResult.entities);
  const loading = useAppSelector(state => state.participantResult.loading);
  const totalItems = useAppSelector(state => state.participantResult.totalItems);

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
      <h2 id="participant-result-heading" data-cy="ParticipantResultHeading">
        <Translate contentKey="proficiencyTestingApp.participantResult.home.title">Participant Results</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.participantResult.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/participant-result/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.participantResult.home.createLabel">Create new Participant Result</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {participantResultList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('reportedQualitativeResult')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.reportedQualitativeResult">
                    Reported Qualitative Result
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportedQualitativeResult')} />
                </th>
                <th className="hand" onClick={sort('reportedQuantitativeValue')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.reportedQuantitativeValue">
                    Reported Quantitative Value
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportedQuantitativeValue')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.unit">Unit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('lotNumber')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.lotNumber">Lot Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lotNumber')} />
                </th>
                <th className="hand" onClick={sort('expiryDate')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.expiryDate">Expiry Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('expiryDate')} />
                </th>
                <th className="hand" onClick={sort('zScore')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.zScore">Z Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('zScore')} />
                </th>
                <th className="hand" onClick={sort('calculatedScore')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.calculatedScore">Calculated Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('calculatedScore')} />
                </th>
                <th className="hand" onClick={sort('comments')}>
                  <Translate contentKey="proficiencyTestingApp.participantResult.comments">Comments</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('comments')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participantResult.assay">Assay</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participantResult.testKit">Test Kit</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participantResult.sample">Sample</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participantResult.shipmentParticipantMap">
                    Shipment Participant Map
                  </Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {participantResultList.map(participantResult => (
                <tr key={`entity-${participantResult.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/participant-result/${participantResult.id}`} variant="link" size="sm">
                      {participantResult.id}
                    </Button>
                  </td>
                  <td>{participantResult.reportedQualitativeResult}</td>
                  <td>{participantResult.reportedQuantitativeValue}</td>
                  <td>{participantResult.unit}</td>
                  <td>{participantResult.lotNumber}</td>
                  <td>
                    {participantResult.expiryDate ? (
                      <TextFormat type="date" value={participantResult.expiryDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{participantResult.zScore}</td>
                  <td>{participantResult.calculatedScore}</td>
                  <td>{participantResult.comments}</td>
                  <td>
                    {participantResult.assay ? <Link to={`/assay/${participantResult.assay.id}`}>{participantResult.assay.id}</Link> : ''}
                  </td>
                  <td>
                    {participantResult.testKit ? (
                      <Link to={`/test-kit/${participantResult.testKit.id}`}>{participantResult.testKit.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {participantResult.sample ? (
                      <Link to={`/shipment-sample/${participantResult.sample.id}`}>{participantResult.sample.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {participantResult.shipmentParticipantMap ? (
                      <Link to={`/shipment-participant-map/${participantResult.shipmentParticipantMap.id}`}>
                        {participantResult.shipmentParticipantMap.id}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/participant-result/${participantResult.id}`}
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
                        to={`/participant-result/${participantResult.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/participant-result/${participantResult.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.participantResult.home.notFound">No Participant Results found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={participantResultList && participantResultList.length > 0 ? '' : 'd-none'}>
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

export default ParticipantResult;
