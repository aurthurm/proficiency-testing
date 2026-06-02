import dayjs from 'dayjs';

import { IFeedbackQuestion } from 'app/shared/model/feedback-question.model';
import { IParticipant } from 'app/shared/model/participant.model';
import { IShipment } from 'app/shared/model/shipment.model';

export interface IParticipantFeedback {
  id?: number;
  answer?: string | null;
  submittedAt?: dayjs.Dayjs | null;
  question?: IFeedbackQuestion;
  participant?: IParticipant;
  shipment?: IShipment | null;
}

export const defaultValue: Readonly<IParticipantFeedback> = {};
