package com.lightricks.feedexercise.ui.feed

import android.content.Context
import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.database.getFeedDatabase
import com.lightricks.feedexercise.network.FeedApi
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel(private val feedRepository: FeedRepository) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private val feedItems = MediatorLiveData<List<FeedItem>>()
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val UNKNOWN_ERROR_MESSAGE = "An Unknown Error Occurred, Please Try Again Later"

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        isEmpty.postValue(false)
        isLoading.postValue(true)
        feedItems.addSource(feedRepository.getFeedData()) { result ->
            if (result.isEmpty()) isEmpty.postValue(true)
            else {
                isEmpty.postValue(false)
                feedItems.postValue(result)
            }
        }
        refresh()
    }

    fun refresh() {
        var disposable = feedRepository.refresh()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { isLoading.postValue(false) },
                { error -> handleError(error?.message)})
    }

    private fun handleError(message: String?){
        networkErrorEvent.postValue(Event(message?:UNKNOWN_ERROR_MESSAGE))
        isLoading.postValue(false)
    }


}

/**
 * This class creates instances of [FeedViewModel].
 * (With the feed repository injected to it)
 */

class FeedViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val feedRepository = FeedRepository(getFeedDatabase(context), FeedApi.service)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")

        return FeedViewModel(feedRepository) as T
    }
}