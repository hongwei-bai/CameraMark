package cameranativetest.aibdp.jd.com.cameranativetest.controller

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.jd.aibdp.dbgmsgview.DbgMsgView
import java.io.IOException
import java.util.*

/**
 * Package: cameranativetest.aibdp.jd.com.cameranativetest
 * User: baihongwei1
 * Email: baihongwei1@jd.com
 * Date: 2018/9/5
 * Time: 11:35
 * Description:
 */
class CameraHelper {
    companion object {
        private val TAG = "CameraHelper"
        private val RATIO_ACCURACY = 10000
        private val RATIO_16_10: Int = (16f * RATIO_ACCURACY / 10f).toInt()
        private val RATIO_16_9: Int = (16f * RATIO_ACCURACY / 9f).toInt()
        private val RATIO_15_9: Int = (15f * RATIO_ACCURACY / 9f).toInt()
        private val RATIO_4_3: Int = (4f * RATIO_ACCURACY / 3f).toInt()
    }

    // Devices on some older versions require stopPreview while switching resolutions.
    private var needStopWhileSwitchingResolution = false

    private var numberOfCameras: Int = 0
    private var faceBackCameraId: Int = 0
    private var faceBackCameraOrientation: Int = 0
    private var faceFrontCameraId: Int = 0
    private var faceFrontCameraOrientation: Int = 0

    var camera: Camera? = null
    var cameraId: Int = 0
    var displayRotation = 90
    private var previewSize: Camera.Size? = null
    var cameraFlag = false;

    val cameraStringSortedById: List<String>
        get() {
            Log.i(TAG, "cameraStringSortedById");
            val list = ArrayList<String>()
            for (i in 0 until Camera.getNumberOfCameras()) {
                val cameraInfo = Camera.CameraInfo()

                Camera.getCameraInfo(i, cameraInfo)

                val sb = StringBuilder("摄像头$i ")
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    faceBackCameraId = i
                    faceBackCameraOrientation = cameraInfo.orientation
                    sb.append("后置 orientation:" + cameraInfo.orientation)
                } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    faceFrontCameraId = i
                    faceFrontCameraOrientation = cameraInfo.orientation
                    sb.append("前置 orientation:" + cameraInfo.orientation)
                }
                list.add(sb.toString())
            }
            return list
        }

    //有多少个摄像头
    val cameraInfo: List<Camera.CameraInfo>
        get() {
            val num = Camera.getNumberOfCameras()
            val list = ArrayList<Camera.CameraInfo>()
            for (i in 0 until num) {
                val cameraInfo = Camera.CameraInfo()
                list.add(cameraInfo)
            }
            return list
        }

    val resolutions: Map<String, Camera.Size>
        get() {
            Log.i(TAG, "resolutions");
            val map = HashMap<String, Camera.Size>()
            camera?.let {
                val parameters = it.parameters
                val sizeList = parameters.supportedPreviewSizes
                for (size in sizeList) {
                    val name = size.width.toString() + "x" + size.height + " " + if (getRatioString(size) != null) "(" + getRatioString(size) + ")" else ""
//                    val name = size.width.toString() + "x" + size.height + " " + getRatioString(size)?.toString()?:"2"
                    map[name] = size
                }
            }
            return map
        }

    val currentResolution: Camera.Size?
        get() {
            Log.i(TAG, "currentResolution");
            return camera?.parameters?.previewSize
        }

    //获取相机参数
//    val supportResolutions: List<Camera.Size>
//        get() {
//            camera?.let {
//                val parameters = it.parameters
//                return parameters.supportedPreviewSizes
//            }
//        }

    fun init() {
        Log.i(TAG, "init");
        //有多少个摄像头
        numberOfCameras = Camera.getNumberOfCameras()

        for (i in 0 until numberOfCameras) {
            val cameraInfo = Camera.CameraInfo()

            Camera.getCameraInfo(i, cameraInfo)
            //后置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                faceBackCameraId = i
                faceBackCameraOrientation = cameraInfo.orientation
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                faceFrontCameraId = i
                faceFrontCameraOrientation = cameraInfo.orientation
            }//前置摄像头
        }
    }

    fun startPreview(mContext: Context, surfaceHolder: SurfaceHolder) {
        camera?.let { camera ->
            Log.i(TAG, "startPreview");
            try {
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(cameraId, cameraInfo)
                val cameraRotationOffset = cameraInfo.orientation

                //获取相机参数
                val parameters = camera.parameters
                //设置对焦模式
                //            setAutoFocus(camera, parameters);
                //设置闪光模式
                //            setFlashMode(mCameraConfigProvider.getFlashMode());

                //            if (mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_PHOTO
                //                    || mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_UNSPECIFIED)
                //                turnPhotoCameraFeaturesOn(camera, parameters);
                //            else if (mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_PHOTO)
                //                turnVideoCameraFeaturesOn(camera, parameters);

                val rotation = (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
                var degrees = 0
                when (rotation) {
                    Surface.ROTATION_0 -> degrees = 0
                    Surface.ROTATION_90 -> degrees = 90
                    Surface.ROTATION_180 -> degrees = 180
                    Surface.ROTATION_270 -> degrees = 270
                }// Natural orientation
                // Landscape left
                // Upside down
                // Landscape right

                //根据前置与后置摄像头的不同，设置预览方向，否则会发生预览图像倒过来的情况。
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    displayRotation = (cameraRotationOffset + degrees) % 360
                    displayRotation = (360 - displayRotation) % 360 // compensate
                } else {
                    displayRotation = (cameraRotationOffset - degrees + 360) % 360
                }
                camera.setDisplayOrientation(displayRotation)

                //            if (Build.VERSION.SDK_INT > 13
                //                    && (mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_VIDEO
                //                    || mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_UNSPECIFIED)) {
                ////                parameters.setRecordingHint(true);
                //            }
                //
                //            if (Build.VERSION.SDK_INT > 14
                //                    && parameters.isVideoStabilizationSupported()
                //                    && (mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_VIDEO
                //                    || mCameraConfigProvider.getMediaAction() == CameraConfig.MEDIA_ACTION_UNSPECIFIED)) {
                //                parameters.setVideoStabilization(true);
                //            }

                //设置预览大小
                camera.takeIf { needStopWhileSwitchingResolution }?.stopPreview()
                previewSize = parameters.previewSize
                previewSize?.let { previewSize ->
                    previewSize.width = 1920
                    previewSize.height = 1080
                    parameters.setPreviewSize(previewSize.width, previewSize.height)
//                                parameters.setPictureSize(photoSize.getWidth(), photoSize.getHeight());
                }

                //设置相机参数
                camera.parameters = parameters
            } catch (error: IOException) {
                error.printStackTrace()
                Log.e(TAG, "Error setting camera preview: " + error.message)
            } catch (ignore: Exception) {
                ignore.printStackTrace()
                Log.e(TAG, "Error starting camera preview: " + ignore.message)
                ignore.message?.takeIf { it.contains("setParameters failed") }?.apply { needStopWhileSwitchingResolution = true }
            }

            try {
                //设置surfaceHolder
                camera.setPreviewDisplay(surfaceHolder)
                //开启预览
                camera.startPreview()
            } catch (error: IOException) {
                error.printStackTrace()
                Log.e(TAG, "Error setting camera preview: " + error.message)
            } catch (ignore: Exception) {
                ignore.printStackTrace()
                Log.e(TAG, "Error starting camera preview: " + ignore.message)
            }
        }
    }

    fun setCameraOrientation(angle: Int) {
        Log.i(TAG, "setCameraOrientation");
        displayRotation = angle
        camera?.setDisplayOrientation(angle)
    }

    fun switchResolution(previewSize: Camera.Size?) {
        Log.i(TAG, "switchResolution");
        camera?.takeIf { needStopWhileSwitchingResolution }?.stopPreview()
        previewSize?.let { switchResolution(previewSize.width, previewSize.height) }
        camera?.takeIf { needStopWhileSwitchingResolution }?.startPreview()
    }

    fun switchResolution(width: Int, height: Int) {
        camera?.let { camera ->
            Log.i(TAG, "switchResolution int,int");
            //获取相机参数
            val parameters = camera.parameters

            previewSize = parameters.previewSize
            previewSize?.let { previewSize ->
                previewSize.width = width
                previewSize.height = height
                parameters.setPreviewSize(previewSize.width, previewSize.height)
            }

            //设置相机参数
            camera.parameters = parameters
        }

    }

    fun openFront() {
        Log.i(TAG, "openFront");
        cameraId = faceFrontCameraId
        open()
    }

    fun openBack() {
        Log.i(TAG, "openBack");
        cameraId = faceBackCameraId
        open()
    }

    fun openAny() {
        try {
            Log.i(TAG, "openAny");
            openFront()
            cameraId = faceFrontCameraId
            DbgMsgView.postMessage("openFront succ")
            return
        } catch (e: Exception) {
            DbgMsgView.postMessage("openFront error: " + e.localizedMessage)
        }

        for (i in 0 until numberOfCameras) {
            try {
                openById(i)
                cameraId = i
                DbgMsgView.postMessage("openById $i succ")
                return
            } catch (e: Exception) {
                DbgMsgView.postMessage("openById:" + i + " error: " + e.localizedMessage)
            }

        }

        DbgMsgView.postMessage("openAny failed!")
    }

    fun openById(id: Int) {
        Log.i(TAG, "openById: " + id);
        cameraId = id
        open()
    }

    fun open() {
        Log.i(TAG, "open");
        camera = Camera.open(cameraId)
        cameraFlag = true
    }

    fun release() {
        if (cameraFlag) {
            Log.i(TAG, "release");
            camera?.setPreviewCallback(null)
            camera?.release()
            cameraFlag = false
        }
    }

    fun refresh() {
        camera?.let {
            it.stopPreview()
            it.startPreview()
        }
    }

    fun setPreviewFrameCallback(callback: Camera.PreviewCallback) {
        Log.i(TAG, "setPreviewFrameCallback");
        camera?.setPreviewCallback(callback)
    }

    val cameraOrientation: Int
        get() {
            Log.i(TAG, "cameraOrientation");
            val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, cameraInfo)
            return cameraInfo.orientation
        }

    fun getRatioString(size: Camera.Size): String? {
        Log.i(TAG, "getRatioString");
        val ratioTransformed = size.width * RATIO_ACCURACY / size.height
        when (ratioTransformed) {
            RATIO_16_9 -> return "16:9"
            RATIO_4_3 -> return "4:3"
            RATIO_16_10 -> return "16:10"
            RATIO_15_9 -> return "15:9"
        }
        return null
    }

    fun pausePreview() {
        camera?.stopPreview()
    }

    fun resumePreview() {
        camera?.startPreview()
    }
}
