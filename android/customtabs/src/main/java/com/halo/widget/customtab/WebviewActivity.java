/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.customtab;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by ndn on 10/15/18.
 */
public class WebviewActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "extra.url";
    public static final String EXTRA_TOOLBAR = "extra.toolbar";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_act);
        String url = getIntent().getStringExtra(EXTRA_URL);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_TOOLBAR);
        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (toolbarTitle != null && !toolbarTitle.isEmpty()) {
            setTitle(toolbarTitle);
        } else {
            setTitle(url);
        }
        setupActionBar();
        webView.loadUrl(url);
    }

    private void setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}