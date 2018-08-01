package com.linkedin.platform.errors;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LIAuthError {

    private static final String TAG = LIAuthError.class.getName();

    private LIAppErrorCode errorCode;
    private String errorMsg;

    public LIAuthError(String errorInfo, String errorMsg) {
        LIAppErrorCode liAuthErrorCode = LIAppErrorCode.findErrorCode(errorInfo);
        errorCode = liAuthErrorCode;
        this.errorMsg = errorMsg;
    }

    public LIAuthError(LIAppErrorCode errorCode, String errorMsg) {
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
