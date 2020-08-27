package com.lightricks.feedexercise.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.FeedData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(private val feedDatabase: FeedDatabase, private val fetcher: FeedApiService) {

    private val feedItems =
        Transformations.map(feedDatabase.feedItemDao().getAll()) { feedItemEntities ->
            feedItemEntities.map { FeedItem(it.id, it.thumbnailUrl, it.isPremium) }
        }

    private val PREFIX_URL = "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"

    fun getFeedData(): LiveData<List<FeedItem>> = feedItems

    init {
        refresh().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun refresh(): Completable {
        Log.d("debugging", "in refresh feed repository")
        val single = getDataFromNetwork()
        return single.flatMapCompletable { result ->
            feedDatabase.feedItemDao().insertAll(result.metaData.map {
                FeedItemEntity(
                    it.id,
                    PREFIX_URL + it.thumbnailURI,
                    it.isPremium
                )
            })
        }
    }

    private fun getDataFromNetwork(): Single<FeedData> {
        return fetcher.getFeedData()
    }
}