export interface ICountry {
  id?: number;
  isoCode?: string;
  name?: string;
  region?: string | null;
}

export const defaultValue: Readonly<ICountry> = {};
