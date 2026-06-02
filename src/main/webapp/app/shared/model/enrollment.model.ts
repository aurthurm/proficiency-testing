import dayjs from 'dayjs';

import { EnrollmentStatus } from 'app/shared/model/enumerations/enrollment-status.model';
import { IParticipant } from 'app/shared/model/participant.model';
import { IScheme } from 'app/shared/model/scheme.model';

export interface IEnrollment {
  id?: number;
  status?: keyof typeof EnrollmentStatus;
  enrolledOn?: dayjs.Dayjs;
  withdrawnOn?: dayjs.Dayjs | null;
  participant?: IParticipant;
  scheme?: IScheme;
}

export const defaultValue: Readonly<IEnrollment> = {};
