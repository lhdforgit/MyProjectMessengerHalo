package com.hahalolo.call.conference.callbacks;

import com.hahalolo.call.conference.WsException;

public interface ConferenceEntityCallback<T> {
   void onSuccess(T var1);

   void onError(WsException var1);
}
