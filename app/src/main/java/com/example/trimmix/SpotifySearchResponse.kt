package com.example.trimmix

data class SpotifySearchResponse(
    val tracks: Tracks
)

data class Tracks(
    val items: List<Track>
)

data class Track(
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val preview_url: String
)

data class Artist(
    val name: String
)

data class Album(
    val name: String,
    val images: List<Image>
)

data class Image(
    val url: String
)

