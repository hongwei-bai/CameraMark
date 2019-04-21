package cameranativetest.aibdp.jd.com.cameranativetest.view

import android.annotation.SuppressLint
import android.app.Fragment
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cameranativetest.aibdp.jd.com.cameranativetest.R
import cameranativetest.aibdp.jd.com.cameranativetest.controller.CameraHelper
import cameranativetest.aibdp.jd.com.cameranativetest.model.ImageUtil
import cameranativetest.aibdp.jd.com.cameranativetest.ifNotNull
import com.jd.aibdp.dbgmsgview.DbgMsgView
import kotlinx.android.synthetic.main.fragment_camera.*
import java.lang.ref.SoftReference
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CameraFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    companion object {
        private val TAG = "cameraLifeCycle"
        private val SYMBOL_CURRENT_SEL = "*"
        private val ORIENTATIONS: List<Int> = mutableListOf(0, 90, 180, 270)
    }

    private var cameraHelper: CameraHelper = CameraHelper()
    //    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder

    private var previewFrame: ByteArray? = null

    private var adapterSelRes: RecycleViewAdapter? = null
    private var adapterSelCamera: RecycleViewAdapter? = null
    private var adapterSelOrientation: RecycleViewAdapter? = null

    // Layout from activity
    lateinit var recyclerViewSelCamera: RecyclerView
    lateinit var recyclerViewSelRes: RecyclerView
    lateinit var recyclerViewSelOrientation: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        cameraHelper.init()
        cameraHelper.openAny()
        initSurface()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        floatingActionButtonEntry()
        cameraHelper.resumePreview()
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        cameraHelper.pausePreview()
        super.onPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.i(TAG, "onHiddenChanged " + if (hidden) "hiden" else "show")
    }

    fun beforePause() {
        floatingActionButtonExit()
    }

    override fun onDestroy() {
        Log.i("Crash", "fragment onDestory called.")
        releaseSurface()
        cameraHelper.release()
        super.onDestroy()
    }

    @SuppressLint("RestrictedApi")
    fun floatingActionButtonEntry() {
        floatingActionButtonCamera2?.show()
        floatingActionButtonOrientation?.show()
        floatingActionButtonPreview?.show()
    }

    @SuppressLint("RestrictedApi")
    fun floatingActionButtonExit() {
        floatingActionButtonCamera2?.hide()
        floatingActionButtonOrientation?.hide()
        floatingActionButtonPreview?.hide()
    }

    fun appendOrientationAttribute(angle: Int): String {
        val deviceOrientation = cameraHelper.cameraOrientation
        var outputString: String = angle.toString()
        if (angle == deviceOrientation) {
            outputString += SYMBOL_CURRENT_SEL
        }
        return outputString
    }

    private fun initViews() {
        adapterSelCamera = RecycleViewAdapter(activity.applicationContext, mutableListOf())
        adapterSelRes = RecycleViewAdapter(activity.applicationContext, mutableListOf())
        adapterSelOrientation = RecycleViewAdapter(activity.applicationContext, mutableListOf())

        recyclerViewSelCamera.layoutManager = LinearLayoutManager(activity.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapterSelCamera = RecycleViewAdapter(activity.applicationContext, mutableListOf())
        if (recyclerViewSelCamera.itemDecorationCount <= 0) {
            recyclerViewSelCamera.addItemDecoration(ItemDecoration(activity.applicationContext))
        }
        recyclerViewSelCamera.adapter = adapterSelCamera
        recyclerViewSelRes.layoutManager = LinearLayoutManager(activity.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapterSelRes = RecycleViewAdapter(activity.applicationContext, mutableListOf())
        if (recyclerViewSelRes.itemDecorationCount <= 0) {
            recyclerViewSelRes.addItemDecoration(ItemDecoration(activity.applicationContext))
        }
        recyclerViewSelRes.adapter = adapterSelRes

        recyclerViewSelOrientation.layoutManager = LinearLayoutManager(activity.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapterSelOrientation = RecycleViewAdapter(activity.applicationContext, mutableListOf())
        if (recyclerViewSelOrientation.itemDecorationCount <= 0) {
            recyclerViewSelOrientation.addItemDecoration(ItemDecoration(activity.applicationContext))
        }
        recyclerViewSelOrientation.adapter = adapterSelOrientation

        surfaceView.setOnClickListener { v ->
            recyclerViewSelCamera.visibility = View.GONE
            recyclerViewSelRes.visibility = View.GONE
            recyclerViewSelOrientation.visibility = View.GONE
        }

        adapterSelCamera?.setOnItemClickObserver { key ->
            var switchSucc = false
            val cameraId = cameraHelper.cameraStringSortedById.indexOf(key)
            if (cameraId == cameraHelper.cameraId) {
                switchSucc = true
            } else if (cameraId >= 0) {
                switchSucc = switchCamera(cameraId)
                runTaskOnPreviewFrame()
                DbgMsgView.postMessage("openById: $cameraId, switchSucc: $switchSucc")
            } else {
                DbgMsgView.postMessage("openById, invalid id: $cameraId")
            }
            recyclerViewSelCamera.visibility = View.GONE

            if (switchSucc) {
                updateCameraMenu()
                updateResMenu()
                recyclerViewSelRes.visibility = View.VISIBLE
            }
        }

        adapterSelRes?.setOnItemClickObserver { key ->
            val resSize = cameraHelper.resolutions[key]
            cameraHelper.switchResolution(resSize)
            runTaskOnPreviewFrame()
            DbgMsgView.postMessage("Camera resolution has changed to $key")
            recyclerViewSelRes.visibility = View.GONE
        }

        adapterSelOrientation?.setOnItemClickObserver { key ->
            val trimed = key.replace(SYMBOL_CURRENT_SEL, "")
            val orientation = Integer.valueOf(trimed)
            cameraHelper.setCameraOrientation(orientation)
            runTaskOnPreviewFrame()
            updateOrientationMenu()
        }

        floatingActionButtonCamera2.setOnClickListener { v -> onClickFloatingActionButtonCamera() }

        floatingActionButtonOrientation.setOnClickListener { v -> onClickFloatingActionButtonOrientation() }

        floatingActionButtonPreview.setOnClickListener { v -> onClickFloatingActionButtonPreview() }
    }

    fun onClickFloatingActionButtonCamera() {
        recyclerViewSelCamera.visibility = View.VISIBLE
        updateCameraMenu()
        recyclerViewSelRes.visibility = View.GONE
        recyclerViewSelOrientation.visibility = View.GONE
    }

    fun onClickFloatingActionButtonOrientation() {
        recyclerViewSelCamera.visibility = View.GONE
        recyclerViewSelRes.visibility = View.GONE
        recyclerViewSelOrientation.visibility = View.VISIBLE
        updateOrientationMenu()
    }

    fun onClickFloatingActionButtonPreview() {
        ifNotNull(previewFrame, cameraHelper.camera, { bytes, camera ->
            val jpgBytes = ImageUtil.yuv2Jpeg(bytes, camera)
            jpgBytes?.let { promotePreviewDialog(jpgBytes) }
        })
    }

    private fun updateResMenu() {
        adapterSelRes?.let { adapterSelRes ->
            val entrySet = cameraHelper.resolutions.entries
            val resList = ArrayList<String>()
            var i = 0
            for ((key, value) in entrySet) {
                resList.add(key)
                if (cameraHelper.currentResolution == value) {
                    adapterSelRes.selPos = i
                }
                i++
            }
            adapterSelRes.update(resList)
            adapterSelRes.notifyDataSetChanged()
        }
    }

    private fun updateCameraMenu() {
        adapterSelCamera?.let { adapterSelCamera ->
            adapterSelCamera.update(cameraHelper.cameraStringSortedById)
            adapterSelCamera.selPos = cameraHelper.cameraId
            adapterSelCamera.notifyDataSetChanged()
        }
    }

    private fun updateOrientationMenu() {
        val listOrientation = ArrayList<String>().apply {
            ORIENTATIONS.forEach { add(appendOrientationAttribute(it)) }
        }
        adapterSelOrientation?.selPos = ORIENTATIONS.indexOf(cameraHelper.displayRotation)
        adapterSelOrientation?.update(listOrientation)
        adapterSelOrientation?.notifyDataSetChanged()
    }

    private fun promotePreviewDialog(data: ByteArray) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.setTitle("提示")
        alertDialog.setMessage("onPreviewFrame()预览的最近一张图像,点击空白返回.")
        val bitmapSoftReference = SoftReference(BitmapFactory.decodeByteArray(data, 0, data.size))
        val imageView = ImageView(activity)
        imageView.setPadding(40, 0, 40, 50)
        imageView.setImageBitmap(bitmapSoftReference.get())
        alertDialog.setView(imageView)
        alertDialog.show()
    }

    private fun switchCamera(cameraId: Int): Boolean {
        try {
            cameraHelper.release()
            cameraHelper.openById(cameraId)
            cameraHelper.startPreview(activity.applicationContext, surfaceHolder)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            DbgMsgView.postMessage("switchCamera error: " + e.localizedMessage)
        }

        return false
    }

    private fun initSurface() {
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(surfaceHolderCallback)
    }

    private fun releaseSurface() {
        surfaceHolder.removeCallback(surfaceHolderCallback)
    }

    private val surfaceHolderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            cameraHelper.startPreview(activity.applicationContext, surfaceHolder)
            runTaskOnPreviewFrame()
            Log.i(TAG, "surfaceCreated")
            DbgMsgView.postMessage("surfaceCreated")
        }

        override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
            Log.i(TAG, "surfaceChanged")
            DbgMsgView.postMessage("surfaceChanged")
        }

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
//            cameraHelper.release()
            Log.i(TAG, "surfaceDestroyed")
            DbgMsgView.postMessage("surfaceDestroyed")
        }
    }

    private fun runTaskOnPreviewFrame() {
        Log.i(TAG, "runTaskOnPreviewFrame")
        cameraHelper.setPreviewFrameCallback(Camera.PreviewCallback { bytes: ByteArray, camera: Camera ->
            previewFrame = bytes
            Log.i(TAG, "previewFrame")
        })
    }
}
