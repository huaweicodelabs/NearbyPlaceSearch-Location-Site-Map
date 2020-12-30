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

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.huawei.multikits.R

class LoadingDialog(context: Context) : Dialog(context, R.style.Dialog) {
    private var loadingText: TextView
    init {
        setContentView(R.layout.dialog_loading)
        setCanceledOnTouchOutside(false)
        loadingText = this.findViewById(R.id.loading_text) as TextView
    }

    fun setText(name: String) {
        loadingText.text = name
    }
}
