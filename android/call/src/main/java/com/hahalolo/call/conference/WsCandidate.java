package com.hahalolo.call.conference;

public class WsCandidate extends WsRequestPacket {
   Candidate candidate;

   protected static class Candidate {
      String candidate;
      Integer sdpMLineIndex;
      String sdpMid;
      Boolean completed;
   }
}
