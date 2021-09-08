/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.listener.assist;

public interface ListenerAssist {
    /**
     * As default there isn't need to always recover the model, because model is bind with the
     * callback lifecycle
     *
     * @return whether model need to recover always
     */
    boolean isAlwaysRecoverAssistModel();

    void setAlwaysRecoverAssistModel(boolean isAlwaysRecoverAssistModel);

    void setAlwaysRecoverAssistModelIfNotSet(boolean isAlwaysRecoverAssistModel);
}
