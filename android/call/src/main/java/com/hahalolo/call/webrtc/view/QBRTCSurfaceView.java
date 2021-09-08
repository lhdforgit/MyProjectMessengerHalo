package com.hahalolo.call.webrtc.view;

import android.content.Context;
import android.util.AttributeSet;

import com.hahalolo.call.webrtc.QBRTCClient;
import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.EglBase;

public class QBRTCSurfaceView extends BaseSurfaceViewRenderer
{
    private static final String CLASS_TAG;
    Logger LOGGER;
    
    public QBRTCSurfaceView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.LOGGER = Logger.getInstance("RTCClient");
    }
    
    public QBRTCSurfaceView(final Context context) {
        super(context);
        this.LOGGER = Logger.getInstance("RTCClient");
    }
    
    @Override
    protected void init() {
        if (this.inited) {
            return;
        }
        final EglBase eglContext = QBRTCClient.getInstance(this.getContext()).getEglContext();
        if (eglContext == null) {
            return;
        }
        this.LOGGER.d(QBRTCSurfaceView.CLASS_TAG, "init with context" + eglContext);
        this.init(eglContext.getEglBaseContext(), null);
        this.inited = true;
    }
    
    static {
        CLASS_TAG = QBRTCSurfaceView.class.getSimpleName();
    }
}
