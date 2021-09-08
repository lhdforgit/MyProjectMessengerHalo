package com.hahalolo.call.webrtc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class QBRTCSessionDescription implements Serializable {

    private static final long serialVersionUID = 0L;
    private String sessionID;
    private Integer callerID;
    private List<Integer> opponents;
    private QBRTCTypes.QBConferenceType conferenceType;
    private Map<String, String> userInfo;

    public QBRTCSessionDescription(
            final String sessionID,
            final Integer callerID,
            final List<Integer> opponents,
            final QBRTCTypes.QBConferenceType conferenceType) {
        this.sessionID = sessionID;
        this.callerID = callerID;
        this.opponents = opponents;
        this.conferenceType = conferenceType;
        this.userInfo = new HashMap<>();
    }
    
    public QBRTCSessionDescription(
            final Integer callerID,
            final List<Integer> opponents,
            final QBRTCTypes.QBConferenceType conferenceType) {
        this(UUID.randomUUID().toString(), callerID, opponents, conferenceType);
    }
    
    public String getSessionId() {
        return this.sessionID;
    }
    
    public void setSessionID(final String sessionID) {
        this.sessionID = sessionID;
    }
    
    public Integer getCallerID() {
        return this.callerID;
    }
    
    public void setCallerID(final Integer callerID) {
        this.callerID = callerID;
    }
    
    public List<Integer> getOpponents() {
        return this.opponents;
    }
    
    public void setOpponents(final List<Integer> opponents) {
        this.opponents = opponents;
    }

    public QBRTCTypes.QBConferenceType getConferenceType() {
        return this.conferenceType;
    }

    public void setConferenceType(final QBRTCTypes.QBConferenceType conferenceType) {
        this.conferenceType = conferenceType;
    }
    
    public Map<String, String> getUserInfo() {
        return this.userInfo;
    }
    
    public void setUserInfo(final Map<String, String> userInfo) {
        this.userInfo = userInfo;
    }
    
    @Override
    public int hashCode() {
        int result = (this.sessionID != null) ? this.sessionID.hashCode() : 0;
        result = 31 * result + ((this.callerID != null) ? this.callerID.hashCode() : 0);
        result = 31 * result + ((this.opponents != null) ? this.opponents.hashCode() : 0);
        result = 31 * result + ((this.conferenceType != null) ? this.conferenceType.hashCode() : 0);
        result = 31 * result + ((this.userInfo != null) ? this.userInfo.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "QBRTCSessionDescription{sessionID='" + this.sessionID
                + ", callerID='" + this.callerID
                + ", opponents=" + this.opponents
                + ", conferenceType=" + this.conferenceType
                + ", userInfo=" + this.userInfo + '}';
    }
}
