import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ParticipantCustomValue from './participant-custom-value';
import ParticipantCustomValueDeleteDialog from './participant-custom-value-delete-dialog';
import ParticipantCustomValueDetail from './participant-custom-value-detail';
import ParticipantCustomValueUpdate from './participant-custom-value-update';

const ParticipantCustomValueRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ParticipantCustomValue />} />
    <Route path="new" element={<ParticipantCustomValueUpdate />} />
    <Route path=":id">
      <Route index element={<ParticipantCustomValueDetail />} />
      <Route path="edit" element={<ParticipantCustomValueUpdate />} />
      <Route path="delete" element={<ParticipantCustomValueDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParticipantCustomValueRoutes;
