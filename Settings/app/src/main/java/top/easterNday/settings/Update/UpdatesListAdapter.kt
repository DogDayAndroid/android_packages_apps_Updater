package top.easterNday.settings.Update


/*
 * Copyright (C) 2017-2023 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.os.RecoverySystem
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toFile
import androidx.recyclerview.widget.RecyclerView
import top.easterNday.settings.DogDay.LogUtils.logger
import top.easterNday.settings.DogDay.Utils.Companion.copyLink2Clipboard
import top.easterNday.settings.DogDay.Utils.Companion.downloadFromUrl
import top.easterNday.settings.DogDay.Utils.Companion.genDownloadPath
import top.easterNday.settings.DogDay.Utils.Companion.isFileDownloadedAndAvailable
import top.easterNday.settings.DogDay.Utils.Companion.showAlertDialog
import top.easterNday.settings.R


class UpdatesListAdapter(private val mContext: Context, private val dataSet: ArrayList<UpdateItem>) :
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
        val desc: TextView
        val actionButton: Button
        val mMenu: ImageButton

        init {
            // Define click listener for the ViewHolder's View.
            title = view.findViewById(R.id.updateTitle)
            version = view.findViewById(R.id.updateVersion)
            datetime = view.findViewById(R.id.updateDate)
            size = view.findViewById(R.id.updateSize)
            tag = view.findViewById(R.id.updateTag)
            desc = view.findViewById(R.id.updateDesc)
            actionButton = view.findViewById(R.id.updateAction)
            // Action Button
            mMenu = view.findViewById(R.id.updateMenu)
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

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val download_url = dataSet[position].updateUrl
        val update_log = dataSet[position].updateDesc
        val filename = dataSet[position].updateTitle
        val fileUri = genDownloadPath(mContext.getString(R.string.download_sub_dir), filename)

        viewHolder.title.text = filename
        viewHolder.version.text = dataSet[position].updateVersion
        viewHolder.datetime.text = dataSet[position].updateDate
        viewHolder.size.text = dataSet[position].updateSize
        viewHolder.desc.text = update_log
        viewHolder.tag.text = dataSet[position].updateTag


        viewHolder.mMenu.setOnClickListener {
            logger.d("PopUp！")
            showPopup(mContext, viewHolder.mMenu, download_url, update_log)
        }

        if (isFileDownloadedAndAvailable(mContext, download_url, fileUri)) {
            viewHolder.actionButton.text = "刷入"


            viewHolder.actionButton.setOnClickListener {
                val file = fileUri.toFile()
                logger.d("Install %s", file.toString())
                RecoverySystem.installPackage(mContext, file)
            }
        } else {
            viewHolder.actionButton.text = "下载"
            // 点击下载按钮开始下载
            viewHolder.actionButton.setOnClickListener {
                // 调用系统服务进行下载
                downloadFromUrl(
                    mContext,
                    download_url,
                    filename,
                    dataSet[position].updateDesc,
                    fileUri
                )
            }
        }

    }

    fun addUpdateItem(item: UpdateItem) {
        // 异步添加item的逻辑
        dataSet.add(item)
        // 添加完成后调用notifyItemInserted()方法
        notifyItemInserted(dataSet.size - 1) // 获取新添加item的位置
    }

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
                    // 打开网页
                    // val intent = Intent(Intent.ACTION_VIEW)
                    // intent.data = Uri.parse(dataSet[position].updateUrl)
                    // viewHolder.itemView.context.startActivity(intent)
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
}