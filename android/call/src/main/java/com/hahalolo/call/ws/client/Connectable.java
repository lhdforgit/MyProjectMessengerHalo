/*
 * Copyright (C) 2015 Neo Visionaries Inc.
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


import java.util.concurrent.Callable;


/**
 * An implementation of {@link Callable} interface that calls
 * {@link com.hahalolo.call.ws.client.WebSocket#connect()}.
 *
 * @since 1.7
 */
class Connectable implements Callable<com.hahalolo.call.ws.client.WebSocket>
{
    private final com.hahalolo.call.ws.client.WebSocket mWebSocket;


    public Connectable(com.hahalolo.call.ws.client.WebSocket ws)
    {
        mWebSocket = ws;
    }


    @Override
    public com.hahalolo.call.ws.client.WebSocket call() throws com.hahalolo.call.ws.client.WebSocketException
    {
        return mWebSocket.connect();
    }
}
