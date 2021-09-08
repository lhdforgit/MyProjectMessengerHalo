package com.hahalolo.call.conference;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class WsRequestPacket extends WsPacket {
   @SerializedName("handle_id")
   BigInteger handleId;
}
