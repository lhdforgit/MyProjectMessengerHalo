/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.utils.MimeType;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 * Entry for Media selection.
 */
public final class Media {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private Media(Activity activity) {
        this(activity, null);
    }

    private Media(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private Media(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Start Media from an Activity.
     * <p>
     * This Activity's {@link Activity#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param activity Activity instance.
     * @return {@link Media} instance.
     */
    public static Media from(Activity activity) {
        return new Media(activity);
    }

    /**
     * Start Media from a Fragment.
     * <p>
     * This Fragment's {@link Fragment#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param fragment Fragment instance.
     * @return {@link Media} instance.
     */
    public static Media from(Fragment fragment) {
        return new Media(fragment);
    }

    /**
     * Obtain user selected media' {@link Uri} list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media' {@link Uri} list.
     */
    public static List<Uri> obtainResult(Intent data) {
        return data.getParcelableArrayListExtra(MediaAct.EXTRA_RESULT_SELECTION);
    }

    /**
     * Obtain user selected media path list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media path list.
     */
    public static List<String> obtainPathResult(Intent data) {
        return data.getStringArrayListExtra(MediaAct.EXTRA_RESULT_SELECTION_PATH);
    }

    /**
     * Obtain user selected media item list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media item list.
     */
    public static List<Item> obtainItemResult(Intent data) {
        return data.getParcelableArrayListExtra(MediaAct.EXTRA_RESULT_SELECTION_ITEM);
    }

    /**
     * MIME types the selection constrains on.
     * <p>
     * Types not included in the set will still be shown in the grid but can't be chosen.
     *
     * @param mimeTypes MIME types set user can choose from.
     * @return {@link SelectionCreator} to build select specifications.
     * @see MimeType
     * @see SelectionCreator
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes) {
        return this.choose(mimeTypes, true);
    }

    /**
     * MIME types the selection constrains on.
     * <p>
     * Types not included in the set will still be shown in the grid but can't be chosen.
     *
     * @param mimeTypes          MIME types set user can choose from.
     * @param mediaTypeExclusive Whether can choose images and videos at the same time during one single choosing
     *                           process. true corresponds to not being able to choose images and videos at the same
     *                           time, and false corresponds to being able to do this.
     * @return {@link SelectionCreator} to build select specifications.
     * @see MimeType
     * @see SelectionCreator
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        return new SelectionCreator(this, mimeTypes, mediaTypeExclusive);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }
}
