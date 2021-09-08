/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.link_preview.url;

import com.halo.widget.link_preview.SearchUrls;
import com.halo.widget.link_preview.TextCrawler;

import java.util.List;

public class DefaultUrlExtractionStrategy implements UrlExtractionStrategy {

    @Override
    public List<String> extractUrls(String textPassedToTextCrawler) {
        // Don't forget the http:// or https://
        List<String> urls = SearchUrls.matches(textPassedToTextCrawler);

        if (urls.size() > 0) {
            String url = TextCrawler.extendedTrim(urls.get(0));
            urls.set(0, url);
        }
        return urls;
    }
}
