/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.startapp.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.halo.common.utils.ActivityUtils;
import com.halo.common.utils.KeyboardUtils;
import com.halo.data.entities.mongo.token.TokenEntity;
import com.halo.presentation.base.AbsActivity;
import com.halo.presentation.startapp.start.StartAct;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.halo.common.utils.ktx.ActivityKtxKt.transparentStatusNavigationBar;

public class ShareHandlerAct extends AbsActivity {

    @Inject
    ViewModelProvider.Factory factory;

    private ShareHandlerViewModel viewModel;

    private boolean mustLogin = false;
    private String sharedText;
    private Uri imageUri;
    private ArrayList<Uri> imageUris;
    private String type;
    private TokenEntity token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/") || type.startsWith("video/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/") || type.startsWith("video/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void initializeBindingViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), factory).get(ShareHandlerViewModel.class);
    }

    @Override
    public void initializeLayout() {
        setupTheme();
        initCacheToken();
        viewModel.getShareData().observe(this, shareData -> {

        });
    }

    /**
     * Set statusNetwork bar transparent
     * Hide statusNetwork bar and navigation bar
     */
    private void setupTheme() {
        transparentStatusNavigationBar(this, false);
        KeyboardUtils.hideSoftInput(this);
    }

    /**
     * Load cache token for user login statusNetwork
     */
    private void initCacheToken() {
        viewModel.getResourceTokenResult().observe(this, resource -> {
            if (resource != null) {
                // Kiểm tra nếu chưa lưu token vào máy, thì chuyển user đến màn hình khởi động app
                if (resource.getData() == null
                        // Has not access token
                        || TextUtils.isEmpty(resource.getData().getAccessToken())
                        // Has not user id
                        || TextUtils.isEmpty(resource.getData().getUserId())) {
                    // When token info has not info, must login user
                    mustLogin = true;
                    // Load token error, navigate to login
                    navigateApp();
                } else if (resource.getData().isUnactive()) {
                    // When token info has not info, must login user
                    mustLogin = true;
                    // Xoa cache va di toi man hinh dang nhap neu tai khoan chua duoc active
                    // Load token error, navigate to login
                    navigateApp();
                } else {
                    // Đã lấy được token từ cache, va tai khoan da duoc active
                    mustLogin = false;

                    this.token = resource.getData();
                    navigateApp();
                }
            } else {
                // Load token error, navigate to login
                mustLogin = true;
                navigateApp();
            }
        });
    }

    private void handleSendText(Intent intent) {
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    }

    private void handleSendImage(Intent intent) {
        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
    }

    private void handleSendMultipleImages(Intent intent) {
        imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    private void navigateApp() {
        if (mustLogin) {
            navigateStartApp();
        } else {
            navigateCreatePost();
        }
    }

    /**
     * Navigate sign in if user has not login
     */
    private void navigateStartApp() {
        ActivityUtils.startActivity(StartAct.getIntent(this));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /**
     * Navigate main newsfeed if user has login
     */
    private void navigateCreatePost() {
        if (this.token != null) {
            /*MessApplication.getInstance().setToken(this.token);
            navigateCreatePostWithWithPostBodyEntity();*/
        } else {
            navigateStartApp();
        }
    }

    private void navigateCreatePostWithWithPostBodyEntity() {
        // Trường hợp người dùng chia sẽ nội dung bằng chữ
        if (!TextUtils.isEmpty(sharedText)) {

            // Trường hợp người dùng chia sẽ nội dung video hoặc hình ảnh
        } else if (imageUri != null || imageUris != null) {
            navigateCreatePostWithItemGallery();
        } else {
            navigateStartApp();
        }
    }

    private void navigateCreatePostWithItemGallery() {
        // Trường hợp người dùng chia sẽ 1 hình ảnh, hoặc video
        if (imageUri != null) {
            viewModel.onSingleMediaShared(imageUri, type, this);
            // Trường hợp người dùng chia sẽ nhiều hình ảnh, hoặc videos
        } else if (imageUris != null) {
            viewModel.onMultipleMediaShared(imageUris, this);
        }
    }
}