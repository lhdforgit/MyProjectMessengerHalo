package com.hahalolo.call.webrtc;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class BaseClient
{
    public static final String TAG;
    private static final Logger LOGGER;
    static final Handler HANDLER;
    protected CameraVideoCapturer.CameraEventsHandler cameraErrorHandler;
    protected Context context;
    protected LooperExecutor executor;
    protected Executor peerFactoryExecutor;
    protected PeerFactoryManager peerFactoryManager;
    protected EglBase eglBaseContext;
    protected ResourceConfig resourceConfig;
    
    protected BaseClient(final Context context) {
        this.resourceConfig = new ResourceConfig();
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null");
        }
        this.context = context.getApplicationContext();
        this.initTaskExecutor();
    }
    
    private void initTaskExecutor() {
        BaseClient.LOGGER.d("", "init Task Executor");
        (this.executor = new LooperExecutor(this.getClass())).requestStart();
        this.peerFactoryExecutor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public void postOnMainThread(final Runnable runnable) {
        BaseClient.HANDLER.post(runnable);
    }
    
    synchronized PeerFactoryManager getPeerFactoryManager() {
        if (this.peerFactoryManager == null) {
            this.peerFactoryManager = new PeerFactoryManager(this.peerFactoryExecutor, this.context, this.getEglContext());
        }
        return this.peerFactoryManager;
    }
    
    public synchronized EglBase getEglContext() {
        if (this.eglBaseContext == null) {
            this.eglBaseContext = EglBase.create();
        }
        return this.eglBaseContext;
    }
    
    public void releaseEglContext() {
        BaseClient.LOGGER.d("", "releaseEglContext");
        if (this.eglBaseContext != null) {
            this.eglBaseContext.release();
            this.eglBaseContext = null;
        }
    }
    
    public void setResourceConfig(final ResourceConfig resourceConfig) {
        if (resourceConfig != null) {
            this.resourceConfig = resourceConfig;
        }
    }
    
    protected void stopExecutor() {
        if (this.executor != null) {
            BaseClient.LOGGER.d("", "Stop executor");
            this.executor = null;
        }
    }
    
    Executor getWorkerExecutor() {
        return this.peerFactoryExecutor;
    }
    
    protected Map<String, String> getCustomParameters() {
        return new HashMap<String, String>();
    }
    
    public void setCameraErrorHandler(final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler) {
        this.cameraErrorHandler = cameraErrorHandler;
    }
    
    protected void clear() {
        if (this.peerFactoryManager != null) {
            this.peerFactoryManager.dispose();
            this.peerFactoryManager = null;
        }
        if (this.resourceConfig.manageResourceAutomatically) {
            this.releaseEglContext();
        }
        BaseClient.LOGGER.d("", "onSessionClosed");
    }
    
    static {
        TAG = BaseClient.class.getSimpleName();
        LOGGER = Logger.getInstance(BaseClient.TAG);
        HANDLER = new Handler(Looper.getMainLooper());
    }
    
    public static class ResourceConfig
    {
        public boolean manageResourceAutomatically;
        
        public ResourceConfig() {
            this.manageResourceAutomatically = true;
        }
    }
}
