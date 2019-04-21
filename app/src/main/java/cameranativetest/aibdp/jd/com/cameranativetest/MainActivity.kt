package cameranativetest.aibdp.jd.com.cameranativetest

import android.Manifest
import android.animation.*
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.util.Property
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import com.jd.aibdp.dbgmsgview.DbgMsgView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_camera.*

class MainActivity : AppCompatActivity() {
    private var cameraFragment: CameraFragment? = null
    private var screenFragment: ScreenFragment? = null
    private var deviceFragment: DeviceFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DbgMsgView.getInstance().setDebugFlag(false)
        if (requestPermissions()) {
            initView()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    private fun initView() {
        textView.text = "resources.configuration.orientation: " + resources.configuration.orientation

        floatingActionButtonCamera.setOnClickListener { v ->
            loadCameraFragment()
            floatingActionButtonCamera.hide()
            cameraFragment?.onClickFloatingActionButtonCamera()
        }

        floatingActionButtonScreen.setOnClickListener { v ->
            floatingActionButtonCamera.show()
            cameraFragment?.beforePause()
            floatingActionButtonCamera2.hide()
            floatingActionButtonOrientation.hide()

            loadScreenFragment()
        }

        floatingActionButtonDevice.setOnClickListener { v ->
            floatingActionButtonCamera.show()
            cameraFragment?.beforePause()
            floatingActionButtonCamera2.hide()
            floatingActionButtonOrientation.hide()

            loadDeviceFragment()
        }

        loadCameraFragment()
    }

    private fun initCameraFragmentViews(fragment: CameraFragment) {
        fragment.recyclerViewSelCamera = findViewById(R.id.recyclerview_sel_camera)
        fragment.recyclerViewSelRes = findViewById(R.id.recyclerview_sel_res)
        fragment.recyclerViewSelOrientation = findViewById(R.id.recyclerview_sel_orientation)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initView()
    }

    private fun loadCameraFragment() {
        cameraFragment ?: let {
            cameraFragment = CameraFragment()
        }
        initCameraFragmentViews(cameraFragment!!)
        fragmentManager.beginTransaction().replace(R.id.container, cameraFragment).commitAllowingStateLoss()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loadScreenFragment() {
        screenFragment ?: let {
            screenFragment = ScreenFragment()
        }

        var targetView = container
        var startView = floatingActionButtonScreen

        var centerX = (startView.left + startView.right) / 2
        var centerY = (startView.top + startView.bottom) / 2 //- startView.height
        val endRadius = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

        Log.i("aaaa", "x: ${startView.left}, ${startView.right}")
        Log.i("aaaa", "y: ${startView.top}, ${startView.bottom}")
        Log.i("aaaa", "center: $centerX, $centerY")
        Log.i("aaaa", "r: ${startView.width.toFloat()} -> $endRadius")

        val accentColor = ContextCompat.getColor(this, R.color.topeka_primary)
        var colorChange = ObjectAnimator.ofInt(targetView,
                targetView.FOREGROUND_COLOR, accentColor, Color.TRANSPARENT)
                .apply {
                    setEvaluator(ArgbEvaluator())
//                    interpolator = this@QuizActivity.interpolator
                }

        var circularReveal: Animator = ViewAnimationUtils.createCircularReveal(
                targetView, centerX, centerY, startView.width.toFloat() / 2, endRadius)
                .apply {
                    interpolator = FastOutLinearInInterpolator()

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
//                            icon?.visibility = View.GONE
                            removeListener(this)
                        }
                    })
                }

        fragmentManager.beginTransaction().replace(R.id.container, screenFragment).commitAllowingStateLoss()
        ViewCompat.animate(startView)
//                .scaleX(1.2f)
//                .scaleY(1.2f)
//                .alpha(1.2f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationEnd(view: View?) {
                        container.visibility = View.VISIBLE
                    }
                })
                .start()
        with(AnimatorSet()) {
            play(circularReveal).with(colorChange)
            start()
        }
    }

    val android.widget.FrameLayout.FOREGROUND_COLOR: Property<FrameLayout, Int>
        get() = object :
                IntProperty<FrameLayout>("foregroundColor") {

            override fun setValue(layout: FrameLayout, value: Int) {
                if (layout.foreground is ColorDrawable) {
                    (layout.foreground.mutate() as ColorDrawable).color = value
                } else {
                    layout.foreground = ColorDrawable(value)
                }
            }

            override fun get(layout: FrameLayout): Int? {
                return if (layout.foreground is ColorDrawable) {
                    (layout.foreground as ColorDrawable).color
                } else {
                    Color.TRANSPARENT
                }
            }
        }

    abstract class IntProperty<T>(name: String) : Property<T, Int>(Int::class.java, name) {

        /**
         * A type-specific override of the [.set] that is faster when
         * dealing
         * with fields of type `int`.
         */
        abstract fun setValue(type: T, value: Int)

        override fun set(type: T, value: Int?) = setValue(type, value!!.toInt())
    }

    private fun loadDeviceFragment() {
        deviceFragment ?: let {
            deviceFragment = DeviceFragment()
        }
        fragmentManager.beginTransaction().replace(R.id.container, deviceFragment).commitAllowingStateLoss()
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    private fun requestPermissions(): Boolean {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    444)
            return false
        }
        return true
    }
}
