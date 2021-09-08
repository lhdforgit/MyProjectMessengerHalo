package com.hahalolo.call.conference;

public class ConferenceConfig {
   private static String url = null;
   private static String protocol = "janus-protocol";
   private static String plugin = "janus.plugin.videoroom";
   private static int socketTimeOutMs = 10000;
   private static int keepAliveValueSec = 30;

   public static String getUrl() {
      return url;
   }

   public static void setUrl(String url) {
      ConferenceConfig.url = url;
   }

   public static String getProtocol() {
      return protocol;
   }

   public static void setProtocol(String protocol) {
      ConferenceConfig.protocol = protocol;
   }

   public static String getPlugin() {
      return plugin;
   }

   public static void setPlugin(String plugin) {
      ConferenceConfig.plugin = plugin;
   }

   public static int getSocketTimeOutMs() {
      return socketTimeOutMs;
   }

   public static void setSocketTimeOutMs(int socketTimeOutMs) {
      ConferenceConfig.socketTimeOutMs = socketTimeOutMs;
   }

   public static int getKeepAliveValueSec() {
      return keepAliveValueSec;
   }

   public static void setKeepAliveValueSec(int keepAliveValueSec) {
      ConferenceConfig.keepAliveValueSec = keepAliveValueSec;
   }
}
