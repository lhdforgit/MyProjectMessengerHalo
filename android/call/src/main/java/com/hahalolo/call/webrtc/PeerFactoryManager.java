package com.hahalolo.call.webrtc;

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import timber.log.Timber;

public class PeerFactoryManager {

    private final Object lock;
    private Executor executor;
    private Context context;
    private EglBase eglContext;
    private PeerConnectionFactory peerConnectionFactory;
    private volatile boolean isStartedInitialization;
    private boolean aecDunpEnabled;

    public PeerFactoryManager(
            final Executor executor,
            final Context context,
            final EglBase eglContext) {
        this.lock = new Object();
        this.executor = executor;
        this.context = context;
        this.eglContext = eglContext;
    }

    Executor getExecutor() {
        return this.executor;
    }
    
    PeerConnectionFactory getPeerConnectionFactory() {
        synchronized (this.lock) {
            while (this.peerConnectionFactory == null) {
                try {
                    this.lock.wait();
                }
                catch (InterruptedException e) {
                    Timber.e("Waiting peerFactory failed");
                }
            }
        }
        return this.peerConnectionFactory;
    }
    
    void startAECDump() {
        Timber.e("startAECDump file");
        final String packageName = this.context.getPackageName();
        try {
            final File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + packageName);
            if (!file.mkdir()) {
                Timber.w( "creating  directory %s", file.getAbsolutePath());
            }
            final ParcelFileDescriptor aecDumpFileDescriptor = ParcelFileDescriptor.open(new File(file, "audio.aecdump"), 1006632960);
            this.aecDunpEnabled = this.peerConnectionFactory.startAecDump(aecDumpFileDescriptor.getFd(), -1);
            Timber.e( "aecDunpEnabled = %s", this.aecDunpEnabled);
        }
        catch (IOException e) {
            Timber.e( "Can not open aecdump file %s", e.getMessage());
        }
    }
    
    synchronized void createFactory() {
        if (this.isStartedInitialization) {
            Timber.d( "Creating Peer connection factory has already started");
            return;
        }
        this.isStartedInitialization = true;
        Timber.d( "Creating Peer connection factory ");
        this.executor.execute(() -> PeerFactoryManager.this.initPeerConnectionFactory(PeerFactoryManager.this.context));
    }
    
    public void dispose() {
        this.executor.execute(() -> {
            Timber.d("start dispose Peer factory");
            if (PeerFactoryManager.this.peerConnectionFactory != null) {
                if (PeerFactoryManager.this.aecDunpEnabled) {
                    PeerFactoryManager.this.peerConnectionFactory.stopAecDump();
                }
                PeerFactoryManager.this.peerConnectionFactory.dispose();
                PeerFactoryManager.this.peerConnectionFactory = null;
            }
            Timber.d( "dispose Peer factory done");
            PeerFactoryManager.this.executor = null;
        });
    }
    
    private void initPeerConnectionFactory(final Context context) {
        this.initHardwareOptions();
        final PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        if (QBRTCMediaConfig.isLoopBackEnabled()) {
            options.networkIgnoreMask = 0;
        }

        final PeerConnectionFactory.InitializationOptions.Builder optionsBuilder = PeerConnectionFactory.InitializationOptions.builder(context.getApplicationContext());
        optionsBuilder.setFieldTrials("");
        final boolean enableH264HighProfile = QBRTCMediaConfig.VideoCodec.H264.equals(QBRTCMediaConfig.getVideoCodec());
        VideoEncoderFactory encoderFactory;
        VideoDecoderFactory decoderFactory;
        if (QBRTCMediaConfig.isVideoHWAcceleration()) {
            encoderFactory = new DefaultVideoEncoderFactory(this.eglContext.getEglBaseContext(), true, enableH264HighProfile);
            decoderFactory = new DefaultVideoDecoderFactory(this.eglContext.getEglBaseContext());
        } else {
            encoderFactory = new SoftwareVideoEncoderFactory();
            decoderFactory = new SoftwareVideoDecoderFactory();
        }
        PeerConnectionFactory.initialize(optionsBuilder.createInitializationOptions());
        this.peerConnectionFactory = PeerConnectionFactory.builder().setOptions(options).setVideoEncoderFactory(encoderFactory).setVideoDecoderFactory(decoderFactory).createPeerConnectionFactory();
        Timber.d("Peer connection factory initiated from thread %s", Thread.currentThread().getId());
    }
    
    private void initHardwareOptions() {
        final boolean useOpenSLES = QBRTCMediaConfig.isUseOpenSLES();
        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(!useOpenSLES);
        Timber.d("%s OpenSL ES audio if device supports it", (useOpenSLES ? "Enable" : "Disable"));
        final boolean useBuildInAEC = QBRTCMediaConfig.isUseBuildInAEC();
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(!useBuildInAEC);
        Timber.d("%s built-in AEC even if device supports it", (useBuildInAEC ? "Enable" : "Disable"));
    }
}
