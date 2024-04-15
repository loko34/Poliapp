package com.example.poliapp.db.contracts

import android.provider.BaseColumns

class ProfileContract {
    object ProfileEntry : BaseColumns {
        const val TABLE_NAME = "perfiles"
        const val _ID = BaseColumns._ID
        const val COLUMN_NAMES = "nombres"
        const val COLUMN_EMAIL = "correo"
        const val COLUMN_OCCUPATION = "ocupacion"
        const val COLUMN_IMAGE = "imagen"
    }
}