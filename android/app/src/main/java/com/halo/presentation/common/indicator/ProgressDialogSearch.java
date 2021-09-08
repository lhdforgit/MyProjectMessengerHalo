/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.indicator;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.halo.R;
import com.halo.databinding.ProgressDialogSearchBinding;

public class ProgressDialogSearch extends Dialog {

    public ProgressDialogSearch(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public ProgressDialogSearch(@NonNull Context context, int themeResId) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    protected ProgressDialogSearch(@NonNull Context context, boolean cancelable,
                                   @Nullable OnCancelListener cancelListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialogSearchBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.progress_dialog_search,
                null,
                false);
        setContentView(binding.getRoot());
    }
}
