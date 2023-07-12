package top.easterNday.settings.DogDay

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.RecoverySystem
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
         * 该函数将 Unix 时间戳转换为格式为“yyyy-MM-dd HH:mm:ss”的本地时间字符串。
         *
         * @param unixTimeStamp `unixTimeStamp` 参数是一个长值，表示自 1970 年 1 月 1 日 00:00:00 UTC（协调世界时）以来经过的秒数。
         * @return 表示与给定 Unix 时间戳对应的本地时间的格式化字符串。
         */
        fun convertUnixToLocalTime(unixTimeStamp: Long): String {
            val date = Date(unixTimeStamp * 1000) // 将 Unix 时间戳转换为毫秒
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // 指定日期格式和本地化
            sdf.timeZone = TimeZone.getDefault() // 将时区设置为设备的默认时区
            return sdf.format(date) // 格式化为当地时间字符串
        }

        /**
         * 函数“copyLink2Clipboard”将给定的 URL 复制到 Kotlin 中的剪贴板。
         *
         * @param context context 参数是应用程序的当前上下文。它通常作为调用方法的活动或片段传递。它用于访问系统服务和资源。
         * @param url 您要复制到剪贴板的 URL。
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
         * 函数“showAlertDialog”在 Kotlin 中显示具有给定标题和内容的警报对话框。
         *
         * @param context context 参数是应用程序的当前上下文。它通常作为显示对话框的活动或片段传递。
         * @param title title 参数是一个字符串，表示警报对话框的标题。它显示在对话框的顶部。
         * @param content `content` 参数是一个字符串，表示警报对话框的消息或内容。它是将显示给用户的文本。
         */
        fun showAlertDialog(context: Context, title: String, content: String) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(content)
            builder.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }

        /**
         * 函数 `openUrl` 使用 Kotlin 中的意图在 Web 浏览器中打开 URL。
         *
         * @param context context 参数是 Context 类的实例，它提供对特定于应用程序的资源和类的访问。它通常从活动或片段传入。
         * @param uri `uri` 参数是一个字符串，表示您要打开的 URL 或 URI。它可以是 Web URL、文件 URI 或任何其他有效的 URI 方案。
         */
        fun openUrl(context: Context, uri: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(uri)
            context.startActivity(intent)
        }

        fun Context.installPackage(update: Uri) {
            val file = File(update.path.toString())
            if (file.exists()) {
                RecoverySystem.installPackage(this, file)
            }
            else{
                showAlertDialog(this,"错误","没有下载对应文件或对应文件损坏!")
            }
        }

    }
}