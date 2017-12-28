package com.linorz.mygecko

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.linorz.mygecko.tools.GeneTool
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val SELECT_FATHER_GENE_CODE = 1
    private val SELECT_MOTHER_GENE_CODE = 2
    var father_genes: ArrayList<GeneTool.SpecificGene> = arrayListOf()
    var mother_genes: ArrayList<GeneTool.SpecificGene> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_select_father_gene_btn.setOnClickListener {
            val intent = Intent(this, GeneSelectActivity::class.java)
            intent.putExtra("code", SELECT_FATHER_GENE_CODE)
            intent.putParcelableArrayListExtra("genes", father_genes)
            startActivityForResult(intent, SELECT_FATHER_GENE_CODE)
        }
        main_select_mother_gene_btn.setOnClickListener {
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
                main_select_father_gene_btn.text = GeneTool().getInstance().printGenesCh(father_genes)
            }
            SELECT_MOTHER_GENE_CODE -> {
                mother_genes = data!!.getParcelableArrayListExtra("genes")
                main_select_mother_gene_btn.text = GeneTool().getInstance().printGenesCh(mother_genes)
            }
        }
        if (father_genes.isNotEmpty() && mother_genes.isNotEmpty()) {
            var str = ""
            GeneTool().getInstance().getGeneration(father_genes, mother_genes).forEach {
                str += (it.probability * 100).toString() + "% " + GeneTool().getInstance().printGenesCh(it.list_genes) + "\n\n"
            }
            main_chid_gene.text = str
        }
    }
}
