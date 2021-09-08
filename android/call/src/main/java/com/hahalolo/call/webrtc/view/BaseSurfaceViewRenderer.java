package com.hahalolo.call.webrtc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import org.webrtc.SurfaceViewRenderer;

public abstract class BaseSurfaceViewRenderer extends SurfaceViewRenderer
{
    protected boolean inited;
    
    public BaseSurfaceViewRenderer(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }
    
    public BaseSurfaceViewRenderer(final Context context) {
        super(context);
    }
    
    public void surfaceCreated(final SurfaceHolder holder) {
        super.surfaceCreated(holder);
        this.init();
    }
    
    protected abstract void init();
}
