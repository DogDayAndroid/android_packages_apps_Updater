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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 获取系统版本
        view.findViewById<TextView>(R.id.romVersion).text = SystemProperties.get("ro.lineage.version").toString()
        // 获取内核版本
        val uname = Os.uname()
        view.findViewById<TextView>(R.id.ksu_version).text = uname.release
        // 系统指纹 Build.FINGERPRINT

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