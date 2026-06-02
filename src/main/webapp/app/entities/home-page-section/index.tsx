import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import HomePageSection from './home-page-section';
import HomePageSectionDeleteDialog from './home-page-section-delete-dialog';
import HomePageSectionDetail from './home-page-section-detail';
import HomePageSectionUpdate from './home-page-section-update';

const HomePageSectionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<HomePageSection />} />
    <Route path="new" element={<HomePageSectionUpdate />} />
    <Route path=":id">
      <Route index element={<HomePageSectionDetail />} />
      <Route path="edit" element={<HomePageSectionUpdate />} />
      <Route path="delete" element={<HomePageSectionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default HomePageSectionRoutes;
