import dayjs from 'dayjs';

import { IAssay } from 'app/shared/model/assay.model';
import { IShipmentParticipantMap } from 'app/shared/model/shipment-participant-map.model';
import { IShipmentSample } from 'app/shared/model/shipment-sample.model';
import { ITestKit } from 'app/shared/model/test-kit.model';

export interface IParticipantResult {
  id?: number;
  reportedQualitativeResult?: string | null;
  reportedQuantitativeValue?: number | null;
  unit?: string | null;
  lotNumber?: string | null;
  expiryDate?: dayjs.Dayjs | null;
  zScore?: number | null;
  calculatedScore?: number | null;
  comments?: string | null;
  assay?: IAssay | null;
  testKit?: ITestKit | null;
  sample?: IShipmentSample;
  shipmentParticipantMap?: IShipmentParticipantMap;
}

export const defaultValue: Readonly<IParticipantResult> = {};
