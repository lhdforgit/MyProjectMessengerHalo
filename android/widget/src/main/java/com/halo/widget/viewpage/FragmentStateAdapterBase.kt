/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.viewpage

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

open class FragmentStateAdapterBase(fact: FragmentActivity) : FragmentStateAdapter(fact) {
    private val listFragment = mutableListOf<Fragment>()
    override fun getItemCount(): Int {
        return listFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }

    fun addFragment(fragment: Fragment) {
        listFragment.add(fragment)
    }

    fun listFragment(vararg fragment: Fragment) {
        listFragment.addAll(fragment)
    }

    fun getView(position: Int): View? {
        return listFragment[position].view
    }
}