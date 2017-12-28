package com.linorz.mygecko.tools

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by linorz on 2017/12/26.
 */
class GeneTool {
    companion object {
        val instance = GeneTool()
    }

    fun getInstance(): GeneTool {
        return instance
    }

    val TYPE0 = 0 //等显性
    val TYPE1 = 1 //显性
    val TYPE2 = 2 //隐性
    val TYPE3 = 3 //高黄或原色

    data class Gene(var type: Int = -1, var gene_num: Int = -1, var name_eng: String = "", var name_ch: String = "")
    data class ChildGenes(var probability: Double = 0.0, var list_genes: ArrayList<SpecificGene> = arrayListOf())
    data class SpecificGene(var type: Int = -1, var gene_num: Int = -1, var left: Int = -1, var right: Int = -1) : Parcelable {
        override fun writeToParcel(p0: Parcel?, p1: Int) {
            p0?.writeInt(this.type)
            p0?.writeInt(this.gene_num)
            p0?.writeInt(this.left)
            p0?.writeInt(this.right)
        }

        override fun describeContents(): Int {
            return 0
        }

        constructor(source: Parcel) : this(source.readInt(), source.readInt(), source.readInt(), source.readInt())

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SpecificGene> = object : Parcelable.Creator<SpecificGene> {
                override fun createFromParcel(source: Parcel): SpecificGene {
                    return SpecificGene(source)
                }

                override fun newArray(size: Int): Array<SpecificGene?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    var map_gene = mutableMapOf<Int, Gene>()

    constructor() {
        //a->1  A->2
        var num = 1
        map_gene[num] = Gene(TYPE0, num, "mark_snow", "雪花")
        num += 2
        map_gene[num] = Gene(TYPE0, num, "giant", "巨人")
        num += 2
        map_gene[num] = Gene(TYPE0, num, "tangelo", "橘柚")
        num += 2

        map_gene[num] = Gene(TYPE1, num, "lemon_frost", "柠檬霜")
        num += 2
        map_gene[num] = Gene(TYPE1, num, "white_yellow", "白黄")
        num += 2
        map_gene[num] = Gene(TYPE1, num, "enigma", "谜")
        num += 2
        map_gene[num] = Gene(TYPE1, num, "pastel", "蜡笔")
        num += 2


        map_gene[num] = Gene(TYPE2, num, "tremper", "川普白化")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "bell", "贝尔白化")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "rainwater", "雨水白化")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "blizzard", "暴风雪")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "eclipse", "日蚀")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "marble_eye", "大理石眼")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "noir_desir", "欲望黑眼")
        num += 2
        map_gene[num] = Gene(TYPE2, num, "murphy_patternless", "青白化")
        num += 2
        map_gene[num] = Gene(TYPE3, num, "high_yellow", "高黄")
    }


    fun getGeneration(fatherGene: ArrayList<SpecificGene>, motherGene: ArrayList<SpecificGene>): ArrayList<ChildGenes> {
        for (i in 0 until fatherGene.size) {
            val f = (0 until motherGene.size).any { fatherGene[i].gene_num == motherGene[it].gene_num }
            if (!f) when (fatherGene[i].type) {
                TYPE0 -> motherGene.add(SpecificGene(fatherGene[i].type, fatherGene[i].gene_num, fatherGene[i].gene_num, fatherGene[i].gene_num))
                TYPE1 -> motherGene.add(SpecificGene(fatherGene[i].type, fatherGene[i].gene_num, fatherGene[i].gene_num, fatherGene[i].gene_num))
                TYPE2 -> motherGene.add(SpecificGene(fatherGene[i].type, fatherGene[i].gene_num, fatherGene[i].gene_num + 1, fatherGene[i].gene_num + 1))
            }
        }
        for (i in 0 until motherGene.size) {
            val f = (0 until fatherGene.size).any { motherGene[i].gene_num == fatherGene[it].gene_num }
            if (!f) when (motherGene[i].type) {
                TYPE0 -> fatherGene.add(SpecificGene(motherGene[i].type, motherGene[i].gene_num, motherGene[i].gene_num, motherGene[i].gene_num))
                TYPE1 -> fatherGene.add(SpecificGene(motherGene[i].type, motherGene[i].gene_num, motherGene[i].gene_num, motherGene[i].gene_num))
                TYPE2 -> fatherGene.add(SpecificGene(motherGene[i].type, motherGene[i].gene_num, motherGene[i].gene_num + 1, motherGene[i].gene_num + 1))
            }
        }
        for (i in 0 until fatherGene.size)
            if (fatherGene[i].type == TYPE3) {
                fatherGene.removeAt(i)
                break
            }

        for (i in 0 until motherGene.size)
            if (motherGene[i].type == TYPE3) {
                motherGene.removeAt(i)
                break
            }

        fatherGene.forEach { println("father:" + it.gene_num.toString() + " " + it.left.toString() + " " + it.right.toString()) }
        motherGene.forEach { println("mother:" + it.gene_num.toString() + " " + it.left.toString() + " " + it.right.toString()) }

        val result = ArrayList<ArrayList<Any>>()
        for (i in 0 until fatherGene.size) {
            var f = false
            var j = 0
            for (ii in 0 until motherGene.size)
                if (fatherGene[i].gene_num == motherGene[ii].gene_num) {
                    f = true
                    j = ii
                    break
                }
            if (f) result.add(getChildGene(fatherGene[i].gene_num,
                    (fatherGene[i].left + fatherGene[i].right) * (motherGene[j].left + motherGene[j].right)))
        }

        var current_gene = result[0]
        for (i in 1 until result.size) {
            val next_gene = result[i]
            val t_gene: ArrayList<Any> = arrayListOf()
            for (j in 0 until current_gene.size step 2) {
                for (k in 0 until next_gene.size step 2) {
                    t_gene.add((current_gene[j] as Double) * (next_gene[k] as Double))
                    t_gene.add((current_gene[j + 1] as ArrayList<SpecificGene>) + (next_gene[k + 1] as ArrayList<SpecificGene>))
                }
            }
            current_gene = t_gene
        }

        val list_result = ArrayList<ChildGenes>()
        for (i in 0 until current_gene.size step 2)
            list_result.add(ChildGenes(current_gene[i] as Double, current_gene[i + 1] as ArrayList<SpecificGene>))
        return list_result
    }

    fun getChildGene(num: Int, res: Int): ArrayList<Any> {
        val item = ArrayList<Any>()
        val gene = map_gene[num]!!
        when (res) {
            4 * num * num -> {
                //aaxaa -> 100%aa
                item.add(1.0)
                item.add(arrayListOf(SpecificGene(gene.type, num, num, num)))
            }
            4 * num * num + 2 * num -> {
                //Aaxaa -> 50%Aa 50%aa
                item.add(0.5)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num)))
                item.add(0.5)
                item.add(arrayListOf(SpecificGene(gene.type, num, num, num)))
            }
            4 * num * num + 4 * num -> {
                //AAxaa -> 100%Aa
                item.add(1.0)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num)))
            }
            4 * num * num + 4 * num + 1 -> {
                //AaxAa -> 25%AA 50%Aa 25%aa
                item.add(0.25)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num + 1)))
                item.add(0.5)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num)))
                item.add(0.25)
                item.add(arrayListOf(SpecificGene(gene.type, num, num, num)))
            }
            4 * num * num + 6 * num + 2 -> {
                //AAxAa -> 50%AA 50%Aa
                item.add(0.5)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num + 1)))
                item.add(0.5)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num)))
            }
            4 * num * num + 8 * num + 4 -> {
                //AAxAA -> 100%AA
                item.add(1.0)
                item.add(arrayListOf(SpecificGene(gene.type, num, num + 1, num + 1)))
            }

        }
        return item
    }


    fun printGenesCh(genes: ArrayList<GeneTool.SpecificGene>): String {
        var str = ""
        genes.forEach {
            val gene = map_gene[it.gene_num]
            str += when (gene!!.type) {
                TYPE0 -> when (it.left + it.right) {
                    2 * it.gene_num + 1 -> gene.name_ch + " "
                    2 * it.gene_num + 2 -> "超级" + gene.name_ch + " "
                    else -> ""
                }
                TYPE1 -> when (it.left + it.right) {
                    2 * it.gene_num + 1 -> gene.name_ch + " "
                    2 * it.gene_num + 2 -> gene.name_ch + " "
                    else -> ""
                }
                TYPE2 -> when (it.left + it.right) {
                    2 * it.gene_num -> gene.name_ch + " "
                    2 * it.gene_num + 1 -> "隐" + gene.name_ch + " "
                    else -> ""
                }
                TYPE3 -> when (it.left + it.right) {
                    2 * it.gene_num + 1 -> "高黄"
                    2 * it.gene_num + 2 -> "原色"
                    else -> ""
                }
                else -> ""
            }
        }
        if (str.isEmpty()) str = "高黄"

        val wysrde = arrayOf("雪花", "白黄", "贝尔白化", "日蚀", "谜")//方解石
        val srde = arrayOf("雪花", "贝尔白化", "日蚀", "谜") //潜行
        val ssbe = arrayOf("超级雪花", "贝尔白化", "谜") //贝尔斑点狗
        val wk = arrayOf("贝尔白化", "暴风雪", "日蚀") //白骑士
        val bbb = arrayOf("贝尔白化", "暴风雪") //贝尔超级暴风雪
        val ldz = arrayOf("贝尔白化", "日蚀", "青白化")//掠夺者
        val rd = arrayOf("贝尔白化", "日蚀") //雷达
        val aurora = arrayOf("白黄", "贝尔白化")//极光

        val saee = arrayOf("雪花", "川普白化", "日蚀", "谜")//甜甜圈
        val db = arrayOf("川普白化", "暴风雪", "日蚀") //恶魔白酒
        val bb = arrayOf("川普白化", "暴风雪") //超级暴风雪
        val ember = arrayOf("川普白化", "日蚀", "青白化")//灰烬
        val ra = arrayOf("川普白化", "日蚀") //红眼暴龙

        val srwee = arrayOf("雪花", "雨水白化", "日蚀", "谜")//水晶
        val xf = arrayOf("雨水白化", "日蚀", "青白化")//旋风
        val ty = arrayOf("雨水白化", "日蚀")//台风

        val bj = arrayOf("超级雪花", "青白化")//超级白金
        val un = arrayOf("超级雪花", "白黄", "日蚀")//宇宙
        val ssecl = arrayOf("超级雪花", "日蚀")//银河
        val shadow = arrayOf("雪花", "白黄", "日蚀")//影
        val back_hole = arrayOf("雪花", "日蚀", "谜")//黑洞
        val bananaBli = arrayOf("暴风雪", "青白化")//香蕉暴风雪

        val arr = arrayOf(wysrde, srde, ssbe, wk, bbb, ldz, rd, aurora,
                saee, db, bb, ember, ra,
                srwee, xf, ty,
                bj, un, ssecl, shadow, back_hole, bananaBli)
        val arr_str = arrayOf("方解石", "潜行", "贝尔斑点狗", "白骑士", "贝尔超级暴风雪", "掠夺者", "雷达", "极光",
                "甜甜圈", "恶魔白酒", "超级暴风雪", "灰烬", "红眼暴龙",
                "水晶", "旋风", "台风",
                "超级白金", "宇宙", "银河", "影", "黑洞", "香蕉暴风雪")

        for (i in 0 until arr.size) {
            var f = true
            for (j in 0 until arr[i].size) {
                if (!str.contains(arr[i][j]) || str.contains("隐" + arr[i][j])) {
                    f = false
                    break
                }
            }

            if (f) {
                for (k in 0 until arr[i].size) str = str.replace(arr[i][k] + " ", "")
                str += arr_str[i]
                break
            }
        }



        return str
    }
}