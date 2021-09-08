package com.hahalolo.call.conference;

public class WsOfferAnswer extends WsRequestPacket {
   Jsep jsep;

   static enum Type {
      configure,
      start;
   }

   protected static class Jsep {
      String type;
      String sdp;
   }
}
