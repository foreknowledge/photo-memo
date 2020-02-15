package com.foreknowledge.photomemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class MemoDbTable(context: Context) {

    private val dbHelper = PhotoMemoDB(context)

    fun store(title: String, content: String, images: List<Bitmap>) {
        dbHelper.writableDatabase
            .use {
                val memoValues = ContentValues()
                with(memoValues) {
                    put(MemoEntry.TITLE_COL, title)
                    put(MemoEntry.CONTENT_COL, content)
                }

                it.transaction {
                    val memoId = insert(MemoEntry.TABLE_NAME, null, memoValues)

                    for (image in images) {
                        val imageValues = ContentValues()
                        with(imageValues) {
                            put(ImageEntry.MEMO_ID, memoId)
                            put(ImageEntry.IMAGE_COL, toByteArray(image))
                        }

                        insert(ImageEntry.TABLE_NAME, null, imageValues)
                    }
                }
            }
    }

    fun remove(memoId: Long) {
        dbHelper.writableDatabase
            .use {
                it.transaction {
                    delete(MemoEntry.TABLE_NAME, "${MemoEntry._ID} = $memoId", null)

                    delete(ImageEntry.TABLE_NAME, "${ImageEntry.MEMO_ID} = $memoId", null)
                }
            }
    }

    fun readAllMemo(): List<Memo> {
        dbHelper.readableDatabase
            .use {
                val memoList: MutableList<Memo> = mutableListOf()

                var columns = arrayOf(MemoEntry._ID, MemoEntry.TITLE_COL, MemoEntry.CONTENT_COL)
                var order = "${MemoEntry._ID} ASC"

                val memoCursor = it.doQuery(MemoEntry.TABLE_NAME, columns, orderBy = order)
                while (memoCursor.moveToNext()) {
                    val memoId = memoCursor.getLong(MemoEntry._ID)
                    val title = memoCursor.getString(MemoEntry.TITLE_COL)
                    val content = memoCursor.getString(MemoEntry.CONTENT_COL)
                    val images: MutableList<Bitmap> = mutableListOf()

                    columns = arrayOf(ImageEntry.MEMO_ID, ImageEntry.IMAGE_COL)
                    val selection = "${ImageEntry.MEMO_ID} = $memoId"
                    order = "${ImageEntry._ID} ASC"

                    val imageCursor = it.doQuery(ImageEntry.TABLE_NAME, columns, selection = selection, orderBy = order)
                    while (imageCursor.moveToNext())
                        images.add(imageCursor.getBlob(ImageEntry.IMAGE_COL))

                    memoList.add(Memo(memoId, title, content))
                    imageCursor.close()
                }

                memoCursor.close()

                return memoList
            }
    }

    fun readMemo(memoId: Long): Memo {
        dbHelper.readableDatabase
            .use {
                var columns = arrayOf(MemoEntry.TITLE_COL, MemoEntry.CONTENT_COL)
                var selection = "${MemoEntry._ID} = $memoId"
                var order = "${MemoEntry._ID} ASC"

                val memoCursor = it.doQuery(MemoEntry.TABLE_NAME, columns, selection = selection, orderBy = order)
                memoCursor.moveToNext()

                val title = memoCursor.getString(MemoEntry.TITLE_COL)
                val content = memoCursor.getString(MemoEntry.CONTENT_COL)

                memoCursor.close()


                val images: MutableList<Bitmap> = mutableListOf()

                columns = arrayOf(ImageEntry.MEMO_ID, ImageEntry.IMAGE_COL)
                selection = "${ImageEntry.MEMO_ID} = $memoId"
                order = "${ImageEntry._ID} ASC"

                val imageCursor = it.doQuery(ImageEntry.TABLE_NAME, columns, selection = selection, orderBy = order)
                while (imageCursor.moveToNext())
                    images.add(imageCursor.getBlob(ImageEntry.IMAGE_COL))

                imageCursor.close()

                return Memo(memoId, title, content, images)
            }
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}

private fun SQLiteDatabase.doQuery(table: String, columns: Array<String>, selection: String? = null
                                   , selectionArgs: Array<String>? = null, groupBy: String? = null
                                   , having: String? = null, orderBy: String? = null): Cursor {
    return this.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
}

private fun Cursor.getLong(columnName: String) = getLong(getColumnIndex(columnName))
private fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))

private fun Cursor.getBlob(columnName: String): Bitmap {
    val bytes = getBlob(getColumnIndex(columnName))
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()

    val id = try {
        val returnValue = function()
        setTransactionSuccessful()

        returnValue
    } finally {
        endTransaction()
    }
    close()

    return id
}