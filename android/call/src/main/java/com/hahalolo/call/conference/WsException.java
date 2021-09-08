package com.hahalolo.call.conference;

public class WsException extends Exception {
   public WsException(String message, Throwable cause) {
      super(message, cause);
   }

   public WsException(String s) {
      super(s);
   }
}
