package com.hahalolo.call.conference;

public class WsPluginPacket extends WsDataPacket {
   String plugin;

   public void setPlugin(String pluginId) {
      this.plugin = pluginId;
   }
}
