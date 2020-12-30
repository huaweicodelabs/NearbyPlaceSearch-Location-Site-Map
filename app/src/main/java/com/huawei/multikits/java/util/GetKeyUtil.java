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

package com.huawei.multikits.java.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GetKeyUtil {
    public static String getApiKey() {
        String apiKey = null;
        try {
            String API_KEY = "CgB6e3x9KjydY9rP021v6F25p5f8p03dVir916tvpTzP98hmw62p2eXcvXLxyXQJYuWhZkPWcPSyXaxCi1g2MERI";
            apiKey = URLEncoder.encode(API_KEY, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return apiKey;
    }
}
