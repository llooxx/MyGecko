package com.linorz.mygecko

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.linorz.mygecko.tools.StaticMethod
import com.nostra13.universalimageloader.core.ImageLoader

/**
 * Created by linorz on 2018/3/8.
 */
class GeckoAdapter(private val baseContext: Context, geckolist: List<GeckoGson.GeckoBean>) : RecyclerView.Adapter<GeckoAdapter.GeckoItem>() {
    private val inflater: LayoutInflater = LayoutInflater.from(baseContext)
    private val list: List<GeckoGson.GeckoBean> = geckolist

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeckoItem {
        val view = inflater.inflate(R.layout.gecko_item, parent, false)
        return GeckoItem(view)
    }


    override fun onBindViewHolder(item: GeckoItem, position: Int) {
        item.num.text = list[position].num.toString()
        item.kind.text = list[position].kind.toString()
        item.place.text = list[position].place.toString()
        if (list[position].picture != null)
            ImageLoader.getInstance().displayImage("file://" + DealFile.getFilePath() + "/" +
                    list[position].picture, item.img,
                    StaticMethod.getOptions())
        else
            item.img.setImageResource(android.R.color.darker_gray)
        item.itemView.setOnClickListener {
            val intent = Intent(baseContext, GeckoDetailActivity::class.java)
            intent.putExtra("position", position)
            baseContext.startActivity(intent)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: GeckoItem)
    }

    inner class GeckoItem(var view: View) : RecyclerView.ViewHolder(view) {
        var img: ImageView = view.findViewById(R.id.gecko_item_img)
        var num: TextView = view.findViewById(R.id.gecko_item_num)
        var kind: TextView = view.findViewById(R.id.gecko_item_kind)
        var place: TextView = view.findViewById(R.id.gecko_item_place)
    }
}