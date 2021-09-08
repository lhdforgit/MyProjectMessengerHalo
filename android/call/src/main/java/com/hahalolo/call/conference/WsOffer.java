package com.hahalolo.call.conference;

public class WsOffer extends WsOfferAnswer {
   Body body;

   protected static class Body {
      boolean audio;
      boolean video;
      WsOfferAnswer.Type request;
   }
}
