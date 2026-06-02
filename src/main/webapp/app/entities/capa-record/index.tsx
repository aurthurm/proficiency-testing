import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CapaRecord from './capa-record';
import CapaRecordDeleteDialog from './capa-record-delete-dialog';
import CapaRecordDetail from './capa-record-detail';
import CapaRecordUpdate from './capa-record-update';

const CapaRecordRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CapaRecord />} />
    <Route path="new" element={<CapaRecordUpdate />} />
    <Route path=":id">
      <Route index element={<CapaRecordDetail />} />
      <Route path="edit" element={<CapaRecordUpdate />} />
      <Route path="delete" element={<CapaRecordDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CapaRecordRoutes;
