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

package com.huawei.multikits.kotlin.dialog

import android.content.Context

class DialogManager {
    private var loadingDialog: LoadingDialog? = null

    companion object {
        private val dialogManager = DialogManager()
        fun newInstance(): DialogManager {
            return dialogManager
        }
    }

    fun showDialog(context: Context) {
        if (null == loadingDialog) {
            loadingDialog = LoadingDialog(context)
        }
        loadingDialog?.show()
    }

    fun showDialog(context: Context, name: String) {
        if (null == loadingDialog) {
            loadingDialog = LoadingDialog(context)
            loadingDialog?.setText(name)
        }
        loadingDialog?.show()
    }

    fun dismissDialog() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }
}
