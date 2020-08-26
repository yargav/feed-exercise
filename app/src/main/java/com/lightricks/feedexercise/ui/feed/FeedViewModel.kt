package com.lightricks.feedexercise.ui.feed

import android.util.Log
import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.FeedData
import com.lightricks.feedexercise.util.Event
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.newFixedThreadPoolContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel : ViewModel() {
    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private val feedItems = MutableLiveData<List<FeedItem>>()
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val feedRepository = FeedRepository()


    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        refresh()
    }


    fun refresh() {
        //todo: fix the implementation
        isEmpty.value = true
        isLoading.value = false
        subscribe()
    }

    private fun subscribe() {
        val subscribe = getDataFromNetwork().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> handleResult(result) },
                { error -> handleError(error) })
    }


    private fun handleResult(result: FeedData) {
        isLoading.value = false
        Log.d("show result", result.templatesMetaData.size.toString())
        feedItems.postValue(result.templatesMetaData.map {
            FeedItem(
                it.id,
                "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"+ it.templateThumbnailURI,
                it.isPremium
            )
        })
    }

    //show error
    private fun handleError(error: Throwable) {
        networkErrorEvent.postValue(Event(error.localizedMessage))
    }

    private fun getDataFromNetwork(): Single<FeedData> {
        isEmpty.value = false
        isLoading.value = true
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

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel() as T
    }
}