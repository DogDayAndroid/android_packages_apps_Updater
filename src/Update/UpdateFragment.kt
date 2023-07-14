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
            typeTag = "System"
        } else if (requireArguments().getString("title")!! == requireContext().getString(R.string.text_kernel_update)) {
            typeTag = "Kernel"
        }
        logger.d(typeTag)
        viewModelFragment =
            ViewModelProvider(requireActivity())[typeTag, UpdateFragmentViewModel::class.java]
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.downloadListView.layoutManager = LinearLayoutManager(binding.root.context)
        val dataArray = ArrayList<UpdateItem>()
        val adapter = UpdatesListAdapter(requireContext(), dataArray)
        binding.downloadListView.adapter = adapter

        val cachedData = viewModelFragment.getUpdateData(view.context, typeTag)
        logger.d(cachedData.size)
        if (cachedData.size > 0) {
            logger.d("从本地获取!")
            // Use cached data
            adapter.setData(cachedData)
        } else {
            logger.d("从网络获取!")
            // Fetch data from the network
            fetchDataFromNetwork { updateItems ->
                requireActivity().runOnUiThread {
                    adapter.setData(updateItems)
                    viewModelFragment.setUpdateData(updateItems)
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
                        UpdateItem(requireContext(), infos, typeTag)
                    )
                }

                callback(updateItems)
            }
        })
    }
}
