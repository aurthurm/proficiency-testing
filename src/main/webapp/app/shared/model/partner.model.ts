import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

export interface IPartner {
  id?: number;
  name?: string;
  link?: string | null;
  logoRef?: string | null;
  sortOrder?: number | null;
  status?: keyof typeof ContentStatus;
}

export const defaultValue: Readonly<IPartner> = {};
