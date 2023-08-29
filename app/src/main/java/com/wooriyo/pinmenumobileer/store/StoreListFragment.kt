package com.wooriyo.pinmenumobileer.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StoreListFragment : Fragment() {

    companion object {
        var fragment : StoreListFragment? = null

        fun newInstance(): StoreListFragment {
            if(fragment == null) {
                fragment = StoreListFragment()
            }
            val args = Bundle()

            fragment!!.arguments = args
            return fragment!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}