import announcement from 'app/entities/announcement/announcement.reducer';
import apiRequestLog from 'app/entities/api-request-log/api-request-log.reducer';
import assay from 'app/entities/assay/assay.reducer';
import auditLog from 'app/entities/audit-log/audit-log.reducer';
import capaRecord from 'app/entities/capa-record/capa-record.reducer';
import certificateBatch from 'app/entities/certificate-batch/certificate-batch.reducer';
import certificateTemplate from 'app/entities/certificate-template/certificate-template.reducer';
import contactMessage from 'app/entities/contact-message/contact-message.reducer';
import correctiveAction from 'app/entities/corrective-action/corrective-action.reducer';
import country from 'app/entities/country/country.reducer';
import dataManager from 'app/entities/data-manager/data-manager.reducer';
import participant from 'app/entities/participant/participant.reducer';
import enrollment from 'app/entities/enrollment/enrollment.reducer';
import sampleReferenceResult from 'app/entities/sample-reference-result/sample-reference-result.reducer';
import scheme from 'app/entities/scheme/scheme.reducer';
import schemeConfiguration from 'app/entities/scheme-configuration/scheme-configuration.reducer';
import distribution from 'app/entities/distribution/distribution.reducer';
import shipment from 'app/entities/shipment/shipment.reducer';
import shipmentSample from 'app/entities/shipment-sample/shipment-sample.reducer';
import participantResult from 'app/entities/participant-result/participant-result.reducer';
import shipmentParticipantMap from 'app/entities/shipment-participant-map/shipment-participant-map.reducer';
import testKit from 'app/entities/test-kit/test-kit.reducer';
import modeOfReceipt from 'app/entities/mode-of-receipt/mode-of-receipt.reducer';
import notTestedReason from 'app/entities/not-tested-reason/not-tested-reason.reducer';
import reportConfiguration from 'app/entities/report-configuration/report-configuration.reducer';
import mailTemplate from 'app/entities/mail-template/mail-template.reducer';
import emailMessage from 'app/entities/email-message/email-message.reducer';
import participantMessage from 'app/entities/participant-message/participant-message.reducer';
import homePageSection from 'app/entities/home-page-section/home-page-section.reducer';
import partner from 'app/entities/partner/partner.reducer';
import feedbackQuestion from 'app/entities/feedback-question/feedback-question.reducer';
import participantFeedback from 'app/entities/participant-feedback/participant-feedback.reducer';
import customFieldDefinition from 'app/entities/custom-field-definition/custom-field-definition.reducer';
import participantCustomValue from 'app/entities/participant-custom-value/participant-custom-value.reducer';
import globalConfiguration from 'app/entities/global-configuration/global-configuration.reducer';
import scheduledJob from 'app/entities/scheduled-job/scheduled-job.reducer';
import userLoginHistory from 'app/entities/user-login-history/user-login-history.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  dataManager,
  participant,
  country,
  enrollment,
  scheme,
  schemeConfiguration,
  distribution,
  shipment,
  shipmentSample,
  sampleReferenceResult,
  participantResult,
  shipmentParticipantMap,
  assay,
  testKit,
  modeOfReceipt,
  notTestedReason,
  correctiveAction,
  capaRecord,
  certificateTemplate,
  certificateBatch,
  reportConfiguration,
  mailTemplate,
  emailMessage,
  announcement,
  participantMessage,
  contactMessage,
  homePageSection,
  partner,
  feedbackQuestion,
  participantFeedback,
  customFieldDefinition,
  participantCustomValue,
  globalConfiguration,
  scheduledJob,
  auditLog,
  userLoginHistory,
  apiRequestLog,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
