package com.halo.widget.layout

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.halo.widget.R


sealed class State(val value: Int)
object Content : State(0)
object NoContent : State(3)
object Loading : State(1)
object Error : State(2)

private fun Int.find() = kotlin.runCatching {
    return@runCatching when {
        this == Content.value -> {
            Content
        }
        this == Loading.value -> {
            Loading
        }
        this == Error.value -> {
            Error
        }
        else -> Error
    }
}.getOrNull()


class StateLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var state: State? = null
    private var stateMap = HashMap<State, View>()

    private var loadingRes = -1
    private var errorRes = -1
    private var noContent = -1

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        loadingRes = ta.getResourceId(R.styleable.StateLayout_sl_loading_view, -1)
        errorRes = ta.getResourceId(R.styleable.StateLayout_sl_error_view, -1)
        noContent = ta.getResourceId(R.styleable.StateLayout_sl_empty_view, -1)
        ta.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            throw IllegalArgumentException("You must have only one content view.")
        }
        if (childCount == 1) {
            val contentView = getChildAt(0)
            stateMap[Content] = contentView
        }
        if (loadingRes != -1) {
            setViewForState(Loading, loadingRes)
        }
        if (errorRes != -1) {
            setViewForState(Error, errorRes)
        }
        if (noContent != -1) {
            setViewForState(NoContent, noContent)
        }
    }

    var errorView: View? = null
        private set

    fun setViewForState(state: State, @LayoutRes res: Int) {
        val view = LayoutInflater.from(context).inflate(res, this, false)
        setViewForState(state, view)
        errorView = view
    }

    fun setViewForState(state: State, view: View?) {
        view?.let {
            if (stateMap.containsKey(state)) {
                removeView(stateMap[state])
            }
            addView(view)
            view.visibility = View.GONE
            stateMap[state] = view
        }
    }

    fun setError() = setState(Error)

    fun setLoading() = setState(Loading)

    fun setNoContent() = setState(NoContent)

    fun setContent() = setState(Content)

    fun setState(state: State?) {
        if (this.state == state) {
            return
        }
//        if (!stateMap.containsKey(state)) {
//            throw IllegalStateException("Invalid state: $state")
//        }
        for (key in stateMap.keys) {
            stateMap[key]?.visibility = if (key == state) View.VISIBLE else View.GONE
        }
        this.state = state
    }

    fun getView(state: State): View? {
        return stateMap[state]
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return if (superState == null) {
            superState
        } else {
            SavedState(superState, state?.value ?: Content.value)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            setState(state.state.find())
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {

        var state: Int

        constructor(superState: Parcelable, state: Int) : super(superState) {
            this.state = state
        }

        constructor(source: Parcel) : super(source) {
            state = source.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}