import { ICountry } from 'app/shared/model/country.model';
import { IDataManager } from 'app/shared/model/data-manager.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IParticipant {
  id?: number;
  uniqueIdentifier?: string;
  instituteName?: string;
  departmentName?: string | null;
  email?: string;
  additionalEmail?: string | null;
  address?: string | null;
  shippingAddress?: string | null;
  city?: string | null;
  state?: string | null;
  district?: string | null;
  zip?: string | null;
  region?: string | null;
  phone?: string | null;
  mobile?: string | null;
  affiliation?: string;
  networkTier?: string | null;
  siteType?: string | null;
  fundingSource?: string | null;
  testingVolume?: number | null;
  pepfarId?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  labDirectorName?: string | null;
  labDirectorEmail?: string | null;
  contactPersonName?: string | null;
  contactPersonEmail?: string | null;
  contactPersonPhone?: string | null;
  status?: keyof typeof Status;
  country?: ICountry | null;
  dataManagerses?: IDataManager[] | null;
}

export const defaultValue: Readonly<IParticipant> = {};
