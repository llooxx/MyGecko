package com.linorz.mygecko

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.linorz.mygecko.tools.StaticMethod
import kotlinx.android.synthetic.main.activity_geckos.*
import java.io.File
import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import android.R.menu
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem


/**
 * Created by linorz on 2018/3/2.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var list: ArrayList<GeckoGson.GeckoBean>
    private lateinit var adapter: GeckoAdapter
    private lateinit var editor: SharedPreferences.Editor
    var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun dispatchMessage(msg: android.os.Message) {
            when (msg.what) {
                1 -> StaticMethod.makeText(baseContext, msg.obj.toString() + "完成", Toast.LENGTH_SHORT)
                else -> {
                    StaticMethod.makeText(baseContext, "完成", Toast.LENGTH_SHORT)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geckos)

        StaticMethod.requestPermissions(this)
        StaticMethod.initSomthing(this)

        setSupportActionBar(toolbar)

        editor = this.getSharedPreferences("MyGecko", Context.MODE_PRIVATE).edit()
        list = StaticMethod.getGeckoList(baseContext) as ArrayList<GeckoGson.GeckoBean>
        adapter = GeckoAdapter(this.baseContext, list)
        geckos_recycler.layoutManager = GridLayoutManager(baseContext, 4)
        geckos_recycler.itemAnimator = DefaultItemAnimator()
        geckos_recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun update() {
//        geckos_recycler.adapter = adapter
        val action: Int = intent.getIntExtra("action", 0)
        val position: Int = intent.getIntExtra("position", -1)
        when (action) {
        //没改变
            0 -> {

            }
        //新建
            1 -> {
                val list2 = StaticMethod.getGeckoList(baseContext)
                list.add(list2[list2.size - 1])
                adapter.notifyItemInserted(list.size - 1)
            }
        //编辑
            2 -> {
                val list2 = StaticMethod.getGeckoList(baseContext)
                list[position] = list2[position]
                adapter.notifyItemChanged(position)
            }
        //删除
            3 -> {
                list.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
        intent.putExtra("action", 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @SuppressLint("ShowToast")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this, GeckoDetailEditActivity::class.java))
            }
            R.id.write -> {
                Thread({
                    val geckos = StaticMethod.getGeckoList(baseContext)
                    for (i in 0 until geckos.size) {
                        val it = geckos[i]
                        if (it.picture == null || (it.picture != null && it.picture.isEmpty()))
                            continue
                        val file = File(DealFile.getFilePath() + "/水印")
                        if (!file.exists())
                            file.mkdir()
                        val src: Bitmap = BitmapFactory.decodeFile(DealFile.getFilePath() + "/" +
                                it.picture)
                        val str = "编号（临时）:" + it.num + "\n" +
                                "基因:" + it.kind + "\n" +
                                "性别:" + it.gender + "\n" +
                                "出生日:" + it.birth + "\n" +
                                "位置:" + (i / 4 + 1) + "排"

                        val dst = StaticMethod.writeOnBitmap(this, src, str)
                        val name: String = "Linorz" + it.picture
                        StaticMethod.saveBitmapToSDCard(dst, file.absolutePath, name)
                        src.recycle()
                        dst.recycle()
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.DATA, file.absolutePath + "/" + name)
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, name)
                        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        mHandler.sendMessage(Message.obtain(mHandler, 1, it.num))
                    }

                }).start()
            }
            R.id.save -> {
                StaticMethod.saveGeckoLIst(list, editor)
                list = StaticMethod.getGeckoList(baseContext) as ArrayList<GeckoGson.GeckoBean>
                adapter = GeckoAdapter(baseContext, list)
                geckos_recycler.adapter = adapter
            }
            R.id.shortcut -> {
                val bitmap = StaticMethod.shotRecyclerView(geckos_recycler)
                val file = File(DealFile.getFilePath() + "/截图")
                if (!file.exists())
                    file.mkdir()
                StaticMethod.saveBitmapToSDCard(bitmap, file.absolutePath, "all.jpg")
                mHandler.sendEmptyMessage(0)
            }
            R.id.flags -> {
                geckos_recycler.setItemTouchHelper(list, true) {
                    StaticMethod.saveGeckoLIst(list, editor)
                }
            }
        }
        return true
    }
}