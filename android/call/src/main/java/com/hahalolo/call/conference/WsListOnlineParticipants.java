package com.hahalolo.call.conference;

public class WsListOnlineParticipants extends WsRequestPacket {
   Body body;

   protected static class Body {
      String room;
      WsPacket.Type request;
   }
}
