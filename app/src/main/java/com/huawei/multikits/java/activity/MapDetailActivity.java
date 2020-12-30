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

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.multikits.R;
import com.huawei.multikits.java.util.GetKeyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "MainActivity";
    private static final int ROUTE_PLANNING_SUCCESS = 1;
    private static final int ROUTE_PLANNING_FAILED = 2;
    private List<Polyline> mPolyline = new ArrayList<>();
    private List<List<LatLng>> mPaths = new ArrayList<>();
    private MapView mMapView;
    private HuaweiMap hMap;
    private String apiKey;
    private ImageView mDriving;
    private ImageView mBicycling;
    private ImageView mWalking;
    private Bundle mapViewBundle;
    private LatLngBounds mLatLngBounds;
    private double destinationLat;
    private double destinationLng;
    private Marker mMarkerOrigin;
    private TextView mTime;
    private TextView mDistance;
    private String distanceText;
    private String durationText;
    private Handler mHandler = new RefreshHandler(this);

    private static class RefreshHandler extends Handler {
        private WeakReference<MapDetailActivity> weakReference;

        public RefreshHandler(MapDetailActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MapDetailActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case ROUTE_PLANNING_SUCCESS:
                    activity.renderRoute();
                    break;
                case ROUTE_PLANNING_FAILED:
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        apiKey = GetKeyUtil.getApiKey();
        getIntentData();
        initViews();
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        clickEvents();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    /**
     * Obtain the transferred data.
     */
    private void getIntentData() {
        if (getIntent() != null) {
            destinationLat = getIntent().getDoubleExtra("destinationLat", 48.893478);
            destinationLng = getIntent().getDoubleExtra("destinationLng", 2.334595);
        }
    }

    /**
     * Initialize the view.
     */
    private void initViews() {
        mMapView = findViewById(R.id.mapview);
        mDriving = findViewById(R.id.img_route_driving);
        mBicycling = findViewById(R.id.img_route_bicycling);
        mWalking = findViewById(R.id.img_route_walking);
        mTime = findViewById(R.id.tv_time);
        mDistance = findViewById(R.id.tv_distance);
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        hMap = map;
        MarkerOptions options = new MarkerOptions();
        LatLng destinationLatLng = new LatLng(destinationLat, destinationLng);
        options.position(destinationLatLng);
        options.anchor(0.5f, 0.9f).anchorMarker(0.5f, 0.9f);
        hMap.addMarker(options);
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15f));
    }

    /**
     * Click Event
     */
    private void clickEvents() {
        mDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/driving?key=" + apiKey;
                planningPaths(url);
            }
        });
        mBicycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/bicycling?key=" + apiKey;
                planningPaths(url);
            }
        });
        mWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/walking?key=" + apiKey;
                planningPaths(url);
            }
        });
    }

    /**
     * Path planning interface request
     *
     * @param url
     */
    private void planningPaths(String url) {
        removePolyline();
        JSONObject json = new JSONObject();
        JSONObject origin = new JSONObject();
        JSONObject destination = new JSONObject();
        try {
            origin.put("lng", 2.334595);
            origin.put("lat", 48.893478);
            destination.put("lng", destinationLng);
            destination.put("lat", destinationLat);
            json.put("origin", origin);
            json.put("destination", destination);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(json));
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("errorMsg", e.getMessage());
                msg.what = ROUTE_PLANNING_FAILED;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    generateRoute(json);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
    }

    /**
     * Parse the returned data.
     *
     * @param json
     */
    private void generateRoute(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.optJSONArray("routes");
            if (null == routes || routes.length() == 0) {
                return;
            }
            JSONObject route = routes.getJSONObject(0);
            // get route bounds
            JSONObject bounds = route.optJSONObject("bounds");
            if (null != bounds && bounds.has("southwest") && bounds.has("northeast")) {
                JSONObject southwest = bounds.optJSONObject("southwest");
                JSONObject northeast = bounds.optJSONObject("northeast");
                assert southwest != null;
                LatLng sw = new LatLng(southwest.optDouble("lat"), southwest.optDouble("lng"));
                assert northeast != null;
                LatLng ne = new LatLng(northeast.optDouble("lat"), northeast.optDouble("lng"));
                mLatLngBounds = new LatLngBounds(sw, ne);
            }
            // get paths
            JSONArray paths = route.optJSONArray("paths");
            assert paths != null;
            for (int i = 0; i < paths.length(); i++) {
                JSONObject path = paths.optJSONObject(i);
                List<LatLng> mPath = new ArrayList<>();
                JSONArray steps = path.optJSONArray("steps");
                distanceText = path.getString("distanceText");
                durationText = path.getString("durationText");
                assert steps != null;
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = steps.optJSONObject(j);
                    JSONArray polyline = step.optJSONArray("polyline");
                    assert polyline != null;
                    for (int k = 0; k < polyline.length(); k++) {
                        if (j > 0 && k == 0) {
                            continue;
                        }
                        JSONObject line = polyline.getJSONObject(k);
                        double lat = line.optDouble("lat");
                        double lng = line.optDouble("lng");
                        LatLng latLng = new LatLng(lat, lng);
                        mPath.add(latLng);
                    }
                }
                mPaths.add(i, mPath);
            }
            mHandler.sendEmptyMessage(ROUTE_PLANNING_SUCCESS);
        } catch (JSONException e) {
        }
    }

    private void renderRoute() {
        mTime.setText(String.format("%s%s", getResources().getString(R.string.route_planning_time), durationText));
        mDistance.setText(String.format("%s%s", getResources().getString(R.string.route_planning_distance), distanceText));
        renderRoute(mPaths, mLatLngBounds);
    }

    /**
     * Path Planning
     *
     * @param paths
     * @param latLngBounds
     */
    private void renderRoute(List<List<LatLng>> paths, LatLngBounds latLngBounds) {
        if (null == paths || paths.size() <= 0 || paths.get(0).size() <= 0) {
            return;
        }
        for (int i = 0; i < paths.size(); i++) {
            List<LatLng> path = paths.get(i);
            PolylineOptions options = new PolylineOptions().color(Color.BLUE).width(5);
            for (LatLng latLng : path) {
                options.add(latLng);
            }
            Polyline polyline = hMap.addPolyline(options);
            mPolyline.add(i, polyline);
        }
        // Draw the start point marker.
        addOriginMarker(paths.get(0).get(0));
        if (null != latLngBounds) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 15);
            hMap.moveCamera(cameraUpdate);
        } else {
            LatLng latLngOrigin = new LatLng(48.893478, 2.334595);
            LatLng latLngDestination = new LatLng(destinationLat, destinationLng);
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(latLngOrigin);
            boundsBuilder.include(latLngDestination);
            hMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));
        }
    }

    /**
     * Add marker
     *
     * @param latLng
     */
    private void addOriginMarker(LatLng latLng) {
        if (null != mMarkerOrigin) {
            mMarkerOrigin.remove();
        }
        mMarkerOrigin = hMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.9f).anchorMarker(0.5f, 0.9f));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    /**
     * Remove Path
     */
    private void removePolyline() {
        for (Polyline polyline : mPolyline) {
            polyline.remove();
        }
        mPolyline.clear();
        mPaths.clear();
        mLatLngBounds = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
