package com.github.windsekirun.rxsociallogin.facebook

import com.facebook.AccessToken
import com.facebook.AccessToken.USER_ID_KEY
import com.facebook.AccessTokenSource
import com.facebook.FacebookException
import com.facebook.internal.Utility
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import pyxis.uzuki.live.richutilskt.utils.getJSONLong
import pyxis.uzuki.live.richutilskt.utils.getJSONString
import java.util.*

/**
 * RxSocialLogin
 * Class: FacebookAccessTokenProvider
 * Created by Pyxis on 2018-11-15.
 *
 * Description:
 */
object FacebookAccessTokenProvider {
    private val CURRENT_JSON_FORMAT = 1
    private val VERSION_KEY = "version"
    private val EXPIRES_AT_KEY = "expires_at"
    private val PERMISSIONS_KEY = "permissions"
    private val DECLINED_PERMISSIONS_KEY = "declined_permissions"
    private val TOKEN_KEY = "token"
    private val SOURCE_KEY = "source"
    private val LAST_REFRESH_KEY = "last_refresh"
    private val APPLICATION_ID_KEY = "application_id"

    @Throws(JSONException::class)
    @JvmOverloads
    fun createFromJSONObject(jsonObject: JSONObject): AccessToken {
        val version = jsonObject.getInt(VERSION_KEY)
        if (version > CURRENT_JSON_FORMAT) {
            throw FacebookException("Unknown AccessToken serialization format.")
        }

        val token = jsonObject.getJSONString(TOKEN_KEY)
        val expiresAt = Date(jsonObject.getJSONLong(EXPIRES_AT_KEY))
        val permissionsArray = jsonObject.getJSONArray(PERMISSIONS_KEY)
        val declinedPermissionsArray = jsonObject.getJSONArray(DECLINED_PERMISSIONS_KEY)
        val lastRefresh = Date(jsonObject.getJSONLong(LAST_REFRESH_KEY))
        val source = AccessTokenSource.valueOf(jsonObject.getJSONString(SOURCE_KEY))
        val applicationId = jsonObject.getJSONString(APPLICATION_ID_KEY)
        val userId = jsonObject.getJSONString(USER_ID_KEY)

        return AccessToken(
                token,
                applicationId,
                userId,
                Utility.jsonArrayToStringList(permissionsArray),
                Utility.jsonArrayToStringList(declinedPermissionsArray),
                source,
                expiresAt,
                lastRefresh)
    }

    @Throws(JSONException::class)
    fun toJSONObject(accessToken: AccessToken): JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put(VERSION_KEY, CURRENT_JSON_FORMAT)
        jsonObject.put(TOKEN_KEY, accessToken.token)
        jsonObject.put(EXPIRES_AT_KEY, accessToken.expires.time)
        val permissionsArray = JSONArray(accessToken.permissions)
        jsonObject.put(PERMISSIONS_KEY, permissionsArray)
        val declinedPermissionsArray = JSONArray(accessToken.declinedPermissions)
        jsonObject.put(DECLINED_PERMISSIONS_KEY, declinedPermissionsArray)
        jsonObject.put(LAST_REFRESH_KEY, accessToken.lastRefresh.time)
        jsonObject.put(SOURCE_KEY, accessToken.source.name)
        jsonObject.put(APPLICATION_ID_KEY, accessToken.applicationId)
        jsonObject.put(USER_ID_KEY, accessToken.userId)

        return jsonObject
    }
}

fun AccessToken.toJSONObject() = FacebookAccessTokenProvider.toJSONObject(this)