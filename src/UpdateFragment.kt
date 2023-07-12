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
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * This function sets up a RecyclerView with a custom adapter and populates it with data fetched from a
     * remote server.
     *
     * @param view The root view of the fragment or activity.
     * @param savedInstanceState The `savedInstanceState` parameter is a Bundle object that contains the
     * data that was saved in the `onSaveInstanceState()` method. It can be used to restore the state of
     * the fragment when it is recreated.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* The code is setting up a RecyclerView with a custom adapter and populating it with data fetched from
        a remote server. */
        binding.downloadListView.layoutManager = LinearLayoutManager(binding.root.context)
        val dataArray = ArrayList<UpdateItem>()
        val adapter = UpdatesListAdapter(requireContext(), dataArray)
        binding.downloadListView.adapter = adapter

        /* The code block is making an asynchronous HTTP GET request to a remote server using the OkHttp
        library. */
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(requireContext().getString(R.string.releases_info_url))
            .get()
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