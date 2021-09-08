package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBRTCMediaStream;

public interface QBRTCMediaCapturerCallback {
    void onInitLocalMediaStream(final QBRTCMediaStream mediaStream);
}
