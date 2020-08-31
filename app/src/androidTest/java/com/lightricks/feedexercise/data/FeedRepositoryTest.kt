package com.lightricks.feedexercise.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.MockFeedApiService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FeedRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var feedRepository: FeedRepository

    //constants for the hard coded template used
    private val PREFIX_URL = "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"
    private val ID = "01E18PGE1RYB3R9YF9HRXQ0ZSD"
    private val THUMBNAIL_URI = "UnleashThePowerOfNatureThumbnail.jpg"
    private val CONFIGURATION = "lensflare-unleash-the-power-of-nature.json"
    private val IS_NEW = false
    private val IS_PREMIUM = true
    private val CATEGORIES = listOf(
        "01DJ4TM160ETZR0NT4HA2M0ZTK",
        "01DJ4TM161MRR86QFAXJTWP7NM"
    )
    private val NAME = "lens-flare-template.json"

    @Mock
    private val mockFeedApiService = MockFeedApiService()
    @Mock
    private lateinit var feedDatabase: FeedDatabase

    @Before
    fun initializeFeedRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        feedDatabase = Room.inMemoryDatabaseBuilder(context, FeedDatabase::class.java).build()
        feedRepository = FeedRepository(feedDatabase, mockFeedApiService)
    }


    @Test
    fun refresh_SavedItemsInRepositoryTest() {
        feedRepository.refresh().blockingAwait()
        assertThat(
            feedRepository.getFeedData().blockingObserve()
        ).isEqualTo(
            listOf(
                FeedItem(
                    ID,
                    PREFIX_URL + THUMBNAIL_URI,
                    IS_PREMIUM
                )
            )
        )
    }

    @Test
    fun refresh_SavedItemsInDatabaseTest() {
        feedRepository.refresh().blockingAwait()
        assertThat(feedDatabase.feedItemDao().getAll().blockingObserve()).isEqualTo(
            listOf(
                FeedItemEntity(
                    ID,
                    PREFIX_URL + THUMBNAIL_URI,
                    IS_PREMIUM
                )
            )
        )
    }

    @Test
    fun feedItems_EmptyAfterInitTest() {
        assertThat(feedRepository.getFeedData().blockingObserve()).isEmpty()
    }

    @Test
    fun feedItems_UpdatedAfterDeleteTest() {
        feedRepository.refresh().blockingAwait()
        feedDatabase.feedItemDao().deleteAll(
            listOf(
                FeedItemEntity(
                    ID,
                    PREFIX_URL + THUMBNAIL_URI,
                    IS_PREMIUM
                )
            )
        ).blockingAwait()

        assertThat(feedDatabase.feedItemDao().getAll().blockingObserve()).isEmpty()
        assertThat(feedRepository.getFeedData().blockingObserve()).isEmpty()
    }


    @After
    fun disposeFeedRepository() {
        feedDatabase.close()
    }
}

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            value = t
            latch.countDown()
            removeObserver(this)
        }
    }

    observeForever(observer)
    latch.await(5, TimeUnit.SECONDS)
    return value
}
