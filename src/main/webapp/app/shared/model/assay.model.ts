import { AssayType } from 'app/shared/model/enumerations/assay-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';
import { IScheme } from 'app/shared/model/scheme.model';

export interface IAssay {
  id?: number;
  name?: string;
  assayType?: keyof typeof AssayType;
  manufacturer?: string | null;
  status?: keyof typeof Status;
  scheme?: IScheme | null;
}

export const defaultValue: Readonly<IAssay> = {};
