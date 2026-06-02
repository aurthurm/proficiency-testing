import dayjs from 'dayjs';

import { ICertificateBatch } from 'app/shared/model/certificate-batch.model';
import { IDistribution } from 'app/shared/model/distribution.model';
import { ShipmentStatus } from 'app/shared/model/enumerations/shipment-status.model';
import { IScheme } from 'app/shared/model/scheme.model';

export interface IShipment {
  id?: number;
  code?: string;
  shipmentDate?: dayjs.Dayjs;
  responseDeadline?: dayjs.Dayjs;
  responsesOpen?: boolean | null;
  autoCloseAtDeadline?: boolean | null;
  allowEditingResponse?: boolean | null;
  issuingAuthority?: string | null;
  coordinatorName?: string | null;
  coordinatorEmail?: string | null;
  coordinatorPhone?: string | null;
  numberOfSamples?: number | null;
  maxScore?: number | null;
  status?: keyof typeof ShipmentStatus;
  attributes?: string | null;
  reportsGeneratedAt?: dayjs.Dayjs | null;
  finalizedAt?: dayjs.Dayjs | null;
  distribution?: IDistribution | null;
  scheme?: IScheme;
  certificateBatcheses?: ICertificateBatch[] | null;
}

export const defaultValue: Readonly<IShipment> = {
  responsesOpen: false,
  autoCloseAtDeadline: false,
  allowEditingResponse: false,
};
