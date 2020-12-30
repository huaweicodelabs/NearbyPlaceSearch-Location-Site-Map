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
import com.huawei.hms.site.api.model.Site
import com.huawei.multikits.R

public class AddressListAdapter(private var sites: List<Site>, var categoryName: String) :
    RecyclerView.Adapter<AddressListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address_list, null)
        val holder = Holder(view)
        holder.view.setOnClickListener { view ->
            val position = holder.adapterPosition
            val siteId = sites!![position].siteId
            onItemClickListener!!.onItemClick(view, siteId)
        }
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.mName.text = sites[position].name
        setCategoryImage(categoryName, holder.mAddressImage)
        holder.mDetail.text = sites[position].formatAddress
        holder.mDistance.text = sites[position].distance.toString() + "m"
    }

    private fun setCategoryImage(categoryName: String, mAddressImage: ImageView) {
        when (categoryName) {
            "Supermarket" -> mAddressImage.setBackgroundResource(R.mipmap.supermarket)
            "Food" -> mAddressImage.setBackgroundResource(R.mipmap.food)
            "Hotel" -> mAddressImage.setBackgroundResource(R.mipmap.hotel)
            "Malls" -> mAddressImage.setBackgroundResource(R.mipmap.mall)
            "Hairdressing salon" -> mAddressImage.setBackgroundResource(R.mipmap.barbershop)
            "Florists" -> mAddressImage.setBackgroundResource(R.mipmap.flower_shop)
            "Theater" -> mAddressImage.setBackgroundResource(R.mipmap.movie)
            "Pet shop" -> mAddressImage.setBackgroundResource(R.mipmap.pet_shop)
            else -> mAddressImage.setBackgroundResource(R.mipmap.ic_launcher_round)
        }
    }

    override fun getItemCount(): Int {
        return sites?.size ?: 0
    }

    private lateinit var onItemClickListener: OnItemClickListener
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, siteId: String)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mName: TextView = itemView.findViewById(R.id.name)
        val mDetail: TextView = itemView.findViewById(R.id.detail)
        val mDistance: TextView = itemView.findViewById(R.id.distance)
        val mAddressImage: ImageView = itemView.findViewById(R.id.address_image)
        val view: View = itemView
    }
}
