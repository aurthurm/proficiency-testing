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

import { getEntities } from './scheduled-job.reducer';

export const ScheduledJob = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const scheduledJobList = useAppSelector(state => state.scheduledJob.entities);
  const loading = useAppSelector(state => state.scheduledJob.loading);
  const totalItems = useAppSelector(state => state.scheduledJob.totalItems);

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
      <h2 id="scheduled-job-heading" data-cy="ScheduledJobHeading">
        <Translate contentKey="proficiencyTestingApp.scheduledJob.home.title">Scheduled Jobs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.scheduledJob.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/scheduled-job/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.scheduledJob.home.createLabel">Create new Scheduled Job</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {scheduledJobList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('jobType')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.jobType">Job Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('jobType')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('requestedBy')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.requestedBy">Requested By</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedBy')} />
                </th>
                <th className="hand" onClick={sort('requestedOn')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.requestedOn">Requested On</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedOn')} />
                </th>
                <th className="hand" onClick={sort('startedAt')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.startedAt">Started At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('startedAt')} />
                </th>
                <th className="hand" onClick={sort('lastHeartbeat')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.lastHeartbeat">Last Heartbeat</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastHeartbeat')} />
                </th>
                <th className="hand" onClick={sort('completedAt')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.completedAt">Completed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('completedAt')} />
                </th>
                <th className="hand" onClick={sort('progressCompleted')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.progressCompleted">Progress Completed</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('progressCompleted')} />
                </th>
                <th className="hand" onClick={sort('progressTotal')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.progressTotal">Progress Total</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('progressTotal')} />
                </th>
                <th className="hand" onClick={sort('summary')}>
                  <Translate contentKey="proficiencyTestingApp.scheduledJob.summary">Summary</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('summary')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {scheduledJobList.map(scheduledJob => (
                <tr key={`entity-${scheduledJob.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/scheduled-job/${scheduledJob.id}`} variant="link" size="sm">
                      {scheduledJob.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.JobType.${scheduledJob.jobType}`} />
                  </td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.JobStatus.${scheduledJob.status}`} />
                  </td>
                  <td>{scheduledJob.requestedBy}</td>
                  <td>
                    {scheduledJob.requestedOn ? <TextFormat type="date" value={scheduledJob.requestedOn} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {scheduledJob.startedAt ? <TextFormat type="date" value={scheduledJob.startedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {scheduledJob.lastHeartbeat ? (
                      <TextFormat type="date" value={scheduledJob.lastHeartbeat} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {scheduledJob.completedAt ? <TextFormat type="date" value={scheduledJob.completedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{scheduledJob.progressCompleted}</td>
                  <td>{scheduledJob.progressTotal}</td>
                  <td>{scheduledJob.summary}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/scheduled-job/${scheduledJob.id}`}
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
                        to={`/scheduled-job/${scheduledJob.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/scheduled-job/${scheduledJob.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.scheduledJob.home.notFound">No Scheduled Jobs found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={scheduledJobList && scheduledJobList.length > 0 ? '' : 'd-none'}>
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

export default ScheduledJob;
