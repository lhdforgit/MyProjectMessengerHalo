package com.hahalolo.call.conference;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class WsParserUtils {
   public static WsPacket parse(String rawMessage) {
      try {
         JSONObject jsonObject = new JSONObject(rawMessage);
         String janus = jsonObject.getString("janus");
         return parsePacketType(janus, rawMessage);
      } catch (JSONException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   private static WsPacket parsePacketType(String janus, String rawMNessage) {
      WsPacket.Type type = WsPacket.Type.fromString(janus);
      switch(type) {
      case ack:
         return createAck(rawMNessage);
      case success:
         return createSuccesResponse(rawMNessage);
      case event:
         return createEventResponse(rawMNessage);
      case webrtcup:
         return createWebrtcUpResponse(rawMNessage);
      case media:
         return createWsMediaResponse(rawMNessage);
      case hangup:
         return createWsHangUpResponse(rawMNessage);
      case slowlink:
         return createWsSlowLink(rawMNessage);
      default:
         return createResponsePacket(rawMNessage);
      }
   }

   private static WsSlowLink createWsSlowLink(String raw) {
      return (WsSlowLink)parseToModel(raw, WsSlowLink.class);
   }

   private static WsHangUp createWsHangUpResponse(String raw) {
      return (WsHangUp)parseToModel(raw, WsHangUp.class);
   }

   private static WsMedia createWsMediaResponse(String raw) {
      return (WsMedia)parseToModel(raw, WsMedia.class);
   }

   private static WsWebRTCUp createWebrtcUpResponse(String raw) {
      return (WsWebRTCUp)parseToModel(raw, WsWebRTCUp.class);
   }

   private static WsEvent createEventResponse(String raw) {
      return (WsEvent)parseToModel(raw, WsEvent.class);
   }

   private static WsPacket createResponsePacket(String raw) {
      return parseToModel(raw, WsResponse.class);
   }

   private static WsPacket createSuccesResponse(String raw) {
      return parseToModel(raw, WsDataPacket.class);
   }

   private static WsAck createAck(String raw) {
      return (WsAck)parseToModel(raw, WsAck.class);
   }

   private static <T extends WsPacket> T parseToModel(String raw, Class<T> aclass) {
      Gson gson = new Gson();
      T fromJson = gson.fromJson(raw, aclass);
      return fromJson;
   }

   public static String decode(WsPacket packet) {
      Gson gson = new Gson();
      return gson.toJson(packet);
   }
}
