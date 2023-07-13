package top.easterNday.settings.Update

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpdateFragmentViewModel : ViewModel() {
    private val updateData = MutableLiveData<ArrayList<UpdateItem>>()

    fun getUpdateData(): LiveData<ArrayList<UpdateItem>> {
        return updateData
    }

    fun setUpdateData(context: Context, key: String, data: ArrayList<UpdateItem>) {
        updateData.value = data

        // TODO: 保存数据到 SharedPreferences 或者 SQLite
        // 保存数据到 SharedPreferences
        // val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        // val editor = sharedPreferences.edit()
        // editor.putString(key, data.toString())
        // editor.apply()
    }
}

