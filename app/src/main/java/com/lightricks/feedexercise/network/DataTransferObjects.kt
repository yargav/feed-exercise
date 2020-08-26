package com.lightricks.feedexercise.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * todo: add Data Transfer Object data class(es) here
 */

@JsonClass(generateAdapter = true)
data class FeedData(@Json(name = "templatesMetadata") val templatesMetaData: List<Template> = listOf()){
    @JsonClass(generateAdapter = true)
    data class Template(
        @Json(name = "configuration") val configuration: String,
        @Json(name = "id") val id: String,
        @Json(name = "isNew")val isNew: Boolean,
        @Json(name = "isPremium")val isPremium: Boolean,
        @Json(name = "templateCategories") val templateCategories: List<String>,
        @Json(name = "templateName") val templateName: String,
        @Json(name = "templateThumbnailURI") val templateThumbnailURI: String
    )


}




