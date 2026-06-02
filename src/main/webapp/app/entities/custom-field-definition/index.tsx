import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CustomFieldDefinition from './custom-field-definition';
import CustomFieldDefinitionDeleteDialog from './custom-field-definition-delete-dialog';
import CustomFieldDefinitionDetail from './custom-field-definition-detail';
import CustomFieldDefinitionUpdate from './custom-field-definition-update';

const CustomFieldDefinitionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CustomFieldDefinition />} />
    <Route path="new" element={<CustomFieldDefinitionUpdate />} />
    <Route path=":id">
      <Route index element={<CustomFieldDefinitionDetail />} />
      <Route path="edit" element={<CustomFieldDefinitionUpdate />} />
      <Route path="delete" element={<CustomFieldDefinitionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CustomFieldDefinitionRoutes;
