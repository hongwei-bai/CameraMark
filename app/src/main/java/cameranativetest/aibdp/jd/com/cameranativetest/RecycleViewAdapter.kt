package cameranativetest.aibdp.jd.com.cameranativetest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Package: com.jd.aibdp.sample
 * User: 白宏伟
 * Email: baihongwei1@jd.com
 * Date: 2018/5/10
 * Time: 18:29
 * Description:
 */
class RecycleViewAdapter(context: Context, lists: List<String>) : RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {
    private var contentList: List<String>? = null
    private var context: Context? = null
    private var observer: (String) -> Unit = {}
    var selPos = -1

    init {
        this.context = context
        contentList = lists
    }

    fun update(lists: List<String>) {
        contentList = lists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_rv_sel_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        contentList?.let {
            holder.mTitleTv.text = it[position]
            holder.itemView.setOnClickListener { v ->
                observer(it[position])
            }
            holder.itemView.isSelected = (selPos == position)
        }
    }

    override fun getItemCount(): Int {
        return contentList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTitleTv: TextView

        init {
            mTitleTv = itemView.findViewById(R.id.tv_title)
        }
    }

    fun setOnItemClickObserver(ob: (String) -> Unit) {
        observer = ob
    }
}