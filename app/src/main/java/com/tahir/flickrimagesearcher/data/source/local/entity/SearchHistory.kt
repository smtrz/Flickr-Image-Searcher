package com.tahir.flickrimagesearcher.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [Index(value = ["query"], unique = true)]
)
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String
)