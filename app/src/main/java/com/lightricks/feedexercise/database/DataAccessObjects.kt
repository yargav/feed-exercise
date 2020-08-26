package com.lightricks.feedexercise.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable

/***
 * Data access objects interface for the feed data (
 */

@Dao
interface FeedItemDao {
    @Query("SELECT * FROM feedItems")
    fun getAll(): LiveData<List<FeedItemEntity>>

    @Query("SELECT size() FROM feedItems")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(feedItems: List<FeedItemEntity>): Completable

    @Delete
    fun deleteAll(): Completable
}