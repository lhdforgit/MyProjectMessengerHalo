package com.hahalolo.call.conference;

import com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType;

import java.util.UUID;

public class ConferenceSessionDescription {
   private String sessionID;
   private String dialogID;
   private QBConferenceRole conferenceRole;
   private QBConferenceType conferenceType;

   public ConferenceSessionDescription(QBConferenceType conferenceType) {
      this(UUID.randomUUID().toString());
      this.conferenceType = conferenceType;
   }

   public ConferenceSessionDescription(String sessionID) {
      this.sessionID = sessionID;
   }

   public ConferenceSessionDescription() {
      this(UUID.randomUUID().toString());
   }

   public String getSessionID() {
      return this.sessionID;
   }

   public void setSessionID(String sessionID) {
      this.sessionID = sessionID;
   }

   public String getDialogID() {
      return this.dialogID;
   }

   public void setDialogID(String dialogID) {
      this.dialogID = dialogID;
   }

   public QBConferenceType getConferenceType() {
      return this.conferenceType;
   }

   public QBConferenceRole getConferenceRole() {
      return this.conferenceRole;
   }

   public void setConferenceRole(QBConferenceRole conferenceRole) {
      this.conferenceRole = conferenceRole;
   }
}
