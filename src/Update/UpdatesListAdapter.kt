package top.easterNday.settings.Update


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import top.easterNday.settings.DogDay.Utils.Companion.copyLink2Clipboard
import top.easterNday.settings.DogDay.Utils.Companion.installPackage
import top.easterNday.settings.DogDay.Utils.Companion.showAlertDialog
import top.easterNday.settings.R
import java.io.File
import java.util.*


class
UpdatesListAdapter(private val mContext: Context, private val dataSet: ArrayList<UpdateItem>) :
    RecyclerView.Adapter<UpdatesListAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val version: TextView
        val datetime: TextView
        val size: TextView
        val tag: TextView
        val actionButton: Button

        val mMenu: ImageButton
        val mProgress: LinearLayout
        val mProgressBar: ProgressBar
        val mProgressBarPercent: TextView
        val mProgressText: TextView

        init {
            // Define click listener for the ViewHolder's View.
            title = view.findViewById(R.id.updateTitle)
            version = view.findViewById(R.id.updateVersion)
            datetime = view.findViewById(R.id.updateDate)
            size = view.findViewById(R.id.updateSize)
            tag = view.findViewById(R.id.updateTag)
            actionButton = view.findViewById(R.id.updateAction)
            // Action Button
            mMenu = view.findViewById(R.id.updateMenu)
            // ProcessBar
            mProgress = view.findViewById(R.id.update_progress_container)
            mProgressBar = view.findViewById(R.id.update_progress_bar)
            mProgressBarPercent = view.findViewById(R.id.update_progress_percent)
            mProgressText = view.findViewById(R.id.update_progress_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.update_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val downloadUrl = dataSet[position].updateUrl
        val updateLog = dataSet[position].updateDesc
        val filename = dataSet[position].updateTitle

        viewHolder.title.text = filename
        viewHolder.version.text = dataSet[position].updateVersion
        viewHolder.datetime.text = dataSet[position].updateDate
        viewHolder.size.text = dataSet[position].updateSize
        viewHolder.tag.text = dataSet[position].updateTag

        // 绑定 Popup 菜单功能
        viewHolder.mMenu.setOnClickListener {
            viewHolder.showPopup(downloadUrl, updateLog)
        }

        // 设定下载显示
        viewHolder.setDownload(downloadUrl, filename)
    }

    fun addUpdateItem(item: UpdateItem) {
        // 异步添加item的逻辑
        dataSet.add(item)
        // 添加完成后调用notifyItemInserted()方法
        notifyItemInserted(dataSet.size - 1) // 获取新添加item的位置
    }

    private fun ViewHolder.showPopup(url: String, log: String) {
        val popup = PopupMenu(mContext, mMenu)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_update_item, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            // 处理选项的点击事件
            when (menuItem.itemId) {
                R.id.menu_copy_url -> {
                    // 复制内容到剪贴板
                    copyLink2Clipboard(mContext, url)
                    true
                }

                R.id.menu_update_log -> {
                    showAlertDialog(mContext, mContext.getString(R.string.text_menu_update_log), log)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    // 控制进度部分和大小部分的显示
    private fun ViewHolder.showProgress(flag: Boolean) {
        val visibility = if (flag) View.VISIBLE else View.INVISIBLE

        size.visibility = if (flag) View.INVISIBLE else View.VISIBLE
        mProgress.visibility = visibility
        mProgressBar.visibility = visibility
        mProgressBarPercent.visibility = visibility
        mProgressText.visibility = visibility
    }

    private fun ViewHolder.onActionButtonClick(action: () -> Unit) {
        actionButton.setOnClickListener(null)
        actionButton.setOnClickListener {
            action.invoke()
        }
    }

    private fun ViewHolder.setDownload(downloadUrl: String, filename: String) {

        fun getDownloadUri(): Uri {
            val romsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Roms")
            if (!romsDir.exists()) {
                romsDir.mkdirs()
            }
            val update = File(romsDir.path, filename)
            return update.toUri()
        }

        val downloadManager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadRequest = DownloadManager.Request(Uri.parse(downloadUrl))
        // 设置在什么网络情况下进行下载
        downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        //设置通知栏标题
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        // request.setTitle(filename)
        // request.setDescription(desc)
        // 设置下载目录为系统的下载目录
        //downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        downloadRequest.setDestinationUri(getDownloadUri())

        var downloadId: Long = -1 // 用于保存下载任务的ID


        // 更新下载按钮的文本
        fun updateActionButtonText(text: String) {
            actionButton.text = text
        }

        // 更新按钮点击事件
        fun updateActionButtonClick(action: () -> Unit) {
            actionButton.setOnClickListener(null)
            actionButton.setOnClickListener {
                action.invoke()
            }
        }

        // 暂停下载任务
        fun pauseDownload() {
            downloadManager.remove(downloadId)
            updateActionButtonText(mContext.getString(R.string.update_download))
        }


        // 开始下载任务
        fun startDownload() {
            downloadId = downloadManager.enqueue(downloadRequest)
            updateActionButtonText(mContext.getString(R.string.update_downloading))
        }

        // 查询下载任务的状态和进度
        fun queryDownloadStatus() {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)

            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val progress =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                when (status) {
                    DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                        showProgress(true)
                        val percent = progress * 100 / total
                        mProgressBar.progress = percent
                        mProgressBarPercent.text = mContext.getString(R.string.text_progress, percent)
                        mProgressText.text = mContext.getString(
                            R.string.text_progress_total,
                            Formatter.formatFileSize(mContext, progress.toLong()),
                            Formatter.formatFileSize(mContext, total.toLong())
                        )

                        // 下载暂停，显示继续按钮
                        updateActionButtonText(mContext.getString(R.string.update_pause))
                        updateActionButtonClick {
                            pauseDownload()
                        }
                    }

                    DownloadManager.STATUS_PAUSED -> {
                        // 下载暂停，显示继续按钮
                        updateActionButtonText(mContext.getString(R.string.update_resume))
                        updateActionButtonClick {
                            startDownload()
                        }
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        // 下载完成，显示安装按钮或其他操作
                        showProgress(false)
                        updateActionButtonText(mContext.getString(R.string.update_flash))
                        updateActionButtonClick {
                            mContext.installPackage(getDownloadUri())
                        }
                    }

                    DownloadManager.STATUS_FAILED -> {
                        // 下载失败，显示重新下载按钮
                        updateActionButtonText(mContext.getString(R.string.update_download))
                        updateActionButtonClick {
                            startDownload()
                        }
                    }
                }
            } else {
                updateActionButtonText(mContext.getString(R.string.update_download))
                updateActionButtonClick {
                    startDownload()
                }
            }

            cursor.close()
        }

        // 定时查询下载状态
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                (mContext as Activity).runOnUiThread {
                    queryDownloadStatus()
                }
            }
        }
        timer.schedule(timerTask, 0, 1000) // 每隔1秒查询一次下载状态
    }
}