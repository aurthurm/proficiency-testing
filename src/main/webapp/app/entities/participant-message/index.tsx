import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ParticipantMessage from './participant-message';
import ParticipantMessageDeleteDialog from './participant-message-delete-dialog';
import ParticipantMessageDetail from './participant-message-detail';
import ParticipantMessageUpdate from './participant-message-update';

const ParticipantMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ParticipantMessage />} />
    <Route path="new" element={<ParticipantMessageUpdate />} />
    <Route path=":id">
      <Route index element={<ParticipantMessageDetail />} />
      <Route path="edit" element={<ParticipantMessageUpdate />} />
      <Route path="delete" element={<ParticipantMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParticipantMessageRoutes;
