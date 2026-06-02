import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Announcement from './announcement';
import AnnouncementDeleteDialog from './announcement-delete-dialog';
import AnnouncementDetail from './announcement-detail';
import AnnouncementUpdate from './announcement-update';

const AnnouncementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Announcement />} />
    <Route path="new" element={<AnnouncementUpdate />} />
    <Route path=":id">
      <Route index element={<AnnouncementDetail />} />
      <Route path="edit" element={<AnnouncementUpdate />} />
      <Route path="delete" element={<AnnouncementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AnnouncementRoutes;
