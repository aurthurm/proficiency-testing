import { ICustomFieldDefinition } from 'app/shared/model/custom-field-definition.model';
import { IParticipant } from 'app/shared/model/participant.model';

export interface IParticipantCustomValue {
  id?: number;
  value?: string | null;
  participant?: IParticipant;
  definition?: ICustomFieldDefinition;
}

export const defaultValue: Readonly<IParticipantCustomValue> = {};
