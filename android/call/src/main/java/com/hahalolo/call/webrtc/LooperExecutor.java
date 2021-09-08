package com.hahalolo.call.webrtc;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.hahalolo.call.webrtc.util.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public class LooperExecutor extends Thread implements Executor
{
    private static final String TAG = "LooperExecutor";
    private static final Logger LOGGER;
    private final Object looperStartedEvent;
    private String executorOwner;
    private ExecuteCondition executeCondition;
    private Handler handler;
    private volatile boolean running;
    private long threadId;
    private volatile boolean stopping;
    private Set<Object> customers;
    private Set<ExecutorLifecycleListener> lifecycleListeners;
    
    public LooperExecutor() {
        this.looperStartedEvent = new Object();
        this.handler = null;
        this.running = false;
        this.stopping = false;
        this.customers = new HashSet<Object>();
        this.lifecycleListeners = new HashSet<ExecutorLifecycleListener>();
    }
    
    public LooperExecutor(final ExecuteCondition executeCondition) {
        this();
        this.executeCondition = executeCondition;
    }
    
    public void setExecutionCondition(final ExecuteCondition executeCondition) {
        this.executeCondition = executeCondition;
    }
    
    public LooperExecutor(final Class classOwner) {
        this();
        this.executorOwner = classOwner.getSimpleName();
        LooperExecutor.LOGGER.d("LooperExecutor", "Create looper executor on thread: " + Thread.currentThread().getId() + " for " + this.executorOwner);
    }
    
    @Override
    public void run() {
        Looper.prepare();
        synchronized (this.looperStartedEvent) {
            LooperExecutor.LOGGER.d("LooperExecutor", "Looper thread started.");
            this.handler = new Handler();
            this.threadId = Thread.currentThread().getId();
            LooperExecutor.LOGGER.d("LooperExecutor", "Looper thread started on thread." + this.threadId);
            this.looperStartedEvent.notify();
        }
        Looper.loop();
    }
    
    public synchronized void requestStart() {
        LooperExecutor.LOGGER.d("LooperExecutor", "Request Looper start. On " + this.executorOwner);
        if (this.running) {
            return;
        }
        this.running = true;
        this.handler = null;
        this.start();
        synchronized (this.looperStartedEvent) {
            while (this.handler == null) {
                try {
                    this.looperStartedEvent.wait();
                }
                catch (InterruptedException e) {
                    LooperExecutor.LOGGER.e("LooperExecutor", "Can not start looper thread");
                    this.running = false;
                }
            }
        }
    }
    
    public boolean isStarted() {
        return this.running;
    }
    
    public boolean isStopped() {
        return this.stopping;
    }
    
    public synchronized void requestStop() {
        LooperExecutor.LOGGER.d("LooperExecutor", "Request Looper stop. On " + this.executorOwner);
        if (!this.running) {
            return;
        }
        if (this.customers != null && this.customers.size() > 0) {
            LooperExecutor.LOGGER.d("LooperExecutor", "Can't stop tasks execution. Task execution customers list not empty");
            return;
        }
        this.stopping = true;
        this.handler.post((Runnable)new Runnable() {
            @TargetApi(18)
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < 18) {
                    Looper.myLooper().quit();
                }
                else {
                    Looper.myLooper().quitSafely();
                }
                LooperExecutor.LOGGER.d("LooperExecutor", "Looper thread finished.");
                for (final ExecutorLifecycleListener listener : LooperExecutor.this.lifecycleListeners) {
                    listener.onExecutorStop();
                }
            }
        });
    }
    
    public boolean checkOnLooperThread() {
        return Thread.currentThread().getId() == this.threadId;
    }
    
    @Override
    public synchronized void execute(final Runnable runnable) {
        if (this.executeCondition == null || this.executeCondition.isExecutionAllow()) {
            LooperExecutor.LOGGER.d("LooperExecutor", "Request Looper execute.");
            if (!this.running) {
                LooperExecutor.LOGGER.w("LooperExecutor", "Running looper executor without calling requestStart()");
                return;
            }
            if (this.stopping) {
                LooperExecutor.LOGGER.w("LooperExecutor", "looper executor has been finished!");
                return;
            }
            if (Thread.currentThread().getId() == this.threadId) {
                runnable.run();
                LooperExecutor.LOGGER.d("LooperExecutor", "EXECUTE.Run on thread:" + this.threadId + " for " + this.executorOwner);
            }
            else {
                LooperExecutor.LOGGER.d("LooperExecutor", "POST.Run on thread:" + this.threadId + " for " + this.executorOwner);
                this.handler.post(runnable);
            }
        }
    }
    
    public void addExecutorCustomer(final Object customer) {
        this.customers.add(customer);
    }
    
    public void removeExecutorCustomer(final Object customer) {
        this.customers.remove(customer);
    }
    
    public void addExecutorLifecycleListener(final ExecutorLifecycleListener listener) {
        this.lifecycleListeners.add(listener);
    }
    
    public void removeExecutorLifecycleListener(final ExecutorLifecycleListener listener) {
        this.lifecycleListeners.remove(listener);
    }
    
    static {
        LOGGER = Logger.getInstance("RTCClient");
    }
    
    interface ExecutorLifecycleListener
    {
        void onExecutorStop();
    }
    
    interface ExecuteCondition
    {
        boolean isExecutionAllow();
    }
}
