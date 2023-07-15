package top.easterNday.settings.Update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.*
import org.json.JSONArray
import top.easterNday.settings.DogDay.LogUtils.logger
import top.easterNday.settings.R
import top.easterNday.settings.databinding.FragmentUpdateBinding
import java.io.IOException

class UpdateFragment : Fragment() {

    private lateinit var viewModelFragment: UpdateFragmentViewModel

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private var typeTag: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (requireArguments().getString("title")!! == requireContext().getString(R.string.text_system_update)) {
            typeTag = "system"
        } else if (requireArguments().getString("title")!! == requireContext().getString(R.string.text_kernel_update)) {
            typeTag = "kernel"
        }
        logger.d(typeTag)
        viewModelFragment =
            ViewModelProvider(requireActivity())[typeTag, UpdateFragmentViewModel::class.java]
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 设定列表布局
        binding.downloadListView.layoutManager = LinearLayoutManager(binding.root.context)

        // 获取数据
        fun getData(): ArrayList<UpdateItem> {
            return viewModelFragment.getUpdateData(view.context, typeTag)
        }

        // 绑定数据列表
        val adapter = UpdatesListAdapter(requireContext(), getData())
        binding.downloadListView.adapter = adapter

        // 数据不存在则刷新数据
        if (getData().isEmpty()) {
            logger.d("从网络获取!")
            fetchDataFromNetwork { updateItems ->
                requireActivity().runOnUiThread {
                    viewModelFragment.setUpdateData(updateItems)
                    adapter.setData(getData())
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchDataFromNetwork(callback: (ArrayList<UpdateItem>) -> Unit) {
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(requireContext().getString(R.string.releases_info_url))
            .get()
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                logger.e(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                logger.d(response.body.toString())
                val data = JSONArray(response.body.string())

                val updateItems = ArrayList<UpdateItem>()
                for (i in 0 until data.length()) {
                    val infos = data.getJSONObject(i)
                    updateItems.add(
                        UpdateItem(requireContext(), infos)
                    )
                }

                callback(updateItems)
            }
        })
    }
}
