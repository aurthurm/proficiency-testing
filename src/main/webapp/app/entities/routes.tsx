import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Announcement from './announcement';
import ApiRequestLog from './api-request-log';
import Assay from './assay';
import AuditLog from './audit-log';
import CapaRecord from './capa-record';
import CertificateBatch from './certificate-batch';
import CertificateTemplate from './certificate-template';
import ContactMessage from './contact-message';
import CorrectiveAction from './corrective-action';
import Country from './country';
import DataManager from './data-manager';
import Participant from './participant';
import Enrollment from './enrollment';
import SampleReferenceResult from './sample-reference-result';
import Scheme from './scheme';
import SchemeConfiguration from './scheme-configuration';
import Distribution from './distribution';
import Shipment from './shipment';
import ShipmentSample from './shipment-sample';
import ParticipantResult from './participant-result';
import ShipmentParticipantMap from './shipment-participant-map';
import TestKit from './test-kit';
import ModeOfReceipt from './mode-of-receipt';
import NotTestedReason from './not-tested-reason';
import ReportConfiguration from './report-configuration';
import MailTemplate from './mail-template';
import EmailMessage from './email-message';
import ParticipantMessage from './participant-message';
import HomePageSection from './home-page-section';
import Partner from './partner';
import FeedbackQuestion from './feedback-question';
import ParticipantFeedback from './participant-feedback';
import CustomFieldDefinition from './custom-field-definition';
import ParticipantCustomValue from './participant-custom-value';
import GlobalConfiguration from './global-configuration';
import ScheduledJob from './scheduled-job';
import UserLoginHistory from './user-login-history';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/data-manager/*" element={<DataManager />} />
        <Route path="/participant/*" element={<Participant />} />
        <Route path="/country/*" element={<Country />} />
        <Route path="/enrollment/*" element={<Enrollment />} />
        <Route path="/scheme/*" element={<Scheme />} />
        <Route path="/scheme-configuration/*" element={<SchemeConfiguration />} />
        <Route path="/distribution/*" element={<Distribution />} />
        <Route path="/shipment/*" element={<Shipment />} />
        <Route path="/shipment-sample/*" element={<ShipmentSample />} />
        <Route path="/sample-reference-result/*" element={<SampleReferenceResult />} />
        <Route path="/participant-result/*" element={<ParticipantResult />} />
        <Route path="/shipment-participant-map/*" element={<ShipmentParticipantMap />} />
        <Route path="/assay/*" element={<Assay />} />
        <Route path="/test-kit/*" element={<TestKit />} />
        <Route path="/mode-of-receipt/*" element={<ModeOfReceipt />} />
        <Route path="/not-tested-reason/*" element={<NotTestedReason />} />
        <Route path="/corrective-action/*" element={<CorrectiveAction />} />
        <Route path="/capa-record/*" element={<CapaRecord />} />
        <Route path="/certificate-template/*" element={<CertificateTemplate />} />
        <Route path="/certificate-batch/*" element={<CertificateBatch />} />
        <Route path="/report-configuration/*" element={<ReportConfiguration />} />
        <Route path="/mail-template/*" element={<MailTemplate />} />
        <Route path="/email-message/*" element={<EmailMessage />} />
        <Route path="/announcement/*" element={<Announcement />} />
        <Route path="/participant-message/*" element={<ParticipantMessage />} />
        <Route path="/contact-message/*" element={<ContactMessage />} />
        <Route path="/home-page-section/*" element={<HomePageSection />} />
        <Route path="/partner/*" element={<Partner />} />
        <Route path="/feedback-question/*" element={<FeedbackQuestion />} />
        <Route path="/participant-feedback/*" element={<ParticipantFeedback />} />
        <Route path="/custom-field-definition/*" element={<CustomFieldDefinition />} />
        <Route path="/participant-custom-value/*" element={<ParticipantCustomValue />} />
        <Route path="/global-configuration/*" element={<GlobalConfiguration />} />
        <Route path="/scheduled-job/*" element={<ScheduledJob />} />
        <Route path="/audit-log/*" element={<AuditLog />} />
        <Route path="/user-login-history/*" element={<UserLoginHistory />} />
        <Route path="/api-request-log/*" element={<ApiRequestLog />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
