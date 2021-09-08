/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.jsoup.nodes;

import com.halo.widget.jsoup.parser.Tag;

/**
 * Represents a {@link TextNode} as an {@link Element}, to enable text nodes to be selected with
 * the {@link com.halo.widget.jsoup.select.Selector} {@code :matchText} syntax.
 */
public class PseudoTextElement extends Element {

    public PseudoTextElement(Tag tag, String baseUri, Attributes attributes) {
        super(tag, baseUri, attributes);
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }
}
