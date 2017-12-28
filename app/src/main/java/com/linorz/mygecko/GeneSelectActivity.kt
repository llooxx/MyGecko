package com.linorz.mygecko

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.linorz.mygecko.tools.GeneTool
import kotlinx.android.synthetic.main.activity_gene_select.*
import kotlinx.android.synthetic.main.gene_selet_item.view.*
import android.content.Intent


/**
 * Created by linorz on 2017/12/27.
 */
class GeneSelectActivity : AppCompatActivity() {
    private var views = ArrayList<TextView>()
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("InflateParams", "SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gene_select)
        val geneTool = GeneTool().getInstance()
        val color_white = resources.getColor(android.R.color.white, null)
        val color_black = resources.getColor(android.R.color.black, null)
        for ((_, k) in geneTool.map_gene) {
            val item = layoutInflater.inflate(R.layout.gene_selet_item, null)
            item.gene_selet_item_left.setOnClickListener {
                if (item.gene_selet_item_left.currentTextColor == color_white) {
                    item.gene_selet_item_left.setBackgroundColor(android.R.color.transparent)
                    item.gene_selet_item_left.setTextColor(color_black)
                } else {
                    item.gene_selet_item_left.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
                    item.gene_selet_item_left.setTextColor(color_white)
                    item.gene_selet_item_right.setBackgroundColor(android.R.color.transparent)
                    item.gene_selet_item_right.setTextColor(color_black)
                }
            }
            item.gene_selet_item_right.setOnClickListener {
                if (item.gene_selet_item_right.currentTextColor == color_white) {
                    item.gene_selet_item_right.setBackgroundColor(android.R.color.transparent)
                    item.gene_selet_item_right.setTextColor(color_black)
                } else {
                    item.gene_selet_item_right.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
                    item.gene_selet_item_right.setTextColor(color_white)
                    item.gene_selet_item_left.setBackgroundColor(android.R.color.transparent)
                    item.gene_selet_item_left.setTextColor(color_black)
                }
            }
            when (k.type) {
                geneTool.TYPE0 -> {
                    item.gene_selet_item_left.text = k.name_ch
                    item.gene_selet_item_left.tag = arrayOf(k.gene_num + 1, k.gene_num)
                    item.gene_selet_item_right.text = "超级" + k.name_ch
                    item.gene_selet_item_right.tag = arrayOf(k.gene_num + 1, k.gene_num + 1)
                    gene_select_type0.addView(item)
                }
                geneTool.TYPE1 -> {
                    item.gene_selet_item_left.text = k.name_ch
                    item.gene_selet_item_left.tag = arrayOf(k.gene_num + 1, k.gene_num)
                    item.gene_selet_item_right.text = "纯合" + k.name_ch
                    item.gene_selet_item_right.tag = arrayOf(k.gene_num + 1, k.gene_num + 1)
                    gene_select_type1.addView(item)
                }
                geneTool.TYPE2 -> {
                    item.gene_selet_item_left.text = k.name_ch
                    item.gene_selet_item_left.tag = arrayOf(k.gene_num, k.gene_num)
                    item.gene_selet_item_right.text = "隐" + k.name_ch
                    item.gene_selet_item_right.tag = arrayOf(k.gene_num + 1, k.gene_num)
                    gene_select_type2.addView(item)
                }
                geneTool.TYPE3 -> {
                    item.gene_selet_item_left.text = "高黄"
                    item.gene_selet_item_left.tag = arrayOf(k.gene_num, k.gene_num)
                    item.gene_selet_item_right.text = "原色"
                    item.gene_selet_item_right.tag = arrayOf(k.gene_num + 1, k.gene_num + 1)
                    gene_select_type3.addView(item)
                }
            }
            views.add(item.gene_selet_item_left)
            views.add(item.gene_selet_item_right)
        }

        val genes: ArrayList<GeneTool.SpecificGene> = intent.getParcelableArrayListExtra("genes")
        views.forEach {
            for (gene in genes) {
                val arr: Array<Int> = it.tag as Array<Int>
                if (arr[0] == gene.left && arr[1] == gene.right) {
                    it.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
                    it.setTextColor(color_white)
                }
            }
        }

        gene_select_confirm.setOnClickListener {
            val genes = ArrayList<GeneTool.SpecificGene>()
            views.forEach {
                if (it.currentTextColor == color_white) {
                    val arr: Array<Int> = it.tag as Array<Int>
                    val num = if (arr[0] % 2 == 0) arr[0] - 1 else arr[0]
                    genes.add(GeneTool.SpecificGene(geneTool.map_gene[num]!!.type, num, arr[0], arr[1]))
                }
            }

            val intent = Intent()
            intent.putParcelableArrayListExtra("genes", genes)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}