package com.clementiano.simpleinstagram.network

import com.clementiano.simpleinstagram.data.*
import retrofit2.http.*
import rx.Single

interface ApiInterface {

    /* Basic **/
    @FormUrlEncoded
    @POST("oauth/access_token")
    fun getAccessToken(@Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("code") code: String,
                       @Field("grant_type") grantType: String = "authorization_code",
                       @Field("redirect_uri") redirectUrl: String): Single<AccessTokenResponse>

    /* Graph **/
    @GET("/access_token")
    fun getLongLivedAccessToken(
        @Query("client_secret") clientSecret: String,
        @Query("access_token") accessToken: String,
        @Query("grant_type") grantType: String = "ig_exchange_token"): Single<AccessTokenResponse>

    @GET("me")
    fun getProfile(@Query("access_token") accessToken: String,
                   @Query("fields") fields: String = "account_type, username, media_count, username"): Single<MeResponse>

    @GET("me/media")
    fun getMediaList(@Query("access_token") accessToken: String,
                     @Query("fields") fields: String = "id,caption"): Single<MediaResponse>

    @GET("{mediaId}")
    fun getMediaDetail(
        @Path("mediaId") mediaId: String,
        @Query("access_token") accessToken: String,
        @Query("fields") fields: String = "id,caption,media_type,media_url,username,timestamp"
    ): Single<MediaItem>

    /* Plain **/
    @GET("{username}/?__a=1")
    fun getProfileDetails(@Path("username") id: String): Single<ProfileResponse>
}