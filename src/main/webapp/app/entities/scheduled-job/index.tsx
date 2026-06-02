import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ScheduledJob from './scheduled-job';
import ScheduledJobDeleteDialog from './scheduled-job-delete-dialog';
import ScheduledJobDetail from './scheduled-job-detail';
import ScheduledJobUpdate from './scheduled-job-update';

const ScheduledJobRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ScheduledJob />} />
    <Route path="new" element={<ScheduledJobUpdate />} />
    <Route path=":id">
      <Route index element={<ScheduledJobDetail />} />
      <Route path="edit" element={<ScheduledJobUpdate />} />
      <Route path="delete" element={<ScheduledJobDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ScheduledJobRoutes;
