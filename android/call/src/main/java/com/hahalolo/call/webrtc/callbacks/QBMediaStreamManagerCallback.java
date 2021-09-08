package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBMediaStreamManager;

public interface QBMediaStreamManagerCallback {

    void setAudioCategoryError(final Exception e);

    void onMediaStreamChange(final QBMediaStreamManager mediaStreamManager);
}
