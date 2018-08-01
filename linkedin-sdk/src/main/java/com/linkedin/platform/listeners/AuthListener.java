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

package com.linkedin.platform.listeners;

import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.errors.LIDeepLinkError;

public interface AuthListener {

    /**
     * called when the application has been granted authorization to access the LinkedIn member's data
     * see {@link com.linkedin.platform.APIHelper} and {@link com.linkedin.platform.DeepLinkHelper} for
     * how to access LinkedIn data and interact with the LinkedIn application
     */
    void onAuthSuccess();

    /**
     * called when the application has not been granted authorization to access the LinkedIn member's
     * data
     * @param error information on why the authorization did not occur
     */
    void onAuthError(LIAuthError error);
}

