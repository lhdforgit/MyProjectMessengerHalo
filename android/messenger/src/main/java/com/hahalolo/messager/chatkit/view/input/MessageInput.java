/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.view.input;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.halo.data.room.entity.MemberEntity;
import com.halo.data.room.entity.MessageEntity;
import com.hahalolo.messenger.R;

import java.lang.reflect.Field;
import java.util.List;

import static com.halo.data.HalomeConfig.MAX_INPUT_MESSENGER;


/**
 * Created by ndngan on 10/23/2018.
 * Component for input outcoming messages
 */
@SuppressWarnings({"WeakerAccess"})
public class MessageInput extends RelativeLayout
        implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    //    protected MessageEdittext messageInput;
    protected MentionEditText messageInput;
    protected ImageButton messageSendButton;
    protected ImageButton messageHahaButton;
    protected ImageButton messageEmojiButton;

    protected ImageButton attachmentButton;
    protected Space sendButtonSpace, attachmentButtonSpace;

    private CharSequence input;
    private InputListener inputListener;
    private AttachmentsListener attachmentsListener;
    private boolean isTyping;
    private TypingListener typingListener;
    private int delayTypingStatusMillis;

    private boolean sendEnable;

    public void setSendEnable(boolean sendEnable) {
        this.sendEnable = sendEnable;
        Editable inputText = messageInput.getText();
        String content = inputText != null ? inputText.toString() : "";

        toggleFocusEditAction(!content.isEmpty());
        if (inputListener != null) {
            inputListener.onTectChanged(content, sendEnable);
        }
    }

    public void setMessageEdit(@NonNull MessageEntity message) {
        messageInput.setMessageEdit(message);
    }

    private void toggleFocusEditAction(boolean focus) {
        messageSendButton.setVisibility((focus || sendEnable) ? VISIBLE : GONE);
        messageEmojiButton.setVisibility((focus || sendEnable) ? GONE : VISIBLE);
        messageHahaButton.setVisibility((focus || sendEnable) ? GONE : VISIBLE);
    }

    private final Runnable typingTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTyping) {
                isTyping = false;
                if (typingListener != null) typingListener.onStopTyping();
            }
        }
    };

    private boolean lastFocus;

    public MessageInput(Context context) {
        super(context);
        init(context);
    }

    public MessageInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MessageInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Sets callback for 'submit' button.
     *
     * @param inputListener input callback
     */
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    /**
     * Sets callback for 'add' button.
     *
     * @param attachmentsListener input callback
     */
    public void setAttachmentsListener(AttachmentsListener attachmentsListener) {
        this.attachmentsListener = attachmentsListener;
    }

    /**
     * Returns EditText for messages input
     *
     * @return EditText
     */
    public EditText getInputEditText() {
        return messageInput;
    }

    /**
     * Returns `submit` button
     *
     * @return ImageButton
     */
    public ImageButton getButton() {
        return messageSendButton;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.messageSendButton) {
            boolean isSubmitted = onSubmit();
            if (isSubmitted) {
                messageInput.clearText();
            }
            removeCallbacks(typingTimerRunnable);
            post(typingTimerRunnable);
        } else if (id == R.id.attachmentButton) {
            onAddAttachments();
        } else if (id == R.id.messageHahaButton) {
            if (inputListener != null) {
                inputListener.onHahaClick();
            }
        } else if (id == R.id.messageEmojiButton) {
            if (inputListener != null) {
                inputListener.onEmojiClick();
            }
        }

    }

    /**
     * This method is called to notify you that, within s,
     * the count characters beginning at start have just replaced old text that had length before
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        input = s;
        if (inputListener != null) {
            if (s.length() > MAX_INPUT_MESSENGER) {
                messageInput.setText(s.subSequence(0, MAX_INPUT_MESSENGER));
                messageInput.setSelection(s.subSequence(0, MAX_INPUT_MESSENGER).length());
                inputListener.onTextLimited();
            } else {
                inputListener.onTectChanged(input.toString(), sendEnable);
            }
        }
        toggleFocusEditAction((messageInput.getText() != null && !messageInput.getText().toString().isEmpty()));

        messageSendButton.setEnabled(input.length() > 0 && input != null && !input.toString().trim().isEmpty());

        if (s.length() > 0) {
            if (!isTyping) {
                isTyping = true;
                if (typingListener != null) typingListener.onStartTyping();
            }
            removeCallbacks(typingTimerRunnable);
            postDelayed(typingTimerRunnable, delayTypingStatusMillis);

        }
    }

    private void checkLink() {
//        messageInput.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.primary));
//        messageInput.setMovementMethod(null);
//        messageInput.setAutoLinkMask(0);
//        Linkify.addLinks(messageInput, Linkify.WEB_URLS);
//        if(curentComment!=null){
//            if (curentComment.getSubs()!=null&&!curentComment.getSubs().isEmpty()){
//                for (SubModel subModel: curentComment.getSubs()){
//                    if (subModel.getType()==SubType.USER){
//                        Pattern privacyPolicyMatcher = Pattern.compile(subModel.getName());
//                        Linkify.addLinks(edtComment, privacyPolicyMatcher,"");
//                    }
//                }
//            }
//        }
    }

    /**
     * This method is called to notify you that, within s,
     * the count characters beginning at start are about to be replaced by new text with length after.
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //do nothing
    }

    /**
     * This method is called to notify you that, somewhere within s, the text has been changed.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        //do nothing
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (lastFocus && !hasFocus && typingListener != null) {
            typingListener.onStopTyping();
        }
        lastFocus = hasFocus;
        if (v.getId() == R.id.messageInput) {

            if (hasFocus) {
                messageInput.requestFocus();
                if (inputListener != null) {
                    inputListener.onFocusInput();
                }
            } else {
                messageInput.clearFocus();
            }
        }
    }

    @Override
    public void clearFocus() {
        super.clearFocus();

    }

    private boolean onSubmit() {
        String submitText = MentionUtils.INSTANCE.getContentSubmit(messageInput.getTextBase(),
                messageInput.mentionModel());
        return inputListener != null && inputListener.onSubmit(submitText, messageInput.mentionModel() );
    }

    private void onAddAttachments() {
        if (attachmentsListener != null) attachmentsListener.onAddAttachments();
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
        MessageInputStyle style = MessageInputStyle.parse(context, attrs);

        this.messageInput.setMaxLines(style.getInputMaxLines());
        this.messageInput.setHint(style.getInputHint());
        this.messageInput.setInitText(style.getInputText());
        this.messageInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getInputTextSize());
        this.messageInput.setTextColor(style.getInputTextColor());
        this.messageInput.setHintTextColor(style.getInputHintColor());
        checkLink();
        ViewCompat.setBackground(this.messageInput, style.getInputBackground());
        setCursor(style.getInputCursorDrawable());

        this.attachmentButton.setVisibility(style.showAttachmentButton() ? VISIBLE : GONE);
        this.attachmentButton.setImageDrawable(style.getAttachmentButtonIcon());
        this.attachmentButton.getLayoutParams().width = style.getAttachmentButtonWidth();
        this.attachmentButton.getLayoutParams().height = style.getAttachmentButtonHeight();
        ViewCompat.setBackground(this.attachmentButton, style.getAttachmentButtonBackground());

        this.attachmentButtonSpace.setVisibility(style.showAttachmentButton() ? VISIBLE : GONE);
        this.attachmentButtonSpace.getLayoutParams().width = style.getAttachmentButtonMargin();

        this.messageSendButton.setImageDrawable(style.getSendButtonIcon());
        this.messageSendButton.getLayoutParams().width = style.getInputButtonWidth();
        this.messageSendButton.getLayoutParams().height = style.getInputButtonHeight();
        ViewCompat.setBackground(messageSendButton, null);

        this.messageHahaButton.setImageDrawable(style.getHahaButtonIcon());
        this.messageHahaButton.getLayoutParams().width = style.getHahaButtonWidth();
        this.messageHahaButton.getLayoutParams().height = style.getHahaButtonHeight();
        ViewCompat.setBackground(messageHahaButton, null);

        this.messageEmojiButton.setImageDrawable(style.getEmojiButtonIcon());
        this.messageEmojiButton.getLayoutParams().width = style.getEmojiButtonWidth();
        this.messageEmojiButton.getLayoutParams().height = style.getEmojiButtonHeight();
        ViewCompat.setBackground(messageEmojiButton, null);

        this.sendButtonSpace.getLayoutParams().width = style.getInputButtonMargin();

        if (getPaddingLeft() == 0
                && getPaddingRight() == 0
                && getPaddingTop() == 0
                && getPaddingBottom() == 0) {
            setPadding(
                    style.getInputDefaultPaddingLeft(),
                    style.getInputDefaultPaddingTop(),
                    style.getInputDefaultPaddingRight(),
                    style.getInputDefaultPaddingBottom()
            );
        }
        this.delayTypingStatusMillis = style.getDelayTypingStatus();
    }

    public void changeDrawableEmoji(int idDrawable) {
        if (messageEmojiButton != null) {
            messageEmojiButton.setImageResource(idDrawable);
        }
    }


    private void init(Context context) {
        inflate(context, R.layout.view_message_input, this);

        messageInput = findViewById(R.id.messageInput);

        messageSendButton = findViewById(R.id.messageSendButton);
        attachmentButton = findViewById(R.id.attachmentButton);
        sendButtonSpace = findViewById(R.id.sendButtonSpace);
        attachmentButtonSpace = findViewById(R.id.attachmentButtonSpace);

        messageHahaButton = findViewById(R.id.messageHahaButton);

        messageEmojiButton = findViewById(R.id.messageEmojiButton);

        messageHahaButton.setOnClickListener(this);
        messageEmojiButton.setOnClickListener(this);
        messageSendButton.setOnClickListener(this);
        attachmentButton.setOnClickListener(this);
        messageInput.addTextChangedListener(this);
        messageInput.setInitText("");
        messageInput.setOnFocusChangeListener(this);
        messageInput.setOnClickListener(v -> {
            if (inputListener != null) {
                inputListener.onFocusInput();
            }
        });
    }

    private void setCursor(Drawable drawable) {
        if (drawable == null) return;

        try {
            final Field drawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
            drawableResField.setAccessible(true);

            final Object drawableFieldOwner;
            final Class<?> drawableFieldClass;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                drawableFieldOwner = this.messageInput;
                drawableFieldClass = TextView.class;
            } else {
                final Field editorField = TextView.class.getDeclaredField("mEditor");
                editorField.setAccessible(true);
                drawableFieldOwner = editorField.get(this.messageInput);
                drawableFieldClass = drawableFieldOwner != null ? drawableFieldOwner.getClass() : null;
            }
            if (drawableFieldClass != null) {
                final Field drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
                drawableField.setAccessible(true);
                drawableField.set(drawableFieldOwner, new Drawable[]{drawable, drawable});
            }
        } catch (Exception ignored) {
        }
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }

    public void setMentionListener(MentionEditText.MentionListener tagingListener) {
        if (this.messageInput!= null){
            this.messageInput.setMentionListener(tagingListener);
        }
    }

    public void addMentions(MemberEntity member) {
        if (this.messageInput!= null){
            this.messageInput.addMentions(member);
        }
    }

    public void clearMessageInput() {
        messageInput.clearText();
    }

    /**
     * Interface definition for a callback to be invoked when user pressed 'submit' button
     */
    public interface InputListener {

        /**
         * Fires when user presses 'send' button.
         *
         * @param input input entered by user
         * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
         */
        boolean onSubmit(CharSequence input, List<MentionSpannableEntity> mentionTables);

        void onTectChanged(String text, boolean enableSend);

        void onHahaClick();

        void onEmojiClick();

        void onFocusInput();

        void onTextLimited();

    }

    /**
     * Interface definition for a callback to be invoked when user presses 'add' button
     */
    public interface AttachmentsListener {

        /**
         * Fires when user presses 'add' button.
         */
        void onAddAttachments();
    }

    /**
     * Interface definition for a callback to be invoked when user typing
     */
    public interface TypingListener {

        /**
         * Fires when user presses start typing
         */
        void onStartTyping();

        /**
         * Fires when user presses stop typing
         */
        void onStopTyping();

    }
}
