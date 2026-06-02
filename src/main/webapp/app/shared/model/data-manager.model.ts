import { ICountry } from 'app/shared/model/country.model';
import { DataManagerRole } from 'app/shared/model/enumerations/data-manager-role.model';
import { Status } from 'app/shared/model/enumerations/status.model';
import { IParticipant } from 'app/shared/model/participant.model';
import { IUser } from 'app/shared/model/user.model';

export interface IDataManager {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  institute?: string | null;
  primaryEmail?: string;
  secondaryEmail?: string | null;
  phone?: string | null;
  mobile?: string | null;
  language?: string | null;
  role?: keyof typeof DataManagerRole;
  status?: keyof typeof Status;
  qcAccess?: boolean | null;
  viewOnlyAccess?: boolean | null;
  enableTestResponseDate?: boolean | null;
  enableModeOfReceipt?: boolean | null;
  forceProfileCheck?: boolean | null;
  user?: IUser | null;
  country?: ICountry | null;
  participantses?: IParticipant[] | null;
}

export const defaultValue: Readonly<IDataManager> = {
  qcAccess: false,
  viewOnlyAccess: false,
  enableTestResponseDate: false,
  enableModeOfReceipt: false,
  forceProfileCheck: false,
};
