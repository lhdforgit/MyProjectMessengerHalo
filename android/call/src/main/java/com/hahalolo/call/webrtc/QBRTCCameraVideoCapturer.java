package com.hahalolo.call.webrtc;

import android.content.Context;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.VideoCapturer;

import java.util.Arrays;

public class QBRTCCameraVideoCapturer extends QBRTCBaseVideoCapturer
{
    private static final String CLASS_TAG;
    private static final Logger LOGGER;
    private CameraEnumerator cameraEnumerator;
    private String cameraName;
    
    private boolean availableCameras() {
        final int numberOfCameras = this.cameraEnumerator.getDeviceNames().length;
        if (numberOfCameras == 0) {
            QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "No camera on device. Switch to audio only call.");
        }
        return numberOfCameras > 0;
    }
    
    public QBRTCCameraVideoCapturer(final Context context, final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler) throws QBRTCCameraCapturerException {
        this.cameraEnumerator = (CameraEnumerator)new QBCameraEnumerator();
        final boolean canCreateCapturer = context.checkCallingOrSelfPermission("android.permission.CAMERA") == 0 && this.availableCameras();
        if (!canCreateCapturer) {
            throw new QBRTCCameraCapturerException();
        }
        this.videoCapturer = this.createCameraCapturer(this.cameraEnumerator, cameraErrorHandler);
    }
    
    private void switchCameraInternal(final CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
        if (this.videoCapturer != null) {
            QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "Switch camera");
            final CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer)this.videoCapturer;
            cameraVideoCapturer.switchCamera((CameraVideoCapturer.CameraSwitchHandler)new CameraSwitchHandler(cameraSwitchHandler));
        }
    }
    
    public void switchCamera(final CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                QBRTCCameraVideoCapturer.this.switchCameraInternal(cameraSwitchHandler);
            }
        });
    }
    
    private VideoCapturer createCameraCapturer(final CameraEnumerator enumerator, final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler) {
        VideoCapturer videoCapturer = this.createCameraCapturer(enumerator, cameraErrorHandler, true);
        if (videoCapturer == null) {
            videoCapturer = this.createCameraCapturer(enumerator, cameraErrorHandler, false);
        }
        return videoCapturer;
    }
    
    private VideoCapturer createCameraCapturer(final CameraEnumerator enumerator, final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler, final boolean frontFacing) {
        final String[] deviceNames = enumerator.getDeviceNames();
        QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "Looking for front facing cameras.");
        for (final String deviceName : deviceNames) {
            boolean isCameraFrontFacingType;
            if (frontFacing) {
                isCameraFrontFacingType = enumerator.isFrontFacing(deviceName);
            }
            else {
                isCameraFrontFacingType = !enumerator.isFrontFacing(deviceName);
            }
            if (isCameraFrontFacingType) {
                QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "Creating" + (frontFacing ? "front facing camera" : "other camera") + " capturer.");
                final VideoCapturer videoCapturer = (VideoCapturer)enumerator.createCapturer(deviceName, cameraErrorHandler);
                if (videoCapturer != null) {
                    QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "Created camera capturer for " + deviceName);
                    this.cameraName = deviceName;
                    return videoCapturer;
                }
            }
        }
        return null;
    }
    
    public String getCameraName() {
        return this.cameraName;
    }
    
    private void updateCameraName() {
        final String[] deviceNames = this.cameraEnumerator.getDeviceNames();
        final int cameraNameIndex = Arrays.asList(deviceNames).indexOf(this.cameraName);
        this.cameraName = deviceNames[(cameraNameIndex + 1) % deviceNames.length];
    }
    
    static {
        CLASS_TAG = QBRTCCameraVideoCapturer.class.getSimpleName();
        LOGGER = Logger.getInstance(QBRTCCameraVideoCapturer.CLASS_TAG);
    }
    
    public static class QBRTCCameraCapturerException extends Exception
    {
    }
    
    class CameraSwitchHandler implements CameraVideoCapturer.CameraSwitchHandler
    {
        private CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler;
        
        CameraSwitchHandler(final CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
            this.cameraSwitchHandler = cameraSwitchHandler;
        }
        
        public void onCameraSwitchDone(final boolean b) {
            QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "onCameraSwitchDone: " + b);
            QBRTCCameraVideoCapturer.this.updateCameraName();
            if (this.cameraSwitchHandler != null) {
                com.hahalolo.call.webrtc.QBRTCClient.HANDLER.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        CameraSwitchHandler.this.cameraSwitchHandler.onCameraSwitchDone(b);
                    }
                });
            }
        }
        
        public void onCameraSwitchError(final String s) {
            QBRTCCameraVideoCapturer.LOGGER.d(QBRTCCameraVideoCapturer.CLASS_TAG, "onCameraSwitchError: " + s);
            if (this.cameraSwitchHandler != null) {
                com.hahalolo.call.webrtc.QBRTCClient.HANDLER.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        CameraSwitchHandler.this.cameraSwitchHandler.onCameraSwitchError(s);
                    }
                });
            }
        }
    }
}
