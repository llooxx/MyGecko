package com.linorz.mygecko

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.linorz.mygecko.tools.GeneTool
import kotlinx.android.synthetic.main.activity_calculate.*

class CalculateActivity : AppCompatActivity() {
    private val SELECT_FATHER_GENE_CODE = 1
    private val SELECT_MOTHER_GENE_CODE = 2
    var father_genes: ArrayList<GeneTool.SpecificGene> = arrayListOf()
    var mother_genes: ArrayList<GeneTool.SpecificGene> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate)

        calculate_select_father_gene_btn.setOnClickListener {
            val intent = Intent(this, GeneSelectActivity::class.java)
            intent.putExtra("code", SELECT_FATHER_GENE_CODE)
            intent.putParcelableArrayListExtra("genes", father_genes)
            startActivityForResult(intent, SELECT_FATHER_GENE_CODE)
        }
        calculate_select_mother_gene_btn.setOnClickListener {
            val intent = Intent(this, GeneSelectActivity::class.java)
            intent.putExtra("code", SELECT_MOTHER_GENE_CODE)
            intent.putParcelableArrayListExtra("genes", mother_genes)
            startActivityForResult(intent, SELECT_MOTHER_GENE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            SELECT_FATHER_GENE_CODE -> {
                father_genes = data!!.getParcelableArrayListExtra("genes")
                calculate_select_father_gene_btn.text = GeneTool().getInstance().printGenesCh(father_genes)
            }
            SELECT_MOTHER_GENE_CODE -> {
                mother_genes = data!!.getParcelableArrayListExtra("genes")
                calculate_select_mother_gene_btn.text = GeneTool().getInstance().printGenesCh(mother_genes)
            }
        }
        if (father_genes.isNotEmpty() && mother_genes.isNotEmpty()) {
            val probability_list = arrayListOf<Double>()
            val str_list = arrayListOf<String>()
            //获得子基因
            GeneTool().getInstance().getGeneration(father_genes, mother_genes).forEach {
                probability_list.add(it.probability)
                str_list.add(GeneTool().getInstance().printGenesCh(it.list_genes) + "\n\n")
            }
            //合并相同基因
            var i = 0
            while (i < str_list.size) {
                var j = i + 1
                while (j < str_list.size) {
                    if (str_list[i] == str_list[j]) {
                        probability_list[i] = probability_list[i] + probability_list[j]
                        probability_list.removeAt(j)
                        str_list.removeAt(j)
                    }
                    j++
                }
                i++
            }
            var str = ""
            for (i in 0 until str_list.size)
                str += (probability_list[i] * 100).toString() + "% " + str_list[i]

            calculate_chid_gene.text = str
        }
    }
}
