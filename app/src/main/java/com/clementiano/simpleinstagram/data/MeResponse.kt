package com.clementiano.simpleinstagram.data

data class MeResponse(
    val account_type: String,
    val id: String,
    val media_count: Int,
    val username: String
)