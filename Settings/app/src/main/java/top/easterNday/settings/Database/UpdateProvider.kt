package top.easterNday.settings.Database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import top.easterNday.settings.DogDay.LogUtils


class UpdateProvider : ContentProvider() {

    private lateinit var dbHelper: Helper

    // 定义 URI 匹配器
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private val KERNELS = 1
    private val KERNEL_ID = 2

    init {
        // 添加 URI 匹配规则
        uriMatcher.addURI(authority, path, KERNELS)
        uriMatcher.addURI(authority, "${path}/#", KERNEL_ID)
    }

    override fun onCreate(): Boolean {
        dbHelper = Helper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor?

        when (uriMatcher.match(uri)) {
            KERNELS -> {
                cursor = db.query(
                    path,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }

            KERNEL_ID -> {
                val id = ContentUris.parseId(uri)
                val singleSelection = "${Companion.path}.id = ?"
                val singleSelectionArgs = arrayOf(id.toString())

                cursor = db.query(
                    Companion.path,
                    projection,
                    singleSelection,
                    singleSelectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val db = dbHelper.writableDatabase

        // 查询数据是否存在
        val selection = "${path}.id = ?"
        val selectionArgs = arrayOf(values?.getAsString("id"))

        LogUtils.logger.d(authority)

        val cursor = db.query(
            path,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.count > 0) {
            // 数据已存在，执行更新操作
            val updatedRows = db.update(
                Companion.path,
                values,
                selection,
                selectionArgs
            )
            if (updatedRows > 0) {
                context?.contentResolver?.notifyChange(uri, null)
                return uri
            } else {
                cursor.close()
                throw IllegalArgumentException("Failed to update row: $uri")
            }
        }

        // 数据不存在，执行插入操作
        val rowId = db.insert(Companion.path, null, values)
        if (rowId > 0) {
            val insertedUri = ContentUris.withAppendedId(uri, rowId)
            context?.contentResolver?.notifyChange(uri, null)
            return insertedUri
        }

        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val rowsUpdated = db.update(Companion.path, values, selection, selectionArgs)
        if (rowsUpdated > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(Companion.path, selection, selectionArgs)
        if (rowsDeleted > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        fun getURI(): Uri {
            return Uri.parse("content://$authority/$path");
        }

        private const val path = Helper.kernel_tableName
        private const val authority = "${Helper.dbAuthorities}.$path"
    }
}

