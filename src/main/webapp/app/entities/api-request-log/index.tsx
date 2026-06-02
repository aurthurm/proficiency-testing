import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ApiRequestLog from './api-request-log';
import ApiRequestLogDeleteDialog from './api-request-log-delete-dialog';
import ApiRequestLogDetail from './api-request-log-detail';
import ApiRequestLogUpdate from './api-request-log-update';

const ApiRequestLogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ApiRequestLog />} />
    <Route path="new" element={<ApiRequestLogUpdate />} />
    <Route path=":id">
      <Route index element={<ApiRequestLogDetail />} />
      <Route path="edit" element={<ApiRequestLogUpdate />} />
      <Route path="delete" element={<ApiRequestLogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ApiRequestLogRoutes;
