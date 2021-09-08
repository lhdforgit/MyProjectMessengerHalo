package com.hahalolo.incognito.presentation.conversation.view.mention;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;

import com.google.common.collect.Iterators;
import com.hahalolo.incognito.R;
import com.halo.data.common.utils.Strings;
import com.halo.widget.emoji.EmojiEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.hahalolo.incognito.presentation.conversation.view.mention.IncognitoMentionUtils.REGEX_TAG;

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
public class IncognitoMentionEditText extends EmojiEditText {
    public static final String SPERATOR_TAG = "@";
    private static final String SPERATOR_NOT_TAG = "@[^\\w]";

    private int indexEnd = -1;
    private int indexStart = -1;

    @NonNull
    private final List<IncognitoMentionEntity> listTag = new ArrayList<>();

    private void removeTagEntity(String key) {
        Iterators.removeIf(listTag.iterator(), input -> input != null && TextUtils.equals(input.getKey(), key));
    }

    public void addMentions(String id, String name) {
        try {
            if (getText() != null
                    && indexEnd <= getText().length()
                    && indexEnd > indexStart
                    && indexStart >= 0) {
                String query = getBaseText(indexStart + 1, indexEnd);
                IncognitoMentionEntity spanEntity = addNewTag(name, id, name );
                String resultStart = getBaseText(0, indexStart);
                String resultEnd = getBaseText(indexEnd, getText().length());
                indextSelect = resultStart.length() + spanEntity.getContent().length() + 1;
                formatText(resultStart + spanEntity.getText() + " " + resultEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IncognitoMentionEntity addNewTag(String content, String id, String name) {
        IncognitoMentionEntity spanEntity;
        spanEntity = new IncognitoMentionEntity(content, id, name);
        listTag.add(spanEntity);
        return spanEntity;
    }

    public IncognitoMentionEntity getTagEntity(String key) {
        int index = Iterators.indexOf(listTag.iterator(), input -> input != null && TextUtils.equals(input.getKey(), key));
        if (index >= 0) {
            return new IncognitoMentionEntity(listTag.get(index));
        }
        return null;
    }

    private int indextSelect = -1;
    private String lastString;

    public IncognitoMentionEditText(Context context) {
        super(context);
        initView();
    }

    public IncognitoMentionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String thisString = s == null ? "" : s.toString();
                if (mentionListener != null) {
                    mentionListener.onTextChange(thisString);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String thisString = s != null ? s.toString() : "";
                if (!TextUtils.equals(thisString, lastString)) {
                    lastString = thisString;
                    if (checkRemoveTag()) return;
                }
                checkQueryTagFriend(thisString);
            }
        };
        addTextChangedListener(textWatcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setCustomInsertionActionModeCallback(callback);
        }
        setCustomSelectionActionModeCallback(callback);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    String contentCopy;

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.copy && mentionListener != null && mentionListener.enablePopupMenu()) {
            contentCopy = getTextCopy();
            if (!TextUtils.isEmpty(contentCopy)) {
                copyText(contentCopy);
            }
            return true;
        }
        return super.onTextContextMenuItem(id);
    }

    ActionMode.Callback callback = new ActionMode.Callback() {
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == android.R.id.copy) {
                contentCopy = getTextCopy();
                return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (!TextUtils.isEmpty(contentCopy)) {
                copyText(contentCopy);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            boolean enable = mentionListener!=null && mentionListener.enablePopupMenu();
            if (enable)menu.clear();
            return true;
        }
    };

    private String getTextCopy() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        String textBase = getText() != null ? getText().toString() : "";
        if (start >= 0
                && start <= end
                && end <= textBase.length()) {
            return textBase.substring(start, end);
        }
        return "";
    }

    private void copyText(@NonNull String s) {
        try {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("label", s);
                clipboard.setPrimaryClip(clip);
                contentCopy = "";
                Toast.makeText(getContext(), R.string.taging_coppy_complete, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    private void formatText(@NonNull String content) {
        List<IncognitoMentionEntity> stringList = sublistTag(content);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int i = 0; i < stringList.size(); i++) {
            IncognitoMentionEntity spanEntity = stringList.get(i);
            if (spanEntity.isTag()
//                    && spanEntity.getEnd() <= sb.length()
            ) {
                IncognitoMentionSpaned tagingSpaned = new IncognitoMentionSpaned(spanEntity);
                sb.append(tagingSpaned);
            } else {
                sb.append(spanEntity.getContent());
            }
        }
        lastString = sb.toString();
        super.setText(sb);
        if (indextSelect >= 0 && indextSelect < sb.length()) {
            setSelection(indextSelect);
            indextSelect = -1;
        } else {
            setSelection(sb.length());
        }
    }

    @Override
    public void setSelection(int index) {
        try {
            // Nội dung tin nhắn input có giới hạn số lượng kí tự
            super.setSelection(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void setMessageEdit(@NonNull MessageEntity message) {
//        indextSelect = -1;
//        listTag.clear();
//        formatText(ViewTransform.INSTANCE.buildTextMentionInput(message, listTag));
//    }

//    public void setQuoteMessage(@NonNull MessageEntity message) {
//        indextSelect = -1;
//        listTag.clear();
//        if (message.getMember() != null) {
//            MentionEntity mentionEntity = new MentionEntity(message.getMember().getMentionName(""), message.getMember());
//            listTag.add(mentionEntity);
//            formatText(String.format(MentionUtils.REGEX_FORMAT, mentionEntity.getKey()) + " ");
//        }
//    }

    public void setInitText(String inputText) {
        formatText(inputText == null ? "" : inputText);
    }

    public void clearText() {
        formatText("");
    }

    @NonNull
    public String getTextBase() {
        return getText() == null ? "" : getBaseText(0, getText().length());
    }

    @NonNull
    public String getTextContent() {
        return getText() == null ? "" : getText().toString();
    }

    private void checkRemoveTagEntity(IncognitoMentionEntity spanEntity) {
        //remove if text base not have key taging
        if (!getTextBase().contains(spanEntity.getKey())) {
            removeTagEntity(spanEntity.getKey());
        }
    }

    private boolean checkRemoveTag() {
        Editable editable = getText();
        if (editable != null) {
            String content = editable.toString();
            IncognitoMentionSpaned.MentionColorSpan[] spans = editable.getSpans(0, editable.length(), IncognitoMentionSpaned.MentionColorSpan.class);
            if (spans != null && spans.length > 0) {
                HashSet<IncognitoMentionSpaned.MentionColorSpan> hashSet = new HashSet<>(Arrays.asList(spans));
                ArrayList<IncognitoMentionSpaned.MentionColorSpan> list = new ArrayList<>(hashSet);
                for (IncognitoMentionSpaned.MentionColorSpan colorSpan : list) {
                    int start = editable.getSpanStart(colorSpan);
                    int end = editable.getSpanEnd(colorSpan);
                    if (start < end) {
                        String text = content.substring(start, end);
                        if (colorSpan.getSpanEntity() != null
                                && !TextUtils.equals(text, colorSpan.getSpanEntity().getContent())) {
                            checkRemoveTagEntity(colorSpan.getSpanEntity());
                            indextSelect = getSelectionEnd();
                            formatText(getBaseText(0, start) + text + getBaseText(end, editable.length()));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @NonNull
    private List<IncognitoMentionEntity> sublistTag(String content) {
        List<IncognitoMentionEntity> stringList = new ArrayList<>();
        String regex = IncognitoMentionUtils.getRegex(listTag);
        int s = 0;
        if (regex != null && !TextUtils.isEmpty(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String group = matcher.group();
                if (s != matcher.start()) {
                    stringList.add(new IncognitoMentionEntity(content.substring(s, matcher.start()), s, matcher.start()));
                }
                IncognitoMentionEntity spanEntity = getTagEntity(group.replace(REGEX_TAG, ""));
                if (spanEntity != null) {
                    spanEntity.setLocation(matcher.start(), matcher.end());
                    stringList.add(spanEntity);
                } else {
                    stringList.add(new IncognitoMentionEntity(group, matcher.start(), matcher.end()));
                }
                s = matcher.end();
            }
        }
        if (content != null && s != content.length()) {
            stringList.add(new IncognitoMentionEntity(content.substring(s), s, content.length()));
        }
        return stringList;
    }

    private void checkQueryTagFriend(String text) {
        if (mentionListener != null) {
            int selectCurrent = getSelectionEnd();
            if (selectCurrent >= 0
                    && selectCurrent <= text.length()
                    && !TextUtils.isEmpty(text.substring(0, selectCurrent))
                    && text.substring(0, selectCurrent).contains(SPERATOR_TAG)) {
                indexStart = text.substring(0, selectCurrent).lastIndexOf(SPERATOR_TAG);
                indexEnd = selectCurrent;
                if (indexEnd > indexStart && indexStart >= 0) {
                    String textQuery = text.substring(indexStart, indexEnd);
                    Pattern pattern = Pattern.compile(SPERATOR_NOT_TAG);
                    Matcher matcher = pattern.matcher(textQuery);
                    mentionListener.onQueryTagFriend(matcher.find() ? null : textQuery.substring(1));
                } else {
                    mentionListener.onQueryTagFriend(null);
                }
            } else {
                resetIndex();
                mentionListener.onQueryTagFriend(null);
            }
        }
    }

    private String getBaseText(int start, int end) {
        try {
            Editable editable = getText();
            String result = "";
            if (editable != null) {
                String content = editable.toString();
                IncognitoMentionSpaned.MentionColorSpan[] spans = editable.getSpans(0, editable.length(), IncognitoMentionSpaned.MentionColorSpan.class);
                if (spans != null && spans.length > 0) {
                    HashSet<IncognitoMentionSpaned.MentionColorSpan> hashSet = new HashSet<>(Arrays.asList(spans));
                    ArrayList<IncognitoMentionSpaned.MentionColorSpan> list = new ArrayList<>(hashSet);
                    Collections.sort(list, (tagingColorSpan, t1) -> editable.getSpanStart(tagingColorSpan) - editable.getSpanStart(t1));
                    StringBuilder resultBuilder = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        if (start < end) {
                            IncognitoMentionSpaned.MentionColorSpan curentSpan = list.get(i);
                            int spanStart = editable.getSpanStart(curentSpan);
                            int spanEnd = editable.getSpanEnd(curentSpan);

                            if (start <= spanStart
                                    && spanEnd <= end) {
                                // chua span trong khong start-end
                                resultBuilder.append(content.substring(start, spanStart));
                                resultBuilder.append(curentSpan.getSpanEntity().getText());
                                start = spanEnd;
                            } else if (start < (Math.min(end, spanEnd))) {
                                resultBuilder.append(content.substring(start, Math.min(end, spanEnd)));
                                start = Math.min(end, spanEnd);
                            }
                        }
                    }
                    result = resultBuilder.toString();
                    result = result + content.substring(start, end);
                } else if (start < end) {
                    return content.substring(start, end);
                }
            }
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    private void resetIndex() {
        indexStart = -1;
        indexEnd = -1;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);

        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String[]{"image/jpg", "image/png", "image/jpeg", "image/gif"});
        InputConnectionCompat.OnCommitContentListener callback = (inputContentInfo, flags, opts) -> {
            // read and display inputContentInfo asynchronously
            if (BuildCompat.isAtLeastNMR1() && (flags &
                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                try {
                    inputContentInfo.requestPermission();
                } catch (Exception e) {
                    return false; // return false if failed
                }
            }
            // read and display inputContentInfo asynchronously.
            // call inputContentInfo.releasePermission() as needed.
            Uri content = inputContentInfo.getContentUri();
            String contentQuery = content.getQueryParameter("fileName");
            Uri linkUri = inputContentInfo.getLinkUri();
            if (mentionListener != null && linkUri != null && !linkUri.toString().isEmpty()) {
                mentionListener.onInsertStickerFromKeybroad(contentQuery != null
                                && contentQuery.contains("sticker"),
                        linkUri);
            }
            return true;  // return true if succeeded
        };
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }

    MentionListener mentionListener;

    public void setMentionListener(MentionListener mentionListener) {
        this.mentionListener = mentionListener;
    }

    public List<IncognitoMentionEntity> getMentionModel() {
        try {
            List<IncognitoMentionEntity> result = new ArrayList<>();
            Editable editable = getText();
            if (editable != null) {
                String content = editable.toString();
                IncognitoMentionSpaned.MentionColorSpan[] spans = editable.getSpans(0,
                        editable.length(),
                        IncognitoMentionSpaned.MentionColorSpan.class);
                int spaceStart = 0;
                if (content.startsWith(" ") || content.startsWith("\n")) {
                    int len = length();
                    while ((spaceStart < len)
                            && ((content.charAt(spaceStart) == ' ')
                            || (content.charAt(spaceStart) == '\n'))) {
                        spaceStart++;
                    }
                }
                if (spans != null && spans.length > 0) {
                    HashSet<IncognitoMentionSpaned.MentionColorSpan> hashSet = new HashSet<>(Arrays.asList(spans));
                    ArrayList<IncognitoMentionSpaned.MentionColorSpan> list = new ArrayList<>(hashSet);
                    for (IncognitoMentionSpaned.MentionColorSpan colorSpan : list) {
                        int start = editable.getSpanStart(colorSpan);
                        int end = editable.getSpanEnd(colorSpan);
                        if (start < end) {
                            String text = content.substring(start, end);
                            IncognitoMentionEntity spanEntity = colorSpan.getSpanEntity();
                            if (colorSpan.getSpanEntity() != null && TextUtils.equals(text, colorSpan.getSpanEntity().getContent())) {
                                spanEntity.setLocation(start - spaceStart, end - spaceStart);
                                result.add(spanEntity);
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public interface MentionListener {

        default boolean enablePopupMenu() {
            return false;
        }

        void onQueryTagFriend(String s);

        void onTextChange(String s);

        void onInsertStickerFromKeybroad(boolean sticker, Uri uri);
    }
}