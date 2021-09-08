package com.hahalolo.messager.bubble

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.bubble.container.BubbleContainerLayout
import com.hahalolo.messager.bubble.container.BubbleContainerViewModel
import com.hahalolo.messager.bubble.conversation.BubbleConversationViewModel
import com.hahalolo.messager.bubble.conversation.detail.ConversationDetailViewModel
import com.hahalolo.messager.bubble.conversation.detail.friend.BubbleFriendViewModel
import com.hahalolo.messager.bubble.conversation.detail.gallery.BubbleGalleryViewModel
import com.hahalolo.messager.bubble.conversation.detail.member.BubbleMemberViewModel
import com.hahalolo.messager.bubble.conversation.home.ConversationHomeViewModel
import com.hahalolo.messager.bubble.conversation.message.ConversationMessageViewModel
import com.hahalolo.messager.bubble.head.BubbleHeadViewModel
import com.hahalolo.messager.bubble.model.BubbleInfo
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.halo.common.utils.AppUtils
import com.halo.data.cache.pref.login.LoginPrefImpl
import com.halo.data.entities.media.MediaEntity
import com.halo.data.repository.tet.TetRepository
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass


class BubbleService : LifecycleService(), ViewModelStoreOwner {

    @Inject
    lateinit var messengerController: MessengerController

    @Inject
    lateinit var loginPref: LoginPrefImpl

//    @Inject
//    lateinit var groupRepository: ChatRoomRepository
//
//    @Inject
//    lateinit var reactionRepository: ChatReactionRepository
//
//    @Inject
//    lateinit var messageRepository: ChatMessageRepository

    @Inject
    lateinit var tetRepository: TetRepository

//    @Inject
//    lateinit var chatAttachmentRepository: ChatAttachmentRepository
//
//    @Inject
//    lateinit var friendRepository: ChatFriendRepository
//
//    @Inject
//    lateinit var chatRepository: ChatRepository

    private lateinit var windowManager: WindowManager

    private lateinit var bubbleContainerLayout: BubbleContainerLayout

    private lateinit var innerReceiver: InnerReceiver

    val messageViewModel: ConversationMessageViewModel by lazy {
        ConversationMessageViewModel(
            userTokenCallback,
//            groupRepository,
//            chatRepository,
//            reactionRepository,
//            messageRepository,
            tetRepository
        )
    }

    val detailViewModel: ConversationDetailViewModel by lazy {
        ConversationDetailViewModel(
//            chatRepository,
            userTokenCallback,
//            friendRepository,
//            groupRepository,
//            chatAttachmentRepository
        )
    }

    val homeViewModel: ConversationHomeViewModel by lazy {
        ConversationHomeViewModel(
            userTokenCallback,
//            chatRepository,
//            groupRepository
        )
    }

    val containerViewModel: BubbleContainerViewModel by lazy {
        BubbleContainerViewModel(
            userTokenCallback,
//            groupRepository
        )
    }

    val bubbleConversationViewModel: BubbleConversationViewModel by lazy {
        BubbleConversationViewModel()
    }

    val bubbleFriendViewModel: BubbleFriendViewModel by lazy {
        BubbleFriendViewModel(
            userTokenCallback,
//            friendRepository,
//            groupRepository
        )
    }

    val bubbleMemberViewModel: BubbleMemberViewModel by lazy {
        BubbleMemberViewModel(
            userTokenCallback,
//            groupRepository,
//            chatRepository,
//            friendRepository
        )
    }

    val bubbleGalleryViewModel: BubbleGalleryViewModel by lazy {
        BubbleGalleryViewModel(
            userTokenCallback,
//            chatAttachmentRepository
        )
    }

    private val userTokenCallback = object : UserTokenCallback {
        override fun invoke(): String {
            return getUserTokenId()
        }
    }

    private val serviceCallback = object : BubbleServiceCallback {
        override fun collapse() {
            this@BubbleService.collapse()
        }

        override fun getLifecycle(): LifecycleOwner {
            return this@BubbleService
        }

        override fun getViewModelStoreOwner(): ViewModelStoreOwner {
            return this@BubbleService
        }

        override fun <T : Any> createViewModel(kClass: KClass<T>): Any? {
            if (kClass == ConversationDetailViewModel::class) {
                return detailViewModel
            } else if (kClass == ConversationMessageViewModel::class) {
                return messageViewModel
            } else if (kClass == BubbleHeadViewModel::class) {
                return BubbleHeadViewModel(getUserTokenId())
            } else if (kClass == BubbleContainerViewModel::class) {
                return containerViewModel
            } else if (kClass == ConversationHomeViewModel::class) {
                return homeViewModel
            } else if (kClass == BubbleConversationViewModel::class) {
                return bubbleConversationViewModel
            } else if (kClass == BubbleFriendViewModel::class) {
                return bubbleFriendViewModel
            } else if (kClass == BubbleMemberViewModel::class) {
                return bubbleMemberViewModel
            } else if (kClass == BubbleGalleryViewModel::class) {
                return bubbleGalleryViewModel
            }
            return null
        }

        /*Room Detail Callback */
        override fun updateAvatarGroupChat(roomId: String?) {
            messengerController.updateAvatarGroupChat(roomId)
            collapse()
        }

        override fun createGroupWith(memberEntity: MemberEntity?, tag: Int) {
            memberEntity?.let {
                bubbleContainerLayout.navigateCreateGroupWith(it, tag)
            }
        }

        override fun navigateGallery(roomId: String?, tabIndex: Int) {
            messengerController.navigateGallery(roomId, tabIndex)
            collapse()
        }

        override fun navigateViewAvatar(targetView: ImageView?, medias: MutableList<MediaEntity>) {
            messengerController.navigateViewAvatar(targetView, medias)
            collapse()
        }

        override fun navigateChatMessageMember(roomId: String, tag: Int?) {
            bubbleContainerLayout.navigateMember(roomId, tag)
        }

        override fun navigateChatMessage(userId: String) {
            messengerController.navigateChatMessage(userId, this@BubbleService)
            collapse()
        }

        override fun navigatePersonalWall(userId: String) {
            messengerController.navigatePersonalWall(userId)
            collapse()
        }

        override fun navigateOpenMedia(listImage: MutableList<AttachmentTable>?, position: Int) {
            messengerController.navigateOpenMedia(listImage, position)
            collapse()
        }

        override fun navigateChangeNickName(roomId: String, tag: Int) {
            bubbleContainerLayout.navigateMemberNickName(roomId, tag)
        }

        override fun navigateUpdateOwnerMember(roomId: String, tag: Int) {
            bubbleContainerLayout.navigateUpdateOwnerMember(roomId, tag)
        }

        override fun navigateChangeGroupAvatar(roomId: String) {
            messengerController.navigateChangeGroupAvatar(roomId)
            collapse()
        }

        override fun navigateChangeGroupName(roomId: String) {
            messengerController.navigateChangeGroupName(roomId)
            collapse()
        }

        override fun navigateAddMemberInGroup(roomId: String, tag: Int) {
            bubbleContainerLayout.navigateAddMember(roomId, tag)
        }

        override fun navigateShareMedia(roomId: String) {
            messengerController.navigateShareMedia(roomId)
            collapse()
        }

        override fun navigateTakeImageCamera(roomId: String) {
            messengerController.navigateTakeImageCamera(roomId)
            collapse()
        }

        override fun navigateSetting(roomId: String) {
            bubbleContainerLayout.navigateSetting(roomId)
        }

        override fun navigateTakeFileExternal(roomId: String) {
            messengerController.navigateTakeFileExternal(roomId)
            collapse()
        }

        override fun navigateSearchMain() {
            messengerController.navigateSearchMain()
            collapse()
        }

        override fun navigateGalleryMedia(roomId: String, tabIndex: Int) {
            bubbleContainerLayout.navigateGalleryShared(roomId, tabIndex)
        }

        override fun navigateOpenWebView(url: String) {
            messengerController.navigateOpenWebView(url)
            collapse()
        }

        override fun navigateDownloadFile(attachmentTable: AttachmentTable) {
            messengerController.navigateDownloadFile(attachmentTable)
        }

        override fun getAppLang(): String {
            return loginPref.targetToken?.getLang() ?: ""
        }

        override fun getAvatar(): String {
            return loginPref.targetToken?.avatar ?: ""
        }
        /*Room Detail Callback END */

        /*Home Callback */
        override fun onOpenConversation(roomId: String) {
            bubbleContainerLayout.openConversationId(roomId)
        }
        /*Home Callback END*/
    }

    private fun getUserTokenId(): String {
        return loginPref.targetToken?.getUserId() ?: ""
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        instance = this
        initialized = true

        initReceiver()
        buildNotifyBubbleChat(0)

        //Stop the service after 5 seconds if no bubble is displayed
        stopServiceHandler.postDelayed(stopServiceRunner, 5000)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        bubbleContainerLayout =
            BubbleContainerLayout(ContextThemeWrapper(this, R.style.MessengerTheme)).apply {
                this.updateBubbleServiceListener(serviceCallback)
            }

        initHandleData()
//        chatRepository.onLogin(getUserTokenId())
//        chatRepository.onResumeChat()
    }

    private fun initReceiver() {
        try {
            innerReceiver = InnerReceiver()
            val intentFilter = IntentFilter(ACTION_SHOW_BUBBLE_CONTENT)
            registerReceiver(innerReceiver, intentFilter)
            val intentHome = IntentFilter(Intent.CATEGORY_HOME)
            intentHome.addAction( Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            registerReceiver(innerReceiver, intentHome)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearNotifyBubbleChat() {
        if (foreground) {
            stopForeground(true)
        }
    }

    private fun buildNotifyBubbleChat(count: Int) {
        try {
            val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(
                        "overlay_service",
                        "Discord Chat Heads service"
                    )
                } else {
                    ""
                }

            val notificationIntent = Intent(ACTION_SHOW_BUBBLE_CONTENT)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0)
            val notification = NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setContentTitle(
                    if (count > 0) getString(
                        R.string.chat_message_notify_count_title,
                        count
                    ) else getString(R.string.chat_message_notify_title)
                )
                .setSmallIcon(R.drawable.ic_logo_favicon)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent).build()
            if (initialized) {
                startForeground(1001, notification)
                foreground = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var enableBubble = false
    private var enableBubbleHandler = Handler()
    private var enableBubbleRunner = Runnable {
        if (this.enableBubble) {
            bubbleContainerLayout.visibility = View.VISIBLE
        } else {
            bubbleContainerLayout.visibility = View.GONE
        }
    }

    private var stopServiceHandler = Handler()
    private var stopServiceRunner = Runnable {
        clearNotifyBubbleChat()
        stopSelf()
    }

    private fun initHandleData() {
        bubbleContainerLayout.observerBubbleCount(Observer {
            it?.takeIf { it.isNotEmpty() }?.size?.run {
                //Count
                stopServiceHandler.removeCallbacks(stopServiceRunner)
                buildNotifyBubbleChat(this)
            } ?: run {
                //clear ALL
                stopServiceHandler.postDelayed(stopServiceRunner, 1000)
            }
        })
        bubbleContainerLayout.toggled.observe(this, Observer {
            if (it == false && !enableBubble) {
                enableBubbleHandler.removeCallbacks(enableBubbleRunner)
                enableBubbleHandler.postDelayed(enableBubbleRunner, 350)
            }
        })

//        chatRepository.getLiveMainChatEnable().observe(this, Observer {
//            enableBubble = it == false
//            enableBubbleHandler.removeCallbacks(enableBubbleRunner)
//            enableBubbleHandler.postDelayed(enableBubbleRunner, 350)
//        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    fun addView(view: View, params: WindowManager.LayoutParams) {
        try {
            windowManager.addView(view, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.getStringExtra(ACTION)?.run {
            when (this) {
                BubbleServiceAction.LOG_OUT -> {
                    runOnMainLoop {
                        try {
                            bubbleContainerLayout.onClose()
                            stopSelf()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                BubbleServiceAction.OPEN_BUBBLE_CONVERSATION -> {
                    runOnMainLoop {
                        val roomId = intent.getStringExtra(CONVERSATION_ID)
                        bubbleContainerLayout.openConversationId(roomId ?: "")
                    }
                }

                BubbleServiceAction.TAKE_MEDIA_MESSAGE -> {
                    runOnMainLoop {
                        val roomId = intent.getStringExtra(CONVERSATION_ID)
                        val medias = intent.getStringArrayListExtra(MEDIA_DATA)
                        bubbleContainerLayout.takeMediaMessage(roomId, medias?.toMutableList())
                    }
                }

                BubbleServiceAction.TAKE_FILE_MESSAGE -> {
                    runOnMainLoop {
                        val roomId = intent.getStringExtra(CONVERSATION_ID)
                        val path = intent.getStringExtra(MEDIA_DATA)
                        bubbleContainerLayout.takeFileMessage(roomId, path ?: "")
                    }
                }

                BubbleServiceAction.PUSH_NOTIFY -> {
                    runOnMainLoop {
//                        chatRepository.getCountNewRoom(getUserTokenId())
//                        val isFore = AppUtils.isAppForeground()
//                        if (!isFore){
//                            /*
//                            * nếu ứng dụng không đc mở, thêm bubble mới để hiển thị trên màn hình
//                            */
//                            intent.getStringExtra(CONVERSATION_ID)?.run {
//                                bubbleContainerLayout.addConversationId(this, intent.getStringExtra(PUSH_MESSAGE))
//                            }
//                        }
                    }
                }
                else -> {
                }
            }
        }
        return START_NOT_STICKY
    }

    fun collapse() {
        bubbleContainerLayout.collapse()
    }

    fun removeShowContentRunnable() {
        bubbleContainerLayout.removeShowContentRunnable()
    }

    fun updateViewLayout(
        view: View,
        params: WindowManager.LayoutParams
    ) {
        windowManager.updateViewLayout(view, params)
    }

    private fun clearViewInWindowManager() {
        try {
            bubbleContainerLayout.invalidateLayout()
            windowManager.removeView(bubbleContainerLayout)
            windowManager.removeView(bubbleContainerLayout.motionTracker)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearReceiver() {
        try {
            unregisterReceiver(innerReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        clearReceiver()
        clearViewInWindowManager()
        initialized = false
        instance = null
        super.onDestroy()
    }

    class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_SHOW_BUBBLE_CONTENT == action) {
                instance?.bubbleContainerLayout?.visibility = View.VISIBLE
                instance?.bubbleContainerLayout?.showTopBubbleContent()
            }else if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                || TextUtils.equals(action, Intent.CATEGORY_HOME)){
                instance?.collapse()
            }
        }
    }

    companion object {
        const val ENABLE_BUBBLE = true
        var DEBUG = false
        var instance: BubbleService? = null
        var initialized = false
        var foreground = false
        const val ACTION = "BubbleService-ACTION"
        const val MEDIA_DATA = "BubbleService-MEDIA_DATA"
        const val CONVERSATION_ID = "BubbleService-CONVERSATION_ID"
        const val PUSH_MESSAGE = "BubbleService-PUSH_MESSAGE"
        const val ACTION_SHOW_BUBBLE_CONTENT = "BubbleService-ACTION_SHOW_BUBBLE_CONTENT"

        private fun isEnable(context: Context): Boolean {
            return ENABLE_BUBBLE && BubbleAct.isHavePermission(context)
        }

        private fun startService(context: Context, service: Intent, start: Boolean = false) {
            if (!isEnable(context)) return
            try {
                instance?.takeIf { initialized }?.onStartCommand(service, 0, 0) ?: takeIf {
                    start
                }?.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(service)
                    } else {
                        context.startService(service)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun pushNotify(context: Context, bubbleInfo: BubbleInfo) {
            val isFore = AppUtils.isAppForeground()
            initialized
            if (!isFore || initialized){
                /*
                * nếu ứng dụng chưa đc mở start service add bubble
                * nếu ứng dụng đang đc mở, và service đang hoạt động, vẫn start để lấy số lượng tin nhắn mới
                * */
                val service = Intent(context, BubbleService::class.java)
                service.putExtra(ACTION, BubbleServiceAction.PUSH_NOTIFY)
                service.putExtra(CONVERSATION_ID, bubbleInfo.bubbleId)
                service.putExtra(PUSH_MESSAGE, bubbleInfo.message)
                startService(context, service, true)

            }
        }

        fun openBubbleConversation(context: Context, bubbleInfo: BubbleInfo) {
            val service = Intent(context, BubbleService::class.java)
            service.putExtra(ACTION, BubbleServiceAction.OPEN_BUBBLE_CONVERSATION)
            service.putExtra(CONVERSATION_ID, bubbleInfo.bubbleId)
            startService(context, service, true)
        }

        fun pushMediaMessage(context: Context, roomId: String, uris: MutableList<Uri>) {
            val service = Intent(context, BubbleService::class.java)
            service.putExtra(ACTION, BubbleServiceAction.TAKE_MEDIA_MESSAGE)
            service.putExtra(CONVERSATION_ID, roomId)
            service.putStringArrayListExtra(
                MEDIA_DATA,
                uris.map { it.toString() } as ArrayList<String>)
            startService(context, service)
        }

        fun pushFileMessage(context: Context, roomId: String, uri: String) {
            val service = Intent(context, BubbleService::class.java)
            service.putExtra(ACTION, BubbleServiceAction.TAKE_FILE_MESSAGE)
            service.putExtra(CONVERSATION_ID, roomId)
            service.putExtra(MEDIA_DATA, uri)
            startService(context, service)
        }

        fun logOut(context: Context) {
            val service = Intent(context, BubbleService::class.java)
            service.putExtra(ACTION, BubbleServiceAction.LOG_OUT)
            startService(context, service)
        }
    }

    override fun getViewModelStore(): ViewModelStore {
        return ViewModelStore()
    }
}

typealias UserTokenCallback = () -> String

