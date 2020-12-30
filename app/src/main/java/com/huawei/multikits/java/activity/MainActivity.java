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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.multikits.R;
import com.huawei.multikits.java.adapter.MainAdapter;
import com.huawei.multikits.java.bean.MainItemBean;
import com.huawei.multikits.java.dialog.DialogManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.SIMPLIFIED_CHINESE;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int GET_DETAIL_ADDRESS = 0;
    private String mAddress;
    private RecyclerView mRecyclerView;
    private List<MainItemBean> mAddressList;
    private TextView mTvAddress;
    private MainAdapter adapter;
    private LocationCallback mLocationCallback;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mProviderClient;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_DETAIL_ADDRESS) {
                mAddress = msg.obj.toString();
                mTvAddress.setText(mAddress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initViews();
        initLocationRequest();
        requestLocationUpdates();
    }


    /**
     * Initialize data.
     */
    private void initData() {
        mAddressList = new ArrayList<>();
        mAddressList.add(new MainItemBean("Supermarket", getResources().getString(R.string.main_page_supermarket), R.mipmap.supermarket_icon));
        mAddressList.add(new MainItemBean("Hotel", getResources().getString(R.string.main_page_hotel), R.mipmap.hotel_icon));
        mAddressList.add(new MainItemBean("Food",  getResources().getString(R.string.main_page_restaurant), R.mipmap.food_icon));
        mAddressList.add(new MainItemBean("Malls", getResources().getString(R.string.main_page_shopping_mall), R.mipmap.mall_icon));
        mAddressList.add(new MainItemBean("Hairdressing salon",  getResources().getString(R.string.main_page_hair_care), R.mipmap.barbershop_icon));
        mAddressList.add(new MainItemBean("Theater",  getResources().getString(R.string.main_page_movie), R.mipmap.movie_icon));
        mAddressList.add(new MainItemBean("Florists",  getResources().getString(R.string.main_page_flower), R.mipmap.flower_shop_icon));
        mAddressList.add(new MainItemBean("Pet shop", getResources().getString(R.string.main_page_pet), R.mipmap.pet_shop_icon));
    }

    /**
     * Request Location
     */
    private void requestLocationUpdates() {
        DialogManager.newInstance().showDialog(this, getResources().getString(R.string.common_locating));
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        mProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        // Check the device location settings.
        mSettingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Set the location conditions to be met and then initiate a location request.
                        mProviderClient.requestLocationUpdates(mLocationRequest, initLocationCallback(), Looper.getMainLooper())
                                // Listening callback for successful location update request interface
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DialogManager.newInstance().dismissDialog();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                DialogManager.newInstance().dismissDialog();
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(MainActivity.this, 0);
                        } catch (IntentSender.SendIntentException sie) {
                        }
                        break;
                }
            }
        });
    }

    /**
     * Initialize the location request.
     */
    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        //Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(10000);
        //Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Obtains the location callback function LocationCallback.
     *
     * @return
     */
    private LocationCallback initLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    if (null != locations && locations.size() > 0) {
                        Location location = locations.get(0);
                        final double longitude = location.getLongitude();
                        final double latitude = location.getLatitude();
                        // Enable the subthread to invoke the reverse geocoding capability to obtain location information.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Reverse geocoding address
                                    final Geocoder geocoder = new Geocoder(MainActivity.this, SIMPLIFIED_CHINESE);
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    // After the address information is updated successfully, use the handler to update the UI.
                                    if (null != addresses && addresses.size() > 0) {
                                        for (Address address : addresses) {
                                            Message msg = new Message();
                                            msg.what = GET_DETAIL_ADDRESS;
                                            msg.obj = addresses.get(0).getFeatureName();
                                            handler.sendMessage(msg);
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }).start();
                    }
                }
            }
        };
        return mLocationCallback;
    }

    /**
     * Initialize the view.
     */
    private void initViews() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mTvAddress = findViewById(R.id.address);
        adapter = new MainAdapter(mAddressList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setAdapter(adapter);
        // Item Click Event
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String id) {
                Intent intent = new Intent(MainActivity.this, AddressListActivity.class);
                intent.putExtra("CATEGORY_NAME", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
        DialogManager.newInstance().dismissDialog();
    }

    /**
     * Remove Location Request
     */
    private void removeLocationUpdates() {
        if (null != mProviderClient && null != mLocationCallback) {
            mProviderClient.removeLocationUpdates(mLocationCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }
}



