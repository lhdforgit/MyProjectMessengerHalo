/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.resource;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.common.base.Objects;
import com.halo.data.api.utils.ApiResponse;
import com.halo.data.executor.AppExecutors;

import static com.halo.data.common.resource.StatusNetwork.ERROR;

/**
 * Defines two type parameters (ResultType, RequestType) since the data type returned from the API may not match the data type used locally.
 * <p>
 * It starts by observing database for the resource. When the entry is loaded from the database for the first time,
 * NetworkBoundResource checks whether the result is good enough to be dispatched and/or it should be fetched from network.
 * Note that both of these can happen at the same time since you probably want to show the cached data while updating it from the network.
 * If the network call completes successfully, it saves the response into the database and re-initializes the stream.
 * If network request fails, we dispatch a failure directly.
 *
 * @param <ResultType>:  TypeAlbum for the Resource data
 * @param <RequestType>: TypeAlbum for the API response
 * @author ndn
 * Created by ndn
 * Created on 6/7/18.
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource(AppExecutors appExecutors) {

        this.appExecutors = appExecutors;

        result.setValue(Resource.Companion.loading(null));

        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, resultType -> {
            result.removeSource(dbSource);

            if (shouldFetch(resultType)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, data -> setValue(Resource.Companion.success(data)));
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equal(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new hotel, it will dispatch its latest value quickly
        result.addSource(dbSource, resultType -> setValue(Resource.Companion.loading(resultType)));

        result.addSource(apiResponse, requestTypeApiResponse -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (requestTypeApiResponse.isSuccessful()) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(requestTypeApiResponse));
                    appExecutors.mainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(),
                                    newData -> setValue(Resource.Companion.success(newData)))
                    );
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> setValue(Resource.Companion.error(ERROR, requestTypeApiResponse.getErrorMessage(), newData)));
            }
        });
    }

    /**
     * Called when the fetch fails. The child class may want to reset components like rate limiter.
     */
    @MainThread
    protected void onFetchFailed() {

    }

    /**
     * @return a LiveData that represents the resource, implemented in the base class.
     */
    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.getBody();
    }

    /**
     * Called to save the result of the API response into the database or cache
     *
     * @param item API response item {@see RequestType}
     */
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    /**
     * Called with the data in the database or cache to decide whether it should be fetched from the network.
     *
     * @param data database or cache data {@see ResultType}
     * @return true if should fetch api
     */
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    /**
     * Called to get the cached data from the database
     *
     * @return {@see LiveData} data from db or cache
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    /**
     * Called to create the API call.
     *
     * @return {@see LiveData} data from network
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
