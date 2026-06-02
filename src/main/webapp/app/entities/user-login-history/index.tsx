import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserLoginHistory from './user-login-history';
import UserLoginHistoryDeleteDialog from './user-login-history-delete-dialog';
import UserLoginHistoryDetail from './user-login-history-detail';
import UserLoginHistoryUpdate from './user-login-history-update';

const UserLoginHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserLoginHistory />} />
    <Route path="new" element={<UserLoginHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<UserLoginHistoryDetail />} />
      <Route path="edit" element={<UserLoginHistoryUpdate />} />
      <Route path="delete" element={<UserLoginHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserLoginHistoryRoutes;
