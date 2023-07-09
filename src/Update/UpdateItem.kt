package top.easterNday.settings.Update

import android.content.Context
import android.text.format.Formatter
import top.easterNday.settings.DogDay.Utils.Companion.convertUnixToLocalTime
import top.easterNday.settings.R

class UpdateItem(
    context: Context,
    dateItem: Long,
    descItem: String,
    titleItem: String,
    sizeItem: Long,
    tagItem: String,
    urlItem: String,
    versionItem: String,
) {
    val updateTitle = titleItem
    val updateVersion = context.getString(R.string.update_version, versionItem)
    val updateSize: String = context.getString(R.string.update_size, Formatter.formatFileSize(context, sizeItem))
    val updateDate = context.getString(R.string.update_date,convertUnixToLocalTime(dateItem))
    val updateDesc = if (descItem.isNotEmpty()) {
        context.getString(R.string.update_desc, descItem)
    } else {
        context.getString(R.string.update_desc_none)
    }
    val updateUrl = urlItem
    val updateTag = tagItem
}


