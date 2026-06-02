import dayjs from 'dayjs';

import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

export interface IAnnouncement {
  id?: number;
  title?: string;
  body?: string | null;
  status?: keyof typeof ContentStatus;
  publishedFrom?: dayjs.Dayjs | null;
  publishedTo?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IAnnouncement> = {};
