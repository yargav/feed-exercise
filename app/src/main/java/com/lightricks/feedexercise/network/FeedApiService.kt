package com.lightricks.feedexercise.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * Feed API Serive interface (defines the gettting feed data function)
 */

interface FeedApiService {
    @GET("Android/demo/feed.json")
    fun getFeedData(): Single<FeedData>

}

//Singleton object for the feed API object
object FeedApi {
    private val BASE_URL = "https://assets.swishvideoapp.com/"
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
    val service: FeedApiService = retrofit.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(FeedApiService::class.java)
}
