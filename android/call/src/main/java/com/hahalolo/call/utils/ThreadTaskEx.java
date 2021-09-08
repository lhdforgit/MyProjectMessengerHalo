package com.hahalolo.call.utils;

import android.os.Bundle;

import java.util.concurrent.Executor;

public abstract class ThreadTaskEx implements Runnable {
    protected Executor executor;
    protected Bundle parameters;

    public ThreadTaskEx(Bundle parameters2, Executor executor2) {
        this.parameters = parameters2;
        this.executor = executor2;
    }

    /* access modifiers changed from: protected */
    public void execute() {
        this.executor.execute(this);
    }

    public abstract void performInAsync();

    public void run() {
        performInAsync();
    }
}
