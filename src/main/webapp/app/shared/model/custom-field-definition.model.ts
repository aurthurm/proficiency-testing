import { CustomFieldType } from 'app/shared/model/enumerations/custom-field-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface ICustomFieldDefinition {
  id?: number;
  fieldKey?: string;
  label?: string;
  fieldType?: keyof typeof CustomFieldType;
  options?: string | null;
  displayOrder?: number | null;
  status?: keyof typeof Status;
}

export const defaultValue: Readonly<ICustomFieldDefinition> = {};
