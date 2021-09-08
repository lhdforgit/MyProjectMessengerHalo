package com.hahalolo.incognito.presentation.main.search

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.common.base.Preconditions
import com.google.common.base.Strings
import java.util.*

class IncognitoSearchPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return Strings.nullToEmpty(titles[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(Preconditions.checkNotNull(fragment))
        titles.add(title)
    }
}
