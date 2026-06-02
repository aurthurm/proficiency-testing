import dayjs from 'dayjs';

export interface IApiRequestLog {
  id?: number;
  transactionId?: string;
  requestedBy?: string | null;
  requestedOn?: dayjs.Dayjs | null;
  numberOfRecords?: number | null;
  requestType?: string | null;
  testType?: string | null;
  apiUrl?: string | null;
  dataFormat?: string | null;
}

export const defaultValue: Readonly<IApiRequestLog> = {};
