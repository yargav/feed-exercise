package com.lightricks.feedexercise.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.FeedData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(private val feedDatabase: FeedDatabase) {
    private val feedItems =
        Transformations.map(feedDatabase.feedItemDao().getAll()) { feedItemEntities ->
            feedItemEntities.map { FeedItem(it.id, it.thumbnailUrl, it.isPremium) }
        }
    private val BASE_URL = "https://assets.swishvideoapp.com/"
    private val PREFIX_URL = "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"

    fun getFeedData(): LiveData<List<FeedItem>> = feedItems

    init {
        val disposable = refresh().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun refresh(): Completable {
        Log.d("debugging", "in refresh feed repository")
        val single = getDataFromNetwork()
        return single.flatMapCompletable { result ->
            if (result.metaData.isNotEmpty()) {
//                Log.d("debugging", "in flat map completable not empty")
                feedDatabase.feedItemDao().insertAll(result.metaData.map {
                    FeedItemEntity(
                        it.id,
                        PREFIX_URL + it.thumbnailURI,
                        it.isPremium
                    )
                })
            } else {
//                Log.d("debugging", "in flat map completable empty")
                Completable.error(IOException())
            }
        }
    }

    private fun getDataFromNetwork(): Single<FeedData> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val fetcher = retrofit.create(FeedApiService::class.java)
        return fetcher.getFeedData()
    }
}