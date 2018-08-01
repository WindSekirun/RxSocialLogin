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

package com.linkedin.platform;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.internals.BuildConfig;
import com.linkedin.platform.internals.QueueManager;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to make authenticated REST api calls to retrieve LinkedIn data.
 * The LISession must be properly initialized before using this class.
 * @see <a href="https://developer.linkedin.com/rest">https://developer.linkedin.com/rest</a>
 * for information on type of calls available and the information returned.
 * Data is returned in json format.
 */
public class APIHelper {

    private static final String TAG = APIHelper.class.getName();
    private static final String LOCATION_HEADER = "Location";
    private static final String HTTP_STATUS_CODE = "StatusCode";
    private static final String DATA = "responseData";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_SRC = "x-li-src";
    private static final String HEADER_LI_FORMAT = "x-li-format";
    private static final String HEADER_LI_VER = "x-li-msdk-ver";
    private static final String CONTENT_VALUE = "application/json";
    private static final String HEADER_SRC_VALUE = "msdk";
    private static final String HEADER_LI_FORMAT_VALUE = "json";
    private static final String HEADER_LI_PLFM = "x-li-plfm";
    private static final String HEADER_LI_PLFM_ANDROID = "ANDROID_SDK";

    private static APIHelper apiHelper;

    public static APIHelper getInstance(@NonNull Context ctx) {
        if (apiHelper == null) {
            apiHelper = new APIHelper();
            QueueManager.initQueueManager(ctx);
        }
        return apiHelper;
    }

    private Map<String, String> getLiHeaders(String accessToken) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_CONTENT_TYPE, CONTENT_VALUE);
        headers.put(HEADER_AUTHORIZATION, "Bearer " + accessToken);
        headers.put(HEADER_SRC, HEADER_SRC_VALUE);
        headers.put(HEADER_LI_FORMAT, HEADER_LI_FORMAT_VALUE);
        headers.put(HEADER_LI_VER, BuildConfig.MSDK_VERSION);
        headers.put(HEADER_LI_PLFM, HEADER_LI_PLFM_ANDROID);

        return headers;
    }

    private JsonObjectRequest buildRequest(final String accessToken, int method, String url,
                                           JSONObject body, @Nullable final ApiListener apiListener) {
        return new JsonObjectRequest(method,
                url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (apiListener != null) {
                            apiListener.onApiSuccess(ApiResponse.buildApiResponse(response));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (apiListener != null) {
                            LIApiError liLIApiError = LIApiError.buildLiApiError(error);
                            apiListener.onApiError(liLIApiError);
                        }
                    }
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    JSONObject responseData = new JSONObject();
                    responseData.put(HTTP_STATUS_CODE, response.statusCode);
                    String location = response.headers.get(LOCATION_HEADER);
                    if (!TextUtils.isEmpty(location)) {
                        responseData.put(LOCATION_HEADER, location);
                    }
                    if (response.data != null && response.data.length != 0) {
                        String responseDataAsString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        responseData.put(DATA, responseDataAsString);

                    }
                    return Response.success(responseData, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getLiHeaders(accessToken);
            }
        };
    }

    private void request(@NonNull Context context, int method, @NonNull String url, @Nullable JSONObject body, @Nullable ApiListener apiListener) {
        LISession session = LISessionManager.getInstance(context.getApplicationContext()).getSession();
        if (!session.isValid()) {
            if (apiListener != null) {
                apiListener.onApiError(new LIApiError(LIApiError.ErrorType.accessTokenIsNotSet, "access toke is not set", null));
            }
            return;
        }
        JsonObjectRequest jsonObjectRequest = buildRequest(session.getAccessToken().getValue(), method, url, body, apiListener);
        jsonObjectRequest.setTag(context == null ? TAG : context);
        QueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using GET Method
     *
     * @param context
     * @param url         rest api endpoint to call. example: "https://api.linkedin.com/v1/people/~:(first-name,last-name,public-profile-url)"
     * @param apiListener
     */
    public void getRequest(@NonNull Context context, String url, ApiListener apiListener) {
        request(context, Request.Method.GET, url, null, apiListener);
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using POST Method
     *  @param context
     * @param url
     * @param body
     * @param apiListener
     */
    public void postRequest(@NonNull Context context, String url, JSONObject body, ApiListener apiListener) {
        request(context, Request.Method.POST, url, body, apiListener);
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using POST Method
     *  @param context
     * @param url
     * @param body
     * @param apiListener
     */
    public void postRequest(Context context, String url, String body, ApiListener apiListener) {
        try {
            JSONObject bodyObject = body != null ? new JSONObject(body) : null;
            postRequest(context, url, bodyObject, apiListener);
        } catch (JSONException e) {
            apiListener.onApiError(new LIApiError("Unable to convert body to json object " + e.toString(), e));
        }
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using PUT Method
     *  @param context
     * @param url
     * @param body
     * @param apiListener
     */
    public void putRequest(Context context, String url, JSONObject body, ApiListener apiListener) {
        request(context, Request.Method.PUT, url, body, apiListener);
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using PUT method with
     * string body
     * @param context
     * @param url
     * @param body
     * @param apiListener
     */
    public void putRequest(@NonNull Context context, String url, String body, ApiListener apiListener) {
        try {
            JSONObject bodyObject = body != null ? new JSONObject(body) : null;
            putRequest(context, url, bodyObject, apiListener);
        } catch (JSONException e) {
            apiListener.onApiError(new LIApiError("Unable to convert body to json object " + e.toString(), e));
        }
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using DELETE Method
     *
     * @param context
     * @param url
     * @param apiListener
     */
    public void deleteRequest(@NonNull Context context, String url, ApiListener apiListener) {
        request(context, Request.Method.DELETE, url, null, apiListener);
    }

    /**
     * cancel any unsent api calls
     * @param context
     */
    public void cancelCalls(@NonNull Context context) {
        QueueManager.getInstance(context).getRequestQueue().cancelAll(context);
    }

}
