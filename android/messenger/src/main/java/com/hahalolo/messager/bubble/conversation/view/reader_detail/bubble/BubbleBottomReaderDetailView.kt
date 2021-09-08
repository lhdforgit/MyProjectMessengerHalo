package com.hahalolo.messager.bubble.conversation.view.reader_detail.bubble

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hahalolo.messager.bubble.base.BubbleBottomDialogAbs
import com.hahalolo.messager.bubble.conversation.view.reader_detail.ChatReaderDetailListener
import com.hahalolo.messager.bubble.conversation.view.reader_detail.adapter.ReaderDetailAdapter
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.databinding.BubbleBottomReaderDetailViewBinding
import com.halo.widget.HaloLinearLayoutManager

class BubbleBottomReaderDetailView : BubbleBottomDialogAbs {

    private lateinit var binding: BubbleBottomReaderDetailViewBinding
    private var listReader: MutableList<MemberEntity>? = null
    private var adapter: ReaderDetailAdapter? = null
    private var listener: ChatReaderDetailListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override val listSize: Int?
        get() = listReader?.size

    override val contentLayout: FrameLayout?
        get() = binding.contentLayout


    override fun initializeBinding() {
        val inflater = LayoutInflater.from(context)
        binding = BubbleBottomReaderDetailViewBinding.inflate(inflater, this, false)
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun updateLayout() {
        super.updateLayout()
        initRec()
    }

    override fun initAction() {
        super.initAction()
        binding.closeBt.setOnClickListener {
            hideDialog()
        }
    }

    private fun initRec() {
        binding.apply {
            readerRec.layoutManager =
                HaloLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            readerRec.setHasFixedSize(false)
            adapter = ReaderDetailAdapter(listener, Glide.with(this@BubbleBottomReaderDetailView))
            readerRec.adapter = adapter
            adapter?.updateData(listReader)
        }
    }

    fun setListReader(listReader: MutableList<MemberEntity>?) {
        this.listReader = listReader
    }

    fun setListener(listener: ChatReaderDetailListener?) {
        this.listener = listener
    }

}