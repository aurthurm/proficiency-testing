import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ParticipantFeedback from './participant-feedback';
import ParticipantFeedbackDeleteDialog from './participant-feedback-delete-dialog';
import ParticipantFeedbackDetail from './participant-feedback-detail';
import ParticipantFeedbackUpdate from './participant-feedback-update';

const ParticipantFeedbackRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ParticipantFeedback />} />
    <Route path="new" element={<ParticipantFeedbackUpdate />} />
    <Route path=":id">
      <Route index element={<ParticipantFeedbackDetail />} />
      <Route path="edit" element={<ParticipantFeedbackUpdate />} />
      <Route path="delete" element={<ParticipantFeedbackDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParticipantFeedbackRoutes;
