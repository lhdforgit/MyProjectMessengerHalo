/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.link_preview;

import android.os.AsyncTask;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;

/**
 * ImagePickingStrategy that mimics the original behavior of TextCrawler.  If a meta image tag exists,
 * it is used, if not, the document is scraped to find other images.
 */
class DefaultImagePickingStrategy extends BaseImagePickingStrategy {

    /**
     * Gets images from the html code
     */
    @Override
    public List<String> getImages(AsyncTask asyncTask, Document document, HashMap<String, String> metaTags) {
        List<String> images = getMetaImage(metaTags);

        if (images.isEmpty()) {
            images.addAll(getImagesFromImgTags(asyncTask, document));
        }
        return images;
    }
}
