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


package com.huawei.multikits.java.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.huawei.hms.site.api.model.Site;
import com.huawei.multikits.R;
import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.Holder> {
    private List<Site> sites;
    private String categoryName;

    public AddressListAdapter(List<Site> sites, String categoryName) {
        this.sites = sites;
        this.categoryName = categoryName;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_list, null);
        final Holder holder = new Holder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String siteId = sites.get(position).getSiteId();
                onItemClickListener.onItemClick(view, siteId);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (null != sites && sites.size() > 0) {
            if (TextUtils.isEmpty(categoryName)) {
                return;
            }
            setCategoryImage(categoryName, holder.mAddressImage);
            holder.mName.setText(sites.get(position).getName());
            holder.mDetail.setText(sites.get(position).getFormatAddress());
            holder.mDistance.setText(String.valueOf(sites.get(position).getDistance()) + "m");
        }
    }

    private void setCategoryImage(String categoryName, ImageView mAddressImage) {
        switch (categoryName) {
            case "Supermarket":
                mAddressImage.setBackgroundResource(R.mipmap.supermarket);
                break;
            case "Food":
                mAddressImage.setBackgroundResource(R.mipmap.food);
                break;
            case "Hotel":
                mAddressImage.setBackgroundResource(R.mipmap.hotel);
                break;
            case "Malls":
                mAddressImage.setBackgroundResource(R.mipmap.mall);
                break;
            case "Hairdressing salon":
                mAddressImage.setBackgroundResource(R.mipmap.barbershop);
                break;
            case "Florists":
                mAddressImage.setBackgroundResource(R.mipmap.flower_shop);
                break;
            case "Theater":
                mAddressImage.setBackgroundResource(R.mipmap.movie);
                break;
            case "Pet shop":
                mAddressImage.setBackgroundResource(R.mipmap.pet_shop);
                break;
            default:
                mAddressImage.setBackgroundResource(R.mipmap.ic_launcher_round);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sites == null ? 0 : sites.size();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String siteId);
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mDetail;
        private TextView mDistance;
        private ImageView mAddressImage;
        private View view;

        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            mName = itemView.findViewById(R.id.name);
            mDetail = itemView.findViewById(R.id.detail);
            mDistance = itemView.findViewById(R.id.distance);
            mAddressImage = itemView.findViewById(R.id.address_image);
        }
    }
}
