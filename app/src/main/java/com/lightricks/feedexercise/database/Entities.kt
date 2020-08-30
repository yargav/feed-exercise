package com.lightricks.feedexercise.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class, here represents one feed Item (template)
 */
@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @PrimaryKey val id: String,
    val thumbnailUrl: String,
    val isPremium: Boolean
)