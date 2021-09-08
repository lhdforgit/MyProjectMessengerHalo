package com.halo.common.utils

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Create by ndn
 * Create on 8/1/20
 * com.halo.common.utils
 */
abstract class EvenObserver<T>: Observer<T> {
    override fun onComplete() {

    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

    override fun onNext(t: T) {

    }

    override fun onSubscribe(d: Disposable) {

    }
}