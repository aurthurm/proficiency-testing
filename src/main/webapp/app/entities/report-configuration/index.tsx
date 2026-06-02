import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ReportConfiguration from './report-configuration';
import ReportConfigurationDeleteDialog from './report-configuration-delete-dialog';
import ReportConfigurationDetail from './report-configuration-detail';
import ReportConfigurationUpdate from './report-configuration-update';

const ReportConfigurationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ReportConfiguration />} />
    <Route path="new" element={<ReportConfigurationUpdate />} />
    <Route path=":id">
      <Route index element={<ReportConfigurationDetail />} />
      <Route path="edit" element={<ReportConfigurationUpdate />} />
      <Route path="delete" element={<ReportConfigurationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReportConfigurationRoutes;
