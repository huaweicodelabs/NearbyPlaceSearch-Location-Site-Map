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


package com.huawei.multikits.java.dialog;
import android.content.Context;

public class DialogManager {
    private static DialogManager dialogManager = new DialogManager();
    private LoadingDialog loadingDialog;
    public static DialogManager newInstance() {
        return dialogManager;
    }

    public void showDialog(Context context) {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(context);
        }
        loadingDialog.show();
    }

    public void showDialog(Context context, String name) {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setText(name);
        }
        loadingDialog.show();
    }

    public void dismissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
