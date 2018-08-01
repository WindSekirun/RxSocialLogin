/*
    Copyright 2014 LinkedIn Corp.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.linkedin.platform.errors;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LIDeepLinkError {

    private static final String TAG = LIDeepLinkError.class.getName();

    private LIAppErrorCode errorCode;
    private String errorMsg;

    public LIDeepLinkError(@NonNull String errorInfo, String errorMsg) {
        LIAppErrorCode liAppErrorCode = LIAppErrorCode.findErrorCode(errorInfo);
        this.errorCode = liAppErrorCode;
        this.errorMsg = errorMsg;
    }

    public LIDeepLinkError(LIAppErrorCode errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorCode", errorCode.name());
            jsonObject.put("errorMessage", errorMsg);
            return jsonObject.toString(2);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

}