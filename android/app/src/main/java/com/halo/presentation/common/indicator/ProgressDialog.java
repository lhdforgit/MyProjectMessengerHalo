/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.indicator;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.halo.R;
import com.halo.databinding.ProgressDialogBinding;

public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    protected ProgressDialog(@NonNull Context context, boolean cancelable,
                             @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    private ProgressDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.progress_dialog,
                null,
                false);
        setContentView(binding.getRoot());
        try {
            animate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Animation logo Hahalolo
     */
    private void animate() {
        if (binding.animationView != null) {
            binding.animationView.playAnimation();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        try {
            if (binding.animationView != null) {
                binding.animationView.cancelAnimation();
            }
        } catch (Exception e) {

        }
    }
}
