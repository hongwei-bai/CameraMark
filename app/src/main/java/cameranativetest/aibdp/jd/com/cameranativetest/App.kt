package cameranativetest.aibdp.jd.com.cameranativetest

import android.app.Application
import com.mike.tudict.component.CrashHandler
import com.squareup.leakcanary.LeakCanary


/**
 * Author: Mike
 * Email: bhw8412@hotmail.com
 * Date: 2019/4/21
 * Description:
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this)

        CrashHandler().init(this)
    }
}