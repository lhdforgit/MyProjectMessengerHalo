package com.hahalolo.call.conference;

import android.text.TextUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class WsDataPacket extends WsPacket {
   SessionData data;
   Plugindata plugindata;

   public SessionData getData() {
      return this.data;
   }

   boolean isGetParticipant() {
      return this.plugindata != null && this.plugindata.data != null && TextUtils.equals(this.plugindata.data.videoroom, "participants");
   }

   boolean isVideoRoomEvent() {
      return this.plugindata != null && this.plugindata.data != null && TextUtils.equals(this.plugindata.data.videoroom, "event");
   }

   static class Plugindata {
      Data data;

      static class Data {
         String videoroom;
         String room;
         String error;
         String error_code;
         List<Map<String, Object>> participants;
      }
   }

   static class SessionData {
      BigInteger id;

      public BigInteger getId() {
         return this.id;
      }
   }
}
