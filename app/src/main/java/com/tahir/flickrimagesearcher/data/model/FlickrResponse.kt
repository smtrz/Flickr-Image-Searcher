package com.tahir.flickrimagesearcher.data.model

data class FlickrResponse(
    val photos: PhotoResponse
)
data class PhotoResponse(
    val photo: List<Photo>
)

data class Photo(
    val id: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String
) {
    fun getImageUrl(): String {
        return "https://farm$farm.static.flickr.com/$server/${id}_${secret}.jpg"
    }
}