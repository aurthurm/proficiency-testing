import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ShipmentParticipantMap from './shipment-participant-map';
import ShipmentParticipantMapDeleteDialog from './shipment-participant-map-delete-dialog';
import ShipmentParticipantMapDetail from './shipment-participant-map-detail';
import ShipmentParticipantMapUpdate from './shipment-participant-map-update';

const ShipmentParticipantMapRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ShipmentParticipantMap />} />
    <Route path="new" element={<ShipmentParticipantMapUpdate />} />
    <Route path=":id">
      <Route index element={<ShipmentParticipantMapDetail />} />
      <Route path="edit" element={<ShipmentParticipantMapUpdate />} />
      <Route path="delete" element={<ShipmentParticipantMapDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ShipmentParticipantMapRoutes;
