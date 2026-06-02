import dayjs from 'dayjs';

import { IParticipant } from 'app/shared/model/participant.model';

export interface IParticipantMessage {
  id?: number;
  subject?: string | null;
  body?: string | null;
  isRead?: boolean | null;
  sentAt?: dayjs.Dayjs | null;
  participant?: IParticipant;
}

export const defaultValue: Readonly<IParticipantMessage> = {
  isRead: false,
};
