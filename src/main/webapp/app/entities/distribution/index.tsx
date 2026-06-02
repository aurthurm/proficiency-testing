import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Distribution from './distribution';
import DistributionDeleteDialog from './distribution-delete-dialog';
import DistributionDetail from './distribution-detail';
import DistributionUpdate from './distribution-update';

const DistributionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Distribution />} />
    <Route path="new" element={<DistributionUpdate />} />
    <Route path=":id">
      <Route index element={<DistributionDetail />} />
      <Route path="edit" element={<DistributionUpdate />} />
      <Route path="delete" element={<DistributionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DistributionRoutes;
