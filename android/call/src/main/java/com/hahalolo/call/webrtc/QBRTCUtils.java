package com.hahalolo.call.webrtc;

import org.webrtc.PeerConnection;

import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

class QBRTCUtils {

    public static List<PeerConnection.IceServer> getIceServersList() {
        if (QBRTCConfig.getIceServerList() == null) {
            final List<PeerConnection.IceServer> iceServersList = new LinkedList<>();
            iceServersList.add(PeerConnection.IceServer.builder("stun:meet-jit-si-turnrelay.jitsi.net:443").createIceServer());
            return iceServersList;
        }
        return com.hahalolo.call.webrtc.QBRTCConfig.getIceServerList();
    }

    public static void abortUnless(final boolean condition, final String msg) {
        if (!condition) {
            reportError(msg);
        }
    }

    public static void reportError(final String errorMessage) {
        Timber.e( "Peerconnection ERROR: %s", errorMessage);
    }
}