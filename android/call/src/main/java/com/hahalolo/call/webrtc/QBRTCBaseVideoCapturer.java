package com.hahalolo.call.webrtc;

import android.content.Context;

import com.hahalolo.call.webrtc.callbacks.QBRTCVideoCapturer;
import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.CapturerObserver;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;

abstract class QBRTCBaseVideoCapturer implements QBRTCVideoCapturer
{
    private static final String CLASS_TAG;
    private static final Logger LOGGER;
    protected VideoCapturer videoCapturer;
    protected LooperExecutor executor;
    
    public QBRTCBaseVideoCapturer() {
        (this.executor = new LooperExecutor(this.getClass())).requestStart();
    }
    
    @Override
    public void startCapture(final int width, final int height, final int framerate) {
        QBRTCBaseVideoCapturer.LOGGER.d(QBRTCBaseVideoCapturer.CLASS_TAG, "startCapture: " + width + "x" + height + "@" + framerate);
        this.videoCapturer.startCapture(width, height, framerate);
    }
    
    @Override
    public void stopCapture() throws InterruptedException {
        QBRTCBaseVideoCapturer.LOGGER.d(QBRTCBaseVideoCapturer.CLASS_TAG, "stopCapture: ");
        this.videoCapturer.stopCapture();
    }
    
    @Override
    public void changeCaptureFormat(final int width, final int height, final int framerate) {
        this.executor.execute(() -> this.changeCaptureFormatInternal(width, height, framerate));
    }
    
    @Override
    public void dispose() {
        this.videoCapturer.dispose();
    }
    
    @Override
    public VideoCapturer getNativeCapturer() {
        return this.videoCapturer;
    }
    
    public void initialize(final SurfaceTextureHelper surfaceTextureHelper, final Context context, final CapturerObserver capturerObserver) {
        this.videoCapturer.initialize(surfaceTextureHelper, context, capturerObserver);
    }
    
    public boolean isScreencast() {
        return this.videoCapturer.isScreencast();
    }
    
    private void changeCaptureFormatInternal(final int width, final int height, final int framerate) {
        QBRTCBaseVideoCapturer.LOGGER.d(QBRTCBaseVideoCapturer.CLASS_TAG, "changeCaptureFormat: " + width + "x" + height + "@" + framerate);
        this.videoCapturer.changeCaptureFormat(width, height, framerate);
    }
    
    static {
        CLASS_TAG = QBRTCBaseVideoCapturer.class.getSimpleName();
        LOGGER = Logger.getInstance(QBRTCBaseVideoCapturer.CLASS_TAG);
    }
}
