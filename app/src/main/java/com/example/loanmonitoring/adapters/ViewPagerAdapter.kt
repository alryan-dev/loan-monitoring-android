package com.example.loanmonitoring.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    parentFragment: Fragment,
    private val fragmentList: List<Fragment>,
) :
    FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}