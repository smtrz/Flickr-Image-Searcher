package com.tahir.flickrimagesearcher.util

import com.tahir.flickrimagesearcher.BuildConfig

// Search API
const val BASE_URL = "https://api.flickr.com/"
const val METHOD = "flickr.photos.search"
const val API_KEY = BuildConfig.FLICKR_API_KEY
const val FORMAT = "json"
const val NO_JSON_CALLBACK = 1
const val SAFE_SEARCH = 1

