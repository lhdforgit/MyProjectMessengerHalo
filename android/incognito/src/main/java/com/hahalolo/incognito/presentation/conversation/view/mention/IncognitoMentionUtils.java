/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.incognito.presentation.conversation.view.mention;

import java.util.List;

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
public class IncognitoMentionUtils {
    public final static String REGEX_TAG = "::";
    public final static String REGEX_FORMAT = "::%s::";

    static String getRegex(List<IncognitoMentionEntity> listTag) {
        StringBuilder result = new StringBuilder();
        if (listTag != null && !listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); i++) {
                result.append(i == 0 ? "" : "|").append(String.format(REGEX_FORMAT, listTag.get(i).getKey()));
            }
            return result.toString();
        }
        return null;
    }
}
