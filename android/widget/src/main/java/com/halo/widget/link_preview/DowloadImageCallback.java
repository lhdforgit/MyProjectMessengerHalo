/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.link_preview;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface DowloadImageCallback {

	/**
	 * 
	 * @param imageView
	 *            ImageView to receive the bitmap.
	 * @param loadedBitmap
	 *            Bitmap downloaded from url.
	 * @param url
	 *            Image url.
	 */
	void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url);

}
