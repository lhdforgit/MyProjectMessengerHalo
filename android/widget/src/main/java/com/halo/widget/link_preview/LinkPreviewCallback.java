/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.link_preview;

/**
 * Callback that is invoked with before and after the loading of a link preview
 * 
 */
public interface LinkPreviewCallback {

	void onPre();

	/**
	 * 
	 * @param sourceContent
	 *            Class with all contents from preview.
	 * @param isNull
	 *            Indicates if the content is null.
	 */
	void onPos(SourceContent sourceContent, boolean isNull);
}
