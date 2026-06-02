import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ParticipantResult from './participant-result';
import ParticipantResultDeleteDialog from './participant-result-delete-dialog';
import ParticipantResultDetail from './participant-result-detail';
import ParticipantResultUpdate from './participant-result-update';

const ParticipantResultRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ParticipantResult />} />
    <Route path="new" element={<ParticipantResultUpdate />} />
    <Route path=":id">
      <Route index element={<ParticipantResultDetail />} />
      <Route path="edit" element={<ParticipantResultUpdate />} />
      <Route path="delete" element={<ParticipantResultDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParticipantResultRoutes;
