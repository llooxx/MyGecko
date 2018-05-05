package com.linorz.mygecko

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.linorz.mygecko.tools.StaticMethod
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_gecko_detail.*
import java.io.File

/**
 * Created by linorz on 2018/3/2.
 */
class GeckoDetailActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("InflateParams", "SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gecko_detail)
        val position = intent.getIntExtra("position", -1)
        if (position != -1) {
            val geckoList = StaticMethod.getGeckoList(baseContext)

            gecko_detail_num.text = geckoList[position].num.toString()
            gecko_detail_kind.text = geckoList[position].kind.toString()
            gecko_detail_gender.text = geckoList[position].gender.toString()
            gecko_detail_birth.text = geckoList[position].birth.toString()
            gecko_detail_eggtime.text = geckoList[position].eggtime.toString()
            gecko_detail_parent.text = geckoList[position].parent.toString()
            gecko_detail_place.text = geckoList[position].place.toString()
            ImageLoader.getInstance().displayImage("file://" + DealFile.getFilePath() + "/" +
                    geckoList[position].picture, gecko_detail_img,
                    StaticMethod.getOptions())
            //修改
            gecko_detail_edit.setOnClickListener {
                val intent = Intent(this, GeckoDetailEditActivity::class.java)
                intent.putExtra("position", position)
                startActivity(intent)
            }
            //删除
            gecko_detail_edit.setOnLongClickListener {
                DealFile.delete(DealFile.getBaseSDCardPath().getPath() + "/" +
                        geckoList[position].picture)
                geckoList.removeAt(position)

                val editor = this.getSharedPreferences("MyGecko", Context.MODE_PRIVATE).edit()

                StaticMethod.saveGeckoLIst(geckoList, editor)

//                finish()
                var new_intent = Intent(this, MainActivity::class.java)
                new_intent.putExtra("action", 3)
                new_intent.putExtra("position", position)
                startActivity(new_intent)
                return@setOnLongClickListener true
            }
            //水印
            gecko_detail_write.setOnClickListener({
                var file = File(DealFile.getFilePath() + "/水印")
                if (!file.exists())
                    file.mkdir()
                var src: Bitmap = BitmapFactory.decodeFile(DealFile.getFilePath() + "/" +
                        geckoList[position].picture)
                var str = "编号:" + geckoList[position].num + "\n" +
                        "基因:" + geckoList[position].kind + "\n" +
                        "性别:" + geckoList[position].gender + "\n" +
                        "出生日:" + geckoList[position].birth + "\n" +
                        "位置:" + geckoList[position].place

                var dst = StaticMethod.writeOnBitmap(this, src, str)
                var name: String = "Linorz" + geckoList[position].picture
                StaticMethod.saveBitmapToSDCard(dst, file.absolutePath, name)
                src.recycle()
                dst.recycle()
            })
        }
    }
}