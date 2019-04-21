package cameranativetest.aibdp.jd.com.cameranativetest.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

/**
 * Package: com.jd.aibdp.sample
 * User: 白宏伟
 * Email: baihongwei1@jd.com
 * Date: 2018/3/8
 * Time: 15:53
 * Description: 初始化界面RecycleView水平分割线样式定义
 */

class ItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private var context: Context? = null

    init {
        this.context = context
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, 5, 0, 0)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        val paint = Paint(Color.WHITE)
//
//        if (parent.childCount > 0) {
//            val firstChild = parent.getChildAt(0)
//            c.drawLine(0f, firstChild.y, firstChild.width.toFloat(), firstChild.y, paint)
//        }
//
//        for (i in 0 until parent.childCount) {
//            val child = parent.getChildAt(i)
//            val y = (child.y + child.height).toInt()
//            c.drawLine(0f, y.toFloat(), child.width.toFloat(), y.toFloat(), paint)
//        }
    }
}
