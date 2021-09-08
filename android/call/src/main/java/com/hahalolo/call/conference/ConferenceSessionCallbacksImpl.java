package com.hahalolo.call.conference;

import com.hahalolo.call.conference.callbacks.ConferenceSessionCallbacks;

import java.util.ArrayList;

public class ConferenceSessionCallbacksImpl implements ConferenceSessionCallbacks {
   public void onPublishersReceived(ArrayList<Integer> publishers) {
   }

   public void onPublisherLeft(Integer userID) {
   }

   public void onMediaReceived(String type, boolean success) {
   }

   public void onSlowLinkReceived(boolean uplink, int nacks) {
   }

   public void onError(WsException ex) {
   }

   public void onSessionClosed(ConferenceSession session) {
   }
}
