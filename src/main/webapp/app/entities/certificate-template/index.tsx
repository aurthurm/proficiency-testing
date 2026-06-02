import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CertificateTemplate from './certificate-template';
import CertificateTemplateDeleteDialog from './certificate-template-delete-dialog';
import CertificateTemplateDetail from './certificate-template-detail';
import CertificateTemplateUpdate from './certificate-template-update';

const CertificateTemplateRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CertificateTemplate />} />
    <Route path="new" element={<CertificateTemplateUpdate />} />
    <Route path=":id">
      <Route index element={<CertificateTemplateDetail />} />
      <Route path="edit" element={<CertificateTemplateUpdate />} />
      <Route path="delete" element={<CertificateTemplateDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CertificateTemplateRoutes;
