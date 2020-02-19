package com.foreknowledge.photomemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class MemoDbTable(context: Context) {
    private val dbHelper = PhotoMemoDB(context)

    fun store(title: String, content: String, imagePaths: List<String>) {
        dbHelper.writableDatabase
            .use {
                val memoValues = ContentValues()
                with(memoValues) {
                    put(MemoEntry.TITLE_COL, title)
                    put(MemoEntry.CONTENT_COL, content)
                }

                it.transaction {
                    val memoId = insert(MemoEntry.TABLE_NAME, null, memoValues)

                    for (imagePath in imagePaths) {
                        val imageValues = ContentValues()
                        with(imageValues) {
                            put(ImageEntry.MEMO_ID, memoId)
                            put(ImageEntry.IMAGE_PATH_COL, imagePath)
                        }

                        insert(ImageEntry.TABLE_NAME, null, imageValues)
                    }
                }
            }
    }

    fun update(memo: Memo) {
        dbHelper.writableDatabase
            .use {
                val memoValues = ContentValues()
                with(memoValues) {
                    put(MemoEntry._ID, memo.id)
                    put(MemoEntry.TITLE_COL, memo.title)
                    put(MemoEntry.CONTENT_COL, memo.content)
                }

                it.transaction {
                    update(MemoEntry.TABLE_NAME, memoValues, "${MemoEntry._ID} = ${memo.id}", null)

                    delete(ImageEntry.TABLE_NAME, "${ImageEntry.MEMO_ID} = ${memo.id}", null)

                    for (imagePath in memo.imagePaths) {
                        val imageValues = ContentValues()
                        with(imageValues) {
                            put(ImageEntry.MEMO_ID, memo.id)
                            put(ImageEntry.IMAGE_PATH_COL, imagePath)
                        }

                        insert(ImageEntry.TABLE_NAME, null, imageValues)
                    }
                }
            }
    }

    fun remove(memoId: Long) {
        val imagePaths = readImagePaths(memoId)

        dbHelper.writableDatabase
            .use {
                it.transaction {
                    delete(MemoEntry.TABLE_NAME, "${MemoEntry._ID} = $memoId", null)
                    delete(ImageEntry.TABLE_NAME, "${ImageEntry.MEMO_ID} = $memoId", null)

                    FileHelper.deleteFiles(imagePaths)
                }
            }
    }

    fun readAllMemo(): List<Memo> {
        val memoList: MutableList<Memo> = mutableListOf()

        val columns = arrayOf(MemoEntry._ID, MemoEntry.TITLE_COL, MemoEntry.CONTENT_COL)
        val order = "${MemoEntry._ID} ASC"

        dbHelper.readableDatabase
            .use {
                val memoCursor = it.doQuery(MemoEntry.TABLE_NAME, columns, orderBy = order)
                while (memoCursor.moveToNext()) {
                    val memoId = memoCursor.getLong(MemoEntry._ID)
                    val title = memoCursor.getString(MemoEntry.TITLE_COL)
                    val content = memoCursor.getString(MemoEntry.CONTENT_COL)

                    memoList.add(Memo(memoId, title, content, readImagePaths(memoId)))
                }

                memoCursor.close()

                return memoList
            }
    }

    fun readMemo(memoId: Long): Memo {
        val columns = arrayOf(MemoEntry.TITLE_COL, MemoEntry.CONTENT_COL)
        val selection = "${MemoEntry._ID} = $memoId"
        val order = "${MemoEntry._ID} ASC"

        dbHelper.readableDatabase
            .use {
                val memoCursor = it.doQuery(MemoEntry.TABLE_NAME, columns, selection = selection, orderBy = order)
                memoCursor.moveToNext()

                val title = memoCursor.getString(MemoEntry.TITLE_COL)
                val content = memoCursor.getString(MemoEntry.CONTENT_COL)

                memoCursor.close()

                return Memo(memoId, title, content, readImagePaths(memoId))
            }
    }

    fun readImagePaths(memoId: Long): List<String> {
        val imagePaths: MutableList<String> = mutableListOf()

        val columns = arrayOf(ImageEntry.MEMO_ID, ImageEntry.IMAGE_PATH_COL)
        val selection = "${ImageEntry.MEMO_ID} = $memoId"
        val order = "${ImageEntry._ID} ASC"

        dbHelper.readableDatabase
            .use {
                val imageCursor = it.doQuery(ImageEntry.TABLE_NAME, columns, selection = selection, orderBy = order)
                while (imageCursor.moveToNext())
                    imagePaths.add(imageCursor.getString(ImageEntry.IMAGE_PATH_COL))

                imageCursor.close()
            }

        return imagePaths
    }

    private fun SQLiteDatabase.doQuery(table: String, columns: Array<String>, selection: String? = null
                               , selectionArgs: Array<String>? = null, groupBy: String? = null
                               , having: String? = null, orderBy: String? = null): Cursor {
        return this.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
    }

    private fun Cursor.getLong(columnName: String) = getLong(getColumnIndex(columnName))
    private fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))

    private inline fun SQLiteDatabase.transaction(function: SQLiteDatabase.() -> Unit) {
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
}