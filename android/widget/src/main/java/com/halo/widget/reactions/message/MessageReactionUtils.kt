/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.reactions.message

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.PopupWindow
import com.halo.common.utils.SizeUtils
import com.halo.widget.reactions.*

object MessageReactionUtils {
    private var currentPopupShowing: PopupWindow? = null

    fun show(popup: PopupWindow) {
        if (currentPopupShowing != null && currentPopupShowing != popup) currentPopupShowing?.dismiss()
        currentPopupShowing = popup
    }

    fun clearPopup() {
        if (currentPopupShowing != null) {
            currentPopupShowing?.dismiss()
            currentPopupShowing = null
        }
    }

    fun initPopup(context: Context): MessageReactionPopup {
        val config = ReactionsConfigBuilder(context)
            .withReactions(
                intArrayOf(
                    ReactionEmotions.LIKE.resDrawable,
                    ReactionEmotions.LOVE.resDrawable,
                    ReactionEmotions.HAHA.resDrawable,
                    ReactionEmotions.WOW.resDrawable,
                    ReactionEmotions.SORRY.resDrawable,
                    ReactionEmotions.ANGRY.resDrawable
                )
            )
            .withPopupColor(Color.WHITE)
            .withReactionSize(SizeUtils.dp2px(36.0f))
            .withVerticalMargin(SizeUtils.dp2px(4.0f))
            .withSingleTapUpCallback(true)
            .build()
        val popup = MessageReactionPopup(
            context, config
        )
        return popup
    }

    fun showReactions(
        context: Context, target: View?,
        reactionSelectedListener: ReactionSelectedListener,
        reactionClickListener: ReactionClickListener,
        messageMenuListener: MessageMenuListener
    ) {
        target?.run {
            val config = ReactionsConfigBuilder(context)
                .withReactions(
                    intArrayOf(
                        ReactionEmotions.LIKE.resDrawable,
                        ReactionEmotions.LOVE.resDrawable,
                        ReactionEmotions.HAHA.resDrawable,
                        ReactionEmotions.WOW.resDrawable,
                        ReactionEmotions.SORRY.resDrawable,
                        ReactionEmotions.ANGRY.resDrawable
                    )
                )
                .withPopupColor(Color.WHITE)
                .withReactionSize(SizeUtils.dp2px(36.0f))
                .withVerticalMargin(SizeUtils.dp2px(4.0f))
                .withSingleTapUpCallback(true)
                .withReactionButton(this)
                .build()
            val popup = MessageReactionPopup(
                context, config,
                reactionSelectedListener,
                reactionClickListener,
                messageMenuListener
            )

            popup.setPopupWithTarget(this)
        }
    }
}