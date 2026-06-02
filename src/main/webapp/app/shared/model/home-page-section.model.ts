import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

export interface IHomePageSection {
  id?: number;
  section?: string;
  type?: string | null;
  title?: string | null;
  text?: string | null;
  link?: string | null;
  fileRef?: string | null;
  icon?: string | null;
  displayOrder?: number | null;
  status?: keyof typeof ContentStatus;
}

export const defaultValue: Readonly<IHomePageSection> = {};
