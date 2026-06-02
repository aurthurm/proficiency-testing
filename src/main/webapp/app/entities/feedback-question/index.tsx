import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FeedbackQuestion from './feedback-question';
import FeedbackQuestionDeleteDialog from './feedback-question-delete-dialog';
import FeedbackQuestionDetail from './feedback-question-detail';
import FeedbackQuestionUpdate from './feedback-question-update';

const FeedbackQuestionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FeedbackQuestion />} />
    <Route path="new" element={<FeedbackQuestionUpdate />} />
    <Route path=":id">
      <Route index element={<FeedbackQuestionDetail />} />
      <Route path="edit" element={<FeedbackQuestionUpdate />} />
      <Route path="delete" element={<FeedbackQuestionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FeedbackQuestionRoutes;
