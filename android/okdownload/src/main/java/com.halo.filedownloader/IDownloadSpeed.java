/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader;

/**
 * The interface for the downloading speed.
 */

public interface IDownloadSpeed {

    /**
     * The downloading monitor, used for calculating downloading speed.
     */
    interface Monitor {
        /**
         * Start the monitor.
         */
        void start(long startBytes);

        /**
         * End the monitor, and calculate the average speed during the entire downloading processing
         *
         * @param sofarBytes The so far downloaded bytes.
         */
        void end(long sofarBytes);

        /**
         * Refresh the downloading speed.
         *
         * @param sofarBytes The so far downloaded bytes.
         */
        void update(long sofarBytes);

        /**
         * Reset the monitor.
         */
        void reset();

    }

    /**
     * For lookup the downloading speed data.
     */
    interface Lookup {
        /**
         * @return The currently downloading speed when the task is running.
         * The average speed when the task is finished.
         */
        int getSpeed();

        /**
         * @param minIntervalUpdateSpeed The minimum interval to update the speed, used to adjust
         *                               the refresh frequent.
         */
        void setMinIntervalUpdateSpeed(int minIntervalUpdateSpeed);
    }
}
