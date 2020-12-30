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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.multikits.R;
import com.huawei.multikits.java.adapter.AddressListAdapter;
import com.huawei.multikits.java.dialog.DialogManager;
import com.huawei.multikits.java.util.GetKeyUtil;

import java.util.List;


public class AddressListActivity extends Activity {
    private static final String TAG = "AddressListActivity";
    private RecyclerView mRecyclerView;
    private SearchService searchService;
    private String apiKey;
    private String categoryName;
    private List<Site> sites;
    private AddressListAdapter addressListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        apiKey = GetKeyUtil.getApiKey();
        initView();
        getIntentData();
        queryData();
    }

    /**
     * Initializing the View
     */
    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
    }

    /**
     * Search result query
     */
    private void queryData() {
        DialogManager.newInstance().showDialog(this);
        // Creating a SearchService Instance
        searchService = SearchServiceFactory.create(this, apiKey);
        // Create request body
        NearbySearchRequest request = new NearbySearchRequest();
        Coordinate location = new Coordinate(48.893478, 2.334595);
        request.setLocation(location);
        request.setQuery(categoryName);
        request.setRadius(1000);
        request.setHwPoiType(HwLocationType.ADDRESS);
        request.setLanguage("fr");
        request.setPageIndex(1);
        request.setPageSize(5);
        // Create Search Results Listener
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Normal result returned
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                DialogManager.newInstance().dismissDialog();
                if (results == null || results.getTotalCount() <= 0) {
                    return;
                }
                sites = results.getSites();
                if (sites == null || sites.size() == 0) {
                    return;
                }
                setListAdapter();
            }

            // Abnormal result returned
            @Override
            public void onSearchError(SearchStatus status) {
                DialogManager.newInstance().dismissDialog();
            }
        };
        // Invoke the peripheral search interface.
        searchService.nearbySearch(request, resultListener);
    }

    /**
     * Display List Data
     */
    private void setListAdapter() {
        addressListAdapter = new AddressListAdapter(sites, categoryName);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
        mRecyclerView.setAdapter(addressListAdapter);
        addressListAdapter.setOnItemClickListener(new AddressListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String siteId) {
                Intent intent = new Intent(AddressListActivity.this, AddressDetailActivity.class);
                intent.putExtra("SITE_ID", siteId);
                intent.putExtra("CATEGORY_NAME", categoryName);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogManager.newInstance().dismissDialog();
    }

    /**
     * Obtains the transmitted data.
     */
    private void getIntentData() {
        if (getIntent() != null) {
            categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        }
    }
}
