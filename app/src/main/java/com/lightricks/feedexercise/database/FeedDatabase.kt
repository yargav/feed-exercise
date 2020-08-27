package com.lightricks.feedexercise.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * abstract class that holds the feed database
 */
@Database(entities = [FeedItemEntity::class], version = 1, exportSchema = false)
abstract class FeedDatabase: RoomDatabase() {
    abstract fun feedItemDao(): FeedItemDao

}