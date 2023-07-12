package top.easterNday.settings.DogDay

import android.content.Context
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadManager(private val context: Context, private val downloadUrl: String) {
    private val client: OkHttpClient = OkHttpClient()
    private var downloadPath: String = ""
    private var downloadedBytes: Long = 0
    private var totalBytes: Long = 0
    private var isPaused: Boolean = false
    private var isDownloadCompleted: Boolean = false

    init {
        // 设置下载路径为Download文件夹下的Rom文件夹
        val downloadDir = File(context.getExternalFilesDir(null), "Rom")
        downloadDir.mkdirs()
        downloadPath = File(downloadDir, getFileName(downloadUrl)).absolutePath

        // 从持久化存储中读取已下载的字节数和总字节数
        val savedDownloadInfo = readDownloadInfo()
        if (savedDownloadInfo != null) {
            downloadedBytes = savedDownloadInfo.first
            totalBytes = savedDownloadInfo.second
        }
    }

    fun startDownload() {
        isPaused = false

        val requestBuilder = Request.Builder()
            .url(downloadUrl)

        // 设置Range请求头
        val rangeHeader = "bytes=$downloadedBytes-"
        requestBuilder.addHeader("Range", rangeHeader)

        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 下载失败处理逻辑
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body.let { responseBody ->
                    if (!response.isSuccessful) {
                        // 下载失败处理逻辑
                        responseBody.close()
                        return
                    }

                    val outputStream = if (downloadedBytes > 0) {
                        FileOutputStream(downloadPath, true)
                    } else {
                        FileOutputStream(downloadPath)
                    }

                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (responseBody.byteStream().read(buffer).also { bytesRead = it } != -1) {
                        if (isPaused) {
                            // 下载被暂停，中断下载逻辑
                            break
                        }

                        outputStream.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        // 进度更新逻辑
                        val progress = (downloadedBytes.toFloat() / totalBytes * 100).toInt()
                        updateProgress(progress)
                    }

                    outputStream.close()
                    responseBody.close()

                    // 下载完成处理逻辑
                    if (downloadedBytes == totalBytes) {
                        isDownloadCompleted = true
                        clearDownloadInfo()
                    } else {
                        // 保存已下载的字节数和总字节数
                        saveDownloadInfo(downloadedBytes, totalBytes)
                    }
                }
            }
        })
    }

    fun pauseDownload() {
        isPaused = true
    }

    fun isCompleted(): Boolean {
        return isDownloadCompleted
    }

    private fun getFileName(url: String): String {
        val index = url.lastIndexOf("/")
        return if (index != -1) {
            url.substring(index + 1)
        } else {
            "file"
        }
    }

    private fun updateProgress(progress: Int) {
        // 更新下载进度的逻辑，这里只返回下载进度的百分比数字部分
        // 示例中使用回调函数将进度传递给调用者，你可以根据实际需求进行修改
        // 示例中的回调函数是一个接口 OnProgressListener，你可以根据实际情况定义自己的接口
        onProgressListener?.onProgress(progress)
    }

    private fun downloadCompleted() {
        // 下载完成的逻辑
    }

    private fun readDownloadInfo(): Pair<Long, Long>? {
        // 从持久化存储中读取已下载的字节数和总字节数
        // 返回一个Pair<Long, Long>对象，表示已下载的字节数和总字节数
        // 如果没有保存的信息，返回null
        // 示例中使用SharedPreferences进行持久化存储，您可以根据实际情况进行修改
        val sharedPreferences =
            context.getSharedPreferences("DownloadInfo", Context.MODE_PRIVATE)
        val downloadedBytes =
            sharedPreferences.getLong(downloadUrl + "_downloadedBytes", 0)
        val totalBytes = sharedPreferences.getLong(downloadUrl + "_totalBytes", 0)
        return if (downloadedBytes > 0 && totalBytes > 0) {
            Pair(downloadedBytes, totalBytes)
        } else {
            null
        }
    }

    private fun saveDownloadInfo(downloadedBytes: Long, totalBytes: Long) {
        // 保存已下载的字节数和总字节数到持久化存储
        // 示例中使用SharedPreferences进行持久化存储，您可以根据实际情况进行修改
        val sharedPreferences =
            context.getSharedPreferences("DownloadInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(downloadUrl + "_downloadedBytes", downloadedBytes)
        editor.putLong(downloadUrl + "_totalBytes", totalBytes)
        editor.apply()
    }

    private fun clearDownloadInfo() {
        // 清除已下载的字节数和总字节数的持久化存储
        // 示例中使用SharedPreferences进行持久化存储，您可以根据实际情况进行修改
        val sharedPreferences =
            context.getSharedPreferences("DownloadInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(downloadUrl + "_downloadedBytes")
        editor.remove(downloadUrl + "_totalBytes")
        editor.apply()
    }

    interface OnProgressListener {
        fun onProgress(progress: Int)
    }

    private var onProgressListener: OnProgressListener? = null

    fun setOnProgressListener(listener: OnProgressListener) {
        onProgressListener = listener
    }
}
