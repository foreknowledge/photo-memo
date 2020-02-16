package com.foreknowledge.photomemo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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