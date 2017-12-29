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

    val map_gene = mutableMapOf<Int, Gene>()

    constructor() {
        //a->1  A->2
        for (i in 0 until type_tab.size)
            map_gene[i * 2 + 1] = Gene(type_tab[i][0] as Int, i * 2 + 1, type_tab[i][1] as String, type_tab[i][2] as String)

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

        for (i in 0 until easy_name.size) {
            var f = true
            for (j in 1 until easy_name[i].size)
                if (!str.contains(easy_name[i][j]) || str.contains("隐" + easy_name[i][j])) {
                    f = false
                    break
                }
            if (f) {
                var ff = false //超级雪花判断
                for (k in 0 until easy_name[i].size) {
                    if (easy_name[i][k] == "雪花" && str.contains("超级雪花")) {
                        ff = true
                        str = str.replace("超级雪花 ", "")
                    } else
                        str = str.replace(easy_name[i][k] + " ", "")

                }
                str += if (ff) "超级" + easy_name[i][0]
                else easy_name[i][0]
                break
            }
        }
        return str
    }


    private val type_tab = arrayOf(
            arrayOf(TYPE0, "mark_snow", "雪花"),
            arrayOf(TYPE0, "giant", "巨人"),
            arrayOf(TYPE0, "tangelo", "橘柚"),
            arrayOf(TYPE0, "lemon_frost", "柠檬霜"),
            
            arrayOf(TYPE1, "white_yellow", "白黄"),
            arrayOf(TYPE1, "enigma", "谜"),
            arrayOf(TYPE1, "pastel", "蜡笔"),

            arrayOf(TYPE2, "tremper", "川普白化"),
            arrayOf(TYPE2, "bell", "贝尔白化"),
            arrayOf(TYPE2, "rainwater", "雨水白化"),
            arrayOf(TYPE2, "blizzard", "暴风雪"),
            arrayOf(TYPE2, "eclipse", "日蚀"),
            arrayOf(TYPE2, "marble_eye", "大理石眼"),
            arrayOf(TYPE2, "noir_desir", "欲望黑眼"),
            arrayOf(TYPE2, "murphy_patternless", "青白化"),

            arrayOf(TYPE3, "high_yellow", "高黄")
    )
    private val easy_name = arrayOf(
            arrayOf("方解石", "雪花", "白黄", "贝尔白化", "日蚀", "谜"),//方解石
            arrayOf("潜行", "雪花", "贝尔白化", "日蚀", "谜"),//潜行
            arrayOf("贝尔斑点狗", "超级雪花", "贝尔白化", "谜"),//贝尔斑点狗
            arrayOf("白骑士", "贝尔白化", "暴风雪", "日蚀"), //白骑士
            arrayOf("贝尔超级暴风雪", "贝尔白化", "暴风雪"),//贝尔超级暴风雪
            arrayOf("掠夺者", "贝尔白化", "日蚀", "青白化"),//掠夺者
            arrayOf("雷达", "贝尔白化", "日蚀"),//雷达
            arrayOf("极光", "白黄", "贝尔白化"),//极光

            arrayOf("甜甜圈", "雪花", "川普白化", "日蚀", "谜"),//甜甜圈
            arrayOf("恶魔白酒", "川普白化", "暴风雪", "日蚀"), //恶魔白酒
            arrayOf("超级暴风雪", "川普白化", "暴风雪"),//超级暴风雪
            arrayOf("灰烬", "川普白化", "日蚀", "青白化"),//灰烬
            arrayOf("红眼暴龙", "川普白化", "日蚀"), //红眼暴龙

            arrayOf("水晶", "雪花", "雨水白化", "日蚀", "谜"),//水晶
            arrayOf("旋风", "雨水白化", "日蚀", "青白化"),//旋风
            arrayOf("台风", "雨水白化", "日蚀"),//台风

            arrayOf("超级白金", "超级雪花", "青白化"),//超级白金
            arrayOf("宇宙", "超级雪花", "白黄", "日蚀"),//宇宙
            arrayOf("银河", "超级雪花", "日蚀"),//银河
            arrayOf("影", "雪花", "白黄", "日蚀"),//影
            arrayOf("黑洞", "雪花", "日蚀", "谜"),//黑洞
            arrayOf("香蕉暴风雪", "暴风雪", "青白化")//香蕉暴风雪
    )
}