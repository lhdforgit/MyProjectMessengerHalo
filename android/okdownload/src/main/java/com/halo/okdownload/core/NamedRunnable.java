/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core;

public abstract class NamedRunnable implements Runnable {

    protected final String name;

    public NamedRunnable(String name) {
        this.name = name;
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        try {
            execute();
        } catch (InterruptedException e) {
            interrupted(e);
        } finally {
            Thread.currentThread().setName(oldName);
            finished();
        }
    }

    protected abstract void execute() throws InterruptedException;

    protected abstract void interrupted(InterruptedException e);

    protected abstract void finished();
}
