package com.clementiano.simpleinstagram.data

data class MediaResponse(
    val data: ArrayList<MediaData>,
    val paging: Paging
)

data class MediaData(
    val caption: String,
    val id: String
)

data class Paging(
    val cursors: Cursors
)

data class Cursors(
    val after: String,
    val before: String
)

data class MediaItem(
    val id: String,
    val media_type: String,
    val media_url: String,
    val timestamp: String,
    val username: String
)