import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Assay from './assay';
import AssayDeleteDialog from './assay-delete-dialog';
import AssayDetail from './assay-detail';
import AssayUpdate from './assay-update';

const AssayRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Assay />} />
    <Route path="new" element={<AssayUpdate />} />
    <Route path=":id">
      <Route index element={<AssayDetail />} />
      <Route path="edit" element={<AssayUpdate />} />
      <Route path="delete" element={<AssayDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AssayRoutes;
