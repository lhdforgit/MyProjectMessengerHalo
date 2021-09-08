/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.link_preview.url;

import java.util.List;

/**
 * Provides the means for extracting URL(s) from text.
 */
public interface UrlExtractionStrategy {
    List<String> extractUrls(String textPassedToTextCrawler);
}
