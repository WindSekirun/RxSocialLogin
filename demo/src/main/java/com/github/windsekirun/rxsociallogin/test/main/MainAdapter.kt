package com.github.windsekirun.rxsociallogin.test.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.github.windsekirun.rxsociallogin.intenal.model.PlatformType
import com.github.windsekirun.rxsociallogin.intenal.utils.weak
import com.github.windsekirun.rxsociallogin.test.R
import com.github.windsekirun.rxsociallogin.test.databinding.MainItemBinding

/**
 * RxSocialLogin
 * Class: MainAdapter
 * Created by Pyxis on 2019-02-07.
 *
 *
 * Description:
 */
class MainAdapter : RecyclerView.Adapter<MainAdapter.BindingViewHolder<MainItemBinding>>() {
    private lateinit var binding: MainItemBinding
    private var context: Context? by weak(null)
    private var itemList = ArrayList<PlatformType>()
    private var clickListener: ((PlatformType, Int) -> Unit)? = null

    init {
        itemList.clear()
        itemList.addAll(PlatformType.values())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<MainItemBinding> {
        context = parent.context
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.main_item, parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<MainItemBinding>, position: Int) {
        holder.binding.type = itemList[position]
        holder.binding.root.setOnClickListener {
            clickListener?.invoke(itemList[position], position)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = itemList.size

    fun setOnClickListener(listener: (PlatformType, Int) -> Unit) {
        clickListener = listener
    }

    class BindingViewHolder<out V : ViewDataBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)
}
