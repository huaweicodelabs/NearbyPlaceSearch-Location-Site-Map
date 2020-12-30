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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.huawei.multikits.R;
import com.huawei.multikits.java.bean.MainItemBean;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Holder> {
    private List<MainItemBean> mList;

    public MainAdapter(List<MainItemBean> list) {
        this.mList = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_recycleview, null);
        final Holder holder = new Holder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String id = mList.get(position).getId();
                onItemClickListener.onItemClick(view, id);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (null != mList && mList.size() > 0) {
            holder.mTvName.setText(mList.get(position).getName());
            holder.mImageView.setImageResource(mList.get(position).getPicture());
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String id);
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private ImageView mImageView;
        private View view;

        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            mTvName = itemView.findViewById(R.id.tv_name);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }
}
