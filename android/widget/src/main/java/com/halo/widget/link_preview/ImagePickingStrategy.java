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
 * A strategy for how to select the images to return.
 */
public interface ImagePickingStrategy {

    void setImageQuantity(int imageQuantity);

    int getImageQuantity();

    List<String> getImages(AsyncTask asyncTask, Document doc, HashMap<String, String> metaTags);
}
