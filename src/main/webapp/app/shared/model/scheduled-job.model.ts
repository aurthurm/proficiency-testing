import dayjs from 'dayjs';

import { JobStatus } from 'app/shared/model/enumerations/job-status.model';
import { JobType } from 'app/shared/model/enumerations/job-type.model';

export interface IScheduledJob {
  id?: number;
  jobType?: keyof typeof JobType;
  status?: keyof typeof JobStatus;
  requestedBy?: string | null;
  requestedOn?: dayjs.Dayjs | null;
  startedAt?: dayjs.Dayjs | null;
  lastHeartbeat?: dayjs.Dayjs | null;
  completedAt?: dayjs.Dayjs | null;
  progressCompleted?: number | null;
  progressTotal?: number | null;
  summary?: string | null;
}

export const defaultValue: Readonly<IScheduledJob> = {};
