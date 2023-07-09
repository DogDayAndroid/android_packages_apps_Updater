package top.easterNday.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.*
import org.json.JSONArray
import top.easterNday.settings.DogDay.LogUtils
import top.easterNday.settings.Update.UpdateItem
import top.easterNday.settings.Update.UpdatesListAdapter
import top.easterNday.settings.databinding.FragmentUpdateBinding
import java.io.IOException


class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this creates a vertical layout Manager
        binding.downloadListView.layoutManager = LinearLayoutManager(binding.root.context)
        // 设置数据
        val dataArray = ArrayList<UpdateItem>()
        // 设置适配器（第一个参数：上下文；第二个参数：listview的每一个item的布局文件，这里使用系统提供的；第三个参数：数据源）
        val adapter = UpdatesListAdapter(requireContext(), dataArray)
        binding.downloadListView.adapter = adapter

        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(requireContext().getString(R.string.releases_info_url))
            .get() //默认就是GET请求，可以不写
            .build()
        val call: Call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtils.logger.e(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                LogUtils.logger.d(response.body.toString())
                val data = JSONArray(response.body.string())

                for (i in 0 until data.length()) {
                    val infos = data.getJSONObject(i)

                    requireActivity().runOnUiThread {
                        adapter.addUpdateItem(
                            UpdateItem(
                                requireContext(),
                                infos.getLong("datetime"),
                                infos.getString("desc"),
                                infos.getString("filename"),
                                infos.getLong("size"),
                                infos.getString("tag"),
                                infos.getString("url"),
                                infos.getString("version")
                            )
                        )
                    }
                }
                LogUtils.logger.json(data[0].toString())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}