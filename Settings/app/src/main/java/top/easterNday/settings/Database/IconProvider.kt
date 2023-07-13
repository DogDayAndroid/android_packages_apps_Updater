package top.easterNday.settings.Database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import top.easterNday.settings.GlobalSettings

class IconContentProvider : ContentProvider() {

    private lateinit var dbHelper: Helper

    // 定义 ContentProvider 的授权信息
    private val authority = GlobalSettings.dbAuthorities
    private val path = Helper.icon_tableName

    // 定义 URI 匹配器
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    // 定义 URI 匹配的常量
    private val ICONS = 1
    private val ICON_ID = 2

    init {
        // 添加 URI 匹配规则
        uriMatcher.addURI(authority, path, ICONS)
        uriMatcher.addURI(authority, "$path/#", ICON_ID)
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
            ICONS -> {
                cursor = db.query(
                    Helper.icon_tableName,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }

            ICON_ID -> {
                val id = ContentUris.parseId(uri)
                val singleSelection = "${Helper.icon_tableName}.packageName = ?"
                val singleSelectionArgs = arrayOf(id.toString())

                cursor = db.query(
                    Helper.icon_tableName,
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
        val rowId = db.insert(Helper.icon_tableName, null, values)
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
        val rowsUpdated = db.update(Helper.icon_tableName, values, selection, selectionArgs)
        if (rowsUpdated > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(Helper.icon_tableName, selection, selectionArgs)
        if (rowsDeleted > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}
