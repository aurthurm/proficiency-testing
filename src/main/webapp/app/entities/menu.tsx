import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/data-manager">
        <Translate contentKey="global.menu.entities.dataManager" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/participant">
        <Translate contentKey="global.menu.entities.participant" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/country">
        <Translate contentKey="global.menu.entities.country" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/enrollment">
        <Translate contentKey="global.menu.entities.enrollment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/scheme">
        <Translate contentKey="global.menu.entities.scheme" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/scheme-configuration">
        <Translate contentKey="global.menu.entities.schemeConfiguration" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/distribution">
        <Translate contentKey="global.menu.entities.distribution" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/shipment">
        <Translate contentKey="global.menu.entities.shipment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/shipment-sample">
        <Translate contentKey="global.menu.entities.shipmentSample" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/sample-reference-result">
        <Translate contentKey="global.menu.entities.sampleReferenceResult" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/participant-result">
        <Translate contentKey="global.menu.entities.participantResult" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/shipment-participant-map">
        <Translate contentKey="global.menu.entities.shipmentParticipantMap" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/assay">
        <Translate contentKey="global.menu.entities.assay" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/test-kit">
        <Translate contentKey="global.menu.entities.testKit" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/mode-of-receipt">
        <Translate contentKey="global.menu.entities.modeOfReceipt" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/not-tested-reason">
        <Translate contentKey="global.menu.entities.notTestedReason" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/corrective-action">
        <Translate contentKey="global.menu.entities.correctiveAction" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/capa-record">
        <Translate contentKey="global.menu.entities.capaRecord" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/certificate-template">
        <Translate contentKey="global.menu.entities.certificateTemplate" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/certificate-batch">
        <Translate contentKey="global.menu.entities.certificateBatch" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/report-configuration">
        <Translate contentKey="global.menu.entities.reportConfiguration" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/mail-template">
        <Translate contentKey="global.menu.entities.mailTemplate" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/email-message">
        <Translate contentKey="global.menu.entities.emailMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/announcement">
        <Translate contentKey="global.menu.entities.announcement" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/participant-message">
        <Translate contentKey="global.menu.entities.participantMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/contact-message">
        <Translate contentKey="global.menu.entities.contactMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/home-page-section">
        <Translate contentKey="global.menu.entities.homePageSection" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/partner">
        <Translate contentKey="global.menu.entities.partner" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/feedback-question">
        <Translate contentKey="global.menu.entities.feedbackQuestion" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/participant-feedback">
        <Translate contentKey="global.menu.entities.participantFeedback" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/custom-field-definition">
        <Translate contentKey="global.menu.entities.customFieldDefinition" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/participant-custom-value">
        <Translate contentKey="global.menu.entities.participantCustomValue" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/global-configuration">
        <Translate contentKey="global.menu.entities.globalConfiguration" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/scheduled-job">
        <Translate contentKey="global.menu.entities.scheduledJob" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/audit-log">
        <Translate contentKey="global.menu.entities.auditLog" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/user-login-history">
        <Translate contentKey="global.menu.entities.userLoginHistory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/api-request-log">
        <Translate contentKey="global.menu.entities.apiRequestLog" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
