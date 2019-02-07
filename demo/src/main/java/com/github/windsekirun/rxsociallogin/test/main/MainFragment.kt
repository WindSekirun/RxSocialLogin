package com.github.windsekirun.rxsociallogin.test.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.windsekirun.rxsociallogin.RxSocialLogin
import com.github.windsekirun.rxsociallogin.test.R
import com.github.windsekirun.rxsociallogin.test.databinding.MainFragmentBinding
import com.github.windsekirun.rxsociallogin.test.dialog.ResultDialogFragment
import com.github.windsekirun.rxsociallogin.test.utils.subscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

/**
 * RxSocialLogin
 * Class: MainFragment
 * Created by Pyxis on 2019-02-07.
 *
 * Description:
 */
class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxSocialLogin.initialize(requireActivity())

        // observe result of RxSocialLogin
        RxSocialLogin.result()
                .subscribe { item, throwable ->
                    if (item == null) {
                        showErrorDialog(throwable)
                        return@subscribe
                    }

                    ResultDialogFragment.show(requireActivity(), item)
                }.addTo(compositeDisposable)


        // set inner content of RecyclerView
        val adapter = MainAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter.notifyDataSetChanged()

        adapter.setOnClickListener { platformType, _ ->
            RxSocialLogin.login(platformType)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun showErrorDialog(throwable: Throwable?) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("Login failed... ${throwable?.message ?: "Unknown Error"}")
            show()
        }
    }
}
