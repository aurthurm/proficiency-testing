import { ICertificateTemplate } from 'app/shared/model/certificate-template.model';
import { ResultModality } from 'app/shared/model/enumerations/result-modality.model';
import { SchemeType } from 'app/shared/model/enumerations/scheme-type.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IScheme {
  id?: number;
  code?: string;
  name?: string;
  schemeType?: keyof typeof SchemeType;
  modality?: keyof typeof ResultModality;
  status?: keyof typeof Status;
  certificateTemplate?: ICertificateTemplate | null;
}

export const defaultValue: Readonly<IScheme> = {};
