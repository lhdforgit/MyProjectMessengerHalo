package com.hahalolo.call.conference.view;

import android.content.Context;
import android.util.AttributeSet;

import com.hahalolo.call.conference.ConferenceClient;
import com.hahalolo.call.webrtc.util.Logger;
import com.hahalolo.call.webrtc.view.BaseSurfaceViewRenderer;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon.RendererEvents;

public class QBConferenceSurfaceView extends BaseSurfaceViewRenderer {
   private static final String CLASS_TAG = QBConferenceSurfaceView.class.getSimpleName();
   private Logger LOGGER;

   public QBConferenceSurfaceView(Context context, AttributeSet attrs) {
      super(context, attrs);
      this.LOGGER = Logger.getInstance(ConferenceClient.TAG);
   }

   public QBConferenceSurfaceView(Context context) {
      super(context);
      this.LOGGER = Logger.getInstance(ConferenceClient.TAG);
   }

   protected void init() {
      if (!this.inited) {
         EglBase eglContext = ConferenceClient.getInstance(this.getContext()).getEglContext();
         if (eglContext != null) {
            this.LOGGER.d(CLASS_TAG, "init with context" + eglContext);
            this.init(eglContext.getEglBaseContext(), (RendererEvents)null);
            this.inited = true;
         }
      }
   }
}
