import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DataManager from './data-manager';
import DataManagerDeleteDialog from './data-manager-delete-dialog';
import DataManagerDetail from './data-manager-detail';
import DataManagerUpdate from './data-manager-update';

const DataManagerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DataManager />} />
    <Route path="new" element={<DataManagerUpdate />} />
    <Route path=":id">
      <Route index element={<DataManagerDetail />} />
      <Route path="edit" element={<DataManagerUpdate />} />
      <Route path="delete" element={<DataManagerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DataManagerRoutes;
