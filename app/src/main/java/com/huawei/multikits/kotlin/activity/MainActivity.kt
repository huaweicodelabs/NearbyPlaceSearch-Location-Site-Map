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

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import com.huawei.multikits.R
import com.huawei.multikits.kotlin.adapter.MainAdapter
import com.huawei.multikits.kotlin.bean.MainItemBean
import com.huawei.multikits.kotlin.dialog.DialogManager
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val GET_DETAIL_ADDRESS = 0
    }

    private lateinit var mAddress: String
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAddressList: MutableList<MainItemBean>
    private lateinit var mTvAddress: TextView
    private lateinit var adapter: MainAdapter
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mProviderClient: FusedLocationProviderClient


    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == GET_DETAIL_ADDRESS) {
                mAddress = msg.obj.toString()
                mTvAddress.text = mAddress
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initViews()
        initLocationRequest()
        requestLocationUpdates()
    }

    /**
     * Initialize data.
     */
    private fun initData() {
        mAddressList = ArrayList()
        mAddressList.add(MainItemBean("Supermarket",resources.getString(R.string.main_page_supermarket), R.mipmap.supermarket_icon))
        mAddressList.add(MainItemBean("Hotel", resources.getString(R.string.main_page_hotel), R.mipmap.hotel_icon))
        mAddressList.add(MainItemBean("Food", resources.getString(R.string.main_page_restaurant), R.mipmap.food_icon))
        mAddressList.add(MainItemBean("Malls", resources.getString(R.string.main_page_shopping_mall), R.mipmap.mall_icon))
        mAddressList.add(MainItemBean("Hairdressing salon", resources.getString(R.string.main_page_hair_care), R.mipmap.barbershop_icon))
        mAddressList.add(MainItemBean("Theater", resources.getString(R.string.main_page_movie), R.mipmap.movie_icon))
        mAddressList.add(MainItemBean("Florists", resources.getString(R.string.main_page_flower), R.mipmap.flower_shop_icon))
        mAddressList.add(MainItemBean("Pet shop", resources.getString(R.string.main_page_pet), R.mipmap.pet_shop_icon))
    }

    /**
     * Request Location
     */
    private fun requestLocationUpdates() {
        DialogManager.newInstance().showDialog(this, resources.getString(R.string.common_locating))
        val builder = LocationSettingsRequest.Builder()
        mLocationRequest = LocationRequest()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()
        mProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        // Check the device location settings.
        mSettingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    // Set the location conditions to be met and then initiate a location request.
                    mProviderClient.requestLocationUpdates(
                            mLocationRequest,
                            initLocationCallback(),
                            Looper.getMainLooper()
                    ) // Listening callback for successful location update request interface
                            .addOnSuccessListener {
                                DialogManager.newInstance().dismissDialog()
                            }
                }.addOnFailureListener { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(this@MainActivity, 0)
                        } catch (sie: SendIntentException) {
                        }
                    }
                }
    }

    /**
     * Initialize the location request.
     */
    private fun initLocationRequest() {
        mLocationRequest = LocationRequest()
        //Sets the interval for location update (unit: Millisecond)
        mLocationRequest.interval = 10000
        //Sets the priority
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Obtains the location callback function LocationCallback.
     *
     * @return
     */
    private fun initLocationCallback(): LocationCallback {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult != null) {
                    val locations = locationResult.locations
                    if (null != locations && locations.size > 0) {
                        val location = locations[0]
                        val longitude = location.longitude
                        val latitude = location.latitude
                        // Enable the subthread to invoke the reverse geocoding capability to obtain location information.
                        val geocode = Geocoder(this@MainActivity, Locale.SIMPLIFIED_CHINESE)
                        // 启用子线程调用逆地理编码能力，获取位置信息
                        Thread {
                            try {
                                // Reverse geocoding address
                                val addresses = geocode.getFromLocation(latitude, longitude, 1)
                                if (null != addresses && addresses.size > 0) {
                                    // After the address information is updated successfully, use the handler to update the UI.
                                    for (address in addresses) {
                                        val msg = Message()
                                        msg.what = GET_DETAIL_ADDRESS
                                        msg.obj = addresses[0].featureName
                                        handler.sendMessage(msg)
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }.start()
                    }
                }
            }
        }
        return mLocationCallback
    }


    /**
     * Initialize the view.
     */
    private fun initViews() {
        mRecyclerView = findViewById(R.id.recyclerview)
        mTvAddress = findViewById(R.id.address)
        adapter = MainAdapter(mAddressList)
        mRecyclerView.setLayoutManager(GridLayoutManager(this, 4))
        mRecyclerView.setAdapter(adapter)
        // Item Click Event
        adapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener {
            override fun onItemClick(view: View, id: String) {
                val intent = Intent(this@MainActivity, AddressListActivity::class.java)
                intent.putExtra("CATEGORY_NAME", id)
                startActivity(intent)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
        DialogManager.newInstance().dismissDialog()
    }

    /**
     * Remove Location Request
     */
    private fun removeLocationUpdates() {
        if (null != mProviderClient && null != mLocationCallback) {
            mProviderClient.removeLocationUpdates(mLocationCallback).addOnSuccessListener {
            }.addOnFailureListener {
            }
        }
    }
}

