package com.zakharov.lessonsqlkotlin.db

import android.provider.BaseColumns

object MyDbNameClass : BaseColumns {
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_DESCRIPTION = "content"
    const val COLUMN_NAME_IMAGE_URI = "uri"
    const val COLUMN_NAME_TIME = "time"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "MyLessonDb.db"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_NAME_TITLE TEXT, $COLUMN_NAME_DESCRIPTION TEXT, $COLUMN_NAME_IMAGE_URI TEXT, $COLUMN_NAME_TIME TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}