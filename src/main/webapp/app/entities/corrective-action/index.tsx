import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CorrectiveAction from './corrective-action';
import CorrectiveActionDeleteDialog from './corrective-action-delete-dialog';
import CorrectiveActionDetail from './corrective-action-detail';
import CorrectiveActionUpdate from './corrective-action-update';

const CorrectiveActionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CorrectiveAction />} />
    <Route path="new" element={<CorrectiveActionUpdate />} />
    <Route path=":id">
      <Route index element={<CorrectiveActionDetail />} />
      <Route path="edit" element={<CorrectiveActionUpdate />} />
      <Route path="delete" element={<CorrectiveActionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CorrectiveActionRoutes;
