/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.resource

import android.accounts.AuthenticatorException
import com.halo.common.utils.log
import com.halo.data.common.resource.SimpleResource.Companion.failure
import com.halo.data.common.resource.SimpleResource.Companion.success
import com.halo.data.common.resource.SimpleResource.Failure
import java.net.ConnectException
import java.net.UnknownHostException

sealed class State<V> {

    class StartLoad<V> : State<V>()

    class Complete<V> : State<V>()
}



/**
 * Chia simple Resource thành 2 luồng 1 thành công, 2 thất bại
 * */
open class Ether<V>( val t: SimpleResource<V>) : State<V>(){

    val value: V?
        get() = t.getOrNull()

    fun ether(failure: (SimpleResource.Failure) -> Unit, done: (V) -> Unit): Ether<V> {
        runCatching {
            if (t.isSuccess) {
                done.invoke(t.getOrNull()!!)
            } else {
                failure.invoke(t.getErrorOrNull()!!)
            }
        }.getOrElse {
            it.printStackTrace()
        }
        return this
    }

    suspend fun etherSuspend(
            failure: suspend (SimpleResource.Failure) -> Unit,
            done: suspend (V) -> Unit
    ): Ether<V> {
        runCatching {
            if (t.isSuccess) {
                done.invoke(t.getOrNull()!!)
            } else {
                failure.invoke(t.getErrorOrNull()!!)
            }
        }.getOrElse {
            "Error Ether ${it.message}".log()
            it.printStackTrace()
        }
        return this
    }

    fun <Transform> map(value: (Ether<V>) -> Transform)
            : Ether<Transform> {
        return Ether(if (t.isSuccess) {
            success(value.invoke(this))
        } else {
            t.getErrorOrNull()!!.run {
                failure(this.code, this.message)
            }
        })
    }
}
/**
 * Data của mongo được nén trong simple resource nếu thành công
 * Nếu thất bại data sẽ được nén trong Failure
 * @see Failure
 * */
inline class SimpleResource<T> constructor(
        val value: Any?
) : java.io.Serializable {

    val isSuccess: Boolean get() = value !is Failure

    val isFailure: Boolean get() = value is Failure

    fun getOrNull(): T? =
            when {
                isFailure -> null
                (value != null) -> value as? T?
                else -> null
            }

    fun getErrorOrNull(): Failure? = when {
        isFailure -> value as? Failure?
        else -> null
    }

    val toEther
        get() = Ether(this)

    companion object {

        const val EXCEPTION = -1001

        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <F> success(value: F): SimpleResource<F> = SimpleResource(value)
        /**
         * Returns an instance that encapsulates the given [code] [message] as failure.
         */
        fun <T> failure(code: Int, message: String?): SimpleResource<T> =
                SimpleResource(createFailure(code, message))

        fun <T> failureNetwork() =
                SimpleResource<T>(createFailure(500, ""))

        fun <T> emptyFailure() =
                SimpleResource<T>(createFailure(204, ""))

        fun <T> failureCommon() =
                SimpleResource<T>(createFailure(404, ""))
        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        fun <T> failure(exception: Throwable?): SimpleResource<T> =
                SimpleResource(createFailure(exception))
    }

    data class Failure(
            @JvmField val code: Int,
            @JvmField val message: String?,
            @JvmField val exception: Throwable? = null
    ) : java.io.Serializable {

        val isUnauthorized: Boolean
            get() = code == 401

        val isEmpty: Boolean
            get() = code == 204
                    || code == 204

        companion object {
            @JvmStatic
            fun createNoContent(): Failure {
                return Failure(204, "", null)
            }

            @JvmStatic
            fun createNetwork(): Failure {
                return Failure(500, "", null)
            }
        }
    }
}

/**
 * Ném ra author
 * */
object UnAuthorized : AuthenticatorException() {
    fun check(code: Int) = code == 401
}

inline val Int.isNoContent
    inline get() = this == 204
            || this == 204

fun createFailure(code: Int, message: String?): Any = SimpleResource.Failure(code, message)

fun createFailure(exception: Throwable?): Any =
        SimpleResource.Failure(SimpleResource.EXCEPTION, "", exception)

/**
 * throwable
 * */
inline fun <reified T> Throwable.catchSimpleResultThrow() = when (this) {
    is UnknownHostException -> {
        failure<T>(500, "")
    }

    is ConnectException -> {
        failure<T>(500, "")
    }

    else -> {
        failure<T>(404, "")
    }
}