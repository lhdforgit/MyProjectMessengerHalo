package com.hahalolo.call.conference;

import com.google.gson.annotations.SerializedName;

public class WsRoomPacket extends WsRequestPacket {
   Body body;

   static enum Type {
      join,
      leave;
   }

   protected static class Body {
      @SerializedName("id")
      Integer userId;
      Integer feed;
      String ptype;
      Type request;
      String room;
   }
}
