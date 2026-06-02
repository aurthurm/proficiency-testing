import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ContactMessage from './contact-message';
import ContactMessageDeleteDialog from './contact-message-delete-dialog';
import ContactMessageDetail from './contact-message-detail';
import ContactMessageUpdate from './contact-message-update';

const ContactMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ContactMessage />} />
    <Route path="new" element={<ContactMessageUpdate />} />
    <Route path=":id">
      <Route index element={<ContactMessageDetail />} />
      <Route path="edit" element={<ContactMessageUpdate />} />
      <Route path="delete" element={<ContactMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ContactMessageRoutes;
