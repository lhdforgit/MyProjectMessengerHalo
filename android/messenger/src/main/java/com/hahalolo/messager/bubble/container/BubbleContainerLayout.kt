package com.hahalolo.messager.bubble.container

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Observer
import com.halo.data.room.entity.MemberEntity

class BubbleContainerLayout : AbsBubbleContainerLayout {

    constructor(context: Context) : super(context){
    }
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs){
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun initializeBinding() {

    }

    override fun initializeLayout() {

    }

    // thêm và show bubble
    fun openConversationId(conversationId: String){
//        viewModel?.addWaitForShow(conversationId)
    }

    //Thêm bubble Head
    fun addConversationId(conversationId: String, pushMessage:String?=null ) {
//        viewModel?.updateBubbleId(conversationId, pushMessage)
    }

    fun navigateSetting(roomId: String) {
        contentConversation.navigateSetting(roomId)
    }

    fun navigateHome(){
        contentConversation.navigateHome()
    }

    fun navigateMember(roomId: String, tag : Int?){
        contentConversation.navigateMember(roomId, tag)
    }

    fun navigateAddMember(roomId: String, tag : Int){
        contentConversation.navigateAddMember(roomId, tag)
    }

    fun navigateCreateGroupWith(member: MemberEntity, tag : Int){
        contentConversation.navigateCreateGroupWith(member, tag)
    }

    fun navigateMemberNickName(roomId: String, tag : Int){
        contentConversation.navigateMemberNickName(roomId, tag)
    }

    fun navigateUpdateOwnerMember(roomId: String, tag : Int){
        contentConversation.navigateUpdateOwnerMember(roomId, tag)
    }

    fun navigateGalleryShared(roomId: String, tabIndex : Int){
        contentConversation.navigateGalleryShared(roomId, tabIndex)
    }

    fun takeMediaMessage(roomId: String?, medias: MutableList<String>?){
//        roomId?.takeIf { it.isNotEmpty() && !medias.isNullOrEmpty()}?.run{
//            val showed = showBubbleContent(this )
//            if (showed) contentConversation.takeMediaMessage(this, medias)
//            else {
//                addConversationId(roomId)
//                viewModel?.addWaitSendMedia(roomId, medias)
//            }
//        }
    }

    fun takeFileMessage(roomId: String?, path : String){
        roomId?.takeIf { it.isNotEmpty() && !path.isEmpty()}?.run{
            val showed = showBubbleContent(this )
            if (showed) contentConversation.takeFileMessage(this, path)
        }
    }

    override fun invalidateLayout() {
        super.invalidateLayout()

    }
}