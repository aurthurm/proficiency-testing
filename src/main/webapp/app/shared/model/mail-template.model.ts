import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

export interface IMailTemplate {
  id?: number;
  code?: string;
  subject?: string | null;
  htmlBody?: string | null;
  status?: keyof typeof ContentStatus | null;
}

export const defaultValue: Readonly<IMailTemplate> = {};
