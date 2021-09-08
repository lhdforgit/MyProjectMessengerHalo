/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.messages;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.graphics.drawable.DrawableCompat;

import com.hahalolo.messager.chatkit.commons.Style;
import com.hahalolo.messager.chatkit.holder.MessageGroupType;
import com.hahalolo.messenger.R;

@SuppressWarnings("WeakerAccess")
public class MessagesListStyle extends Style {

    public int textAutoLinkMask;

    public int incomingBubbleDrawable;
    public int incomingDefaultBubbleColor;
    public int incomingDefaultBubblePressedColor;
    public int incomingDefaultBubbleSelectedColor;

    public int incomingImageOverlayDrawable;
    public int incomingDefaultImageOverlayPressedColor;
    public int incomingDefaultImageOverlaySelectedColor;

    public int incomingDefaultBubblePaddingLeft;
    public int incomingDefaultBubblePaddingRight;
    public int incomingDefaultBubblePaddingTop;
    public int incomingDefaultBubblePaddingBottom;

    public int outcomingBubbleDrawable;
    public int outcomingDefaultBubbleColor;
    public int outcomingDefaultBubblePressedColor;
    public int outcomingDefaultBubbleSelectedColor;

    public int outcomingFileBubbleDrawable;
    public int outcomingFileBubbleColor;
    public int outcomingFileBubblePressedColor;
    public int outcomingFileBubbleSelectedColor;

    public int outcomingImageOverlayDrawable;
    public int outcomingDefaultImageOverlayPressedColor;
    public int outcomingDefaultImageOverlaySelectedColor;

    public int outcomingDefaultBubblePaddingLeft;
    public int outcomingDefaultBubblePaddingRight;
    public int outcomingDefaultBubblePaddingTop;
    public int outcomingDefaultBubblePaddingBottom;

    public int dateHeaderPadding;
    public String dateHeaderFormat;

    static MessagesListStyle parse(Context context, AttributeSet attrs) {
        MessagesListStyle style = new MessagesListStyle(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MessagesList);

        style.textAutoLinkMask = typedArray.getInt(R.styleable.MessagesList_textAutoLink, 0);

        style.incomingBubbleDrawable = typedArray.getResourceId(R.styleable.MessagesList_incomingBubbleDrawable, -1);
        style.incomingDefaultBubbleColor = typedArray.getColor(R.styleable.MessagesList_incomingDefaultBubbleColor,
                style.getColor(R.color.messenger_incoming_bubble_text));
        style.incomingDefaultBubblePressedColor = typedArray.getColor(R.styleable.MessagesList_incomingDefaultBubblePressedColor,
                style.getColor(R.color.messenger_incoming_bubble_pressed_text));
        style.incomingDefaultBubbleSelectedColor = typedArray.getColor(R.styleable.MessagesList_incomingDefaultBubbleSelectedColor,
                style.getColor(R.color.messenger_incoming_bubble_selected_text));

        style.incomingImageOverlayDrawable = typedArray.getResourceId(R.styleable.MessagesList_incomingImageOverlayDrawable, -1);
        style.incomingDefaultImageOverlayPressedColor = typedArray.getColor(R.styleable.MessagesList_incomingDefaultImageOverlayPressedColor,
                style.getColor(android.R.color.transparent));
        style.incomingDefaultImageOverlaySelectedColor = typedArray.getColor(R.styleable.MessagesList_incomingDefaultImageOverlaySelectedColor,
                style.getColor(R.color.cornflower_blue_light_40));

        style.incomingDefaultBubblePaddingLeft = typedArray.getDimensionPixelSize(R.styleable.MessagesList_incomingBubblePaddingLeft,
                style.getDimension(R.dimen.message_padding_left));
        style.incomingDefaultBubblePaddingRight = typedArray.getDimensionPixelSize(R.styleable.MessagesList_incomingBubblePaddingRight,
                style.getDimension(R.dimen.message_padding_right));
        style.incomingDefaultBubblePaddingTop = typedArray.getDimensionPixelSize(R.styleable.MessagesList_incomingBubblePaddingTop,
                style.getDimension(R.dimen.message_padding_top));
        style.incomingDefaultBubblePaddingBottom = typedArray.getDimensionPixelSize(R.styleable.MessagesList_incomingBubblePaddingBottom,
                style.getDimension(R.dimen.message_padding_bottom));

        style.outcomingBubbleDrawable = typedArray.getResourceId(R.styleable.MessagesList_outcomingBubbleDrawable, -1);
        style.outcomingDefaultBubbleColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultBubbleColor,
                style.getColor(R.color.messenger_outcoming_bubble_text));
        style.outcomingDefaultBubblePressedColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultBubblePressedColor,
                style.getColor(R.color.messenger_outcoming_bubble_pressed_text));
        style.outcomingDefaultBubbleSelectedColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultBubbleSelectedColor,
                style.getColor(R.color.messenger_outcoming_bubble_selected_text));

        style.outcomingFileBubbleDrawable = typedArray.getResourceId(R.styleable.MessagesList_outcomingBubbleDrawable, -1);
        style.outcomingFileBubbleColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultBubbleColor,
                style.getColor(R.color.cornflower_blue_two_file));
        style.outcomingFileBubblePressedColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultBubblePressedColor,
                style.getColor(R.color.cornflower_blue_two_file));
        style.outcomingFileBubbleSelectedColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultBubbleSelectedColor,
                style.getColor(R.color.cornflower_blue_two_24_file));


        style.outcomingImageOverlayDrawable = typedArray.getResourceId(R.styleable.MessagesList_outcomingImageOverlayDrawable, -1);
        style.outcomingDefaultImageOverlayPressedColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultImageOverlayPressedColor,
                style.getColor(android.R.color.transparent));
        style.outcomingDefaultImageOverlaySelectedColor = typedArray.getColor(R.styleable.MessagesList_outcomingDefaultImageOverlaySelectedColor,
                style.getColor(R.color.cornflower_blue_light_40));

        style.outcomingDefaultBubblePaddingLeft = typedArray.getDimensionPixelSize(R.styleable.MessagesList_outcomingBubblePaddingLeft,
                style.getDimension(R.dimen.message_padding_left));
        style.outcomingDefaultBubblePaddingRight = typedArray.getDimensionPixelSize(R.styleable.MessagesList_outcomingBubblePaddingRight,
                style.getDimension(R.dimen.message_padding_right));
        style.outcomingDefaultBubblePaddingTop = typedArray.getDimensionPixelSize(R.styleable.MessagesList_outcomingBubblePaddingTop,
                style.getDimension(R.dimen.message_padding_top));
        style.outcomingDefaultBubblePaddingBottom = typedArray.getDimensionPixelSize(R.styleable.MessagesList_outcomingBubblePaddingBottom,
                style.getDimension(R.dimen.message_padding_bottom));

        style.dateHeaderPadding = typedArray.getDimensionPixelSize(R.styleable.MessagesList_dateHeaderPadding,
                style.getDimension(R.dimen.message_date_header_padding));
        style.dateHeaderFormat = typedArray.getString(R.styleable.MessagesList_dateHeaderFormat);
        typedArray.recycle();

        return style;
    }

    private MessagesListStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Drawable getMessageSelector(@ColorInt int normalColor, @ColorInt int selectedColor,
                                        @ColorInt int pressedColor, @DrawableRes int shape) {

        Drawable drawable = DrawableCompat.wrap(getVectorDrawable(shape)).mutate();
        DrawableCompat.setTintList(
                drawable,
                new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_selected},
                                new int[]{android.R.attr.state_pressed},
                                new int[]{-android.R.attr.state_pressed,
                                        -android.R.attr.state_selected}
                        },
                        new int[]{selectedColor, pressedColor, normalColor}
                ));
        return drawable;
    }

    public int getIncomingDefaultBubblePaddingLeft() {
        return incomingDefaultBubblePaddingLeft;
    }

    public int getIncomingDefaultBubblePaddingRight() {
        return incomingDefaultBubblePaddingRight;
    }

    public int getIncomingDefaultBubblePaddingTop() {
        return incomingDefaultBubblePaddingTop;
    }

    public int getIncomingDefaultBubblePaddingBottom() {
        return incomingDefaultBubblePaddingBottom;
    }

    public Drawable getOutcomingBubbleDrawable() {
        if (outcomingBubbleDrawable == -1) {
            return getMessageSelector(outcomingDefaultBubbleColor, outcomingDefaultBubbleSelectedColor,
                    outcomingDefaultBubblePressedColor, R.drawable.shape_outcoming_message);
        } else {
            return getDrawable(outcomingBubbleDrawable);
        }
    }

    public Drawable getOutcomingBubbleDrawable(MessageGroupType messageGroupType) {
        if (outcomingBubbleDrawable == -1) {
            return getMessageSelector(outcomingDefaultBubbleColor, outcomingDefaultBubbleSelectedColor,
                    outcomingDefaultBubblePressedColor, getOutcommingSource(messageGroupType));
        } else {
            return getDrawable(outcomingBubbleDrawable);
        }
    }

    public Drawable getOutcomingImageOverlayDrawable() {
        if (outcomingImageOverlayDrawable == -1) {
            return getMessageSelector(Color.TRANSPARENT, outcomingDefaultImageOverlaySelectedColor,
                    outcomingDefaultImageOverlayPressedColor, R.drawable.shape_outcoming_message);
        } else {
            return getDrawable(outcomingImageOverlayDrawable);
        }
    }

    public Drawable getIncomingBubbleDrawable() {
        if (incomingBubbleDrawable == -1) {
            return getMessageSelector(incomingDefaultBubbleColor, incomingDefaultBubbleSelectedColor,
                    incomingDefaultBubblePressedColor, R.drawable.shape_incoming_message);
        } else {
            return getDrawable(incomingBubbleDrawable);
        }
    }

    public Drawable getIncomingBubbleDrawable(MessageGroupType messageGroupType) {
        if (incomingBubbleDrawable == -1) {
            return getMessageSelector(incomingDefaultBubbleColor,
                    incomingDefaultBubbleSelectedColor,
                    incomingDefaultBubblePressedColor,
                    getIncommingSource(messageGroupType));
        } else {
            return getDrawable(incomingBubbleDrawable);
        }
    }

    public Drawable getIncomingImageOverlayDrawable() {
        if (incomingImageOverlayDrawable == -1) {
            return getMessageSelector(Color.TRANSPARENT, incomingDefaultImageOverlaySelectedColor,
                    incomingDefaultImageOverlayPressedColor, R.drawable.shape_incoming_message);
        } else {
            return getDrawable(incomingImageOverlayDrawable);
        }
    }

    public int getIncomingImageOverlayResource(MessageGroupType messageGroupType) {
        if (messageGroupType != null) {
            switch (messageGroupType) {
                case LAST:
                    return R.drawable.over_incoming_message_boder_last;
                case ONLY:
                    return R.drawable.over_incoming_message_boder;
                case START:
                    return R.drawable.over_incoming_message_boder_start;
                case MEDIUM:
                    return R.drawable.over_incoming_message_boder_in;
            }
        }
        return R.drawable.over_incoming_message_boder;
    }

    public int getOutcomingImageOverlayResource(MessageGroupType messageGroupType) {
        if (messageGroupType != null) {
            switch (messageGroupType) {
                case LAST:
                    return R.drawable.over_outcoming_message_boder_last;
                case ONLY:
                    return R.drawable.over_outcoming_message_boder;
                case START:
                    return R.drawable.over_outcoming_message_boder_start;
                case MEDIUM:
                    return R.drawable.over_outcoming_message_boder_in;
            }
        }
        return R.drawable.over_outcoming_message_boder;
    }

    public int getInComingStickerOverlayResource(MessageGroupType messageGroupType) {
        if (messageGroupType != null) {
            switch (messageGroupType) {
                case LAST:
                    return R.drawable.over_incoming_message_last;
                case ONLY:
                    return R.drawable.over_incoming_message;
                case START:
                    return R.drawable.over_incoming_message_start;
                case MEDIUM:
                    return R.drawable.over_incoming_message_in;
            }
        }
        return R.drawable.over_outcoming_message;
    }

    public int getOutComingStickerOverlayResource(MessageGroupType messageGroupType) {
        if (messageGroupType != null) {
            switch (messageGroupType) {
                case LAST:
                    return R.drawable.over_outcoming_message_last;
                case ONLY:
                    return R.drawable.over_outcoming_message;
                case START:
                    return R.drawable.over_outcoming_message_start;
                case MEDIUM:
                    return R.drawable.over_outcoming_message_in;
            }
        }
        return R.drawable.over_outcoming_message;
    }

    private int getIncommingSource(MessageGroupType messageGroupType) {
        if (messageGroupType != null) {
            switch (messageGroupType) {
                case START:
                    return R.drawable.shape_incoming_message_start;
                case MEDIUM:
                    return R.drawable.shape_incoming_message_in;
                case LAST:
                    return R.drawable.shape_incoming_message_last;
                case ONLY:
                    return R.drawable.shape_incoming_message;
            }
        }
        return R.drawable.shape_incoming_message;
    }

    private int getOutcommingSource(MessageGroupType messageGroupType) {
        if (messageGroupType != null) {
            switch (messageGroupType) {
                case START:
                    return R.drawable.shape_outcoming_message_start;
                case MEDIUM:
                    return R.drawable.shape_outcoming_message_in;
                case LAST:
                    return R.drawable.shape_outcoming_message_last;
                case ONLY:
                    return R.drawable.shape_outcoming_message;
            }
        }
        return R.drawable.shape_outcoming_message;
    }


}
