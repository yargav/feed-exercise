package com.lightricks.feedexercise.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.lightricks.feedexercise.database.FeedDatabase
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

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository {
    private var feedItems = MutableLiveData<List<FeedItem>>()

    init {
        subscribe()
    }

//    private fun createDatabase(){
//        val db = Room.databaseBuilder(
//            ,
//            FeedDatabase::class.java, "database-name"
//        ).build()
//    }

    private fun subscribe() {
        val subscribe = getDataFromNetwork().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> handleResult(result) },
                { error -> handleError(error.message) })
    }

    fun getFeedData(): LiveData<List<FeedItem>> = feedItems;


    private fun handleResult(result: FeedData) {
        Log.d("show result", result.metaData.size.toString())
        feedItems.postValue(result.metaData.map {
            FeedItem(
                it.id,
                "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"+ it.thumbnailURI,
                it.isPremium
            )
        })
    }


    //show error
    private fun handleError(message:String?){
        Log.d("show error", message)
    }

    private fun getDataFromNetwork(): Single<FeedData> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder().baseUrl("https://assets.swishvideoapp.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val fetcher = retrofit.create(FeedApiService::class.java)
        return fetcher.getFeedData()
    }
}