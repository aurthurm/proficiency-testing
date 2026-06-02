import dayjs from 'dayjs';

import { EmailStatus } from 'app/shared/model/enumerations/email-status.model';
import { IMailTemplate } from 'app/shared/model/mail-template.model';

export interface IEmailMessage {
  id?: number;
  fromEmail?: string | null;
  fromName?: string | null;
  replyTo?: string | null;
  toEmail?: string;
  cc?: string | null;
  bcc?: string | null;
  subject?: string | null;
  body?: string | null;
  attachmentRef?: string | null;
  status?: keyof typeof EmailStatus;
  failureType?: string | null;
  failureReason?: string | null;
  queuedOn?: dayjs.Dayjs | null;
  sentAt?: dayjs.Dayjs | null;
  retryCount?: number | null;
  template?: IMailTemplate | null;
}

export const defaultValue: Readonly<IEmailMessage> = {};
