package com.clementiano.simpleinstagram.data

data class ProfileResponse(
    val graphql: Graphql,
    val logging_page_id: String,
    val show_follow_dialog: Boolean,
    val show_suggested_profiles: Boolean,
    val toast_content_on_load: Any
)

data class Graphql(
    val user: User
)

data class User(
    val biography: String,
    val blocked_by_viewer: Boolean,
    val business_category_name: Any,
    val connected_fb_page: Any,
    val country_block: Boolean,
    val edge_felix_video_timeline: EdgeFelixVideoTimeline,
    val edge_follow: EdgeFollow,
    val edge_followed_by: EdgeFollowedBy,
    val edge_media_collections: EdgeMediaCollections,
    val edge_mutual_followed_by: EdgeMutualFollowedBy,
    val edge_owner_to_timeline_media: EdgeOwnerToTimelineMedia,
    val edge_saved_media: EdgeSavedMedia,
    val external_url: Any,
    val external_url_linkshimmed: Any,
    val followed_by_viewer: Boolean,
    val follows_viewer: Boolean,
    val full_name: String,
    val has_blocked_viewer: Boolean,
    val has_channel: Boolean,
    val has_requested_viewer: Boolean,
    val highlight_reel_count: Int,
    val id: String,
    val is_business_account: Boolean,
    val is_joined_recently: Boolean,
    val is_private: Boolean,
    val is_verified: Boolean,
    val profile_pic_url: String,
    val profile_pic_url_hd: String,
    val requested_by_viewer: Boolean,
    val username: String
)

data class EdgeFollow(
    val count: Int
)

data class EdgeFelixVideoTimeline(
    val count: Int,
    val edges: List<Any>,
    val page_info: PageInfo
)

data class PageInfo(
    val end_cursor: Any,
    val has_next_page: Boolean
)

data class EdgeOwnerToTimelineMedia(
    val count: Int,
    val edges: List<Any>,
    val page_info: PageInfo
)

data class EdgeFollowedBy(
    val count: Int
)

data class EdgeMutualFollowedBy(
    val count: Int,
    val edges: List<Any>
)

data class EdgeMediaCollections(
    val count: Int,
    val edges: List<Any>,
    val page_info: PageInfo
)

data class EdgeSavedMedia(
    val count: Int,
    val edges: List<Any>,
    val page_info: PageInfo
)