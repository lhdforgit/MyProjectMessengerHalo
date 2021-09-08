package com.hahalolo.incognito.presentation.conversation.view.sticker

import android.widget.EditText
import androidx.lifecycle.Observer
import com.giphy.sdk.core.models.Media
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable

interface IncognitoMessageStickerListener {

    fun onSelectType(type: IncognitoMessageStickerType)

    fun observerStickerType(observer: Observer<IncognitoMessageStickerType>)

    fun observerSticker(packId: String, observer: Observer<MutableList<StickerTable>>)
    fun observerStickerPacks(observer: Observer<MutableList<StickerPackTable>>)
    fun observerEmojiPacks(observer: Observer<String>)
    fun observerFavoritePacks(observer: Observer<MutableList<StickerTable>>)

    fun onClickFavorite(data: StickerTable)
    fun onClickSticker(stickerTable: StickerTable)
    fun onClickGif(media: Media)
    fun onClickEmoji()

    fun closeGif()
    fun focusEditText(): EditText
}