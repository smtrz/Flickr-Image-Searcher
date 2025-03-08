package com.tahir.flickrimagesearcher.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val query: String // ✅ Ensures uniqueness (NO DUPLICATES)
)