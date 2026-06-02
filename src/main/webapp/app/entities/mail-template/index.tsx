import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MailTemplate from './mail-template';
import MailTemplateDeleteDialog from './mail-template-delete-dialog';
import MailTemplateDetail from './mail-template-detail';
import MailTemplateUpdate from './mail-template-update';

const MailTemplateRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MailTemplate />} />
    <Route path="new" element={<MailTemplateUpdate />} />
    <Route path=":id">
      <Route index element={<MailTemplateDetail />} />
      <Route path="edit" element={<MailTemplateUpdate />} />
      <Route path="delete" element={<MailTemplateDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MailTemplateRoutes;
