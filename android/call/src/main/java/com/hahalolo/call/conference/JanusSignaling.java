package com.hahalolo.call.conference;

import com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType;
import com.hahalolo.call.webrtc.QBSignaling;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.math.BigInteger;
import java.util.Map;

public interface JanusSignaling extends QBSignaling {
   void startSession() throws WsException;

   void attachPlugin(String var1, Integer var2) throws WsException;

   void detachPlugin(BigInteger var1) throws WsException;

   void joinDialog(String var1, int var2, String var3) throws WsException;

   void leave(int var1) throws WsException;

   void sendKeepAlive() throws WsException;

   void sendOffer(SessionDescription var1, Integer var2, QBConferenceType var3, Map<String, String> var4) throws WsException;

   void sendAnswer(SessionDescription var1, Integer var2, QBConferenceType var3, Map<String, String> var4) throws WsException;

   void sendIceCandidate(IceCandidate var1, int var2, Map<String, String> var3) throws WsException;

   void sendIceCandidateComplete(int var1) throws WsException;

   void sendGetOnlineParticipants(String var1, int var2) throws WsException;

   void destroySession() throws WsException;
}
