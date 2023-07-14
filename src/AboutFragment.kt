package top.easterNday.settings

import android.content.ContentValues
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
import top.easterNday.settings.Database.IconProvider
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
            args.putString("title", requireContext().getString(R.string.text_system_update))
            // args.putString("extraTitle", "作者")
            findNavController().navigate(R.id.action_aboutFragment_to_updateFragment, args)
        }

        binding.updateSu.setOnClickListener {
            // 数据传递
            val args = Bundle()
            args.putString("title", requireContext().getString(R.string.text_kernel_update))
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
                "iVBORw0KGgoAAAANSUhEUgAAADcAAAAyCAYAAAD4FkP1AAAAAXNSR0IArs4c6QAAAARzQklUCAgICHwIZIgAAAV2SURBVGiBzZp7iBVlGMafd9lSIlNaQ0sU2cp0MbAsIxU0ygt2Qa0NUwwr2IrUJDL9J9ggkIKykFC3iyUJYmVhFqbYdtss8bKaIhS5aka2EYWWZRG//vjmrNP4zfWcPfbAMHtm3svzzMx3e7+VMgJoBt4GlgKzgUuz+qbEnQKsAjqBlcDISsTNQ2AmZ+JvYD1wD1BXIOYVwMueuFRKpGUk0iZpdIJJp6S1kpaa2aGI7yhJYyT1Di6dJ6le0jhJfVNSzzWzF7JwLARgWszTjUNr6Pgrp68Pzd0pbn0FCJaLQgITP0tgtKS2Qk+l8phkZpvzONSk3L+9DDKVxgfA9XkcalPujyqDjCRtlrRX0k5JbWb2XfgmMFyusxkhaaSka1PivQZMN7N9ZbECegB/FmgfbcBioKFAzonACtyYF4f3yhIWJBqbU9R24M6yE7vcA4AlwImYXOX1oMAjGUWdAhYBaZ94EQ4NwPKYvHPLCfxiBmGHgesqqCeOy10x+WcUCdYD2Jki7Issbwu4CWgCZgFDCqlzcYZ4OPwOTMgbaHGKsP0Z4/QBOiK+G4BbCgrs5+FyBLgqa4AG4OdyhQWxZiXEKSQSmO+J9SnQM4vz6wmEjpCji8ctk9KwChicU6Av7vNpTklPGuDhCpDwoQOYkyNuLfC5J87dcQ4XAvsTCHycR1hOcSVkHr+Amz3+O+KMH0tJfFsVxEGOTxT3SUfRVLofnjgnzS72mdmGvOIK4oIctm94rjX95xcwOeVpPlmEJW58y4PPCuTwzUMnSKffnL8hnsb7eZMGOJzD9qSkpwvk2O655gZ2oCfwT8oT7VMgaWnMzIJdwDUFc/iKTLsl9+bmKHnRetDMfi2SWNLRDDZvSZpsZv6eLh3HPNdGAHU1koanOP9YMKnM7LiklpjbH0lqNLM7zKyzaA5JF8Zcb6iRdEmK88VlJJak5ZLCb/5LSfea2Q1m9maZsSUprjhcXytpQIpzmvhEmFk7MC34eSha1ywHwFiVOg+HU5K2Sbpc0kABRzM0+BsrRaiSANYBm3CThat9BlnwylngHgvc0mcd0Jpm+G1GgQ9UiXsS13Nw5Y/wGrEVuC/OYUtGcQAXVVlML2A8sABoIbk6sAlXRO5CraSDOfJ1Ag9JejdagyxIvkbSwMgxKDgPlZSnLDFJ0hhgoZmtkCTDVZGWRQw7JK2W9JPcIFkjqTE4SmiX9Imk7yWdCI7fgnNPuV2duKMuEJDWUxfFE2bWLGBw6NXuAeYBZ8zMcQ34/4I24GvglwSbGSXiW3C7pv18j4Fi67LuQCuR9R4wEHgOVwIJY1jJYBFwWYywNdXl34UO3K7t+Mj1xhie5wKPBjYnwze8W7S4Xqra2AcsiPCIwiswsJ0HJC+sgRH4v+njuG55cGAX/mR3BL/XAgdzCNoPrAbuj+Hiw/QE7o1S8hbWfEnRddwySc+YWXgROi7091dm1lXkAfrKbYPFbYVtk9RuZkVWHs8CB8zsQPSGmfnKD12k+nue1EyPXaPHLnP9HjeNmohrK6vxVL9wX1AcNmXNFQ74UiRIXCP21Q6Xx9guwe0vHAB+AP6IIbyQ0Mqf9Hbf5MuXJG5PyHlNjI3vrYHb+ekdsX0qhWAUu4GpuJ4yqbQPcbXKBHF1wNbAeXaMTdKg/mDIrhpjZL63FxB7Fbg15l4amoFvulcTAI+TZRMkRoSvkddXgXQajpAwHBQGbkPxbGIlcGUaz6L72PUF/cpFi6QWM9uZxTjTP7ZFAUyRG5gnS+ruPfF2SRslvZNVVAmFxIWB2/CfKlfc7V9uvAB7JX0oaaOZbS0apGxxYQCD5FbQw4LzUEm9guP80N8nJB0PnY/JTcV2SdpTcDp2Bv4F0JnG62CO9gUAAAAASUVORK5CYII="
            );
            values.put("iconColor", "#fff6ab00")
            values.put("contributorName", "fankes")
            values.put("isEnabled", true)
            values.put("isEnabledAll", false)
            val uri = IconProvider.getURI()
            resolver.insert(uri, values)
            Toast.makeText(view.context, "数据插入成功", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}