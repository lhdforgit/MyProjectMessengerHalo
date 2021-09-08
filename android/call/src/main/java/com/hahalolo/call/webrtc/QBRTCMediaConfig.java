package com.hahalolo.call.webrtc;

/**
 * Class for holding video & audio constrains for session ({@link QBRTCSession}).
 */
public final class QBRTCMediaConfig {
    private static int videoWidth;
    private static int videoHeight;
    private static int videoFps;
    private static int videoStartBitrate;
    private static boolean videoHWAcceleration;
    private static boolean useOpenSLES;
    private static boolean useBuildInAEC;
    private static boolean captureFramesToTexture;
    private static int audioStartBitrate;
    private static AudioCodec audioCodec;
    private static VideoCodec videoCodec;
    private static boolean useProximitySensor;
    private static boolean tracingEnabled;
    private static boolean loopBackEnabled;
    private static boolean audioProcessingEnabled;
    private static boolean aecDumpEnabled;
    
    public static void setAudioCodec(final AudioCodec audioCodec) {
        QBRTCMediaConfig.audioCodec = audioCodec;
    }
    
    public static void setVideoCodec(final VideoCodec videoCodec) {
        QBRTCMediaConfig.videoCodec = videoCodec;
    }
    
    public static AudioCodec getAudioCodec() {
        return QBRTCMediaConfig.audioCodec;
    }
    
    public static VideoCodec getVideoCodec() {
        return QBRTCMediaConfig.videoCodec;
    }
    
    public static int getVideoWidth() {
        return QBRTCMediaConfig.videoWidth;
    }
    
    public static void setVideoWidth(final int videoWidth) {
        QBRTCMediaConfig.videoWidth = videoWidth;
    }
    
    public static int getVideoHeight() {
        return QBRTCMediaConfig.videoHeight;
    }
    
    public static void setVideoHeight(final int videoHeight) {
        QBRTCMediaConfig.videoHeight = videoHeight;
    }
    
    public static int getVideoFps() {
        return QBRTCMediaConfig.videoFps;
    }
    
    public static void setVideoFps(final int videoFps) {
        QBRTCMediaConfig.videoFps = videoFps;
    }
    
    public static int getVideoStartBitrate() {
        return QBRTCMediaConfig.videoStartBitrate;
    }
    
    public static void setVideoStartBitrate(final int videoStartBitrate) {
        QBRTCMediaConfig.videoStartBitrate = videoStartBitrate;
    }
    
    public static int getAudioStartBitrate() {
        return QBRTCMediaConfig.audioStartBitrate;
    }
    
    public static void setAudioStartBitrate(final int audioStartBitrate) {
        QBRTCMediaConfig.audioStartBitrate = audioStartBitrate;
    }
    
    public static boolean isVideoHWAcceleration() {
        return QBRTCMediaConfig.videoHWAcceleration;
    }
    
    public static void setVideoHWAcceleration(final boolean videoHWAcceleration) {
        QBRTCMediaConfig.videoHWAcceleration = videoHWAcceleration;
    }
    
    public static boolean isCaptureFramesToTexture() {
        return QBRTCMediaConfig.captureFramesToTexture;
    }
    
    public static void setCaptureFramesToTexture(final boolean captureFramesToTexture) {
        QBRTCMediaConfig.captureFramesToTexture = captureFramesToTexture;
    }
    
    public static boolean isUseProximitySensor() {
        return QBRTCMediaConfig.useProximitySensor;
    }
    
    public static void setUseProximitySensor(final boolean useProximitySensor) {
        QBRTCMediaConfig.useProximitySensor = useProximitySensor;
    }
    
    public static boolean isUseOpenSLES() {
        return QBRTCMediaConfig.useOpenSLES;
    }
    
    public static void setUseOpenSLES(final boolean useOpenSLES) {
        QBRTCMediaConfig.useOpenSLES = useOpenSLES;
    }
    
    public static boolean isUseBuildInAEC() {
        return QBRTCMediaConfig.useBuildInAEC;
    }
    
    public static void setUseBuildInAEC(final boolean useBuildInAEC) {
        QBRTCMediaConfig.useBuildInAEC = useBuildInAEC;
    }
    
    public static boolean isTracingEnabled() {
        return QBRTCMediaConfig.tracingEnabled;
    }
    
    public static void setTracingEnabled(final boolean tracingEnabled) {
        QBRTCMediaConfig.tracingEnabled = tracingEnabled;
    }
    
    public static boolean isLoopBackEnabled() {
        return QBRTCMediaConfig.loopBackEnabled;
    }
    
    public static void setLoopBackEnabled(final boolean loopBackEnabled) {
        QBRTCMediaConfig.loopBackEnabled = loopBackEnabled;
    }
    
    public static boolean isAECDumpEnabled() {
        return QBRTCMediaConfig.aecDumpEnabled;
    }
    
    public static void setAecDumpEnabled(final boolean aecDumpEnabled) {
        QBRTCMediaConfig.aecDumpEnabled = aecDumpEnabled;
    }
    
    public static boolean isAudioProcessingEnabled() {
        return QBRTCMediaConfig.audioProcessingEnabled;
    }
    
    public static void setAudioProcessingEnabled(final boolean audioProcessingEnabled) {
        QBRTCMediaConfig.audioProcessingEnabled = audioProcessingEnabled;
    }
    
    static {
        QBRTCMediaConfig.videoWidth = 0;
        QBRTCMediaConfig.videoHeight = 0;
        QBRTCMediaConfig.videoFps = 0;
        QBRTCMediaConfig.videoStartBitrate = 0;
        QBRTCMediaConfig.videoHWAcceleration = false;
        QBRTCMediaConfig.useOpenSLES = false;
        QBRTCMediaConfig.useBuildInAEC = true;
        QBRTCMediaConfig.captureFramesToTexture = false;
        QBRTCMediaConfig.audioStartBitrate = 0;
        QBRTCMediaConfig.audioCodec = AudioCodec.ISAC;
        QBRTCMediaConfig.videoCodec = null;
        QBRTCMediaConfig.useProximitySensor = false;
        QBRTCMediaConfig.tracingEnabled = false;
        QBRTCMediaConfig.loopBackEnabled = false;
        QBRTCMediaConfig.audioProcessingEnabled = true;
        QBRTCMediaConfig.aecDumpEnabled = false;
    }
    
    public enum VideoQuality
    {
        HD_VIDEO(1280, 720), 
        VGA_VIDEO(640, 480), 
        QBGA_VIDEO(320, 240);
        
        public final int width;
        public final int height;
        
        private VideoQuality(final int width, final int height) {
            this.width = width;
            this.height = height;
        }
    }
    
    public enum AudioCodec
    {
        OPUS("opus"), 
        ISAC("ISAC");
        
        private String description;
        
        private AudioCodec(final String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return this.description;
        }
    }
    
    public enum VideoCodec
    {
        VP8("VP8"), 
        VP9("VP9"), 
        H264("H264");
        
        private final String description;
        
        private VideoCodec(final String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return this.description;
        }
    }
}
