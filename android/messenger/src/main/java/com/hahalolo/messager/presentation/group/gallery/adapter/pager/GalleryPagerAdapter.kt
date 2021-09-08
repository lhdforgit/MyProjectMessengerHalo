/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery.adapter.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Strings
import java.util.*

class GalleryPagerAdapter (fm: FragmentManager)  : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  {

    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return Strings.nullToEmpty(titles[position])
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(checkNotNull(fragment))
        titles.add(title)
    }
}
