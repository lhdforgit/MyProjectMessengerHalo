package com.hahalolo.call.conference;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.List;

public class WsEvent extends WsResponse {
   BigInteger sender;
   Plugindata plugindata;
   Jsep jsep;

   boolean isJoinEvent() {
      return this.isEventWithPublishers() && TextUtils.equals(this.plugindata.data.videoroom, "joined");
   }

   boolean isEventError() {
      return TextUtils.equals(this.plugindata.data.videoroom, "event") && this.plugindata.data.error != null;
   }

   boolean isRemoteSDPEventAnswer() {
      return this.isEventSDP() && TextUtils.equals(this.jsep.type, "answer");
   }

   boolean isRemoteSDPEventOffer() {
      return this.isEventSDP() && TextUtils.equals(this.jsep.type, "offer");
   }

   boolean isPublisherEvent() {
      return this.isEventWithPublishers() && TextUtils.equals(this.plugindata.data.videoroom, "event");
   }

   boolean isUnPublishedEvent() {
      return this.isWithPluginData() && this.plugindata.data.unpublished != null;
   }

   boolean isStartedEvent() {
      return this.isWithPluginData() && this.plugindata.data.started != null;
   }

   boolean isLeavePublisherEvent() {
      return this.isWithPluginData() && this.plugindata.data.leaving != null && this.getTransaction() == null;
   }

   boolean isLeaveCurrentUserEvent() {
      return this.isWithPluginData() && this.plugindata.data.leaving != null && this.getTransaction() != null;
   }

   boolean isLeftCurrentUserEvent() {
      return this.isWithPluginData() && this.plugindata.data.left != null && this.getTransaction() != null;
   }

   private boolean isEventWithPublishers() {
      return this.isWithPluginData() && this.plugindata.data.publishers != null;
   }

   private boolean isEventSDP() {
      return this.jsep != null && this.jsep.sdp != null;
   }

   private boolean isWithPluginData() {
      return this.plugindata != null && this.plugindata.data != null;
   }

   static class Jsep {
      String type;
      String sdp;
   }

   public static class Publisher {
      public Integer id;
      @SerializedName("audio_codec")
      public String audioCodec;
      @SerializedName("video_codec")
      public String videoCodec;
      public Boolean talking;

      public String toString() {
         return "{id= " + this.id + ", audioCodec= " + this.audioCodec + ", videoCodec= " + this.videoCodec + ", talking= " + this.talking + "}";
      }
   }

   static class Plugindata {
      String plugin;
      Data data;

      static class Data {
         String videoroom;
         String error;
         @SerializedName("error_code")
         Integer errorCode;
         BigInteger room;
         String configured;
         String description;
         String started;
         Integer id;
         @SerializedName("private_id")
         String privateId;
         String leaving;
         String left;
         Integer unpublished;
         List<Publisher> publishers;
      }
   }
}
