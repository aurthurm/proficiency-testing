import dayjs from 'dayjs';

import { IShipment } from 'app/shared/model/shipment.model';

export interface IShipmentSample {
  id?: number;
  label?: string;
  displayOrder?: number | null;
  isControl?: boolean | null;
  isMandatory?: boolean | null;
  sampleScore?: number | null;
  preparationDate?: dayjs.Dayjs | null;
  shipment?: IShipment;
}

export const defaultValue: Readonly<IShipmentSample> = {
  isControl: false,
  isMandatory: false,
};
