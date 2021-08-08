package com.airqualitymonitor.main

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airqualitymonitor.R
import com.airqualitymonitor.databinding.DataItemsBinding


class Adapter(private val listener: ArticleItemListener) :
    RecyclerView.Adapter<DataViewHolder>() {

    interface ArticleItemListener {
        fun onClickedArticle(index: Int)
    }

    private var list: List<Cities> = ArrayList()

    fun setData(data: List<Cities>) {
        list = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding: DataItemsBinding =
            DataItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(list[position], position)

    override fun getItemCount(): Int = list.size
}

class DataViewHolder(
    private val itemBinding: DataItemsBinding,
    private val listener: Adapter.ArticleItemListener,
) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

    private lateinit var article: Cities

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(dataItem: Cities, position: Int) {
        itemBinding.root.setTag(R.string.app_name, position)
        try {

            article = dataItem
            itemBinding.title.text = dataItem.city
            itemBinding.subTitle.text =
                Html.fromHtml(
                    "Air Quality: ${dataItem.aqi} <b>(${dataItem.status.second})</b>"
                )
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, intArrayOf(
                    Color.parseColor(dataItem.status.first),
                    Color.parseColor("#ffffff")
                )
            )
            gradientDrawable.cornerRadius = 0f
            itemBinding.parentView.background = gradientDrawable


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        listener.onClickedArticle(v?.getTag(R.string.app_name) as Int)
    }
}