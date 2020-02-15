package com.foreknowledge.photomemo

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
    const val IMAGE_COL = "image"
}