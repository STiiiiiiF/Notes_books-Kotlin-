package com.zakharov.lessonsqlkotlin.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {

    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    suspend fun insertToDb(title: String, content: String, uri: String, time: String) =
        withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put(MyDbNameClass.COLUMN_NAME_TITLE, title)
                put(MyDbNameClass.COLUMN_NAME_DESCRIPTION, content)
                put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
                put(MyDbNameClass.COLUMN_NAME_TIME, time)
            }
            db?.insert(MyDbNameClass.TABLE_NAME, null, values)
        }

    suspend fun updateItem(title: String, description: String, uri: String, id: Int, time: String) =
        withContext(Dispatchers.IO) {
            val selection = BaseColumns._ID + "=$id"
            val values = ContentValues().apply {
                put(MyDbNameClass.COLUMN_NAME_TITLE, title)
                put(MyDbNameClass.COLUMN_NAME_DESCRIPTION, description)
                put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
                put(MyDbNameClass.COLUMN_NAME_TIME, "изм. $time")

            }
            db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)

        }

    fun removeItemFromDb(id: String) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    @SuppressLint("Range")
    suspend fun readDbData(searchText: String): ArrayList<ListItem> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME,
            null,
            selection,
            arrayOf("%$searchText%"),
            null,
            null,
            null
        )

        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataTitle =
                    cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
                val dataDescription =
                    cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_DESCRIPTION))
                val dataUri =
                    cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
                val dataId =
                    cursor?.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val dataTime =
                    cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME))
                val item = ListItem()
                item.title = dataTitle.toString()
                item.desc = dataDescription.toString()
                item.uri = dataUri.toString()
                item.time = dataTime.toString()
                item.id = dataId!!
                dataList.add(item)
            }
        }
        cursor?.close()
        return@withContext dataList
    }

    fun closeDb() {
        myDbHelper.close()
    }
}


