package com.hahalolo.call.conference;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

public interface JanusResponseEventCallback {
   void onRemoteSDPEventAnswer(String var1);

   void onRemoteSDPEventOffer(int var1, String var2);

   void onJoinEvent(ArrayList<Integer> var1, BigInteger var2);

   void onPublishedEvent(ArrayList<Integer> var1);

   void onUnPublishedEvent(int var1);

   void onStartedEvent(String var1);

   void onLeavePublisherEvent(int var1);

   void onLeaveCurrentUserEvent(boolean var1);

   void onWebRTCUpReceived(BigInteger var1);

   void onMediaReceived(String var1, boolean var2);

   void onSlowLinkReceived(boolean var1, int var2);

   void onHangUp(String var1);

   void onEventError(String var1, int var2);

   void onPacketError(String var1);

   void onGetOnlineParticipants(Map<Integer, Boolean> var1);

   void onVideoRoomEvent(String var1);
}
