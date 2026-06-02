import dayjs from 'dayjs';

export interface IContactMessage {
  id?: number;
  name?: string;
  email?: string;
  subject?: string | null;
  message?: string;
  ipAddress?: string | null;
  submittedAt?: dayjs.Dayjs | null;
  isHandled?: boolean | null;
}

export const defaultValue: Readonly<IContactMessage> = {
  isHandled: false,
};
