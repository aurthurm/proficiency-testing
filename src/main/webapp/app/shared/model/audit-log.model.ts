import dayjs from 'dayjs';

import { AuditAction } from 'app/shared/model/enumerations/audit-action.model';

export interface IAuditLog {
  id?: number;
  action?: keyof typeof AuditAction;
  statement?: string;
  performedBy?: string | null;
  performedByRole?: string | null;
  performedOn?: dayjs.Dayjs;
  ipAddress?: string | null;
  userAgent?: string | null;
  sessionHash?: string | null;
}

export const defaultValue: Readonly<IAuditLog> = {};
