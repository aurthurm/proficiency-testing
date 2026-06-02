import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Enrollment from './enrollment';
import EnrollmentDeleteDialog from './enrollment-delete-dialog';
import EnrollmentDetail from './enrollment-detail';
import EnrollmentUpdate from './enrollment-update';

const EnrollmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Enrollment />} />
    <Route path="new" element={<EnrollmentUpdate />} />
    <Route path=":id">
      <Route index element={<EnrollmentDetail />} />
      <Route path="edit" element={<EnrollmentUpdate />} />
      <Route path="delete" element={<EnrollmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EnrollmentRoutes;
