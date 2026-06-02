import dayjs from 'dayjs';

import { DistributionStatus } from 'app/shared/model/enumerations/distribution-status.model';

export interface IDistribution {
  id?: number;
  code?: string;
  distributionDate?: dayjs.Dayjs;
  status?: keyof typeof DistributionStatus;
}

export const defaultValue: Readonly<IDistribution> = {};
