import dayjs from 'dayjs';

import { IScheme } from 'app/shared/model/scheme.model';

export interface ISchemeConfiguration {
  id?: number;
  version?: number;
  effectiveDate?: dayjs.Dayjs;
  passingScore?: number;
  documentationWeight?: number | null;
  allowLateResponse?: boolean | null;
  optionalFields?: string | null;
  scoringRules?: string | null;
  isActive?: boolean | null;
  scheme?: IScheme;
}

export const defaultValue: Readonly<ISchemeConfiguration> = {
  allowLateResponse: false,
  isActive: false,
};
