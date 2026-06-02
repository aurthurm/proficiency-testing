export interface IGlobalConfiguration {
  id?: number;
  configKey?: string;
  configValue?: string | null;
  description?: string | null;
}

export const defaultValue: Readonly<IGlobalConfiguration> = {};
