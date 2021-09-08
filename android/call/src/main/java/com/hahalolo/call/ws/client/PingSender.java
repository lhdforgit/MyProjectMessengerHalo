/*
 * Copyright (C) 2015-2016 Neo Visionaries Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hahalolo.call.ws.client;


class PingSender extends PeriodicalFrameSender
{
    private static final String TIMER_NAME = "PingSender";


    public PingSender(com.hahalolo.call.ws.client.WebSocket webSocket, com.hahalolo.call.ws.client.PayloadGenerator generator)
    {
        super(webSocket, TIMER_NAME, generator);
    }


    @Override
    protected com.hahalolo.call.ws.client.WebSocketFrame createFrame(byte[] payload)
    {
        return com.hahalolo.call.ws.client.WebSocketFrame.createPingFrame(payload);
    }
}
