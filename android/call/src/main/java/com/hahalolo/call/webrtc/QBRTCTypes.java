package com.hahalolo.call.webrtc;

public final class QBRTCTypes
{
    /**
     * peer connection states.
     */
    public enum QBRTCConnectionState
    {
        QB_RTC_CONNECTION_UNKNOWN, // A peer connection state is unknown. This can occur when none of the other states are fit for the current situation.
        QB_RTC_CONNECTION_NEW, // A peer connection has been created and has not done any networking yet.
        QB_RTC_CONNECTION_WAIT, // A peer connection is in a waiting state.
        QB_RTC_CONNECTION_PENDING, // A peer connection is in a pending state for other actions to occur.
        QB_RTC_CONNECTION_CONNECTING, // One or more of the ICE transports are currently in the process of establishing a connection.
        QB_RTC_CONNECTION_CHECKING, // The ICE agent has been given one or more remote candidates and is checking pairs of local and remote candidates against one another to try to find a compatible match, but has not yet found a pair which will allow the peer connection to be made. It is possible that the gathering of candidates is also still underway.
        QB_RTC_CONNECTION_CONNECTED, // A usable pairing of local and remote candidates has been found for all components of the connection, and the connection has been established.
        QB_RTC_CONNECTION_DISCONNECTED, // A peer has been disconnected from the call session. But the call session is still open and the peer can be reconnected to the call session.
        QB_RTC_CONNECTION_CLOSED, // A peer connection was closed. But the call session can still be open because there can several peer connections in a single call session. The ICE agent for this peer connection has shut down and is no longer handling requests.
        QB_RTC_CONNECTION_DISCONNECT_TIMEOUT, // The peer connection was disconnected by the timeout.
        QB_RTC_CONNECTION_NOT_ANSWER, // No answer received from the remote peer.
        QB_RTC_CONNECTION_NOT_OFFER, 
        QB_RTC_CONNECTION_REJECT, // An incoming call has been rejected by the remote peer without accepting the call.
        QB_RTC_CONNECTION_HANG_UP, // The connection was hung up by the remote peer.
        QB_RTC_CONNECTION_FAILED, // One or more of the ICE transports on the connection is in the failed state. This can occur in different circumstances, for example, bad network, etc.
        QB_RTC_CONNECTION_ERROR // A peer connection has an error.
    }
    
    public enum QBRTCCloseReason
    {
        QB_RTC_UNKNOWN, 
        QB_RTC_ANSWER_TIMEOUT, 
        QB_RTC_REJECTED, 
        QB_RTC_HANG_UP, 
        QB_RTC_RECEIVE_HANG_UP, 
        QB_RTC_RECEIVE_REJECT, 
        QB_RTC_FAILED, 
        QB_RTC_ERROR;
    }
    
    public enum QBConferenceType
    {
        QB_CONFERENCE_TYPE_VIDEO(1), 
        QB_CONFERENCE_TYPE_AUDIO(2);
        
        private final int value;
        
        private QBConferenceType(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return "QBConferenceType{value='" + this.value + '\'' + '}';
        }
    }
}
