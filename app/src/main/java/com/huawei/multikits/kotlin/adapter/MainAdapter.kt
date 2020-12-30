/**
 *Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.multikits.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.multikits.R
import com.huawei.multikits.kotlin.bean.MainItemBean

class MainAdapter(list: List<MainItemBean>) : RecyclerView.Adapter<MainAdapter.Holder>() {
    private val mList: List<MainItemBean> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_recycleview, null)
        val holder = Holder(view)
        holder.view.setOnClickListener { view ->
            val position = holder.adapterPosition
            val id = mList[position].id
            onItemClickListener!!.onItemClick(view, id)
        }
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.mTvName.text = mList[position].name
        holder.mImageView.setImageResource(mList[position].picture)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    private lateinit var onItemClickListener: OnItemClickListener
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, id: String)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvName = itemView.findViewById(R.id.tv_name) as TextView
        val mImageView = itemView.findViewById(R.id.image_view) as ImageView
        val view = itemView
    }
}
