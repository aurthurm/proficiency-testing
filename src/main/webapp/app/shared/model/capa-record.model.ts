import dayjs from 'dayjs';

import { ICorrectiveAction } from 'app/shared/model/corrective-action.model';
import { Status } from 'app/shared/model/enumerations/status.model';
import { IShipmentParticipantMap } from 'app/shared/model/shipment-participant-map.model';

export interface ICapaRecord {
  id?: number;
  rootCause?: string | null;
  actionTaken?: string | null;
  actionDate?: dayjs.Dayjs | null;
  status?: keyof typeof Status | null;
  followUpDate?: dayjs.Dayjs | null;
  correctiveAction?: ICorrectiveAction | null;
  shipmentParticipantMap?: IShipmentParticipantMap;
}

export const defaultValue: Readonly<ICapaRecord> = {};
