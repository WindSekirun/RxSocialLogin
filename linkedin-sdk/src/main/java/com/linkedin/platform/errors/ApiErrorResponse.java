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

import org.json.JSONException;
import org.json.JSONObject;

public class ApiErrorResponse {

    private static final String TAG = ApiErrorResponse.class.getName();
    public static final String ERROR_CODE = "errorCode";
    public static final String MESSAGE = "message";
    public static final String REQUEST_ID = "requestId";
    public static final String STATUS = "status";
    public static final String TIMESTAMP = "timestamp";
    private JSONObject jsonApiErrorResponse;

    final public int errorCode;
    final public String message;
    final public String requestId;
    final public int status;
    final public long timestamp;

    private ApiErrorResponse(JSONObject jsonApiErrorResponse, int errorCode, String message, String requestId, int status, long timestamp) {
        this.jsonApiErrorResponse = jsonApiErrorResponse;
        this.errorCode = errorCode;
        this.message = message;
        this.requestId = requestId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public static ApiErrorResponse build(byte[] apiErrorResponseData) throws JSONException {
        return build(new JSONObject(new String(apiErrorResponseData)));

    }

    public static ApiErrorResponse build(JSONObject jsonErr) throws JSONException {
        return new ApiErrorResponse(jsonErr, jsonErr.optInt(ERROR_CODE, -1), jsonErr.optString(MESSAGE),
                jsonErr.optString(REQUEST_ID), jsonErr.optInt(STATUS, -1), jsonErr.optLong(TIMESTAMP, 0));
    }

    public int getErrorCode() {
        return jsonApiErrorResponse.optInt(ERROR_CODE, -1);
    }

    public String getMessage() {
        return jsonApiErrorResponse.optString(MESSAGE);
    }

    public String getRequestId() {
        return jsonApiErrorResponse.optString(REQUEST_ID);
    }

    public int getStatus() {
        return jsonApiErrorResponse.optInt(STATUS, -1);
    }

    public long getTimestamp() {
        return jsonApiErrorResponse.optLong(TIMESTAMP, 0);
    }

    @Override
    public String toString()    {
        try {
            return jsonApiErrorResponse.toString(2);
        } catch (JSONException e) {
        }
        return null;
    }
}

