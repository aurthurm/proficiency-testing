import { ContentStatus } from 'app/shared/model/enumerations/content-status.model';

export interface IFeedbackQuestion {
  id?: number;
  questionText?: string;
  displayOrder?: number | null;
  status?: keyof typeof ContentStatus;
}

export const defaultValue: Readonly<IFeedbackQuestion> = {};
