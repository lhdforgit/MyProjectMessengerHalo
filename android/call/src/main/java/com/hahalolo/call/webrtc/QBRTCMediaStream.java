package com.hahalolo.call.webrtc;


import org.webrtc.MediaStream;

public class QBRTCMediaStream {
    private QBRTCTypes.QBConferenceType conferenceType;
    private MediaStream mediaStream;

    QBRTCMediaStream(final MediaStream mediaStream,
                     final QBRTCTypes.QBConferenceType conferenceType) {
        this.mediaStream = mediaStream;
        this.conferenceType = conferenceType;
    }

    public QBRTCTypes.QBConferenceType getConferenceType() {
        return this.conferenceType;
    }

    public MediaStream getMediaStream() {
        return this.mediaStream;
    }
}
