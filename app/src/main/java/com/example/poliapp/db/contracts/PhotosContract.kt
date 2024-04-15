package com.example.poliapp.db.contracts

import android.provider.BaseColumns

class PhotosContract {
    object PhotosEntry : BaseColumns {
        const val TABLE_NAME = "photos"
        const val _ID = BaseColumns._ID
        const val COLUMN_NAME = "nombre"
        const val COLUMN_DESCRIPTION = "descripcion"
        const val COLUMN_IMAGE = "imagen"
    }
}