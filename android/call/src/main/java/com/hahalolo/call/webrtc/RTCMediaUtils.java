package com.hahalolo.call.webrtc;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.MediaConstraints;
import org.webrtc.SessionDescription;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RTCMediaUtils
{
    private static final String CLASS_TAG;
    private static final Logger LOGGER;
    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    protected static final int MAX_VIDEO_WIDTH = 1280;
    protected static final int MAX_VIDEO_HEIGHT = 1280;
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    protected static final int MAX_VIDEO_FPS = 30;
    
    public static MediaConstraints createVideoConstraints(final com.hahalolo.call.webrtc.QBRTCMediaConfig sessionParameters) {
        final MediaConstraints mediaConstraints = new MediaConstraints();
        int videoWidth = com.hahalolo.call.webrtc.QBRTCMediaConfig.getVideoWidth();
        int videoHeight = com.hahalolo.call.webrtc.QBRTCMediaConfig.getVideoHeight();
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", com.hahalolo.call.webrtc.QBRTCMediaConfig.isLoopBackEnabled() ? "false" : "true"));
        if (videoWidth > 0 && videoHeight > 0) {
            videoWidth = Math.min(videoWidth, 1280);
            videoHeight = Math.min(videoHeight, 1280);
            RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "Set constraints for video:" + videoWidth + ":" + videoHeight);
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minWidth", Integer.toString(videoWidth)));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(videoWidth)));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minHeight", Integer.toString(videoHeight)));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(videoHeight)));
        }
        int videoFps = com.hahalolo.call.webrtc.QBRTCMediaConfig.getVideoFps();
        if (videoFps > 0) {
            videoFps = Math.min(videoFps, 30);
            RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "Set constraints for fps:" + videoFps);
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(videoFps)));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(videoFps)));
        }
        RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "mediaConstraints = " + mediaConstraints.toString());
        return mediaConstraints;
    }
    
    public static MediaConstraints createAudioConstraints() {
        final MediaConstraints mediaConstraints = new MediaConstraints();
        if (!com.hahalolo.call.webrtc.QBRTCMediaConfig.isAudioProcessingEnabled()) {
            RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "Disabling audio processing");
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "false"));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "false"));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "false"));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "false"));
        }
        RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "mediaConstraints = " + mediaConstraints.toString());
        return mediaConstraints;
    }
    
    public static MediaConstraints createConferenceConstraints(final com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType conferenceType) {
        final MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        if (com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(conferenceType) || com.hahalolo.call.webrtc.QBRTCMediaConfig.isLoopBackEnabled()) {
            sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        }
        else {
            sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
        }
        return sdpMediaConstraints;
    }
    
    public static MediaConstraints createPeerConnectionConstraints() {
        final MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", com.hahalolo.call.webrtc.QBRTCMediaConfig.isLoopBackEnabled() ? "false" : "true"));
        return mediaConstraints;
    }
    
    private static String preferCodec(final String sdpDescription, final String codec, final boolean isAudio) {
        final String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        final String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        final Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; i < lines.length && (mLineIndex == -1 || codecRtpMap == null); ++i) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
            }
            else {
                final Matcher codecMatcher = codecPattern.matcher(lines[i]);
                if (codecMatcher.matches()) {
                    codecRtpMap = codecMatcher.group(1);
                }
            }
        }
        if (mLineIndex == -1) {
            RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at " + lines[mLineIndex]);
        final String[] origMLineParts = lines[mLineIndex].split(" ");
        if (origMLineParts.length > 3) {
            final StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            while (origPartIndex < origMLineParts.length) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
                ++origPartIndex;
            }
            lines[mLineIndex] = newMLine.toString();
            RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "Change media description: " + lines[mLineIndex]);
        }
        else {
            RTCMediaUtils.LOGGER.e(RTCMediaUtils.CLASS_TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
        }
        final StringBuilder newSdpDescription = new StringBuilder();
        for (final String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }
    
    private static String setStartBitrate(final String codec, final boolean isVideoCodec, final String sdpDescription, final int bitrateKbps) {
        final String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; ++i) {
            final Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            RTCMediaUtils.LOGGER.w(RTCMediaUtils.CLASS_TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; ++i) {
            final Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "Found " + codec + " " + lines[i]);
                if (isVideoCodec) {
                    final StringBuilder sb = new StringBuilder();
                    final String[] array = lines;
                    final int n = i;
                    array[n] = sb.append(array[n]).append("; x-google-start-bitrate=").append(bitrateKbps).toString();
                }
                else {
                    final StringBuilder sb2 = new StringBuilder();
                    final String[] array2 = lines;
                    final int n2 = i;
                    array2[n2] = sb2.append(array2[n2]).append("; maxaveragebitrate=").append(bitrateKbps * 1000).toString();
                }
                RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }
        final StringBuilder newSdpDescription = new StringBuilder();
        for (int j = 0; j < lines.length; ++j) {
            newSdpDescription.append(lines[j]).append("\r\n");
            if (!sdpFormatUpdated && j == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " " + "x-google-start-bitrate" + "=" + bitrateKbps;
                }
                else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " " + "maxaveragebitrate" + "=" + bitrateKbps * 1000;
                }
                RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }
        }
        return newSdpDescription.toString();
    }
    
    public static String generateRemoteDescription(final SessionDescription sdp, final com.hahalolo.call.webrtc.QBRTCMediaConfig parameters, final com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType conferenceType) {
        String sdpDescription = sdp.description;
        final com.hahalolo.call.webrtc.QBRTCMediaConfig.AudioCodec audioCodec = com.hahalolo.call.webrtc.QBRTCMediaConfig.getAudioCodec();
        final com.hahalolo.call.webrtc.QBRTCMediaConfig.VideoCodec videoCodec = com.hahalolo.call.webrtc.QBRTCMediaConfig.getVideoCodec();
        if (audioCodec != null) {
            RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "generateRemoteDescription:  audioCodec=" + audioCodec.getDescription());
            sdpDescription = preferCodec(sdpDescription, audioCodec.getDescription(), true);
        }
        final boolean isVideoConference = com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(conferenceType);
        if (isVideoConference && videoCodec != null) {
            RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "generateRemoteDescription:  videoCodec=" + videoCodec.getDescription());
            sdpDescription = preferCodec(sdpDescription, videoCodec.getDescription(), false);
        }
        final int videoStartBitrate = com.hahalolo.call.webrtc.QBRTCMediaConfig.getVideoStartBitrate();
        if (isVideoConference && videoStartBitrate > 0) {
            sdpDescription = setStartBitrate(com.hahalolo.call.webrtc.QBRTCMediaConfig.VideoCodec.VP8.getDescription(), true, sdpDescription, videoStartBitrate);
            sdpDescription = setStartBitrate(com.hahalolo.call.webrtc.QBRTCMediaConfig.VideoCodec.VP9.getDescription(), true, sdpDescription, videoStartBitrate);
            sdpDescription = setStartBitrate(com.hahalolo.call.webrtc.QBRTCMediaConfig.VideoCodec.H264.getDescription(), true, sdpDescription, videoStartBitrate);
        }
        final int audioStartBitrate = com.hahalolo.call.webrtc.QBRTCMediaConfig.getAudioStartBitrate();
        if (audioStartBitrate > 0) {
            sdpDescription = setStartBitrate(com.hahalolo.call.webrtc.QBRTCMediaConfig.AudioCodec.OPUS.getDescription(), false, sdpDescription, audioStartBitrate);
        }
        return sdpDescription;
    }
    
    public static String generateLocalDescription(final SessionDescription sdp, final com.hahalolo.call.webrtc.QBRTCMediaConfig parameters, final com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType conferenceType) {
        String sdpDescription = sdp.description;
        final com.hahalolo.call.webrtc.QBRTCMediaConfig.AudioCodec audioCodec = com.hahalolo.call.webrtc.QBRTCMediaConfig.getAudioCodec();
        final com.hahalolo.call.webrtc.QBRTCMediaConfig.VideoCodec videoCodec = com.hahalolo.call.webrtc.QBRTCMediaConfig.getVideoCodec();
        if (audioCodec != null) {
            RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "generateLocalDescription:  audioCodec=" + audioCodec.getDescription());
            sdpDescription = preferCodec(sdpDescription, audioCodec.getDescription(), true);
        }
        if (com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(conferenceType) && videoCodec != null) {
            RTCMediaUtils.LOGGER.d(RTCMediaUtils.CLASS_TAG, "generateLocalDescription:  videoCodec=" + videoCodec.getDescription());
            sdpDescription = preferCodec(sdpDescription, videoCodec.getDescription(), false);
        }
        return sdpDescription;
    }
    
    static {
        CLASS_TAG = RTCMediaUtils.class.getSimpleName();
        LOGGER = Logger.getInstance("RTCClient");
    }
}
