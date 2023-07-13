package top.easterNday.settings

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.SystemProperties
import android.system.Os
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import top.easterNday.settings.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        return binding.root
    }


    /**
     * This function sets up the view and handles button clicks for updating the system and kernel
     * versions.
     *
     * @param view The view parameter is the root view of the fragment. It represents the layout file
     * associated with the fragment and contains all the UI elements defined in that layout file.
     * @param savedInstanceState The `savedInstanceState` parameter is a `Bundle` object that contains the
     * saved state of the fragment. It is used to restore the previous state of the fragment when it is
     * recreated.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* This line of code is finding the TextView with the id "romVersion" in the current view and setting
        its text to the value of the system property "ro.lineage.version". The value is converted to a
        string using the `toString()` method. */
        view.findViewById<TextView>(R.id.romVersion).text = SystemProperties.get("ro.lineage.version").toString()

        /* The code `val uname = Os.uname()` is calling the `uname()` function from the `Os` class. This
        function returns a structure containing information about the current operating system. */
        val uname = Os.uname()
        view.findViewById<TextView>(R.id.ksu_version).text = uname.release

        // TODO: Build.FINGERPRINT

        binding.updateSystem.setOnClickListener {
            // 数据传递
            val args = Bundle()
            args.putString("title", "系统更新")
            // args.putString("extraTitle", "作者")
            findNavController().navigate(R.id.action_aboutFragment_to_updateFragment, args)
        }

        binding.updateSu.setOnClickListener {
            // 数据传递
            val args = Bundle()
            args.putString("title", "内核更新")
            // args.putString("extraTitle", "编译工具链")
            findNavController().navigate(R.id.action_aboutFragment_to_updateFragment, args)
        }

        binding.settings.setOnClickListener {
            val resolver = view.context.contentResolver
            val values = ContentValues()
            values.put("appName", "虎牙直播·Play")
            values.put("packageName", "com.duowan.kiwi")
            values.put(
                "iconBitmap",
                "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"
            );
            values.put("iconColor", "#fff6ab00")
            values.put("contributorName", "fankes")
            values.put("isEnabled", true)
            values.put("isEnabledAll", false)
            val uri = Uri.parse("content://top.easterNday/ICON")
            resolver.insert(uri, values)
            Toast.makeText(view.context, "数据插入成功", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}