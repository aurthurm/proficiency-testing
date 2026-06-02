import { Status } from 'app/shared/model/enumerations/status.model';

export interface INotTestedReason {
  id?: number;
  reason?: string;
  status?: keyof typeof Status;
}

export const defaultValue: Readonly<INotTestedReason> = {};
