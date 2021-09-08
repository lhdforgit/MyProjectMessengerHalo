package com.hahalolo.socket

import android.app.Application
import android.os.Handler
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hahalolo.socket.model.data.Typing
import com.hahalolo.socket.model.socket.*
import com.hahalolo.socket.perf.SocketPref
import com.halo.common.network.InternetAvailabilityChecker
import com.halo.common.network.InternetConnectivityListener
import com.halo.constant.HaloConfig
import com.halo.data.api.gateway.GateWayRestApi
import com.halo.data.common.utils.Strings
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.Member
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.message.Message
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.member.MemberRepository
import com.halo.data.repository.message.MessageRepository
import io.crossbar.autobahn.websocket.WebSocketConnection
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler
import io.crossbar.autobahn.websocket.types.ConnectionResponse
import io.crossbar.autobahn.websocket.types.WebSocketOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.internal.commonAsUtf8ToByteArray
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Create by ndn
 * Create on 5/27/21
 * com.hahalolo.socket
 */
@Singleton
class SocketManagerImpl
@Inject constructor(
    val application: Application,
    val gateWayRestApi: GateWayRestApi,
    val messageRepository: MessageRepository,
    val channelRepository: ChannelRepository,
    val memberRepository: MemberRepository,
    private val socketPref: SocketPref
) : SocketManager {

    private var autoConnect = true
    private var currentToken: MessOauth? = null
    private val socketState = MutableLiveData<Int>(SocketState.NONE)
    val option = WebSocketOptions().apply {
        this.reconnectInterval = 500
    }
    var pingTime: Long = 4500

    private var webSocket: WebSocketConnection? = null

    private var mInternetAvailabilityChecker: InternetAvailabilityChecker? = null

    private var internetConnectivityListener = InternetConnectivityListener {
        if (it && socketState.value == SocketState.DISCONNECT && autoConnect) {
            onResume()
        }
    }

    private fun ownerId():String{
        return currentToken?.oauthInfo?.idUser?:""
    }

    override fun getSocketState(): LiveData<Int> {
        return socketState
    }

    private val pingHandler = Handler()

    private var pingRunner: Runnable = object : Runnable {
        override fun run() {
            sendMessage(webSocket, SocketEvent.HEARTBEAT, null)
            pingHandler.postDelayed(this, pingTime)
        }
    }

    init {
        mInternetAvailabilityChecker = try {
            InternetAvailabilityChecker.getInstance()
        } catch (e: Exception) {
            InternetAvailabilityChecker.init(application)
            InternetAvailabilityChecker.getInstance()
        }
        mInternetAvailabilityChecker?.addInternetConnectivityListener(internetConnectivityListener)
    }

    private fun startPing() {
        pingHandler.removeCallbacks(pingRunner)
        pingHandler.postDelayed(pingRunner, 0)
    }

    private fun getToken(): String {
        return socketPref.getToken()
    }

    private fun getSessionId(): String? {
        return socketPref.getSessionId(getToken()).takeIf { it.isNotEmpty() }
    }

    private fun saveSessionId(sessionId: String?) {
        sessionId?.takeIf { it.isNotEmpty() }?.run {
            Strings.log("SocketManagerImpl saveSessionId ", this)
            socketPref.insertSessionId(getToken(), this)
        }
    }

    private fun saveSocketHost(host:String?){
        Strings.log("SocketManagerImpl saveSocketHost ", host)
        host?.takeIf { it.isNotEmpty() }?.run {
            socketPref.insertSocketHost(this)
        }
    }

    private fun socketHost():String{
        return (socketPref.getSocketHost().takeIf { it.isNotEmpty() } ?: HaloConfig.SOCKET_HOST).apply {
            Strings.log("SocketManagerImpl socketHost ", this)
        }
    }

    private fun tokenError() {
        socketState.value = SocketState.AUTHEN
    }

    private suspend fun initSocketHost(): Boolean{
        // lấy socket host
        kotlin.runCatching {
            gateWayRestApi.gateWay()?.url?.takeIf { it.isNotEmpty() }?.run {
                if (!TextUtils.equals(this, socketHost())){
                    Strings.log("SocketManagerImpl SOCKET initSocketHost ", this)
                    // this new Host - save and return true
                    saveSocketHost(this)
                    return true
                }
            }
        }.getOrElse {
            it.printStackTrace()
        }
        return false
    }

    override fun initSocket(userToken: MessOauth?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (currentToken != null && TextUtils.equals(
                    currentToken?.token,
                    userToken?.token
                )
            ) return@launch
            Strings.log("SocketManagerImpl SOCKET initSocket ", userToken)
            socketPref.insertToken(userToken?.token ?: "")
            socketState.postValue(SocketState.NONE)
            this@SocketManagerImpl.currentToken = userToken

            //init socket host
            initSocketHost()

            kotlin.runCatching {
                webSocket = WebSocketConnection()
                webSocket?.setOptions(option)
                webSocket?.connect(
                    socketHost(),
                    object : WebSocketConnectionHandler() {
                        override fun onMessage(payload: String) {
                            super.onMessage(payload)
                        }

                        override fun onMessage(payload: ByteArray, isBinary: Boolean) {
                            super.onMessage(payload, isBinary)
                            Strings.log("SocketManagerImpl SOCKET onMessage ", String(payload))
                            String(payload).run {
                                val result = loadEvent(this)
                                Strings.log(
                                    "SocketManagerImpl SOCKET onMessage result ",
                                    result
                                )
                                when (result?.payload) {
                                    is Payload.Hello -> {
                                        result.payload.heartbeatIntervalMs.takeIf { it > 0 }
                                            ?.toLong()?.run {
                                            pingTime = this
                                        }
                                        getSessionId()?.run {
                                            //đã có sessionId
                                            sendMessage(
                                                webSocket,
                                                SocketEvent.RESUME,
                                                Payload.Resume(
                                                    token = getToken(),
                                                    sessionId = this,
                                                    0
                                                )
                                            )
                                        } ?: kotlin.run {
                                            // chưa có sessionId
                                            sendMessage(
                                                webSocket,
                                                SocketEvent.IDENTIFY,
                                                Payload.Identify(getToken())
                                            )
                                        }
                                    }
                                    is Payload.Ready -> {
                                        socketState.value = SocketState.CONNECTED
                                        saveSessionId(result.payload.sessionId)
                                        startPing()
                                    }
                                    is Payload.Resumed -> {
                                        socketState.value = SocketState.CONNECTED
                                        saveSessionId(result.payload.sessionId)
                                        startPing()
                                    }
                                    is Payload.Exception -> {
                                        when (result.payload.code) {
                                            ExceptionCode.AUTHENTICATION_FAILED,
                                            ExceptionCode.ALREADY_AUTHENTICATED,
                                            ExceptionCode.NOT_AUTHENTICATED -> {
                                                //Token error
                                                tokenError()
                                            }

                                            ExceptionCode.INVALID_SESSION -> {
                                                //Sesion Id error
                                                sendMessage(
                                                    webSocket,
                                                    SocketEvent.IDENTIFY,
                                                    Payload.Identify(getToken())
                                                )
                                            }

                                            else -> {

                                            }
                                        }
                                    }
                                    is Payload.Ping -> {
                                        startPing()
                                    }
                                    is Payload.Replay -> {
                                        sendMessage(
                                            webSocket, SocketEvent.REPLAY_ACK, Payload.ReplayAck(
                                                token = getToken(),
                                                sessionId = getSessionId(),
                                                totalReplayed = result.payload.offlineMessages
                                            )
                                        )
                                    }

                                    is Payload.UserTyping ->{
                                        result.payload.typing?.run {
                                            channelRepository.onUserTyping(this.workspaceId?:"",
                                                this.channelId?:"",
                                                this.userId?:"")
                                        }
                                    }

                                    is Payload.MemberCreate -> {
                                        result.payload.member?.run {
                                            memberRepository.insertNewMember(this)
                                        }
                                    }

                                    is Payload.ChannelCreate -> {
                                        result.payload.channel?.run {
                                            channelRepository.onNewChannel(getToken(), this)
                                        }
                                    }

                                    is Payload.MessageCreate -> {
                                        result.payload.message?.run {
                                            messageRepository.insertNewMessage(getToken(), ownerId(),this)
                                        }
                                    }

                                    else -> {

                                    }
                                }
                            }
                        }

                        override fun onOpen() {
                            Strings.log("SocketManagerImpl SOCKET onOpen ")
                        }

                        override fun onConnect(response: ConnectionResponse?) {
                            super.onConnect(response)
                            socketState.value = SocketState.CONNECTING
                        }

                        override fun onClose(code: Int, reason: String) {
                            socketState.value = SocketState.DISCONNECT
                            Strings.log("SocketManagerImpl SOCKET onClose "+ code+": "+ reason)
                            if (autoConnect) onResume()
                        }
                    })
            }.getOrElse {
                Strings.log("SocketManagerImpl SOCKET initSocket error ", it)
                it.printStackTrace()
            }
        }
    }

    private fun sendMessage(
        webSocket: WebSocketConnection?,
        event: String?,
        data: Payload?
    ) {
        webSocket?.takeIf { it.isConnected }?.run {
            val list = mutableListOf<Any?>()
            list.add(null)
            list.add(null)
            list.add(event)
            list.add(data)
            Strings.log("SocketManagerImpl SOCKET sendMessage " + event + ": ", data)
            this.sendMessage(Gson().toJson(list).toString().commonAsUtf8ToByteArray(), true)
        }
    }

    fun createEvent(event: String, data: Any): ByteArray {
        val list = mutableListOf<String?>()
        list.add(null)
        list.add(null)
        list.add(event)
        list.add(Gson().toJson(data))
        return Gson().toJson(list).toString().commonAsUtf8ToByteArray()
    }

    fun loadEvent(msg: String): SocketModel? {
        try {
            val arrays = JSONArray(msg)
            val action = arrays.get(1) as? String
            val event = arrays.get(2) as? String
            val value = (arrays.get(3) as? JSONObject)?.toString()
            var data: Payload? = null
            when (event) {
                SocketEvent.HELLO -> {
                    data = Gson().fromJson(value, Payload.Hello::class.java)
                }
                SocketEvent.IDENTIFY -> {
                    data = Gson().fromJson(value, Payload.Identify::class.java)
                }
                SocketEvent.RESUME -> {
                    data = Gson().fromJson(value, Payload.Resume::class.java)
                }
                SocketEvent.READY -> {
                    data = Gson().fromJson(value, Payload.Ready::class.java)
                }
                SocketEvent.REPLAY -> {
                    data = Gson().fromJson(value, Payload.Replay::class.java)
                }
                SocketEvent.REPLAY_ACK -> {
                    data = Gson().fromJson(value, Payload.ReplayAck::class.java)
                }
                SocketEvent.EXCEPTION -> {
                    data = Gson().fromJson(value, Payload.Exception::class.java)
                }
                SocketEvent.RESUMED -> {
                    data = Gson().fromJson(value, Payload.Resumed::class.java)
                }
                SocketEvent.HEARTBEAT -> {
                    data = Payload.Ping(event)
                }
                SocketEvent.MESSAGE -> {
                    when (action) {
                        MessageAction.MESSAGE_CREATED -> {
                            data =
                                Payload.MessageCreate(Gson().fromJson(value, Message::class.java))
                        }
                        MessageAction.CHANNEL_CREATED -> {
                            data =
                                Payload.ChannelCreate(Gson().fromJson(value, Channel::class.java))
                        }
                        MessageAction.MEMBER_CREATED -> {
                            data = Payload.MemberCreate(Gson().fromJson(value, Member::class.java))
                        }
                        MessageAction.USER_TYPING ->{
                            data = Payload.UserTyping(Gson().fromJson(value, Typing::class.java ))
                        }
                    }
                }

                else -> {
                }
            }
            return SocketModel(event, data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onResume() {
        Strings.log("SocketManagerImpl SOCKET onResume ")
        autoConnect = true
        try {
            // resume connect socket host
            CoroutineScope(Dispatchers.IO).launch {
                // init socket host
                if (initSocketHost()){
                    // is new host , to initSocket
                    initSocket(this@SocketManagerImpl.currentToken)
                }else{
                    // old socket host = to reconnect
                    option.reconnectInterval = 500
                    webSocket?.setOptions(option)
                    webSocket?.takeIf { !it.isConnected }?.runCatching {
                        this.reconnect()
                    }?.getOrElse {
                        it.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        autoConnect = false
        try {
            option.reconnectInterval = 500
            webSocket?.setOptions(option)
            webSocket?.sendClose()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun disconnect() {
        socketPref.clearToken()
        socketPref.clearSession()
        webSocket?.sendClose()
        webSocket = null
    }
}