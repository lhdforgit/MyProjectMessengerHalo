package com.hahalolo.call.conference;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PacketCollector {
   private volatile long waitStart;
   private WsPacket.Type type;
   private WebSocketConnection connection;
   private boolean cancelled;
   private ArrayBlockingQueue<WsPacket> queue;

   PacketCollector(WsPacket.Type type, WebSocketConnection connection, int bufSize) {
      this.type = type;
      this.connection = connection;
      this.queue = new ArrayBlockingQueue(bufSize);
   }

   PacketCollector(WsPacket.Type type, WebSocketConnection connection) {
      this(type, connection, 5);
   }

   public void cancel() {
      if (!this.cancelled) {
         this.cancelled = true;
         this.connection.removePacketCollector(this);
      }

   }

   public <P extends WsPacket> P nextResultOrThrow(WsPacket.Type type) throws WsException {
      return this.nextResultOrThrow(this.connection.getDefaultTimeOut(), type);
   }

   public <P extends WsPacket> P nextResultOrThrow(long timeout, WsPacket.Type type) throws WsException {
      P packet = this.nextResult(timeout);
      this.cancel();
      if (packet == null) {
         throw new WsNoResponseException("Packet \"" + type.toString() + "\" loss occurs");
      } else {
         return packet;
      }
   }

   public <P extends WsPacket> P nextResult(long timeout) {
      P res = null;
      long remainingWait = timeout;
      this.waitStart = System.currentTimeMillis();
      Object var6 = null;

      do {
         try {
            res = (P) this.queue.poll(remainingWait, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var8) {
            var8.printStackTrace();
         }

         if (res != null) {
            return res;
         }

         remainingWait = timeout - (System.currentTimeMillis() - this.waitStart);
      } while(remainingWait > 0L);

      return null;
   }

   protected void processPacket(WsPacket packet) {
      if (packet.getMessageType().equals(this.type)) {
         while(!this.queue.offer(packet)) {
            this.queue.poll();
         }
      }

   }
}
