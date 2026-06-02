import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SampleReferenceResult from './sample-reference-result';
import SampleReferenceResultDeleteDialog from './sample-reference-result-delete-dialog';
import SampleReferenceResultDetail from './sample-reference-result-detail';
import SampleReferenceResultUpdate from './sample-reference-result-update';

const SampleReferenceResultRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SampleReferenceResult />} />
    <Route path="new" element={<SampleReferenceResultUpdate />} />
    <Route path=":id">
      <Route index element={<SampleReferenceResultDetail />} />
      <Route path="edit" element={<SampleReferenceResultUpdate />} />
      <Route path="delete" element={<SampleReferenceResultDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SampleReferenceResultRoutes;
