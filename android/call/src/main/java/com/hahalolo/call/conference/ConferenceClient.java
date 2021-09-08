package com.hahalolo.call.conference;

import android.content.Context;

import com.hahalolo.call.conference.callbacks.ConferenceEntityCallback;
import com.hahalolo.call.webrtc.BaseClient;
import com.hahalolo.call.webrtc.QBRTCTypes;
import com.hahalolo.call.webrtc.util.Logger;

public class ConferenceClient extends BaseClient {
   public static final String CLASS_TAG = "ConferenceClient";
   private static final Logger LOGGER = Logger.getInstance("ConferenceClient");
   private static volatile ConferenceClient INSTANCE;
   protected Integer currentUserId;
   private JanusSignaler signaler;
   private boolean autoSubscribe = true;

   private ConferenceClient(Context context) {
      super(context);
   }

   public void setAutoSubscribeAfterJoin(boolean autoSubscribe) {
      this.autoSubscribe = autoSubscribe;
   }

   public boolean isAutoSubscribeAfterJoin() {
      return this.autoSubscribe;
   }

   public static ConferenceClient getInstance(Context context) {
      if (INSTANCE == null) {
         Class var1 = ConferenceClient.class;
         synchronized(ConferenceClient.class) {
            if (INSTANCE == null) {
               LOGGER.d("ConferenceClient", "Create ConferenceClient INSTANCE");
               INSTANCE = new ConferenceClient(context);
            }
         }
      }

      return INSTANCE;
   }

   Integer getCurrentUserId() {
      return this.currentUserId;
   }

   public void createSession(int userId, final QBRTCTypes.QBConferenceType conferenceType, final ConferenceEntityCallback<ConferenceSession> callback) {
      LOGGER.d("ConferenceClient", "createSession userId" + userId);
      this.currentUserId = userId;
      this.executor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceClient.this.createSignaling();
               ConferenceClient.this.startSession();
               ConferenceClient.this.attachPlugin(ConferenceConfig.getPlugin());
               ConferenceClient.this.signaler.startAutoSendPresence();
               ConferenceSession conferenceSession = new ConferenceSession(ConferenceClient.this, ConferenceClient.this.cameraErrorHandler, ConferenceClient.this.signaler, conferenceType);
               conferenceSession.addConferenceSessionListener(ConferenceClient.this.new ConferenceSessionCallbacksClosed());
               callback.onSuccess(conferenceSession);
            } catch (WsException var2) {
               ConferenceClient.LOGGER.d("ConferenceClient", "webSocketConnection error: " + var2.getMessage());
               callback.onError(var2);
            }

         }
      });
   }

   private void createSignaling() {
      this.signaler = new JanusSignaler(ConferenceConfig.getUrl(), ConferenceConfig.getProtocol(), ConferenceConfig.getSocketTimeOutMs(), ConferenceConfig.getKeepAliveValueSec());
   }

   private void startSession() throws WsException {
      this.signaler.startSession();
   }

   private void attachPlugin(String plugin) throws WsException {
      this.signaler.attachPlugin(plugin, this.currentUserId);
   }

   private class ConferenceSessionCallbacksClosed extends ConferenceSessionCallbacksImpl {
      private ConferenceSessionCallbacksClosed() {
      }

      public void onSessionClosed(ConferenceSession session) {
         ConferenceClient.this.clear();
      }

      // $FF: synthetic method
      ConferenceSessionCallbacksClosed(Object x1) {
         this();
      }
   }
}
