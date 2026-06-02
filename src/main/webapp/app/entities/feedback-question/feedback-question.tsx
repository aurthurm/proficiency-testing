import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './feedback-question.reducer';

export const FeedbackQuestion = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const feedbackQuestionList = useAppSelector(state => state.feedbackQuestion.entities);
  const loading = useAppSelector(state => state.feedbackQuestion.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="feedback-question-heading" data-cy="FeedbackQuestionHeading">
        <Translate contentKey="proficiencyTestingApp.feedbackQuestion.home.title">Feedback Questions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.feedbackQuestion.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/feedback-question/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.feedbackQuestion.home.createLabel">Create new Feedback Question</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {feedbackQuestionList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.feedbackQuestion.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('questionText')}>
                  <Translate contentKey="proficiencyTestingApp.feedbackQuestion.questionText">Question Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('questionText')} />
                </th>
                <th className="hand" onClick={sort('displayOrder')}>
                  <Translate contentKey="proficiencyTestingApp.feedbackQuestion.displayOrder">Display Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('displayOrder')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.feedbackQuestion.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {feedbackQuestionList.map(feedbackQuestion => (
                <tr key={`entity-${feedbackQuestion.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/feedback-question/${feedbackQuestion.id}`} variant="link" size="sm">
                      {feedbackQuestion.id}
                    </Button>
                  </td>
                  <td>{feedbackQuestion.questionText}</td>
                  <td>{feedbackQuestion.displayOrder}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.ContentStatus.${feedbackQuestion.status}`} />
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/feedback-question/${feedbackQuestion.id}`}
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
                        to={`/feedback-question/${feedbackQuestion.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/feedback-question/${feedbackQuestion.id}/delete`)}
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
              <Translate contentKey="proficiencyTestingApp.feedbackQuestion.home.notFound">No Feedback Questions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default FeedbackQuestion;
