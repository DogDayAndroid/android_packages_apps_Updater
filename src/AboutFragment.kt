package top.easterNday.settings

import android.os.Bundle
import android.os.SystemProperties
import android.system.Os
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}