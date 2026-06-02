import { CertificateType } from 'app/shared/model/enumerations/certificate-type.model';

export interface ICertificateTemplate {
  id?: number;
  certificateType?: keyof typeof CertificateType;
  fileRef?: string | null;
  detectedFields?: string | null;
}

export const defaultValue: Readonly<ICertificateTemplate> = {};
