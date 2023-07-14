package top.easterNday.settings.Update

import android.content.Context
import androidx.lifecycle.ViewModel

class UpdateFragmentViewModel : ViewModel() {

    fun getUpdateData(context: Context, typeTag: String): ArrayList<UpdateItem> {
        return UpdateItem.getAll(context, typeTag)
    }

    fun setUpdateData(data: ArrayList<UpdateItem>) {
        // 保存数据到SQLite
        data.forEach { item ->
            item.updateInfo()
        }
    }
}

