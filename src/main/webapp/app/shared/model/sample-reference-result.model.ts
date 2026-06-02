import { IAssay } from 'app/shared/model/assay.model';
import { IShipmentSample } from 'app/shared/model/shipment-sample.model';

export interface ISampleReferenceResult {
  id?: number;
  qualitativeResult?: string | null;
  quantitativeValue?: number | null;
  unit?: string | null;
  lowerLimit?: number | null;
  upperLimit?: number | null;
  isControlExpected?: boolean | null;
  assay?: IAssay | null;
  sample?: IShipmentSample;
}

export const defaultValue: Readonly<ISampleReferenceResult> = {
  isControlExpected: false,
};
