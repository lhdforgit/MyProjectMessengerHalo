package com.hahalolo.call.webrtc;

import org.webrtc.PeerConnection;

import java.util.List;

public final class QBRTCConfig
{
    private static long dialingTimeInterval;
    private static long answerTimeInterval;
    private static Integer maxOpponentsCount;
    private static Integer disconnectTime;
    private static List<PeerConnection.IceServer> iceServerList;
    private static boolean debugEnabled;
    private static long statsReportInterval;
    
    public static void setStatsReportInterval(final long statsReportInterval) {
        QBRTCConfig.statsReportInterval = statsReportInterval;
    }
    
    public static void setDialingTimeInterval(final long dialingTimeInterval) {
        QBRTCConfig.dialingTimeInterval = dialingTimeInterval;
    }
    
    public static void setAnswerTimeInterval(final long answerTimeInterval) {
        QBRTCConfig.answerTimeInterval = answerTimeInterval;
    }
    
    public static void setMaxOpponentsCount(final Integer maxOpponentsCount) {
        QBRTCConfig.maxOpponentsCount = maxOpponentsCount;
    }
    
    public static void setDisconnectTime(final Integer disconnectTime) {
        QBRTCConfig.disconnectTime = disconnectTime;
    }
    
    public static void setIceServerList(final List<PeerConnection.IceServer> iceServerList) {
        QBRTCConfig.iceServerList = iceServerList;
    }
    
    public static long getDialingTimeInterval() {
        return QBRTCConfig.dialingTimeInterval;
    }
    
    public static long getAnswerTimeInterval() {
        return QBRTCConfig.answerTimeInterval;
    }
    
    public static Integer getMaxOpponentsCount() {
        return QBRTCConfig.maxOpponentsCount;
    }
    
    public static Integer getDisconnectTime() {
        return QBRTCConfig.disconnectTime;
    }
    
    public static List<PeerConnection.IceServer> getIceServerList() {
        return QBRTCConfig.iceServerList;
    }
    
    public static void setDebugEnabled(final boolean debugEnabled) {
        QBRTCConfig.debugEnabled = debugEnabled;
    }
    
    public static boolean isDebugEnabled() {
        return QBRTCConfig.debugEnabled;
    }
    
    public static long getStatsReportTimeInterval() {
        return QBRTCConfig.statsReportInterval;
    }
    
    static {
        QBRTCConfig.dialingTimeInterval = 5L;
        QBRTCConfig.answerTimeInterval = 60L;
        QBRTCConfig.maxOpponentsCount = 10;
        QBRTCConfig.disconnectTime = 10;
        QBRTCConfig.iceServerList = null;
        QBRTCConfig.debugEnabled = true;
        QBRTCConfig.statsReportInterval = 0L;
    }
}
