package com.hahalolo.call.webrtc.callbacks;

import org.webrtc.VideoCapturer;

public interface QBRTCVideoCapturer extends VideoCapturer {

    void startCapture(final int width, final int height, final int fps);
    
    void stopCapture() throws InterruptedException;
    
    void changeCaptureFormat(final int width, final int height, final int frames);
    
    void dispose();
    
    VideoCapturer getNativeCapturer();
}
