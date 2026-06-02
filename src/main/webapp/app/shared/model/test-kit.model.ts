import { Status } from 'app/shared/model/enumerations/status.model';
import { IScheme } from 'app/shared/model/scheme.model';

export interface ITestKit {
  id?: number;
  name?: string;
  manufacturer?: string | null;
  status?: keyof typeof Status;
  scheme?: IScheme | null;
}

export const defaultValue: Readonly<ITestKit> = {};
