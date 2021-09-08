/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.scribbles.sticker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.google.gson.Gson
import com.halo.common.utils.ktx.viewModels
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.editor.R
import com.halo.editor.databinding.ImageEditorStickerActBinding
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.repository.sticker.StickerRepository
import com.halo.widget.sticker.sticker_group.StickerGroupView.StickerGroupListener
import javax.inject.Inject

class ImageEditorStickerSelectActivity : com.hahalolo.playcore.split.DaggerSplitActivity(),
    StickerGroupListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    lateinit var binding: ImageEditorStickerActBinding
    private val viewModel by viewModels<ImageEditorStickerSelectViewModel> { factory }

    private var adapter: ImageEditorStickerAdapter? = null
    private var requestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.image_editor_sticker_act)
        requestManager = Glide.with(this)
        try {
            initPager()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initPager() {
        viewModel.getListPack(this).observe(
            this,
            Observer { packs ->
                if (packs != null) {
                    if (adapter == null) {
                        adapter =
                            ImageEditorStickerAdapter(
                                requestManager ?: Glide.with(this),
                                this
                            )
                    }
                    binding.viewPager.adapter = adapter
                    adapter?.packs = packs
                    TabLayoutMediator(
                        binding.tab,
                        binding.viewPager,
                        TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                            if (position >= 0 && position < packs.size) {
                                val imageView =
                                    ImageView(this@ImageEditorStickerSelectActivity)
                                imageView.layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                requestManager
                                    ?.asDrawable()
                                    ?.load(packs[position].image)
                                    ?.centerInside()
                                    ?.into(imageView)
                                tab.customView = imageView
                            }
                        }
                    ).attach()
                }
            }
        )
    }

    override fun onClickStickerItem(stickerEntity: StickerEntity) {
        Log.i("======", Gson().toJson(stickerEntity))
        val intent = Intent()
        intent.putExtra(STICKER_SELECT_FROM_ASSETS_RESULT, stickerEntity.image)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onClickGifCardItem(gifCard: GifCard) {

    }

    override fun stickerRepository(): StickerRepository? {
        return null
    }

    override fun lifecycleOwner(): LifecycleOwner {
        return this
    }

    companion object {

        const val STICKER_SELECT_FROM_ASSETS_RESULT = "STICKER_SELECT_FROM_ASSETS_RESULT"
    }
}