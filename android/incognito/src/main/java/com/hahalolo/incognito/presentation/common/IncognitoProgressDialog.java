package com.hahalolo.incognito.presentation.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.hahalolo.incognito.R;
import com.hahalolo.incognito.databinding.IncognitoProgressDialogBinding;

import timber.log.Timber;


public class IncognitoProgressDialog extends Dialog {

    public IncognitoProgressDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public IncognitoProgressDialog(@NonNull Context context, int themeResId) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    protected IncognitoProgressDialog(@NonNull Context context, boolean cancelable,
                             @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    private IncognitoProgressDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.incognito_progress_dialog,
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
     * Animation
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
