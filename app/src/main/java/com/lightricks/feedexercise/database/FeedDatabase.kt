package com.lightricks.feedexercise.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * abstract class that holds the feed database
 */
@Database(entities = [FeedItemEntity::class], version = 1, exportSchema = false)
abstract class FeedDatabase : RoomDatabase() {
    abstract fun feedItemDao(): FeedItemDao

}

//method to get a single feed data base
private lateinit var INSTANCE: FeedDatabase

fun getFeedDatabase(context: Context): FeedDatabase {
    synchronized(FeedDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context,
                FeedDatabase::class.java, "FeedDatabase"
            ).build()
        }
    }
    return INSTANCE
}
