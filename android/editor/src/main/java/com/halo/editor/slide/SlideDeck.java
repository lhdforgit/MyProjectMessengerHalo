/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.slide;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.editor.attachments.Attachment;
import com.halo.editor.util.MediaUtil;
import com.halo.stream.Stream;

import java.util.LinkedList;
import java.util.List;

public class SlideDeck {

    private final List<Slide> slides = new LinkedList<>();

    public SlideDeck(@NonNull Context context, @NonNull List<? extends Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Slide slide = MediaUtil.getSlideForAttachment(context, attachment);
            if (slide != null) slides.add(slide);
        }
    }

    public SlideDeck(@NonNull Context context, @NonNull Attachment attachment) {
        Slide slide = MediaUtil.getSlideForAttachment(context, attachment);
        if (slide != null) slides.add(slide);
    }

    public SlideDeck() {
    }

    public void clear() {
        slides.clear();
    }

    @NonNull
    public List<Attachment> asAttachments() {
        List<Attachment> attachments = new LinkedList<>();

        for (Slide slide : slides) {
            attachments.add(slide.asAttachment());
        }

        return attachments;
    }

    @NonNull
    public List<Uri> asUris() {
        List<Uri> uris = new LinkedList<>();

        for (Slide slide : slides) {
            uris.add(slide.getUri());
        }

        return uris;
    }

    public void addSlide(Slide slide) {
        slides.add(slide);
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public boolean containsMediaSlide() {
        for (Slide slide : slides) {
            if (slide.hasImage() || slide.hasVideo() || slide.hasAudio() || slide.hasDocument() || slide.hasSticker() || slide.hasViewOnce()) {
                return true;
            }
        }
        return false;
    }

    public @Nullable
    Slide getThumbnailSlide() {
        for (Slide slide : slides) {
            if (slide.hasImage()) {
                return slide;
            }
        }

        return null;
    }

    public @NonNull
    List<Slide> getThumbnailSlides() {
        return Stream.of(slides).filter(Slide::hasImage).toList();
    }

    @Override
    public String toString() {
        return "SlideDeck{" +
                "slides=" + slides +
                '}';
    }
}
