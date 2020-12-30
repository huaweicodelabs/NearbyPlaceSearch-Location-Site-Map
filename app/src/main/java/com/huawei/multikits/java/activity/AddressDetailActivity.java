/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.multikits.java.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.DetailSearchRequest;
import com.huawei.hms.site.api.model.DetailSearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.multikits.R;
import com.huawei.multikits.java.dialog.DialogManager;
import com.huawei.multikits.java.util.GetKeyUtil;

public class AddressDetailActivity extends Activity {

    private static final String TAG = "AddressDetailActivity";
    private SearchService searchService;
    private RelativeLayout mRlPhone;
    private RelativeLayout mRlWeb;
    private String siteId;
    private TextView mTvSiteId;
    private TextView mTvAddressName;
    private TextView mTvFormatAddress;
    private TextView mTvLat;
    private TextView mTvLng;
    private String apiKey;
    private String categoryName;
    private Site site;
    private ImageView mTitleImage;
    private ImageView mMapFlag;
    private TextView mTvPhone;
    private TextView mTvWeb;
    private double destinationLat;
    private double destinationLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);
        DialogManager.newInstance().showDialog(this);
        apiKey = GetKeyUtil.getApiKey();
        getIntentData();
        initView();
        clickEvent();
        searchDetail();
    }

    /**
     * Click event
     */
    private void clickEvent() {
        mMapFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressDetailActivity.this, MapDetailActivity.class);
                intent.putExtra("destinationLat", destinationLat);
                intent.putExtra("destinationLng", destinationLng);
                startActivity(intent);
            }
        });
    }

    /**
     * Obtains data
     */
    private void getIntentData() {
        if(null!= getIntent()){
            siteId = getIntent().getStringExtra("SITE_ID");
            categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        }
    }

    /**
     * Initialize the view.
     */
    private void initView() {
        mTvSiteId = findViewById(R.id.tv_site_id);
        mTitleImage = findViewById(R.id.address_image_detail);
        mTvAddressName = findViewById(R.id.tv_address_name);
        mTvFormatAddress = findViewById(R.id.tv_format_address);
        mRlPhone = findViewById(R.id.rl_phone);
        mRlWeb = findViewById(R.id.rl_web);
        mTvLat = findViewById(R.id.tv_lat);
        mTvLng = findViewById(R.id.tv_lng);
        mTvPhone = findViewById(R.id.tv_phone);
        mTvWeb = findViewById(R.id.tv_web);
        mMapFlag = findViewById(R.id.map_flag);
    }

    /**
     * Address details search
     */
    private void searchDetail() {
        // Creating a SearchService Instance
        searchService = SearchServiceFactory.create(this, apiKey);
        // Create request body
        DetailSearchRequest request = new DetailSearchRequest();
        request.setSiteId(siteId);
        request.setLanguage("fr");
        // Create Search Results Listener
        SearchResultListener<DetailSearchResponse> resultListener = new SearchResultListener<DetailSearchResponse>() {
            // Normal result returned
            @Override
            public void onSearchResult(DetailSearchResponse result) {
                DialogManager.newInstance().dismissDialog();
                if (result == null) {
                    return;
                }
                site = result.getSite();
                if (site == null) {
                    return;
                }
                try {
                    setUiContent(site);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Abnormal result returned
            @Override
            public void onSearchError(SearchStatus status) {
                DialogManager.newInstance().dismissDialog();
            }
        };
        // Invoke the location details interface.
        searchService.detailSearch(request, resultListener);
    }

    /**
     * Setting Data Display on the GUI
     *
     * @param site
     */
    private void setUiContent(Site site) {
        setTitleImage();
        String siteId = site.getSiteId();
        mTvSiteId.setText(siteId);
        mTvAddressName.setText(site.getName());
        String formatAddress = site.getFormatAddress();
        mTvFormatAddress.setText(formatAddress);
        // Longitude
        destinationLat = site.getLocation().getLat();
        mTvLat.setText(String.valueOf(destinationLat));
        // Latitude
        destinationLng = site.getLocation().getLng();
        mTvLng.setText(String.valueOf(destinationLng));
        String phone = site.getPoi().getPhone();
        if (!TextUtils.isEmpty(phone)) {
            mTvPhone.setText(phone);
        } else {
            mRlPhone.setVisibility(View.GONE);
        }
        String websiteUrl = site.getPoi().getWebsiteUrl();
        if (!TextUtils.isEmpty(websiteUrl)) {
            mTvWeb.setText(websiteUrl);
        } else {
            mRlWeb.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogManager.newInstance().dismissDialog();
    }

    /**
     * Set image display by category.
     */
    private void setTitleImage() {
        switch (categoryName) {
            case "Supermarket":
                mTitleImage.setBackgroundResource(R.mipmap.supermarket_detail);
                break;
            case "Food":
                mTitleImage.setBackgroundResource(R.mipmap.food_detail);
                break;
            case "Hotel":
                mTitleImage.setBackgroundResource(R.mipmap.hotel_detail);
                break;
            case "Malls":
                mTitleImage.setBackgroundResource(R.mipmap.mall_detail);
                break;
            case "Hairdressing salon":
                mTitleImage.setBackgroundResource(R.mipmap.barbershop_detail);
                break;
            case "Florists":
                mTitleImage.setBackgroundResource(R.mipmap.flower_shop_detail);
                break;
            case "Theater":
                mTitleImage.setBackgroundResource(R.mipmap.movie_detail);
                break;
            case "Pet shop":
                mTitleImage.setBackgroundResource(R.mipmap.pet_shop_detail);
                break;
            default:
                mTitleImage.setBackgroundResource(R.mipmap.ic_launcher_round);
                break;
        }
    }
}
