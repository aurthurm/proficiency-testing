import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ModeOfReceipt from './mode-of-receipt';
import ModeOfReceiptDeleteDialog from './mode-of-receipt-delete-dialog';
import ModeOfReceiptDetail from './mode-of-receipt-detail';
import ModeOfReceiptUpdate from './mode-of-receipt-update';

const ModeOfReceiptRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ModeOfReceipt />} />
    <Route path="new" element={<ModeOfReceiptUpdate />} />
    <Route path=":id">
      <Route index element={<ModeOfReceiptDetail />} />
      <Route path="edit" element={<ModeOfReceiptUpdate />} />
      <Route path="delete" element={<ModeOfReceiptDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ModeOfReceiptRoutes;
