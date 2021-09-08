/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.common.androidautodispose.AndroidRxDispose;
import com.halo.common.androidlifecycle.ViewEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ndn on 10/15/18.
 */
public final class RxJavaUtil {
    public static final Object NULL = "";

    private RxJavaUtil() {
    }

    /**
     * @see Disposable#dispose()
     */
    public static void disposeIfNotNull(Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * push work to RxJava io thread {@link AndroidSchedulers#mainThread()}, auto dispose with view destroy event
     */
    public static Disposable workInMainThreadWithView(View view, Action workAction) {
        return Single.just(NULL)
                .observeOn(AndroidSchedulers.mainThread())
                .to(AndroidRxDispose.withSingle(view, ViewEvent.DESTROY))
                .subscribe(o -> workAction.run(),
                        Throwable::printStackTrace);
    }

    /**
     * push work to RxJava io thread {@link AndroidSchedulers#mainThread()}
     */
    public static Disposable workInMainThread(Action workAction) {
        return workInMainThread(NULL, o -> workAction.run());
    }

    /**
     * push work to RxJava io thread {@link AndroidSchedulers#mainThread()}
     */
    public static <D> Disposable workInMainThread(D data, Consumer<D> workAction) {
        return Single.just(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workAction,
                        Throwable::printStackTrace);
    }

    /**
     * push delayed work to RxJava io thread {@link AndroidSchedulers#mainThread()}
     */
    public static <D> Disposable delayInMainThread(D data, long time, TimeUnit unit, Consumer<D> workAction) {
        return Single.just(data)
                .delay(time, unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workAction,
                        Throwable::printStackTrace);
    }

    /**
     * push work to RxJava io thread {@link Schedulers#io()}
     *
     * @param workAction
     */
    public static Disposable workInRxIoThread(Action workAction) {
        return Single.just(NULL)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> workAction.run(),
                        Throwable::printStackTrace);
    }

    /**
     * push work to RxJava computation thread {@link Schedulers#computation()}
     *
     * @param workAction
     */
    public static Disposable workInRxComputationThread(Action workAction) {
        return Single.just(NULL)
                .observeOn(Schedulers.computation())
                .subscribe(o -> workAction.run(), Throwable::printStackTrace);
    }

    public static Disposable workWithUiThread(Action workAction, Action uiAction) {
        return workWithUiThread(workAction, uiAction, Throwable::printStackTrace);
    }

    public static Disposable workWithUiThread(@NonNull Action workAction, @NonNull Action uiAction, Consumer<Throwable> error) {
        return Observable.just(NULL)
                .doOnNext(i -> workAction.run())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> uiAction.run(), error);
    }

    public static <R> Disposable workWithUiResult(Supplier<R> workAction, Consumer<R> uiAction) {
        return workWithUiResult(workAction, uiAction, Throwable::printStackTrace);
    }

    public static <R> Disposable workWithUiResult(Supplier<R> workAction, Consumer<R> uiAction, Consumer<Throwable> error) {
        return Single.just(NULL)
                .map(o -> workAction.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uiAction, error);
    }

    @NonNull
    public static <T> Single<T> neverNull(@Nullable T source) {
        return source == null ? Single.never() : Single.just(source);
    }

    /**
     * wrap nullable source in Observable flatMap
     *
     * @param source
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> Observable<T> neverNullObservable(@Nullable T source) {
        return source == null ? Observable.never() : Observable.just(source);
    }

    public static <T> ObservableTransformer<T, T> iOTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> iOSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> computationTransformer() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> computationSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> newThreadTransformer() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> newThreadSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> clickThrottleTransformer() {
        return observable -> observable.throttleFirst(1, TimeUnit.SECONDS);
    }

    public interface Supplier<T> {
        /**
         * Retrieves an instance of the appropriate type. The returned object may or
         * may not be a new instance, depending on the implementation.
         *
         * @return an instance of the appropriate type
         */
        @NonNull
        T get() throws Exception;
    }
}
