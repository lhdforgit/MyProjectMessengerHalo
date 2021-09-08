package com.hahalolo.call.conference;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.Locale;

public abstract class WsPacket {
   @SerializedName("janus")
   Type messageType;
   @SerializedName("session_id")
   private BigInteger sessionId;
   private String transaction;

   public BigInteger getSessionId() {
      return this.sessionId;
   }

   public Type getMessageType() {
      return this.messageType;
   }

   public void setMessageType(Type messageType) {
      this.messageType = messageType;
   }

   public String getTransaction() {
      return this.transaction;
   }

   public void setSessionId(BigInteger sessionId) {
      this.sessionId = sessionId;
   }

   public void setTransaction(String transaction) {
      this.transaction = transaction;
   }

   public static enum Type {
      message,
      info,
      trickle,
      detach,
      destroy,
      keepalive,
      create,
      attach,
      event,
      error,
      ack,
      success,
      webrtcup,
      hangup,
      detached,
      media,
      listparticipants,
      timeout,
      slowlink;

      public static Type fromString(String string) {
         return valueOf(string.toLowerCase(Locale.US));
      }
   }
}
