import { IScheme } from 'app/shared/model/scheme.model';

export interface ICorrectiveAction {
  id?: number;
  title?: string;
  description?: string | null;
  scheme?: IScheme | null;
}

export const defaultValue: Readonly<ICorrectiveAction> = {};
