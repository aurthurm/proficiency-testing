import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ShipmentSample from './shipment-sample';
import ShipmentSampleDeleteDialog from './shipment-sample-delete-dialog';
import ShipmentSampleDetail from './shipment-sample-detail';
import ShipmentSampleUpdate from './shipment-sample-update';

const ShipmentSampleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ShipmentSample />} />
    <Route path="new" element={<ShipmentSampleUpdate />} />
    <Route path=":id">
      <Route index element={<ShipmentSampleDetail />} />
      <Route path="edit" element={<ShipmentSampleUpdate />} />
      <Route path="delete" element={<ShipmentSampleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ShipmentSampleRoutes;
