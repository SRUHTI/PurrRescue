package com.sruthi.purrrescue.base

import androidx.fragment.app.Fragment
import com.sruthi.purrrescue.ui.main.MainActivity

abstract class BaseFragment: Fragment() {

    fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }

    fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }
}