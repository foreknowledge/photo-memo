package com.foreknowledge.photomemo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

const val DATABASE_NAME = "photomemo.db"
const val DATABASE_VERSION = 10

object MemoEntry: BaseColumns {
    const val TABLE_NAME = "memo"

    const val _ID = "id"
    const val TITLE_COL = "title"
    const val CONTENT_COL = "content"
}

object ImageEntry: BaseColumns {
    const val TABLE_NAME = "image"

    const val _ID = "id"
    const val MEMO_ID = "memo_id"
    const val IMAGE_PATH_COL = "image_path"
}

data class Memo(val id: Long, val title: String, val content: String, val imagePaths: List<String> = listOf())

class PhotoMemoDB(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val SQL_CREATE_MEMO_ENTRIES = "CREATE TABLE IF NOT EXISTS ${MemoEntry.TABLE_NAME} (" +
                "${MemoEntry._ID} INTEGER PRIMARY KEY, " +
                "${MemoEntry.TITLE_COL} TEXT NOT NULL, " +
                "${MemoEntry.CONTENT_COL} TEXT NOT NULL " +
                ")"

        private const val SQL_DELETE_MEMO_ENTRIES = "DROP TABLE IF EXISTS ${MemoEntry.TABLE_NAME}"

        private const val SQL_CREATE_IMAGE_ENTRIES = "CREATE TABLE IF NOT EXISTS ${ImageEntry.TABLE_NAME} (" +
                "${ImageEntry._ID} INTEGER PRIMARY KEY, " +
                "${ImageEntry.MEMO_ID} INTEGER NOT NULL, " +
                "${ImageEntry.IMAGE_PATH_COL} TEXT NOT NULL " +
                ")"

        private const val SQL_DELETE_IMAGE_ENTRIES = "DROP TABLE IF EXISTS ${ImageEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_MEMO_ENTRIES)
        db.execSQL(SQL_CREATE_IMAGE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        db.execSQL(SQL_DELETE_MEMO_ENTRIES)
        db.execSQL(SQL_DELETE_IMAGE_ENTRIES)
        onCreate(db)
    }
}