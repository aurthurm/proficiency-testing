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

import { getEntities } from './email-message.reducer';

export const EmailMessage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const emailMessageList = useAppSelector(state => state.emailMessage.entities);
  const loading = useAppSelector(state => state.emailMessage.loading);
  const totalItems = useAppSelector(state => state.emailMessage.totalItems);

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
      <h2 id="email-message-heading" data-cy="EmailMessageHeading">
        <Translate contentKey="proficiencyTestingApp.emailMessage.home.title">Email Messages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.emailMessage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/email-message/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.emailMessage.home.createLabel">Create new Email Message</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {emailMessageList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('fromEmail')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.fromEmail">From Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fromEmail')} />
                </th>
                <th className="hand" onClick={sort('fromName')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.fromName">From Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fromName')} />
                </th>
                <th className="hand" onClick={sort('replyTo')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.replyTo">Reply To</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('replyTo')} />
                </th>
                <th className="hand" onClick={sort('toEmail')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.toEmail">To Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('toEmail')} />
                </th>
                <th className="hand" onClick={sort('cc')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.cc">Cc</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cc')} />
                </th>
                <th className="hand" onClick={sort('bcc')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.bcc">Bcc</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bcc')} />
                </th>
                <th className="hand" onClick={sort('subject')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.subject">Subject</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('subject')} />
                </th>
                <th className="hand" onClick={sort('body')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.body">Body</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('body')} />
                </th>
                <th className="hand" onClick={sort('attachmentRef')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.attachmentRef">Attachment Ref</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attachmentRef')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('failureType')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.failureType">Failure Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('failureType')} />
                </th>
                <th className="hand" onClick={sort('failureReason')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.failureReason">Failure Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('failureReason')} />
                </th>
                <th className="hand" onClick={sort('queuedOn')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.queuedOn">Queued On</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('queuedOn')} />
                </th>
                <th className="hand" onClick={sort('sentAt')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.sentAt">Sent At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sentAt')} />
                </th>
                <th className="hand" onClick={sort('retryCount')}>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.retryCount">Retry Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('retryCount')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.emailMessage.template">Template</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {emailMessageList.map(emailMessage => (
                <tr key={`entity-${emailMessage.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/email-message/${emailMessage.id}`} variant="link" size="sm">
                      {emailMessage.id}
                    </Button>
                  </td>
                  <td>{emailMessage.fromEmail}</td>
                  <td>{emailMessage.fromName}</td>
                  <td>{emailMessage.replyTo}</td>
                  <td>{emailMessage.toEmail}</td>
                  <td>{emailMessage.cc}</td>
                  <td>{emailMessage.bcc}</td>
                  <td>{emailMessage.subject}</td>
                  <td>{emailMessage.body}</td>
                  <td>{emailMessage.attachmentRef}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.EmailStatus.${emailMessage.status}`} />
                  </td>
                  <td>{emailMessage.failureType}</td>
                  <td>{emailMessage.failureReason}</td>
                  <td>
                    {emailMessage.queuedOn ? <TextFormat type="date" value={emailMessage.queuedOn} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{emailMessage.sentAt ? <TextFormat type="date" value={emailMessage.sentAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{emailMessage.retryCount}</td>
                  <td>
                    {emailMessage.template ? <Link to={`/mail-template/${emailMessage.template.id}`}>{emailMessage.template.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/email-message/${emailMessage.id}`}
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
                        to={`/email-message/${emailMessage.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (globalThis.location.href = `/email-message/${emailMessage.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="proficiencyTestingApp.emailMessage.home.notFound">No Email Messages found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={emailMessageList && emailMessageList.length > 0 ? '' : 'd-none'}>
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

export default EmailMessage;
