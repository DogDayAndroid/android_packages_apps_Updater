package top.easterNday.settings.Update

import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import org.json.JSONObject
import top.easterNday.settings.Database.UpdateProvider
import top.easterNday.settings.DogDay.LogUtils.logger
import top.easterNday.settings.DogDay.Utils.Companion.convertUnixToLocalTime
import top.easterNday.settings.DogDay.Utils.Companion.formatFileSize
import top.easterNday.settings.R

class UpdateItem(
    context: Context,
    json: JSONObject,
    private var typeTag: String
) {
    private val mContext = context

    var id: Long = json.optLong("id")

    var filename: String = json.optString("filename")
    var version: String = context.getString(R.string.update_version, json.optString("version"))
    var size: String = context.getString(R.string.update_size, json.optLong("size").formatFileSize(context))

    var datetime: String = context.getString(
        R.string.update_date,
        json.optLong("datetime").convertUnixToLocalTime()
    )

    var describe: String = if (json.optString("desc").isNotEmpty()) {
        context.getString(R.string.update_desc, json.optString("desc"))
    } else {
        context.getString(R.string.update_desc_none)
    }
    var url: String = json.optString("url")
    var tag: String = json.optString("tag")

    private var downloadID: Long? = null

    constructor(
        context: Context,
        id: Long,
        filename: String,
        version: String,
        size: String,
        datetime: String,
        describe: String,
        url: String,
        tag: String,
        downloadID: Long?,
        typeTag: String
    ) : this(context, JSONObject(), typeTag) {
        this.id = id
        this.filename = filename
        this.version = version
        this.size = size
        this.datetime = datetime
        this.describe = describe
        this.url = url
        this.tag = tag
        this.downloadID = downloadID
        this.typeTag = typeTag
    }

    fun updateInfo() {
        val resolver = mContext.contentResolver
        val values = ContentValues()
        values.put("id", id)
        values.put("filename", filename)
        values.put("version", version)
        values.put("size", size)
        values.put("datetime", datetime)
        values.put("describe", describe)
        values.put("url", url)
        values.put("tag", tag)
        values.put("type", typeTag)
        downloadID?.let { values.put("downloadID", it) }

        val uri = UpdateProvider.getURI()
        resolver.insert(uri, values)
        Toast.makeText(mContext, "更新数据插入成功", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getAll(context: Context, typeTag: String): ArrayList<UpdateItem> {
            val uri = UpdateProvider.getURI()

            val projection: Array<String>? = null // 设置 projection 为空
            val selection = "type = ?" // 查询条件为 "type = ?"
            val selectionArgs = arrayOf(typeTag) // 传入的参数值
            val sortOrder: String? = null // 排序方式为 null

            val resolver = context.contentResolver
            val cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder)

            val newData: ArrayList<UpdateItem> = ArrayList()

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val filename = cursor.getString(cursor.getColumnIndexOrThrow("filename"))
                    val version = cursor.getString(cursor.getColumnIndexOrThrow("version"))
                    val size = cursor.getString(cursor.getColumnIndexOrThrow("size"))
                    val datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"))
                    val describe = cursor.getString(cursor.getColumnIndexOrThrow("describe"))
                    val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))
                    val tag = cursor.getString(cursor.getColumnIndexOrThrow("tag"))
                    val downloadID = cursor.getLong(cursor.getColumnIndexOrThrow("downloadID"))

                    newData.add(
                        UpdateItem(
                            context,
                            id,
                            filename,
                            version,
                            size,
                            datetime,
                            describe,
                            url,
                            tag,
                            downloadID,
                            typeTag
                        )
                    )

                    logger.d(
                        "ID: $id, Filename: $filename, Version: $version, Size: $size, Datetime: $datetime, Describe: $describe, URL: $url, Tag: $tag, DownloadID: $downloadID"
                    )
                }
                cursor.close()
            }
            return newData
        }
    }
}
