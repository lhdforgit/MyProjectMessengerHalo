/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.language;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.halo.R;
import com.halo.databinding.LanguageActBinding;
import com.halo.presentation.base.AbsBackActivity;

import javax.inject.Inject;

public class LanguageAct extends AbsBackActivity {


    @Inject
    ViewModelProvider.Factory factory;

    LanguageActBinding binding;
    LanguageViewModel viewModel;

    @Override
    public void initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(LanguageAct.this, R.layout.language_act);
        viewModel = ViewModelProviders.of(LanguageAct.this, factory).get(LanguageViewModel.class);
    }

    @Override
    public void initializeLayout() {

    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
}
