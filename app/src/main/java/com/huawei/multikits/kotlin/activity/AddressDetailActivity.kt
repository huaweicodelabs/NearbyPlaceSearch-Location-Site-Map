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
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchRequest
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import com.huawei.multikits.R
import com.huawei.multikits.kotlin.dialog.DialogManager
import com.huawei.multikits.kotlin.util.GetKeyUtil

class AddressDetailActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AddressDetailActivity"
    }

    private lateinit var searchService: SearchService
    private lateinit var mRlPhone: RelativeLayout
    private lateinit var mRlWeb: RelativeLayout
    private lateinit var siteId: String
    private lateinit var mTvSiteId: TextView
    private lateinit var mTvAddressName: TextView
    private lateinit var mTvFormatAddress: TextView
    private lateinit var mTvLat: TextView
    private lateinit var mTvLng: TextView
    private lateinit var apiKey: String
    private lateinit var categoryName: String
    private lateinit var site: Site
    private lateinit var mTitleImage: ImageView
    private lateinit var mMapFlag: ImageView
    private lateinit var mTvPhone: TextView
    private lateinit var mTvWeb: TextView
    private var destinationLat = 0.0
    private var destinationLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_detail)
        apiKey = GetKeyUtil.getApiKey()
        getIntentData()
        initView()
        clickEvent()
        searchDetail()
    }


    /**
     * Click event
     */
    private fun clickEvent() {
        mMapFlag.setOnClickListener {
            val intent = Intent(this@AddressDetailActivity, MapDetailActivity::class.java)
            intent.putExtra("destinationLat", destinationLat)
            intent.putExtra("destinationLng", destinationLng)
            startActivity(intent)
        }
    }

    /**
     * Obtains data
     */
    private fun getIntentData() {
        if (intent != null) {
            siteId = intent.getStringExtra("SITE_ID")!!
            categoryName = intent.getStringExtra("CATEGORY_NAME")!!
        }
    }

    /**
     * Initialize the view.
     */
    private fun initView() {
        mTvSiteId = findViewById(R.id.tv_site_id)
        mTitleImage = findViewById(R.id.address_image_detail)
        mTvAddressName = findViewById(R.id.tv_address_name)
        mTvFormatAddress = findViewById(R.id.tv_format_address)
        mRlPhone = findViewById(R.id.rl_phone)
        mRlWeb = findViewById(R.id.rl_web)
        mTvLat = findViewById(R.id.tv_lat)
        mTvLng = findViewById(R.id.tv_lng)
        mTvPhone = findViewById(R.id.tv_phone)
        mTvWeb = findViewById(R.id.tv_web)
        mMapFlag = findViewById(R.id.map_flag)
    }

    /**
     * Address details search
     */
    private fun searchDetail() {
        DialogManager.newInstance().showDialog(this)
        // Creating a SearchService Instance
        searchService = SearchServiceFactory.create(this, apiKey)
        // Create request body
        val request = DetailSearchRequest()
        request.siteId = siteId
        request.language = "fr"
        // Create Search Results Listener
        val resultListener: SearchResultListener<DetailSearchResponse?> =
                object : SearchResultListener<DetailSearchResponse?> {
                    // Normal result returned
                    override fun onSearchResult(result: DetailSearchResponse?) {
                        DialogManager.newInstance().dismissDialog()
                        if (result == null) {
                            return
                        }
                        site = result.site
                        if (site == null) {
                            return
                        }
                        try {
                            setUiContent(site)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Abnormal result returned
                    override fun onSearchError(status: SearchStatus) {
                        DialogManager.newInstance().dismissDialog()
                    }
                }
        // Invoke the location details interface.
        searchService.detailSearch(request, resultListener)
    }

    /**
     * Setting Data Display on the GUI
     *
     * @param site
     */
    private fun setUiContent(site: Site) {
        setTitleImage()
        val siteId = site.siteId
        mTvSiteId.text = siteId
        mTvAddressName.text = site.name
        val formatAddress = site.formatAddress
        mTvFormatAddress.text = formatAddress
        // Longitude
        destinationLat = site.location.lat
        mTvLat.text = destinationLat.toString()
        // Latitude
        destinationLng = site.location.lng
        mTvLng.text = destinationLng.toString()
        val phone = site.poi.phone
        if (!TextUtils.isEmpty(phone)) {
            mTvPhone.text = phone
        } else {
            mRlPhone.visibility = View.GONE
        }
        val websiteUrl = site.poi.websiteUrl
        if (!TextUtils.isEmpty(websiteUrl)) {
            mTvWeb.text = websiteUrl
        } else {
            mRlWeb.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.newInstance().dismissDialog()
    }

    /**
     * Set image display by category.
     */
    private fun setTitleImage() {
        when (categoryName) {
            "Supermarket" -> mTitleImage.setBackgroundResource(R.mipmap.supermarket_detail)
            "Food" -> mTitleImage.setBackgroundResource(R.mipmap.food_detail)
            "Hotel" -> mTitleImage.setBackgroundResource(R.mipmap.hotel_detail)
            "Malls" -> mTitleImage.setBackgroundResource(R.mipmap.mall_detail)
            "Hairdressing salon" -> mTitleImage.setBackgroundResource(R.mipmap.barbershop_detail)
            "Florists" -> mTitleImage.setBackgroundResource(R.mipmap.flower_shop_detail)
            "Theater" -> mTitleImage.setBackgroundResource(R.mipmap.movie_detail)
            "Pet shop" -> mTitleImage.setBackgroundResource(R.mipmap.pet_shop_detail)
            else -> mTitleImage.setBackgroundResource(R.mipmap.ic_launcher_round)
        }
    }
}