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

package com.huawei.multikits.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import com.huawei.multikits.R
import com.huawei.multikits.kotlin.adapter.AddressListAdapter
import com.huawei.multikits.kotlin.dialog.DialogManager
import com.huawei.multikits.kotlin.util.GetKeyUtil

class AddressListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AddressListActivity"
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var searchService: SearchService
    private lateinit var apiKey: String
    private lateinit var categoryName: String
    private lateinit var sites: List<Site>
    private lateinit var addressListAdapter: AddressListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        apiKey = GetKeyUtil.getApiKey()
        initView()
        getIntentData()
        queryData()
    }

    /**
     * Initializing the View
     */
    private fun initView() {
        mRecyclerView = findViewById(R.id.recyclerview)
    }

    /**
     * Search result query
     */
    private fun queryData() {
        DialogManager.newInstance().showDialog(this)
        // Creating a SearchService Instance
        searchService = SearchServiceFactory.create(this, apiKey)
        // Create request body
        val request = NearbySearchRequest()
        val location = Coordinate(48.893478, 2.334595)
        request.location = location
        request.query = categoryName
        request.radius = 1000
        request.hwPoiType = HwLocationType.ADDRESS
        request.language = "fr"
        request.pageIndex = 1
        request.pageSize = 5
        // Create Search Results Listener
        val resultListener: SearchResultListener<NearbySearchResponse?> =
                object : SearchResultListener<NearbySearchResponse?> {
                    // Normal result returned
                    override fun onSearchResult(results: NearbySearchResponse?) {
                        DialogManager.newInstance().dismissDialog()
                        if (results == null || results.totalCount <= 0) {
                            return
                        }
                        sites = results.sites
                        if (sites == null || sites!!.size == 0) {
                            return
                        }
                        setListAdapter()
                    }

                    // Abnormal result returned
                    override fun onSearchError(status: SearchStatus) {
                    }
                }
        // Invoke the peripheral search interface.
        searchService.nearbySearch(request, resultListener)
    }

    /**
     * Display List Data
     */
    private fun setListAdapter() {
        addressListAdapter = AddressListAdapter(sites, categoryName)
        mRecyclerView.layoutManager = LinearLayoutManager(this@AddressListActivity)
        mRecyclerView.adapter = addressListAdapter
        addressListAdapter.setOnItemClickListener(object : AddressListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, siteId: String) {
                val intent = Intent(this@AddressListActivity, AddressDetailActivity::class.java)
                intent.putExtra("SITE_ID", siteId)
                intent.putExtra("CATEGORY_NAME", categoryName)
                startActivity(intent)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.newInstance().dismissDialog()
    }

    /**
     * Obtains the transmitted data.
     */
    private fun getIntentData() {
        if (intent != null) {
            categoryName = intent.getStringExtra("CATEGORY_NAME")!!
        }
    }
}