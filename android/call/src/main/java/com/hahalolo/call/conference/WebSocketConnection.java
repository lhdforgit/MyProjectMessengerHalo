package com.hahalolo.call.conference;

import com.hahalolo.call.webrtc.util.Logger;
import com.hahalolo.call.ws.client.WebSocket;
import com.hahalolo.call.ws.client.WebSocketAdapter;
import com.hahalolo.call.ws.client.WebSocketException;
import com.hahalolo.call.ws.client.WebSocketFactory;
import com.hahalolo.call.ws.client.WebSocketFrame;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebSocketConnection {
   static final String CLASS_TAG = "WebSocketConnection";
   private static int instances = 0;
   private final int instance;
   private static Logger LOGGER;
   private final String url;
   private final String protocol;
   private WebSocket client;
   private BigInteger sessionId;
   private RandomStringGenerator stringGenerator = new RandomStringGenerator();
   private Map<String, WsPacket> transactions = Collections.synchronizedMap(new HashMap());
   private List<WsPacketListener> packetListeners = new CopyOnWriteArrayList();
   private List<PacketCollector> collectors = new CopyOnWriteArrayList();
   private long timeOut;
   private ExecutorService executorService;

   WebSocketConnection(String url, String protocol) {
      this.timeOut = TimeUnit.SECONDS.toMillis(10L);
      this.executorService = Executors.newSingleThreadExecutor();
      this.instance = instances++;
      this.url = url;
      this.protocol = protocol;
   }

   public long getTimeOut() {
      return this.timeOut;
   }

   public void setTimeOut(long timeOut) {
      this.timeOut = timeOut;
   }

   public void addPacketListener(WsPacketListener packetListener) {
      this.packetListeners.add(packetListener);
   }

   public void connect() throws WsException {
      LOGGER.d("WebSocketConnection", "conect to " + this.url + ", " + this.protocol);
      WebSocketFactory factory = new WebSocketFactory();

      try {
         this.client = factory.createSocket(this.url, 30000);
         this.client.addProtocol(this.protocol);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      this.client.addListener(new WebSocketAdapter() {
         public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
         }

         public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
         }

         public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onCloseFrame(websocket, frame);
         }

         public void onTextMessage(WebSocket websocket, String text) throws Exception {
            WebSocketConnection.this.onParseMessage(text);
         }
      });

      try {
         this.client.connect();
         this.onConnectionEstablished((Exception)null, this.client);
      } catch (WebSocketException var3) {
         throw new WsException(var3.getLocalizedMessage(), var3);
      }
   }

   public void authenticate() throws WsException {
      WsDataPacket packet = new WsDataPacket();
      packet.setMessageType(WsPacket.Type.create);
      this.createCollectorAndSend(packet);
   }

   public void send(WsPacket packet) {
      this.awaitOpenConnection();
      this.applyTransactionData(packet);
      String rawMessage = WsParserUtils.decode(packet);
      LOGGER.d("WebSocketConnection", "SND(" + this.instance + "):" + rawMessage);
      this.client.sendText(rawMessage);
   }

   public <P extends WsPacket> P sendSynchronous(WsPacket packet, WsPacket.Type type) throws WsException {
      return this.createCollectorAndSend(packet, type);
   }

   public void closeSession() {
      WsDataPacket packet = new WsDataPacket();
      packet.setMessageType(WsPacket.Type.destroy);

      try {
         this.createCollectorAndSend(packet);
      } catch (WsException var3) {
      }

      this.sessionId = null;
      this.client.sendClose();
   }

   public void removePacketCollector(PacketCollector packetCollector) {
      this.collectors.remove(packetCollector);
   }

   public long getDefaultTimeOut() {
      return this.timeOut;
   }

   private void onConnectionEstablished(Exception ex, WebSocket webSocket) {
      if (ex != null) {
      }

      synchronized(this) {
         this.client = webSocket;
         this.notifyAll();
      }
   }

   private <P extends WsPacket> P createCollectorAndSend(WsPacket packet) throws WsException {
      return this.createCollectorAndSend(packet, WsPacket.Type.success);
   }

   private <P extends WsPacket> P createCollectorAndSend(WsPacket packet, WsPacket.Type type) throws WsException {
      PacketCollector packetCollector = new PacketCollector(type, this);
      this.collectors.add(packetCollector);
      this.send(packet);
      return packetCollector.nextResultOrThrow(packet.getMessageType());
   }

   private void awaitOpenConnection() {
      synchronized(this) {
         while(this.client == null) {
            LOGGER.d("WebSocketConnection", "waiting for connection");

            try {
               this.wait();
            } catch (InterruptedException var4) {
            }
         }

      }
   }

   private void applyTransactionData(WsPacket packet) {
      if (this.sessionId != null) {
         packet.setSessionId(this.sessionId);
      }

      String randomString = this.stringGenerator.getString(12);
      packet.setTransaction(randomString);
      this.transactions.put(randomString, packet);
   }

   private synchronized void onParseMessage(String message) {
      LOGGER.d("WebSocketConnection", "RCV(" + this.instance + "):" + message);
      ListenerNotification listenerNotification = new ListenerNotification();
      listenerNotification.processMsg(message);
      this.executorService.execute(listenerNotification);
   }

   private void notifyErrorListeners(WsPacket reqPacket) {
      Iterator var2 = this.packetListeners.iterator();

      while(var2.hasNext()) {
         WsPacketListener packetListener = (WsPacketListener)var2.next();

         try {
            packetListener.onPacketError(reqPacket, "Error");
         } catch (Exception var5) {
            LOGGER.e("WebSocketConnection", var5.getLocalizedMessage());
         }
      }

   }

   private void notifyListeners(WsPacket packet) {
      Iterator var2 = this.packetListeners.iterator();

      while(var2.hasNext()) {
         WsPacketListener packetListener = (WsPacketListener)var2.next();

         try {
            packetListener.onPacketReceived(packet);
         } catch (Exception var6) {
            String msg = var6.getLocalizedMessage() == null ? "WebSocketConnectionFailed failed" : var6.getLocalizedMessage();
            LOGGER.e("WebSocketConnection", msg);
         }
      }

      this.processResponse(packet);
   }

   private void processResponse(WsPacket packet) {
      Iterator var2 = this.collectors.iterator();

      while(var2.hasNext()) {
         PacketCollector collector = (PacketCollector)var2.next();
         collector.processPacket(packet);
      }

   }

   private void applySession(WsPacket packet) {
      if (this.sessionId == null && packet instanceof WsDataPacket && ((WsDataPacket)packet).getData() != null) {
         BigInteger id = ((WsDataPacket)packet).getData().getId();
         this.sessionId = id;
      }

   }

   public boolean isActiveSession() {
      return this.sessionId != null && this.client.isOpen();
   }

   static {
      LOGGER = Logger.getInstance(ConferenceClient.TAG);
   }

   public interface WsPacketListener {
      void onPacketReceived(WsPacket var1);

      void onPacketError(WsPacket var1, String var2);
   }

   private static class RandomStringGenerator {
      private RandomStringGenerator() {
      }

      String getString(Integer length) {
         UUID uuid = UUID.randomUUID();
         return uuid.toString().substring(0, length);
      }

      // $FF: synthetic method
      RandomStringGenerator(Object x0) {
         this();
      }
   }

   private class ListenerNotification implements Runnable {
      private String rawPacket;

      private ListenerNotification() {
      }

      public void processMsg(String rawMessage) {
         this.rawPacket = rawMessage;
      }

      public void run() {
         WsPacket packet = WsParserUtils.parse(this.rawPacket);
         WebSocketConnection.this.applySession(packet);
         WsPacket reqPacket = (WsPacket)WebSocketConnection.this.transactions.get(packet.getTransaction());
         if (reqPacket == null && packet.getSessionId() == null) {
            WebSocketConnection.this.notifyErrorListeners(packet);
         } else {
            WebSocketConnection.this.notifyListeners(packet);
         }
      }

      // $FF: synthetic method
      ListenerNotification(Object x1) {
         this();
      }
   }
}
