import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TestKit from './test-kit';
import TestKitDeleteDialog from './test-kit-delete-dialog';
import TestKitDetail from './test-kit-detail';
import TestKitUpdate from './test-kit-update';

const TestKitRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TestKit />} />
    <Route path="new" element={<TestKitUpdate />} />
    <Route path=":id">
      <Route index element={<TestKitDetail />} />
      <Route path="edit" element={<TestKitUpdate />} />
      <Route path="delete" element={<TestKitDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TestKitRoutes;
