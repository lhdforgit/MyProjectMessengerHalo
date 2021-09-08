package com.hahalolo.call.conference;

public class WsAnswer extends WsOfferAnswer {
   Body body;

   protected static class Body {
      String room;
      WsOfferAnswer.Type request;
   }
}
