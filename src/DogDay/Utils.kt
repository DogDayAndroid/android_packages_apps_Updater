package top.easterNday.settings.DogDay

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Utils {

//    private fun InstallApk(context: Context, uri: Uri) {
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        intent.setDataAndType(uri, "application/vnd.android.package-archive")
//        context.startActivity(intent)
//    }

    companion object {
        /**
         * The function `toDate` converts a timestamp in seconds to a formatted date and time string in the format "yyyy-MM-dd HH:mm".
         *
         * @param timestamp The `timestamp` parameter is a double value representing the number of seconds since the Unix epoch (January 1, 1970, 00:00:00 UTC).
         * @return The function `toDate` returns a string representation of a date and time in the format "yyyy-MM-dd HH:mm".
         */
        fun convertUnixToLocalTime(unixTimeStamp: Long): String {
            val date = Date(unixTimeStamp * 1000) // 将 Unix 时间戳转换为毫秒
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // 指定日期格式和本地化
            sdf.timeZone = TimeZone.getDefault() // 将时区设置为设备的默认时区
            return sdf.format(date) // 格式化为当地时间字符串
        }

        fun genDownloadPath(subdir: String, filename: String): Uri {
            //设置文件存放目录
            val destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destinationPath = File(destinationDirectory, subdir)
            if (!destinationPath.exists()) {
                destinationPath.mkdirs()
            }
            val destinationFile = File(destinationPath, filename)
            return Uri.fromFile(destinationFile)
        }

        @SuppressLint("Range")
        fun isFileDownloadedAndAvailable(context: Context, url: String, path: Uri): Boolean {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = Query()
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                do {
                    val downloadedUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                    if (downloadedUrl == url) {
                        val file = path.toFile()
                        return file.exists() // 文件存在，表示已下载且未被删除
                    }
                } while (cursor.moveToNext())
            }
            return false // 文件未下载或已被删除
        }

        /**
         * The function `downloadFromUrl` downloads a file from a given URL and saves it to the specified directory on the device.
         *
         * @param context The context parameter is the current context of the application. It is typically passed as the activity or fragment where the download is initiated.
         * @param downloadUrl The URL from which the file needs to be downloaded.
         * @param filename The name of the file to be downloaded.
         * @param desc The `desc` parameter is a description of the file being downloaded. It is used to provide additional information about the download in the notification.
         * @param subdir The `subdir` parameter is a string that represents the subdirectory where the downloaded file will be stored. It is used to create a directory within the Downloads directory on the device's external storage.
         * @return The function `downloadFromUrl` returns a `Long` value, which is the unique ID of the download request that was enqueued in the `DownloadManager`.
         */
        fun downloadFromUrl(
            context: Context,
            downloadUrl: String,
            filename: String,
            desc: String,
            path: Uri
        ): Long {
            val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(downloadUrl))
            // 设置在什么网络情况下进行下载
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            //设置通知栏标题
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(filename)
            request.setDescription(desc)
            request.setDestinationUri(path)
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            return downloadManager.enqueue(request)
        }

        /**
         * The function `copyLink2Clipboard` copies a given URL to the clipboard in Kotlin.
         *
         * @param context The context parameter is the current context of the application. It is typically passed as the activity or fragment where the method is being called from. It is used to access system services and resources.
         * @param url The URL that you want to copy to the clipboard.
         */
        fun copyLink2Clipboard(context: Context, url: String) {
            //获取剪贴板管理器：
            //获取剪贴板管理器：
            val cm: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建普通字符型ClipData
            val mClipData = ClipData.newRawUri("Label", Uri.parse(url))
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData)
        }

        /**
         * The function `showAlertDialog` displays an alert dialog with a given title and content in Kotlin.
         *
         * @param context The context parameter is the current context of the application. It is typically passed as the activity or fragment where the dialog is being shown.
         * @param title The title parameter is a string that represents the title of the alert dialog. It is displayed at the top of the dialog box.
         * @param content The `content` parameter is a string that represents the message or content of the alert dialog. It is the text that will be displayed to the user.
         */
        fun showAlertDialog(context: Context, title: String, content: String) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(content)
            builder.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }

    }
}