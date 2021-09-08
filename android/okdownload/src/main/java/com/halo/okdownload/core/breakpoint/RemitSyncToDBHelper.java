/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;

import androidx.annotation.NonNull;

class RemitSyncToDBHelper {

    private final RemitSyncExecutor executor;

    long delayMillis;

    RemitSyncToDBHelper(@NonNull final RemitSyncExecutor.RemitAgent agent) {
        this(new RemitSyncExecutor(agent));
    }

    RemitSyncToDBHelper(@NonNull final RemitSyncExecutor executor) {
        this.executor = executor;
        this.delayMillis = 1500;
    }

    void shutdown() {
        this.executor.shutdown();
    }

    boolean isNotFreeToDatabase(int id) {
        return !executor.isFreeToDatabase(id);
    }

    void onTaskStart(int id) {
        // discard pending sync if we can
        executor.removePostWithId(id);

        executor.postSyncInfoDelay(id, delayMillis);
    }

    void endAndEnsureToDB(int id) {
        executor.removePostWithId(id);

        try {
            // already synced
            if (executor.isFreeToDatabase(id)) return;

            // force sync for ids
            executor.postSync(id);
        } finally {
            // remove free state
            executor.postRemoveFreeId(id);
        }
    }

    void discard(int id) {
        executor.removePostWithId(id);
        executor.postRemoveInfo(id);
    }
}
