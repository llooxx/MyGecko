package com.linorz.mygecko

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gecko_detail_edit.*
import android.content.Intent
import android.provider.MediaStore
import com.linorz.mygecko.tools.StaticMethod
import com.nostra13.universalimageloader.core.ImageLoader
import java.io.File


/**
 * Created by linorz on 2018/3/3.
 */
class GeckoDetailEditActivity : AppCompatActivity() {
    val PICK_PHOTO = 1
    private var img_path = ""
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("InflateParams", "SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gecko_detail_edit)
        val position = intent.getIntExtra("position", -1)
        gecko_detail_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, PICK_PHOTO)
        }

        if (position == -1)
            newOne()
        else
            editOld(position)

    }

    fun newOne() {
        gecko_detail_complete.setOnClickListener {
            val gecko = GeckoGson.GeckoBean()
            gecko.num = Integer.parseInt(gecko_detail_num.text.toString())
            gecko.kind = gecko_detail_kind.text.toString()
            gecko.gender = gecko_detail_gender.text.toString()
            gecko.birth = gecko_detail_birth.text.toString()
            gecko.eggtime = gecko_detail_eggtime.text.toString()
            gecko.parent = gecko_detail_parent.text.toString()
            gecko.place = gecko_detail_place.text.toString()
            if (!img_path.isEmpty()) {
                val name = gecko.num.toString() + gecko.kind + gecko.birth + gecko.gender + img_path.substring(img_path.indexOf("."))
                DealFile.copyFile(File(img_path), DealFile.getBaseSDCardPath().getPath() + "/", name)
                gecko.picture = name
            }
            val geckoList = StaticMethod.getGeckoList(baseContext)
            geckoList.add(gecko)
            val geckos = GeckoGson()
            for (i in 0 until geckoList.size) {
                geckoList[i].num = i
            }
            geckos.geckos = geckoList

            val editor = this.getSharedPreferences("MyGecko", Context.MODE_PRIVATE).edit()

            StaticMethod.saveGeckoLIst(geckoList, editor)

            val intent2 = Intent(this, MainActivity::class.java)
            intent2.putExtra("action", 1)
            startActivity(intent2)
        }
    }

    fun editOld(position: Int) {
        val geckos = StaticMethod.getGeckoList(baseContext)

        gecko_detail_num.setText(geckos[position].num.toString())
        gecko_detail_kind.setText(geckos[position].kind.toString())
        gecko_detail_gender.setText(geckos[position].gender.toString())
        gecko_detail_birth.setText(geckos[position].birth.toString())
        gecko_detail_eggtime.setText(geckos[position].eggtime.toString())
        gecko_detail_parent.setText(geckos[position].parent.toString())
        gecko_detail_place.setText(geckos[position].place.toString())
        ImageLoader.getInstance().displayImage("file://" + DealFile.getFilePath() + "/" +
                geckos[position].picture, gecko_detail_img,
                StaticMethod.getOptions())

        gecko_detail_complete.setOnClickListener {
            val geckoList = StaticMethod.getGeckoList(baseContext)
            val gecko = geckoList[position]
            gecko.num = Integer.parseInt(gecko_detail_num.text.toString())
            gecko.kind = gecko_detail_kind.text.toString()
            gecko.gender = gecko_detail_gender.text.toString()
            gecko.birth = gecko_detail_birth.text.toString()
            gecko.eggtime = gecko_detail_eggtime.text.toString()
            gecko.parent = gecko_detail_parent.text.toString()
            gecko.place = gecko_detail_place.text.toString()

            var name = ""
            if (img_path.isEmpty()) {
                name = gecko.num.toString() + gecko.kind + gecko.birth + gecko.gender + gecko.picture.substring(gecko.picture.indexOf("."))
                DealFile.renameFile(DealFile.getBaseSDCardPath().getPath() + "/" +
                        gecko.picture, DealFile.getBaseSDCardPath().getPath() + "/" +
                        name)
            } else if (!img_path.isEmpty()) {
                name = gecko.num.toString() + gecko.kind + gecko.birth + gecko.gender + img_path.substring(img_path.indexOf("."))
                DealFile.copyFile(File(img_path), DealFile.getBaseSDCardPath().getPath() + "/", name)
            }
            gecko.picture = name

            val editor = this.getSharedPreferences("MyGecko", Context.MODE_PRIVATE).edit()

            StaticMethod.saveGeckoLIst(geckoList, editor)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("action", 2)
            intent.putExtra("position", position)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_PHOTO -> {
                    //data中自带有返回的uri
                    val photoUri = data.data
                    val photoPath = StaticMethod.getRealPath(this, photoUri)
                    img_path = photoPath
                    ImageLoader.getInstance().displayImage("file://" + img_path, gecko_detail_img,
                            StaticMethod.getOptions())
                }
            }
        }
    }
}