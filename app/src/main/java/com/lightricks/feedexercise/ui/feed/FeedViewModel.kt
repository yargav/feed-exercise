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
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel : ViewModel() {
    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private lateinit var feedItems: LiveData<List<FeedItem>>
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val feedRepository = FeedRepository()


    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    fun setIsLoading(isLoading:Boolean) {this.isLoading.value = isLoading}
    fun setIsEmpty(isEmpty:Boolean) { this.isEmpty.value = isEmpty}

    init {

        refresh()
    }


    fun refresh() {
        isEmpty.value = false
        isLoading.value = false
        feedItems = feedRepository.getFeedData()
//        subscribe()
    }

//    private fun subscribe() {
//        isLoading.value = true
//        val subscribe = feedRepository.getDataFromNetwork().subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result -> handleResult(result) },
//                { error -> handleError(error) })
//    }

//
//    private fun handleResult(result: FeedData) {
//        isLoading.value = false
//        Log.d("show result", result.metaData.size.toString())
//        feedItems.postValue(result.metaData.map {
//            FeedItem(
//                it.id,
//                "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"+ it.thumbnailURI,
//                it.isPremium
//            )
//        })
//    }

    //show error
//    private fun handleError(error: Throwable) {
//        networkErrorEvent.postValue(Event(error.localizedMessage))
//    }

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