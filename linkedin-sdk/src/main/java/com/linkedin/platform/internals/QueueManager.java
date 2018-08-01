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

package com.linkedin.platform.internals;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class QueueManager {

    private static final String TAG = QueueManager.class.getName();
    private static QueueManager queueManager;
    private Context ctx;
    private RequestQueue requestQueue;

    private QueueManager(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(ctx);
    }

    public static void initQueueManager(@NonNull Context ctx) {
        getInstance(ctx);
    }

    public static synchronized QueueManager getInstance(Context context) {
        if (queueManager == null) {
            queueManager = new QueueManager(context);
        }
        return queueManager;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }



}
