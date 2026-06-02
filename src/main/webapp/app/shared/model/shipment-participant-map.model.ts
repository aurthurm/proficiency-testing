import dayjs from 'dayjs';

import { FinalResult } from 'app/shared/model/enumerations/final-result.model';
import { QcStatus } from 'app/shared/model/enumerations/qc-status.model';
import { ResponseStatus } from 'app/shared/model/enumerations/response-status.model';
import { IModeOfReceipt } from 'app/shared/model/mode-of-receipt.model';
import { INotTestedReason } from 'app/shared/model/not-tested-reason.model';
import { IParticipant } from 'app/shared/model/participant.model';
import { IShipment } from 'app/shared/model/shipment.model';

export interface IShipmentParticipantMap {
  id?: number;
  responseStatus?: keyof typeof ResponseStatus | null;
  shipmentReceiptDate?: dayjs.Dayjs | null;
  shipmentTestDate?: dayjs.Dayjs | null;
  shipmentTestReportDate?: dayjs.Dayjs | null;
  submittedAt?: dayjs.Dayjs | null;
  evaluatedAt?: dayjs.Dayjs | null;
  isExcluded?: boolean | null;
  isResponseLate?: boolean | null;
  isPtTestNotPerformed?: boolean | null;
  ptTestNotPerformedComments?: string | null;
  supervisorApproved?: boolean | null;
  participantSupervisor?: string | null;
  userComment?: string | null;
  shipmentScore?: number | null;
  documentationScore?: number | null;
  finalResult?: keyof typeof FinalResult | null;
  failureReason?: string | null;
  evaluationComment?: string | null;
  isFollowup?: boolean | null;
  manualOverride?: boolean | null;
  qcStatus?: keyof typeof QcStatus | null;
  qcDate?: dayjs.Dayjs | null;
  qcDoneBy?: string | null;
  syncedToMobile?: boolean | null;
  syncedOn?: dayjs.Dayjs | null;
  modeOfReceipt?: IModeOfReceipt | null;
  notTestedReason?: INotTestedReason | null;
  shipment?: IShipment;
  participant?: IParticipant;
}

export const defaultValue: Readonly<IShipmentParticipantMap> = {
  isExcluded: false,
  isResponseLate: false,
  isPtTestNotPerformed: false,
  supervisorApproved: false,
  isFollowup: false,
  manualOverride: false,
  syncedToMobile: false,
};
