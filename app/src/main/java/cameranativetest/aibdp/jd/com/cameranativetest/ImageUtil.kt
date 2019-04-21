package cameranativetest.aibdp.jd.com.cameranativetest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Package: com.jd.aibdp.jdutils
 * User: baihongwei1
 * Email: baihongwei1@jd.com
 * Date: 2018/7/16
 * Time: 16:39
 * Description:
 */
object ImageUtil {
    fun yuv2Jpeg(bytes: ByteArray, camera: Camera): ByteArray? {
        val size = camera.parameters.previewSize // 获取预览大小
        val w = size.width // 宽度
        val h = size.height
        val image = YuvImage(bytes, ImageFormat.NV21, w, h, null)
        val os = ByteArrayOutputStream(bytes.size)

        return if (!image.compressToJpeg(Rect(0, 0, w, h), 100, os)) {
            null
        } else os.toByteArray()
    }

    fun bitmap2Bytes(bm: Bitmap, compressFormat: Bitmap.CompressFormat): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(compressFormat, 60, baos)
        return baos.toByteArray()
    }

    /**
     * 左右镜像图片
     */
    fun bitmapMirror(bitmap: Bitmap): Bitmap {
        val modBm = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(modBm)

        val paint = Paint()

        //绘制矩阵  Matrix主要用于对平面进行平移(Translate)，缩放(Scale)，旋转(Rotate)以及斜切(Skew)操作。
        val matrix = Matrix()
        //镜子效果：
        matrix.setScale(-1f, 1f)//翻转
        matrix.postTranslate(bitmap.width.toFloat(), 0f)
        canvas.drawBitmap(bitmap, matrix, paint)
        return modBm
    }

    /*
     * Author: yuanyumin1
     * Date:
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Int, recycle: Boolean): Bitmap {
        val m = Matrix()
        m.setRotate(degrees.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
        val bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)

        if (recycle) bitmap.recycle()
        return bm1
    }


    fun getBitMap(bytes: ByteArray, camera: Camera): Bitmap? {
        if (bytes.size != 0) {
            val previewSize = camera.parameters.previewSize
            val yuvimage = YuvImage(bytes, ImageFormat.NV21, previewSize.width,
                    previewSize.height, null)
            val baos = ByteArrayOutputStream()
            yuvimage.compressToJpeg(Rect(0, 0, previewSize.width,
                    previewSize.height), 100, baos)  //这里 80 是图片质量，取值范围 0-100，100为品质最高
            val jdata = baos.toByteArray()//这时候 bmp 就不为 null 了
            return BitmapFactory.decodeByteArray(jdata, 0, jdata.size)
        } else {
            return null
        }
    }

    fun cropFace(bitmap: Bitmap?, faceDetch: IntArray): Bitmap? {
        return if (bitmap != null && faceDetch.size == 4) {
            Bitmap.createBitmap(bitmap, faceDetch[0], faceDetch[1], faceDetch[2], faceDetch[3], null, false)
        } else {
            null
        }
    }

    /**
     * Author: yuanyumin1
     * 保存图片到本地
     */
    fun saveJpgImageToSDCard(jpg: ByteArray, filepath: String) {
        val bitmap: Bitmap?
        bitmap = BitmapFactory.decodeByteArray(jpg, 0, jpg.size)
        if (null != bitmap) {
            val file = File(filepath)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            try {
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap 图片
     * @param path   保存的图片路径
     * @param name   保存的图片名称
     */
    fun savePic(bitmap: Bitmap?, path: String, name: String) {
        if (null != bitmap) {
            val pathFile = File(path)
            if (!pathFile.exists()) {
                pathFile.mkdirs()
            }
            val file = File(path, name)
            try {
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                fos.flush()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
