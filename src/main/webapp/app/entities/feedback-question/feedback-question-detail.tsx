import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './feedback-question.reducer';

export const FeedbackQuestionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const feedbackQuestionEntity = useAppSelector(state => state.feedbackQuestion.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="feedbackQuestionDetailsHeading">
          <Translate contentKey="proficiencyTestingApp.feedbackQuestion.detail.title">FeedbackQuestion</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{feedbackQuestionEntity.id}</dd>
          <dt>
            <span id="questionText">
              <Translate contentKey="proficiencyTestingApp.feedbackQuestion.questionText">Question Text</Translate>
            </span>
          </dt>
          <dd>{feedbackQuestionEntity.questionText}</dd>
          <dt>
            <span id="displayOrder">
              <Translate contentKey="proficiencyTestingApp.feedbackQuestion.displayOrder">Display Order</Translate>
            </span>
          </dt>
          <dd>{feedbackQuestionEntity.displayOrder}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="proficiencyTestingApp.feedbackQuestion.status">Status</Translate>
            </span>
          </dt>
          <dd>{feedbackQuestionEntity.status}</dd>
        </dl>
        <Button as={Link as any} to="/feedback-question" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/feedback-question/${feedbackQuestionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FeedbackQuestionDetail;
