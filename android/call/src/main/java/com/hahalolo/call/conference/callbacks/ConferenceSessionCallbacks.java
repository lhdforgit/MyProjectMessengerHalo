package com.hahalolo.call.conference.callbacks;

import com.hahalolo.call.conference.ConferenceSession;
import com.hahalolo.call.conference.WsException;

import java.util.ArrayList;

public interface ConferenceSessionCallbacks {
   void onPublishersReceived(ArrayList<Integer> var1);

   void onPublisherLeft(Integer var1);

   void onMediaReceived(String var1, boolean var2);

   void onSlowLinkReceived(boolean var1, int var2);

   void onError(WsException var1);

   void onSessionClosed(ConferenceSession var1);
}
