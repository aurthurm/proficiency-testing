import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EmailMessage from './email-message';
import EmailMessageDeleteDialog from './email-message-delete-dialog';
import EmailMessageDetail from './email-message-detail';
import EmailMessageUpdate from './email-message-update';

const EmailMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EmailMessage />} />
    <Route path="new" element={<EmailMessageUpdate />} />
    <Route path=":id">
      <Route index element={<EmailMessageDetail />} />
      <Route path="edit" element={<EmailMessageUpdate />} />
      <Route path="delete" element={<EmailMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EmailMessageRoutes;
