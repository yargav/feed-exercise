package com.lightricks.feedexercise.ui.feed

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.util.Event

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private val feedItems = MediatorLiveData<List<FeedItem>>()
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val feedRepository = FeedRepository(
        Room.databaseBuilder(
            application.applicationContext,
            FeedDatabase::class.java, "FeedDatabase"
        ).build()
    )


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

    //TODO figure out how to not add another source when refreshed
    fun refresh() {
        isEmpty.postValue(false)
        //TODO check if this is the problem with the load, temporary!
        isLoading.postValue(true)
        Thread.sleep(10)
        isLoading.postValue(false)
    }

}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
//TODO figure out if i need this factory, what to put in it???
class FeedViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(this.application) as T
    }
}