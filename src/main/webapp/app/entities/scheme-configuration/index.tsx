import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SchemeConfiguration from './scheme-configuration';
import SchemeConfigurationDeleteDialog from './scheme-configuration-delete-dialog';
import SchemeConfigurationDetail from './scheme-configuration-detail';
import SchemeConfigurationUpdate from './scheme-configuration-update';

const SchemeConfigurationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SchemeConfiguration />} />
    <Route path="new" element={<SchemeConfigurationUpdate />} />
    <Route path=":id">
      <Route index element={<SchemeConfigurationDetail />} />
      <Route path="edit" element={<SchemeConfigurationUpdate />} />
      <Route path="delete" element={<SchemeConfigurationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SchemeConfigurationRoutes;
