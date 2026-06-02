import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CertificateBatch from './certificate-batch';
import CertificateBatchDeleteDialog from './certificate-batch-delete-dialog';
import CertificateBatchDetail from './certificate-batch-detail';
import CertificateBatchUpdate from './certificate-batch-update';

const CertificateBatchRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CertificateBatch />} />
    <Route path="new" element={<CertificateBatchUpdate />} />
    <Route path=":id">
      <Route index element={<CertificateBatchDetail />} />
      <Route path="edit" element={<CertificateBatchUpdate />} />
      <Route path="delete" element={<CertificateBatchDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CertificateBatchRoutes;
