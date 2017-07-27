package com.app.dr1009.githubsearch


import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.dr1009.githubsearch.databinding.LayoutCardBinding

class RecyclerAdapter(private val mCardList: List<Card>) : RecyclerView.Adapter<RecyclerAdapter.BindingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_card, parent, false)
        return BindingHolder(v)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        val card = mCardList[position]
        holder.getBinding().setVariable(BR.card, card)
        holder.getBinding().executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mCardList.size
    }

    class BindingHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mBinding = DataBindingUtil.bind<LayoutCardBinding>(view)
        fun getBinding(): ViewDataBinding {
            return mBinding
        }
    }
}

