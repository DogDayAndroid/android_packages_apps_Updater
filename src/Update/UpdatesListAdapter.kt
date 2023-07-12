package top.easterNday.settings.Update


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.RecoverySystem
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toFile
import androidx.recyclerview.widget.RecyclerView
import top.easterNday.settings.DogDay.LogUtils.logger
import top.easterNday.settings.DogDay.Utils.Companion.copyLink2Clipboard
import top.easterNday.settings.DogDay.Utils.Companion.downloadFromUrl
import top.easterNday.settings.DogDay.Utils.Companion.genDownloadPath
import top.easterNday.settings.DogDay.Utils.Companion.getDownloadIdByUrl
import top.easterNday.settings.DogDay.Utils.Companion.getDownloadStatusById
import top.easterNday.settings.DogDay.Utils.Companion.queryDownloadProgress
import top.easterNday.settings.DogDay.Utils.Companion.showAlertDialog
import top.easterNday.settings.R
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
        val fileUri = genDownloadPath(mContext.getString(R.string.download_sub_dir), filename)

        viewHolder.title.text = filename
        viewHolder.version.text = dataSet[position].updateVersion
        viewHolder.datetime.text = dataSet[position].updateDate
        viewHolder.size.text = dataSet[position].updateSize
        viewHolder.tag.text = dataSet[position].updateTag

        // 绑定 Popup 菜单功能
        viewHolder.mMenu.setOnClickListener {
            showPopup(mContext, viewHolder.mMenu, downloadUrl, updateLog)
        }

        viewHolder.setDownload(mContext, downloadUrl, fileUri, filename)
    }

    private fun ViewHolder.setDownload(
        context: Context,
        downloadUrl: String,
        fileUri: Uri,
        fileName: String
    ): Long? {
        val fileId: Long? = getDownloadIdByUrl(mContext, downloadUrl, fileUri)
        if (fileId == null) {
            // 设置按钮字样为 下载
            actionButton.text = mContext.getString(R.string.update_download)
            // 取消其他监听内容
            actionButton.removeCallbacks(null)
            // 点击下载按钮开始下载
            actionButton.setOnClickListener {
                // 调用系统服务进行下载
                downloadFromUrl(
                    mContext,
                    downloadUrl,
                    fileName,
                    context.getString(R.string.update_download_info),
                    fileUri
                )
                // 启动计时器，每隔一段时间查询下载进度并更新进度条
                checkDownload(mContext, downloadUrl, fileUri, fileName)
            }
        } else {
            // 判断下载状态
            when (getDownloadStatusById(context, fileId)) {
                // 如果正在下载
                DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                    showProgress(true)
                    // 获取下载进度
                    actionButton.text = context.getString(R.string.update_downloading)
                }
                // 如果下载成功
                DownloadManager.STATUS_SUCCESSFUL -> {
                    // 设置按钮字样为 刷入
                    actionButton.text = mContext.getString(R.string.update_flash)
                    // 刷入当前刷机包
                    actionButton.setOnClickListener {
                        val file = fileUri.toFile()
                        logger.d("Install %s", file.toString())
                        RecoverySystem.installPackage(mContext, file)
                    }
                }
                // 如果下载暂停
                DownloadManager.STATUS_PAUSED -> {
                    // 设置按钮字样为 暂停
                    actionButton.text = mContext.getString(R.string.update_pause)
                }
            }
        }
        return fileId
    }

    // 检查下载进度，更新进度条
    private fun ViewHolder.checkDownload(
        context: Context,
        downloadUrl: String,
        fileUri: Uri,
        fileName: String
    ) {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                (context as Activity).runOnUiThread {
                    val downloadId = setDownload(context, downloadUrl, fileUri, fileName)
                    if (downloadId != null) {
                        val progress = queryDownloadProgress(context, downloadId)

                        mProgressBar.setProgress(progress, true)
                        mProgressBarPercent.text = mContext.getString(R.string.text_progress, progress)
                        // TODO:显示mProgressText内容
                    } else {
                        timer.cancel()
                    }
                }
            }
        }, 0, 1000) // 每隔1秒查询一次下载进度，你可以根据需要调整时间间隔
    }

    /**
     * The function `addUpdateItem` adds an item to a dataset and notifies the adapter that an item has
     * been inserted.
     *
     * @param item The item to be added or updated in the dataset.
     */
    fun addUpdateItem(item: UpdateItem) {
        // 异步添加item的逻辑
        dataSet.add(item)
        // 添加完成后调用notifyItemInserted()方法
        notifyItemInserted(dataSet.size - 1) // 获取新添加item的位置
    }

    /**
     * The function `showPopup` displays a popup menu with options to copy a URL to the clipboard and show
     * an update log.
     *
     * @param c The `Context` object, which represents the current state of the application or activity.
     * @param v The `View` parameter `v` represents the view that the popup menu should be anchored to.
     * This is typically the view that triggered the popup menu to be shown, such as a button or an image.
     * @param url The URL that will be copied to the clipboard when the "Copy URL" option is selected from
     * the popup menu.
     * @param log The `log` parameter is a string that represents the update log or changelog for a
     * particular item. It is used to display the update log when the corresponding menu option is
     * selected.
     */
    private fun showPopup(c: Context, v: View, url: String, log: String) {
        val popup = PopupMenu(c, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_update_item, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            // 处理选项的点击事件
            when (menuItem.itemId) {
                R.id.menu_copy_url -> {
                    // 复制内容到剪贴板
                    copyLink2Clipboard(c, url)
                    true
                }

                R.id.menu_update_log -> {
                    showAlertDialog(c, c.getString(R.string.text_menu_update_log), log)
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
}