package com.lightricks.feedexercise.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable

/***
 * Data access objects interface for the feed data (
 */

@Dao
interface FeedItemDao {
    @Query("SELECT * FROM feed_items")
    fun getAll(): LiveData<List<FeedItemEntity>>

    @Query("SELECT COUNT(*) FROM feed_items")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(feedItems: List<FeedItemEntity>): Completable

    @Delete
    fun deleteAll(feedItems: List<FeedItemEntity>): Completable
}