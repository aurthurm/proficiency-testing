import dayjs from 'dayjs';

import { LoginStatus } from 'app/shared/model/enumerations/login-status.model';

export interface IUserLoginHistory {
  id?: number;
  loginId?: string | null;
  loginContext?: string | null;
  loginStatus?: keyof typeof LoginStatus;
  attemptedAt?: dayjs.Dayjs;
  ipAddress?: string | null;
  browser?: string | null;
  operatingSystem?: string | null;
  sessionHash?: string | null;
}

export const defaultValue: Readonly<IUserLoginHistory> = {};
