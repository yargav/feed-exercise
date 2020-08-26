package com.lightricks.feedexercise.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

/**
 * todo: add the FeedApiService interface and the Retrofit and Moshi code here
 */

interface FeedApiService {

    @GET("Android/demo/feed.json")
    fun getFeedData(): Single<FeedData>


}
