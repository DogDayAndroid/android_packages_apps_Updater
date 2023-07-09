package top.easterNday.settings.DogDay

import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.Logger
import com.elvishew.xlog.XLog
import top.easterNday.settings.BuildConfig


object LogUtils {

    //val isDebug: Boolean = SystemProperties.getBoolean("persist.sys.apk.debug", true)
    private val TAG = "DogDay"

    private var isInit = false

    fun init() {
        val config = LogConfiguration.Builder()
            .logLevel(
                if (BuildConfig.DEBUG) LogLevel.ALL // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                else LogLevel.NONE
            )
            .enableThreadInfo() // 允许打印线程信息，默认禁止
            .enableStackTrace(2) // 允许打印深度为 2 的调用栈信息，默认禁止
            .enableBorder() // 允许打印日志边框，默认禁止
            .build()
        XLog.init(config)
        isInit = true
    }

    val logger: Logger
        get() {
            if (!isInit) {
                init()
            }
            return XLog.tag("TAG").build()
        }
}