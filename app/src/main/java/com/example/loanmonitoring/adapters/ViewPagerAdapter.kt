package com.example.loanmonitoring.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val fragmentList: List<Fragment>,
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}