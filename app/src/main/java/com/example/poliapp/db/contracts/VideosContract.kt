package com.example.poliapp.db.contracts

import android.provider.BaseColumns

class VideosContract {
    object VideosEntry : BaseColumns {
        const val TABLE_NAME = "videos"
        const val _ID = BaseColumns._ID
        const val COLUMN_VIDEO = "video"
    }
}