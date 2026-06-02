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

import { getEntities } from './shipment-participant-map.reducer';

export const ShipmentParticipantMap = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const shipmentParticipantMapList = useAppSelector(state => state.shipmentParticipantMap.entities);
  const loading = useAppSelector(state => state.shipmentParticipantMap.loading);
  const totalItems = useAppSelector(state => state.shipmentParticipantMap.totalItems);

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
      <h2 id="shipment-participant-map-heading" data-cy="ShipmentParticipantMapHeading">
        <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.home.title">Shipment Participant Maps</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/shipment-participant-map/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.home.createLabel">
              Create new Shipment Participant Map
            </Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {shipmentParticipantMapList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('responseStatus')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.responseStatus">Response Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseStatus')} />
                </th>
                <th className="hand" onClick={sort('shipmentReceiptDate')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentReceiptDate">Shipment Receipt Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shipmentReceiptDate')} />
                </th>
                <th className="hand" onClick={sort('shipmentTestDate')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentTestDate">Shipment Test Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shipmentTestDate')} />
                </th>
                <th className="hand" onClick={sort('shipmentTestReportDate')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentTestReportDate">
                    Shipment Test Report Date
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shipmentTestReportDate')} />
                </th>
                <th className="hand" onClick={sort('submittedAt')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.submittedAt">Submitted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submittedAt')} />
                </th>
                <th className="hand" onClick={sort('evaluatedAt')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.evaluatedAt">Evaluated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('evaluatedAt')} />
                </th>
                <th className="hand" onClick={sort('isExcluded')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isExcluded">Is Excluded</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isExcluded')} />
                </th>
                <th className="hand" onClick={sort('isResponseLate')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isResponseLate">Is Response Late</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isResponseLate')} />
                </th>
                <th className="hand" onClick={sort('isPtTestNotPerformed')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isPtTestNotPerformed">
                    Is Pt Test Not Performed
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPtTestNotPerformed')} />
                </th>
                <th className="hand" onClick={sort('ptTestNotPerformedComments')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.ptTestNotPerformedComments">
                    Pt Test Not Performed Comments
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('ptTestNotPerformedComments')} />
                </th>
                <th className="hand" onClick={sort('supervisorApproved')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.supervisorApproved">Supervisor Approved</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supervisorApproved')} />
                </th>
                <th className="hand" onClick={sort('participantSupervisor')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.participantSupervisor">
                    Participant Supervisor
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('participantSupervisor')} />
                </th>
                <th className="hand" onClick={sort('userComment')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.userComment">User Comment</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('userComment')} />
                </th>
                <th className="hand" onClick={sort('shipmentScore')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipmentScore">Shipment Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shipmentScore')} />
                </th>
                <th className="hand" onClick={sort('documentationScore')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.documentationScore">Documentation Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('documentationScore')} />
                </th>
                <th className="hand" onClick={sort('finalResult')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.finalResult">Final Result</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('finalResult')} />
                </th>
                <th className="hand" onClick={sort('failureReason')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.failureReason">Failure Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('failureReason')} />
                </th>
                <th className="hand" onClick={sort('evaluationComment')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.evaluationComment">Evaluation Comment</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('evaluationComment')} />
                </th>
                <th className="hand" onClick={sort('isFollowup')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.isFollowup">Is Followup</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isFollowup')} />
                </th>
                <th className="hand" onClick={sort('manualOverride')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.manualOverride">Manual Override</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('manualOverride')} />
                </th>
                <th className="hand" onClick={sort('qcStatus')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.qcStatus">Qc Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('qcStatus')} />
                </th>
                <th className="hand" onClick={sort('qcDate')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.qcDate">Qc Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('qcDate')} />
                </th>
                <th className="hand" onClick={sort('qcDoneBy')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.qcDoneBy">Qc Done By</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('qcDoneBy')} />
                </th>
                <th className="hand" onClick={sort('syncedToMobile')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.syncedToMobile">Synced To Mobile</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('syncedToMobile')} />
                </th>
                <th className="hand" onClick={sort('syncedOn')}>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.syncedOn">Synced On</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('syncedOn')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.modeOfReceipt">Mode Of Receipt</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.notTestedReason">Not Tested Reason</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.shipment">Shipment</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.participant">Participant</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {shipmentParticipantMapList.map(shipmentParticipantMap => (
                <tr key={`entity-${shipmentParticipantMap.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/shipment-participant-map/${shipmentParticipantMap.id}`} variant="link" size="sm">
                      {shipmentParticipantMap.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.ResponseStatus.${shipmentParticipantMap.responseStatus}`} />
                  </td>
                  <td>
                    {shipmentParticipantMap.shipmentReceiptDate ? (
                      <TextFormat type="date" value={shipmentParticipantMap.shipmentReceiptDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {shipmentParticipantMap.shipmentTestDate ? (
                      <TextFormat type="date" value={shipmentParticipantMap.shipmentTestDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {shipmentParticipantMap.shipmentTestReportDate ? (
                      <TextFormat type="date" value={shipmentParticipantMap.shipmentTestReportDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {shipmentParticipantMap.submittedAt ? (
                      <TextFormat type="date" value={shipmentParticipantMap.submittedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {shipmentParticipantMap.evaluatedAt ? (
                      <TextFormat type="date" value={shipmentParticipantMap.evaluatedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{shipmentParticipantMap.isExcluded ? 'true' : 'false'}</td>
                  <td>{shipmentParticipantMap.isResponseLate ? 'true' : 'false'}</td>
                  <td>{shipmentParticipantMap.isPtTestNotPerformed ? 'true' : 'false'}</td>
                  <td>{shipmentParticipantMap.ptTestNotPerformedComments}</td>
                  <td>{shipmentParticipantMap.supervisorApproved ? 'true' : 'false'}</td>
                  <td>{shipmentParticipantMap.participantSupervisor}</td>
                  <td>{shipmentParticipantMap.userComment}</td>
                  <td>{shipmentParticipantMap.shipmentScore}</td>
                  <td>{shipmentParticipantMap.documentationScore}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.FinalResult.${shipmentParticipantMap.finalResult}`} />
                  </td>
                  <td>{shipmentParticipantMap.failureReason}</td>
                  <td>{shipmentParticipantMap.evaluationComment}</td>
                  <td>{shipmentParticipantMap.isFollowup ? 'true' : 'false'}</td>
                  <td>{shipmentParticipantMap.manualOverride ? 'true' : 'false'}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.QcStatus.${shipmentParticipantMap.qcStatus}`} />
                  </td>
                  <td>
                    {shipmentParticipantMap.qcDate ? (
                      <TextFormat type="date" value={shipmentParticipantMap.qcDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{shipmentParticipantMap.qcDoneBy}</td>
                  <td>{shipmentParticipantMap.syncedToMobile ? 'true' : 'false'}</td>
                  <td>
                    {shipmentParticipantMap.syncedOn ? (
                      <TextFormat type="date" value={shipmentParticipantMap.syncedOn} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {shipmentParticipantMap.modeOfReceipt ? (
                      <Link to={`/mode-of-receipt/${shipmentParticipantMap.modeOfReceipt.id}`}>
                        {shipmentParticipantMap.modeOfReceipt.id}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {shipmentParticipantMap.notTestedReason ? (
                      <Link to={`/not-tested-reason/${shipmentParticipantMap.notTestedReason.id}`}>
                        {shipmentParticipantMap.notTestedReason.id}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {shipmentParticipantMap.shipment ? (
                      <Link to={`/shipment/${shipmentParticipantMap.shipment.id}`}>{shipmentParticipantMap.shipment.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {shipmentParticipantMap.participant ? (
                      <Link to={`/participant/${shipmentParticipantMap.participant.id}`}>{shipmentParticipantMap.participant.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/shipment-participant-map/${shipmentParticipantMap.id}`}
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
                        to={`/shipment-participant-map/${shipmentParticipantMap.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/shipment-participant-map/${shipmentParticipantMap.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.shipmentParticipantMap.home.notFound">
                No Shipment Participant Maps found
              </Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={shipmentParticipantMapList && shipmentParticipantMapList.length > 0 ? '' : 'd-none'}>
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

export default ShipmentParticipantMap;
