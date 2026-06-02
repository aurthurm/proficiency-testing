import { Status } from 'app/shared/model/enumerations/status.model';

export interface IModeOfReceipt {
  id?: number;
  name?: string;
  status?: keyof typeof Status;
}

export const defaultValue: Readonly<IModeOfReceipt> = {};
