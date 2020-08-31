package com.lightricks.feedexercise.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Classes that hold the information from the json file
 */

@JsonClass(generateAdapter = true)
data class FeedData(@Json(name = "templatesMetadata") val templates: List<Template> = listOf())

@JsonClass(generateAdapter = true)
data class Template(
    val configuration: String,
    val id: String,
    val isNew: Boolean,
    val isPremium: Boolean,
    @Json(name = "templateCategories") val categories: List<String>,
    @Json(name = "templateName") val name: String,
    @Json(name = "templateThumbnailURI") val thumbnailURI: String
)




