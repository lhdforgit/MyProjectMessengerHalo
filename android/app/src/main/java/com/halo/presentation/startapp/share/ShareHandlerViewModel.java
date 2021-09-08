/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.startapp.share;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.base.Optional;
import com.halo.data.common.resource.Resource;
import com.halo.data.entities.mongo.token.TokenEntity;
import com.halo.presentation.base.AbsTokenViewModel;

import java.util.List;

import javax.inject.Inject;

public class ShareHandlerViewModel extends AbsTokenViewModel {

    private LiveData<Resource<TokenEntity>> resourceTokenResult;
    private final ShareRepository shareRepository;
    private final MutableLiveData<Optional<ShareData>> shareData;

    @Inject
    public ShareHandlerViewModel( ) {
        shareRepository = new ShareRepository();
        this.shareData = new MutableLiveData<>();
    }

    public LiveData<Resource<TokenEntity>> getResourceTokenResult() {
        return resourceTokenResult;
    }

    void onSingleMediaShared(@NonNull Uri uri, @Nullable String mimeType, Context context) {
        shareRepository.getResolved(context, uri, mimeType, shareData::postValue);
    }

    void onMultipleMediaShared(@NonNull List<Uri> uris, Context context) {
        shareRepository.getResolved(context, uris, shareData::postValue);
    }

    public MutableLiveData<Optional<ShareData>> getShareData() {
        return shareData;
    }
}
