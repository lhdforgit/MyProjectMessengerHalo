/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.incognito.presentation.conversation.view.mention;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.UUID;

import static com.hahalolo.incognito.presentation.conversation.view.mention.IncognitoMentionEditText.SPERATOR_TAG;
import static com.hahalolo.incognito.presentation.conversation.view.mention.IncognitoMentionUtils.REGEX_FORMAT;


/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
public class IncognitoMentionEntity {

    private int start;
    private int end;
    private String key;         // key kí tự ẩn
    private String text;        // Kí tự ẩn
    private String content;     //kí tự đc hiển thị
    private String id;          // id đại diện
    private String name;        // name đại diện

    IncognitoMentionEntity(@NonNull IncognitoMentionEntity spanEntity) {
        this.key = spanEntity.getKey();
        this.text = spanEntity.getText();
        this.content = spanEntity.getContent();
        this.id = spanEntity.id;
        this.name = spanEntity.name;
    }

    public IncognitoMentionEntity(String content, String id, String name) {
        this.key = UUID.randomUUID().toString();
        this.text = String.format(REGEX_FORMAT, key);
        this.content = SPERATOR_TAG +content;
        this.id = id;
        this.name = name;
    }

    public IncognitoMentionEntity(String key, String content, String id, String name) {
        this.key = key;
        this.text = String.format(REGEX_FORMAT, key);
        this.content = SPERATOR_TAG +content;
        this.id = id;
        this.name = name;
    }

    IncognitoMentionEntity(String text, int start, int end) {
        this.text = text;
        this.content = text;
        this.start = start;
        this.end = end;
    }

    void setLocation(int start, int end) {
        this.start = start;
        this.end = end;
    }


    public String getText() {
        return text;
    }

    public String getContent() {
        return content;
    }

    public String getKey() {
        return key;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isTag() {
        return !TextUtils.isEmpty(key) && !TextUtils.isEmpty(content);
    }
}