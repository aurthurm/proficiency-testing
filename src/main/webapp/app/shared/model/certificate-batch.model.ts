import dayjs from 'dayjs';

import { CertificateBatchStatus } from 'app/shared/model/enumerations/certificate-batch-status.model';
import { IShipment } from 'app/shared/model/shipment.model';

export interface ICertificateBatch {
  id?: number;
  name?: string;
  status?: keyof typeof CertificateBatchStatus;
  excellenceCount?: number | null;
  participationCount?: number | null;
  skippedCount?: number | null;
  downloadUrl?: string | null;
  errorMessage?: string | null;
  approvedBy?: string | null;
  approvedOn?: dayjs.Dayjs | null;
  shipmentses?: IShipment[] | null;
}

export const defaultValue: Readonly<ICertificateBatch> = {};
