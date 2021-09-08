/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/15/18.
 */
public final class Listing<T> {

    /**
     * Listener refreshes the whole data and fetches it from scratch.
     */
    public interface Refresh {
        /**
         * Refresh callback
         */
        void refresh();
    }

    /**
     * Listener retries any failed requests.
     */
    public interface Retry {
        /**
         * Retry callback
         */
        void retry();
    }

    // the LiveData of paged lists for the UI to observe
    private final LiveData<PagedList<T>> pagedList;

    // represents the network request statusNetwork to show to the user
    private final LiveData<NetworkState> networkState;

    // represents the refresh statusNetwork to show to the user. Separate from networkState, this
    // value is importantly only when refresh is requested.
    private final LiveData<NetworkState> refreshState;

    // represents the empty state
    private MutableLiveData<Boolean> empty = new MutableLiveData<>();

    // refreshes the whole data and fetches it from scratch.
    private final Refresh refresh;

    // retries any failed requests.
    private final Retry retry;

    //get total resultlist
    private LiveData<Integer> total ;

    public Listing(@NonNull LiveData<PagedList<T>> pagedList,
                   @NonNull LiveData<NetworkState> networkState,
                   @NonNull LiveData<NetworkState> refreshState,
                   @NonNull MutableLiveData<Boolean> empty,
                   @NonNull Refresh refresh,
                   @NonNull Retry retry) {
        this(pagedList, networkState, refreshState, refresh, retry);
        this.empty = empty;
    }

    /**
     * Listing constructor
     *
     * @param pagedList    the LiveData of paged lists for the UI to observe
     * @param networkState represents the network request statusNetwork to show to the user
     * @param refreshState represents the refresh statusNetwork to show to the user. Separate from networkState, this value is importantly only when refresh is requested.
     * @param refresh      refreshes the whole data and fetches it from scratch.
     * @param retry        retries any failed requests.
     */
    public Listing(@NonNull LiveData<PagedList<T>> pagedList,
                   @NonNull LiveData<NetworkState> networkState,
                   @NonNull LiveData<NetworkState> refreshState,
                   @NonNull Refresh refresh,
                   @NonNull Retry retry) {

        this.pagedList = checkNotNull(pagedList);
        this.networkState = checkNotNull(networkState);
        this.refreshState = checkNotNull(refreshState);
        this.refresh = checkNotNull(refresh);
        this.retry = checkNotNull(retry);
    }

    public Listing(@NonNull LiveData<PagedList<T>> pagedList,
                   @NonNull LiveData<NetworkState> networkState,
                   @NonNull LiveData<NetworkState> refreshState,
                   @NonNull LiveData<Integer> total,
                   @NonNull Refresh refresh,
                   @NonNull Retry retry
                   ) {

        this.pagedList = checkNotNull(pagedList);
        this.networkState = checkNotNull(networkState);
        this.refreshState = checkNotNull(refreshState);
        this.total = checkNotNull(total);
        this.refresh = checkNotNull(refresh);
        this.retry = checkNotNull(retry);
    }

    /**
     * @return {@link LiveData} the LiveData of paged lists for the UI to observe
     */
    public LiveData<PagedList<T>> getPagedList() {
        return pagedList;
    }

    /**
     * @return {@link LiveData} represents the network request statusNetwork to show to the user
     */
    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    /**
     * @return {@link LiveData} represents the refresh statusNetwork to show to the user.
     * Separate from networkState, this value is importantly only when refresh is requested.
     */
    public LiveData<NetworkState> getRefreshState() {
        return refreshState;
    }

    /**
     * @return {@link Refresh} refreshes the whole data and fetches it from scratch.
     */
    public Refresh getRefresh() {
        return refresh;
    }

    /**
     * @return {@link Retry} retries any failed requests.
     */
    public Retry getRetry() {
        return retry;
    }

    /**
     * @return {@link MutableLiveData} represents the empty state
     */
    public MutableLiveData<Boolean> getEmpty() {
        return empty;
    }

    public LiveData<Integer> getTotal() {
        return total;
    }
}
