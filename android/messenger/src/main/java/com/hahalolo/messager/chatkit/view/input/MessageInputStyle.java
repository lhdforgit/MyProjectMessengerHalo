/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.view.input;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.hahalolo.messager.chatkit.commons.Style;
import com.hahalolo.messenger.R;


/**
 * Created by ndngan on 10/23/2018.
 * Style for MessageInputStyle customization by xml attributes
 */
@SuppressWarnings("WeakerAccess")
class MessageInputStyle extends Style {

    private static final int DEFAULT_MAX_LINES = 5;
    private static final int DEFAULT_DELAY_TYPING_STATUS = 400;

    private boolean showAttachmentButton;

    private int attachmentButtonBackground;
    private int attachmentButtonDefaultBgColor;
    private int attachmentButtonDefaultBgPressedColor;
    private int attachmentButtonDefaultBgDisabledColor;

    private int attachmentButtonIcon;
    private int attachmentButtonDefaultIconColor;
    private int attachmentButtonDefaultIconPressedColor;
    private int attachmentButtonDefaultIconDisabledColor;

    private int attachmentButtonWidth;
    private int attachmentButtonHeight;
    private int attachmentButtonMargin;

    private int inputButtonBackground;
    private int inputButtonDefaultBgColor;
    private int inputButtonDefaultBgPressedColor;
    private int inputButtonDefaultBgDisabledColor;

    private int inputButtonIcon;
    private int inputButtonDefaultIconColor;
    private int inputButtonDefaultIconPressedColor;
    private int inputButtonDefaultIconDisabledColor;

    private int inputButtonWidth;
    private int inputButtonHeight;
    private int inputButtonMargin;

    private int inputMaxLines;
    private String inputHint;
    private String inputText;

    private int inputTextSize;
    private int inputTextColor;
    private int inputHintColor;

    private Drawable inputBackground;
    private Drawable inputCursorDrawable;

    private int inputDefaultPaddingLeft;
    private int inputDefaultPaddingRight;
    private int inputDefaultPaddingTop;
    private int inputDefaultPaddingBottom;

    private int delayTypingStatus;

    private int hahaButtonWidth;
    private int hahaButtonHeight;

    private int emojiButtonWidth;
    private int emojiButtonHeight;

    @NonNull
    static MessageInputStyle parse(Context context, AttributeSet attrs) {
        MessageInputStyle style = new MessageInputStyle(context, attrs);
        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MessageInput);

            style.showAttachmentButton = typedArray.getBoolean(R.styleable.MessageInput_showAttachmentButton, false);

            style.attachmentButtonBackground = typedArray.getResourceId(R.styleable.MessageInput_attachmentButtonBackground, -1);
            style.attachmentButtonDefaultBgColor = typedArray.getColor(R.styleable.MessageInput_attachmentButtonDefaultBgColor,
                    style.getColor(R.color.app_background));
            style.attachmentButtonDefaultBgPressedColor = typedArray.getColor(R.styleable.MessageInput_attachmentButtonDefaultBgPressedColor,
                    style.getColor(R.color.app_background));
            style.attachmentButtonDefaultBgDisabledColor = typedArray.getColor(R.styleable.MessageInput_attachmentButtonDefaultBgDisabledColor,
                    style.getColor(android.R.color.transparent));

            style.attachmentButtonIcon = typedArray.getResourceId(R.styleable.MessageInput_attachmentButtonIcon, -1);
            style.attachmentButtonDefaultIconColor = typedArray.getColor(R.styleable.MessageInput_attachmentButtonDefaultIconColor,
                    style.getColor(R.color.primary));
            style.attachmentButtonDefaultIconPressedColor = typedArray.getColor(R.styleable.MessageInput_attachmentButtonDefaultIconPressedColor,
                    style.getColor(R.color.primary));
            style.attachmentButtonDefaultIconDisabledColor = typedArray.getColor(R.styleable.MessageInput_attachmentButtonDefaultIconDisabledColor,
                    style.getColor(R.color.cornflower_blue_light_40));

            style.attachmentButtonWidth = typedArray.getDimensionPixelSize(R.styleable.MessageInput_attachmentButtonWidth, style.getDimension(R.dimen.input_button_width));
            style.attachmentButtonHeight = typedArray.getDimensionPixelSize(R.styleable.MessageInput_attachmentButtonHeight, style.getDimension(R.dimen.input_button_height));
            style.attachmentButtonMargin = typedArray.getDimensionPixelSize(R.styleable.MessageInput_attachmentButtonMargin, style.getDimension(R.dimen.input_button_margin));

            style.inputButtonBackground = typedArray.getResourceId(R.styleable.MessageInput_inputButtonBackground, -1);
            style.inputButtonDefaultBgColor = typedArray.getColor(R.styleable.MessageInput_inputButtonDefaultBgColor,
                    style.getColor(R.color.app_background));
            style.inputButtonDefaultBgPressedColor = typedArray.getColor(R.styleable.MessageInput_inputButtonDefaultBgPressedColor,
                    style.getColor(R.color.app_background));

            style.inputButtonDefaultBgDisabledColor = typedArray.getColor(R.styleable.MessageInput_inputButtonDefaultBgDisabledColor,
                    style.getColor(R.color.app_background));

            style.inputButtonIcon = typedArray.getResourceId(R.styleable.MessageInput_inputButtonIcon, -1);
            style.inputButtonDefaultIconColor = typedArray.getColor(R.styleable.MessageInput_inputButtonDefaultIconColor,
                    style.getColor(R.color.text_light));
            style.inputButtonDefaultIconPressedColor = typedArray.getColor(R.styleable.MessageInput_inputButtonDefaultIconPressedColor,
                    style.getColor(R.color.text_light));
            style.inputButtonDefaultIconDisabledColor = typedArray.getColor(R.styleable.MessageInput_inputButtonDefaultIconDisabledColor,
                    style.getColor(R.color.warm_grey));


            style.inputButtonWidth = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonWidth, style.getDimension(R.dimen.input_button_width));
            style.inputButtonHeight = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonHeight, style.getDimension(R.dimen.input_button_height));

            style.hahaButtonWidth = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonWidth,
                    style.getDimension(R.dimen.haha_button_width));
            style.hahaButtonHeight = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonHeight,
                    style.getDimension(R.dimen.haha_button_height));

            style.emojiButtonWidth = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonWidth,
                    style.getDimension(R.dimen.emoji_button_width));
            style.emojiButtonHeight = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonHeight,
                    style.getDimension(R.dimen.emoji_button_height));

            style.inputButtonMargin = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputButtonMargin, style.getDimension(R.dimen.input_button_margin));

            style.inputMaxLines = typedArray.getInt(R.styleable.MessageInput_inputMaxLines, DEFAULT_MAX_LINES);
            style.inputHint = typedArray.getString(R.styleable.MessageInput_inputHint);
            style.inputText = typedArray.getString(R.styleable.MessageInput_inputText);

            style.inputTextSize = typedArray.getDimensionPixelSize(R.styleable.MessageInput_inputTextSize, style.getDimension(R.dimen.input_text_size));
            style.inputTextColor = typedArray.getColor(R.styleable.MessageInput_inputTextColor, style.getColor(R.color.dark_grey_two));
            style.inputHintColor = typedArray.getColor(R.styleable.MessageInput_inputHintColor, style.getColor(R.color.warm_grey_three));

            style.inputBackground = typedArray.getDrawable(R.styleable.MessageInput_inputBackground);
            style.inputCursorDrawable = typedArray.getDrawable(R.styleable.MessageInput_inputCursorDrawable);

            style.delayTypingStatus = typedArray.getInt(R.styleable.MessageInput_delayTypingStatus, DEFAULT_DELAY_TYPING_STATUS);

            typedArray.recycle();

            style.inputDefaultPaddingLeft = style.getDimension(R.dimen.input_padding_left);
            style.inputDefaultPaddingRight = style.getDimension(R.dimen.input_padding_right);
            style.inputDefaultPaddingTop = style.getDimension(R.dimen.input_padding_top);
            style.inputDefaultPaddingBottom = style.getDimension(R.dimen.input_padding_bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return style;
    }

    private MessageInputStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Drawable getSelector(@ColorInt int normalColor, @ColorInt int pressedColor,
                                 @ColorInt int disabledColor, @DrawableRes int shape) {

        Drawable drawable = DrawableCompat.wrap(getVectorDrawable(shape)).mutate();
        DrawableCompat.setTintList(
                drawable,
                new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_enabled, -android.R.attr.state_pressed},
                                new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed},
                                new int[]{-android.R.attr.state_enabled}
                        },
                        new int[]{normalColor, pressedColor, disabledColor}
                ));
        return drawable;
    }

    protected boolean showAttachmentButton() {
        return showAttachmentButton;
    }

    protected Drawable getAttachmentButtonBackground() {
        if (attachmentButtonBackground == -1) {
            return getSelector(attachmentButtonDefaultBgColor, attachmentButtonDefaultBgPressedColor,
                    attachmentButtonDefaultBgDisabledColor, R.drawable.mask);
        } else {
            return getDrawable(attachmentButtonBackground);
        }
    }

    protected Drawable getAttachmentButtonIcon() {
        if (attachmentButtonIcon == -1) {
            return getSelector(attachmentButtonDefaultIconColor, attachmentButtonDefaultIconPressedColor,
                    attachmentButtonDefaultIconDisabledColor, R.drawable.ic_add_attachment);
        } else {
            return getDrawable(attachmentButtonIcon);
        }
    }

    protected int getAttachmentButtonWidth() {
        return attachmentButtonWidth;
    }

    protected int getAttachmentButtonHeight() {
        return attachmentButtonHeight;
    }

    protected int getAttachmentButtonMargin() {
        return attachmentButtonMargin;
    }

    protected Drawable getInputButtonBackground() {
        if (inputButtonBackground == -1) {
            return getSelector(inputButtonDefaultBgColor, inputButtonDefaultBgPressedColor,
                    inputButtonDefaultBgDisabledColor, R.drawable.mask);
        } else {
            return getDrawable(inputButtonBackground);
        }
    }

    protected Drawable getInputButtonIcon() {
        if (inputButtonIcon == -1) {
            return getSelector(inputButtonDefaultIconColor, inputButtonDefaultIconPressedColor,
//                    inputButtonDefaultIconDisabledColor, R.drawable.ic_send);
                    inputButtonDefaultIconDisabledColor, R.drawable.ic_mess_send);
        } else {
            return getDrawable(inputButtonIcon);
        }
    }

    protected int getInputButtonMargin() {
        return inputButtonMargin;
    }

    protected int getInputButtonWidth() {
        return inputButtonWidth;
    }

    protected int getInputButtonHeight() {
        return inputButtonHeight;
    }

    protected int getInputMaxLines() {
        return inputMaxLines;
    }

    protected String getInputHint() {
        return inputHint;
    }

    protected String getInputText() {
        return inputText;
    }

    protected int getInputTextSize() {
        return inputTextSize;
    }

    protected int getInputTextColor() {
        return inputTextColor;
    }

    protected int getInputHintColor() {
        return inputHintColor;
    }

    protected Drawable getInputBackground() {
        return inputBackground;
    }

    protected Drawable getInputCursorDrawable() {
        return inputCursorDrawable;
    }

    protected int getInputDefaultPaddingLeft() {
        return inputDefaultPaddingLeft;
    }

    protected int getInputDefaultPaddingRight() {
        return inputDefaultPaddingRight;
    }

    protected int getInputDefaultPaddingTop() {
        return inputDefaultPaddingTop;
    }

    protected int getInputDefaultPaddingBottom() {
        return inputDefaultPaddingBottom;
    }

    int getDelayTypingStatus() {
        return delayTypingStatus;
    }

    public Drawable getSendButtonIcon() {
        return getDrawable(R.drawable.ic_mess_send);
    }

    public Drawable getHahaButtonIcon() {
        return getDrawable(R.drawable.ic_chat_message_haha_icon);
    }

    public Drawable getEmojiButtonIcon() {
        return getDrawable(R.drawable.ic_chat_message_smiley);
    }

    public int getHahaButtonWidth() {
        return hahaButtonWidth;
    }

    public int getHahaButtonHeight() {
        return hahaButtonHeight;
    }

    public int getEmojiButtonWidth() {
        return emojiButtonWidth;
    }

    public int getEmojiButtonHeight() {
        return emojiButtonHeight;
    }
}
