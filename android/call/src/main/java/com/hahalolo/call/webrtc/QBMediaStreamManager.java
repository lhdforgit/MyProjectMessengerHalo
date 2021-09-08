package com.hahalolo.call.webrtc;

import android.content.Context;
import android.hardware.Camera;

import com.hahalolo.call.webrtc.callbacks.QBMediaStreamManagerCallback;
import com.hahalolo.call.webrtc.callbacks.QBRTCMediaCapturerCallback;
import com.hahalolo.call.webrtc.callbacks.QBRTCVideoCapturer;
import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.AudioTrack;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class QBMediaStreamManager {
    private static final String CLASS_TAG;
    private static final Logger LOGGER;
    private Context context;
    public QBMediaStreamManagerCallback callback;
    public PeerConnectionFactory peerConnectionFactory;
    public MediaStream localMediaStream;
    private QBRTCVideoCapturer videoCapturer;
    private VideoSource videoSource;
    private int videoWidth;
    private int videoHeight;
    private int videoFps;
    private boolean isVideoConference;
    private final Object lock;
    private PeerFactoryManager factoryManager;
    private int cameraId;
    private volatile boolean closed;
    private boolean videoSourceStopped;

    QBMediaStreamManager(final PeerFactoryManager factoryManager,
                         final Context context,
                         final QBMediaStreamManagerCallback callback) {
        this.lock = new Object();
        this.cameraId = -1;
        this.closed = false;
        this.factoryManager = factoryManager;
        this.context = context;
        this.callback = callback;
    }

    public MediaStream getLocalMediaStream() {
        return this.localMediaStream;
    }
    
    public MediaStream initLocalMediaStream(final QBRTCTypes.QBConferenceType conferenceType,
                                            final EglBase.Context eglContext,
                                            final QBRTCMediaCapturerCallback mediaCapturerCallback) {
        QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Init local media stream");
        synchronized (this.lock) {
            if (this.localMediaStream == null) {
                this.peerConnectionFactory = this.factoryManager.getPeerConnectionFactory();
                this.isVideoConference = (QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO == conferenceType);
                this.localMediaStream = this.peerConnectionFactory.createLocalMediaStream("ARDAMS");
                final MediaConstraints audioConstraints = RTCMediaUtils.createAudioConstraints();
                final AudioTrack audioTrack = this.peerConnectionFactory.createAudioTrack("ARDAMSa0", this.peerConnectionFactory.createAudioSource(audioConstraints));
                this.localMediaStream.addTrack(audioTrack);
                this.startAECDump();
                if (mediaCapturerCallback != null) {
                    final QBRTCMediaStream rtcMediaStream = new QBRTCMediaStream(this.localMediaStream, conferenceType);
                    mediaCapturerCallback.onInitLocalMediaStream(rtcMediaStream);
                }
            }
        }
        return this.localMediaStream;
    }
    
    private void startAECDump() {
        if (QBRTCMediaConfig.isAECDumpEnabled()) {
            this.factoryManager.startAECDump();
        }
    }
    
    public int getCurrentCameraId() {
        return this.cameraId;
    }
    
    public void setVideoCapturer(final QBRTCVideoCapturer videoCapturer) {
        QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "setVideoCapturer " + videoCapturer);
        if (videoCapturer == null) {
            return;
        }
        this.releaseCapturer();
        this.initCapture(videoCapturer);
    }
    
    private void initCapture(final QBRTCVideoCapturer videoCapturer) {
        this.videoCapturer = videoCapturer;
        this.tryInitVideo(this.localMediaStream);
    }
    
    private void releaseCapturer() {
        if (this.videoCapturer != null) {
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "stop Capturer ");
            try {
                this.videoCapturer.stopCapture();
            }
            catch (InterruptedException e) {
                QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "error stopping Capturer ");
            }
            this.videoCapturer.dispose();
            this.videoCapturer = null;
        }
        if (this.videoSource != null) {
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Video source state is " + this.videoSource.state());
            this.videoSource.dispose();
            this.videoSource = null;
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Video source disposed");
        }
    }
    
    private void tryInitVideo(final MediaStream mediaStream) {
        if (this.isVideoConference) {
            final VideoTrack videoTrack = this.initVideoTrack();
            if (videoTrack != null) {
                QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "mediaStream.addTrack( " + videoTrack);
                mediaStream.addTrack(videoTrack);
            }
        }
    }
    
    private VideoTrack initVideoTrack() {
        QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Add video stream");
        if (this.videoCapturer != null) {
            return this.createVideoTrack(this.videoCapturer);
        }
        return null;
    }
    
    private VideoTrack createVideoTrack(final QBRTCVideoCapturer capturer) {
        QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "createVideoTrack for " + capturer.toString());
        this.videoSource = this.peerConnectionFactory.createVideoSource(capturer.isScreencast());
        final EglBase eglBase = com.hahalolo.call.webrtc.QBRTCClient.getInstance(this.context).getEglContext();
        final SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        this.videoCapturer.initialize(surfaceTextureHelper, this.context.getApplicationContext(), this.videoSource.getCapturerObserver());
        this.videoWidth = Math.min(QBRTCMediaConfig.getVideoWidth(), 1280);
        this.videoHeight = Math.min(QBRTCMediaConfig.getVideoHeight(), 1280);
        if (QBRTCMediaConfig.getVideoFps() > 0) {
            this.videoFps = QBRTCMediaConfig.getVideoFps();
        }
        else {
            this.videoFps = 30;
        }
        QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "video resolution: " + this.videoWidth + ":" + this.videoHeight + ":" + this.videoFps);
        capturer.startCapture(this.videoWidth, this.videoHeight, this.videoFps);
        final VideoTrack videoTrack = this.peerConnectionFactory.createVideoTrack("ARDAMSv0", this.videoSource);
        videoTrack.setEnabled(true);
        QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "created video track: " + videoTrack);
        return videoTrack;
    }
    
    public void startVideoSource() {
        if (this.isClosed()) {
            return;
        }
        if (this.videoCapturer != null && this.videoSourceStopped) {
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Restart video source.");
            this.videoCapturer.startCapture(this.videoWidth, this.videoHeight, this.videoFps);
            this.videoSourceStopped = false;
        }
    }
    
    public void stopVideoSource() {
        if (this.isClosed()) {
            return;
        }
        if (this.videoCapturer != null && !this.videoSourceStopped) {
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Stop video source.");
            try {
                this.videoCapturer.stopCapture();
            }
            catch (InterruptedException ex) {}
            this.videoSourceStopped = true;
        }
    }

    public boolean isClosed() {
        return this.closed;
    }
    
    private void switchCameraInternal(final CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
        if (this.videoCapturer instanceof QBRTCCameraVideoCapturer) {
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "Switch camera");
            ((QBRTCCameraVideoCapturer)this.videoCapturer).switchCamera((CameraVideoCapturer.CameraSwitchHandler)new CameraSwitchHandler(cameraSwitchHandler));
        }
    }
    
    public void setClosed() {
        this.closed = true;
    }
    
    public void close() {
        this.closeInternal();
    }

    private void closeInternal() {
        this.callback = null;
        this.setClosed();
        this.releaseCapturer();
    }
    
    public QBRTCVideoCapturer getVideoCapturer() {
        return this.videoCapturer;
    }
    
    static {
        CLASS_TAG = QBMediaStreamManager.class.getSimpleName();
        LOGGER = Logger.getInstance(QBMediaStreamManager.CLASS_TAG);
    }
    
    class CameraSwitchHandler implements CameraVideoCapturer.CameraSwitchHandler
    {
        private CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler;
        
        CameraSwitchHandler(final CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
            this.cameraSwitchHandler = cameraSwitchHandler;
        }
        
        public void onCameraSwitchDone(final boolean b) {
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "onCameraSwitchDone: " + b);
            QBMediaStreamManager.this.cameraId = (QBMediaStreamManager.this.cameraId + 1) % Camera.getNumberOfCameras();
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
            QBMediaStreamManager.LOGGER.d(QBMediaStreamManager.CLASS_TAG, "onCameraSwitchError: " + s);
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
