import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import NotTestedReason from './not-tested-reason';
import NotTestedReasonDeleteDialog from './not-tested-reason-delete-dialog';
import NotTestedReasonDetail from './not-tested-reason-detail';
import NotTestedReasonUpdate from './not-tested-reason-update';

const NotTestedReasonRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<NotTestedReason />} />
    <Route path="new" element={<NotTestedReasonUpdate />} />
    <Route path=":id">
      <Route index element={<NotTestedReasonDetail />} />
      <Route path="edit" element={<NotTestedReasonUpdate />} />
      <Route path="delete" element={<NotTestedReasonDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default NotTestedReasonRoutes;
