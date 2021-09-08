package com.hahalolo.call.webrtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

@TargetApi(21)
public class QBRTCScreenCapturer extends QBRTCBaseVideoCapturer
{
    public static final int REQUEST_MEDIA_PROJECTION = 101;
    
    public static void requestPermissions(final Activity context) {
        final MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)context.getSystemService("media_projection");
        context.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 101);
    }
    
    public static void requestPermissions(final Fragment context) {
        final MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)context.getActivity().getSystemService("media_projection");
        context.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 101);
    }
    
    public QBRTCScreenCapturer(final Intent mediaProjectionpermissionResultDate, MediaProjection.Callback callback) {
        if (callback == null) {
            callback = new ProjectionCallback();
        }
        this.videoCapturer = (VideoCapturer)new ScreenCapturerAndroid(mediaProjectionpermissionResultDate, callback);
    }
    
    private static class ProjectionCallback extends MediaProjection.Callback
    {
        private static final String CLASS_TAG;
        private static final Logger LOGGER;
        
        public void onStop() {
            ProjectionCallback.LOGGER.d(ProjectionCallback.CLASS_TAG, "User revoked permission to capture the screen.");
        }
        
        static {
            CLASS_TAG = QBRTCBaseVideoCapturer.class.getSimpleName();
            LOGGER = Logger.getInstance(ProjectionCallback.CLASS_TAG);
        }
    }
}
