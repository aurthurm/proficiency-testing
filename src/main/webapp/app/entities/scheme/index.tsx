import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Scheme from './scheme';
import SchemeDeleteDialog from './scheme-delete-dialog';
import SchemeDetail from './scheme-detail';
import SchemeUpdate from './scheme-update';

const SchemeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Scheme />} />
    <Route path="new" element={<SchemeUpdate />} />
    <Route path=":id">
      <Route index element={<SchemeDetail />} />
      <Route path="edit" element={<SchemeUpdate />} />
      <Route path="delete" element={<SchemeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SchemeRoutes;
