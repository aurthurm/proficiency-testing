import { IScheme } from 'app/shared/model/scheme.model';

export interface IReportConfiguration {
  id?: number;
  reportHeader?: string | null;
  logo?: string | null;
  logoRight?: string | null;
  layout?: string | null;
  format?: string | null;
  topMargin?: number | null;
  instituteAddressPosition?: string | null;
  scheme?: IScheme | null;
}

export const defaultValue: Readonly<IReportConfiguration> = {};
