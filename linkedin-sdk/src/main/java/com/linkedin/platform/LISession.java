package com.linkedin.platform;

public interface LISession {

    /**
     * @return an AccessToken representing the user's authorization of the application
     */
    public AccessToken getAccessToken();

    /**
     * @return true if the AccessToken is valid
     * false is returned if the user has not granted the application access or if the
     * access token has expired
     */
    public boolean isValid();
}
