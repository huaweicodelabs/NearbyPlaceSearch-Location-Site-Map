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
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.*
import com.huawei.multikits.R
import com.huawei.multikits.java.util.GetKeyUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class MapDetailActivity : AppCompatActivity(), OnMapReadyCallback {


    companion object {
        private const val BUNDLE_KEY = "MapViewBundleKey"
        private const val TAG = "MapDetailActivity"
        private const val ROUTE_PLANNING_SUCCESS = 1
        private const val ROUTE_PLANNING_FAILED = 2
    }

    private val mPolyline: MutableList<Polyline> = ArrayList()
    private val mPaths: MutableList<List<LatLng>> = ArrayList()
    private lateinit var mMapView: MapView
    private lateinit var hMap: HuaweiMap
    private lateinit var apiKey: String
    private lateinit var mDriving: ImageView
    private lateinit var mBicycling: ImageView
    private lateinit var mWalking: ImageView
    private var mapViewBundle: Bundle? = null
    private var mLatLngBounds: LatLngBounds? = null
    private var destinationLat = 0.0
    private var destinationLng = 0.0
    private var mMarkerOrigin: Marker? = null
    private lateinit var mTime: TextView
    private lateinit var mDistance: TextView
    private lateinit var distanceText: String
    private lateinit var durationText: String
    private val mHandler: Handler = RefreshHandler(this)

    private class RefreshHandler(activity: MapDetailActivity) : Handler() {
        private var weakReference: WeakReference<MapDetailActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity = weakReference.get()
            if (activity == null || activity.isFinishing) {
                return
            }
            when (msg.what) {
                ROUTE_PLANNING_SUCCESS -> activity.renderRoute()
                ROUTE_PLANNING_FAILED -> {
                    val bundle = msg.data
                    val errorMsg = bundle.getString("errorMsg")
                    Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_detail)
        apiKey = GetKeyUtil.getApiKey()
        getIntentData()
        initViews()
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(BUNDLE_KEY)!!
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
        clickEvents()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(BUNDLE_KEY, mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    /**
     * Obtain the transferred data.
     */
    private fun getIntentData() {
        if (intent != null) {
            destinationLat = intent.getDoubleExtra("destinationLat", 48.893478)
            destinationLng = intent.getDoubleExtra("destinationLng", 2.334595)
        }
    }


    /**
     * Initialize the view.
     */
    private fun initViews() {
        mMapView = findViewById(R.id.mapview)
        mDriving = findViewById(R.id.img_route_driving)
        mBicycling = findViewById(R.id.img_route_bicycling)
        mWalking = findViewById(R.id.img_route_walking)
        mTime = findViewById(R.id.tv_time)
        mDistance = findViewById(R.id.tv_distance)
    }

    override fun onMapReady(map: HuaweiMap) {
        hMap = map
        val options = MarkerOptions()
        val destinationLatLng = LatLng(destinationLat, destinationLng)
        options.position(destinationLatLng)
        options.anchor(0.5f, 0.9f).anchorMarker(0.5f, 0.9f)
        hMap.addMarker(options)
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15f))
    }

    /**
     * Click Event
     */
    private fun clickEvents() {
        mDriving.setOnClickListener {
            val url = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/driving?key=$apiKey"
            planningPaths(url)
        }
        mBicycling.setOnClickListener {
            val url = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/bicycling?key=$apiKey"
            planningPaths(url)
        }
        mWalking.setOnClickListener {
            val url = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/walking?key=$apiKey"
            planningPaths(url)
        }
    }

    /**
     * Path planning interface request
     *
     * @param url
     */
    private fun planningPaths(url: String) {
        removePolyline()
        val json = JSONObject()
        val origin = JSONObject()
        val destination = JSONObject()
        try {
            origin.put("lng", 2.334595)
            origin.put("lat", 48.893478)
            destination.put("lng", destinationLng)
            destination.put("lat", destinationLat)
            json.put("origin", origin)
            json.put("destination", destination)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val client = OkHttpClient()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = Message.obtain()
                val bundle = Bundle()
                bundle.putString("errorMsg", e.message)
                msg.what = ROUTE_PLANNING_FAILED
                msg.data = bundle
                mHandler.sendMessage(msg)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val json = response.body!!.string()
                    generateRoute(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * Parse the returned data.
     *
     * @param json
     */
    private fun generateRoute(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val routes = jsonObject.optJSONArray("routes")
            if (null == routes || routes.length() == 0) {
                return
            }
            val route = routes.getJSONObject(0)
            // get route bounds
            val bounds = route.optJSONObject("bounds")
            if (null != bounds && bounds.has("southwest") && bounds.has("northeast")) {
                val southwest = bounds.optJSONObject("southwest")
                val northeast = bounds.optJSONObject("northeast")
                val sw = LatLng(southwest.optDouble("lat"), southwest.optDouble("lng"))
                val ne = LatLng(northeast.optDouble("lat"), northeast.optDouble("lng"))
                mLatLngBounds = LatLngBounds(sw, ne)
            }
            // get paths
            val paths = route.optJSONArray("paths")!!
            for (i in 0 until paths.length()) {
                val path = paths.optJSONObject(i)
                val mPath: MutableList<LatLng> = ArrayList()
                val steps = path.optJSONArray("steps")
                distanceText = path.getString("distanceText")
                durationText = path.getString("durationText")
                assert(steps != null)
                for (j in 0 until steps!!.length()) {
                    val step = steps.optJSONObject(j)
                    val polyline = step.optJSONArray("polyline")!!
                    for (k in 0 until polyline.length()) {
                        if (j > 0 && k == 0) {
                            continue
                        }
                        val line = polyline.getJSONObject(k)
                        val lat = line.optDouble("lat")
                        val lng = line.optDouble("lng")
                        val latLng = LatLng(lat, lng)
                        mPath.add(latLng)
                    }
                }
                mPaths.add(i, mPath)
            }
            mHandler.sendEmptyMessage(ROUTE_PLANNING_SUCCESS)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderRoute() {
        mTime.text = this.resources.getString(R.string.route_planning_time) + durationText
        mDistance.text = this.resources.getString(R.string.route_planning_distance) + distanceText
        renderRoute(mPaths, mLatLngBounds)
    }

    /**
     * Path Planning
     *
     * @param paths
     * @param latLngBounds
     */
    private fun renderRoute(paths: List<List<LatLng>>?, latLngBounds: LatLngBounds?) {
        if (null == paths || paths.isEmpty() || paths[0].isEmpty()) {
            return
        }
        for (i in paths.indices) {
            val path = paths[i]
            val options = PolylineOptions().color(Color.BLUE).width(5f)
            for (latLng in path) {
                options.add(latLng)
            }
            val polyline = hMap.addPolyline(options)
            mPolyline.add(i, polyline)
        }
        // Draw the start point marker.
        addOriginMarker(paths[0][0])
        if (null != latLngBounds) {
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 15)
            hMap.moveCamera(cameraUpdate)
        } else {
            val latLngOrigin = LatLng(48.893478, 2.334595)
            val latLngDestination = LatLng(destinationLat, destinationLng)
            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(latLngOrigin)
            boundsBuilder.include(latLngDestination)
            hMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15))
        }
    }

    /**
     * Add marker
     *
     * @param latLng
     */
    private fun addOriginMarker(latLng: LatLng) {
        if (null != mMarkerOrigin) {
            mMarkerOrigin!!.remove()
        }
        mMarkerOrigin = hMap.addMarker(
                MarkerOptions().position(latLng).anchor(0.5f, 0.9f).anchorMarker(0.5f, 0.9f)
        )
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    /**
     * Remove Path
     */
    private fun removePolyline() {
        for (polyline in mPolyline) {
            polyline.remove()
        }
        mPolyline.clear()
        mPaths.clear()
        mLatLngBounds = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}