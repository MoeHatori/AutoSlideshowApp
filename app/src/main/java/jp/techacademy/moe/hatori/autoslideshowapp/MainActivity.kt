package jp.techacademy.moe.hatori.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    //URIの配列を保存しておく
    private val picURLInfo = ArrayList<Uri>()

    //URI配列に指定するためのIndex
    private var Nowindex:Int = 0

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

        //再生ボタン実装
        start_button.setOnClickListener {
            if (mTimer == null){
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mTimerSec += 1
                        mHandler.post {
                            timer.text = String.format("%.1f", mTimerSec)
                        }
                    }
                }, 1000, 1000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
            }
            //imageView.setImageURI(picURLInfo[1])
        }

        //進むボタンが押されたときの処理
        prev_button.setOnClickListener {

            Nowindex += 1
            if (Nowindex == picURLInfo.count()){
                Nowindex = 0
            }
            imageView.setImageURI(picURLInfo[Nowindex])

        }


        //戻るボタンが押されたときの処理
        next_button.setOnClickListener {
            //Nowindexが要素数と一致したときの判定処理

            if (Nowindex == picURLInfo.count()){
                Nowindex = picURLInfo.count()-1
            }
            //Nowindexが０だったときの判定処理
            if (Nowindex == 0){
                Nowindex = picURLInfo.count() -1
                imageView.setImageURI(picURLInfo[Nowindex])
            }else{
                Nowindex -= 1
                imageView.setImageURI(picURLInfo[Nowindex])
            }

        }


//        pause_button.setOnClickListener {
//            if (mTimer != null){
//                mTimer!!.cancel()
//                mTimer = null
//            }
//        }


//        reset_button.setOnClickListener {
//            mTimerSec = 0.0
//            timer.text = String.format("%.1f", mTimerSec)
//        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor!!.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                picURLInfo.add(imageUri)
                Log.d("ANDROID_URI", "URI : " + imageUri.toString())

            } while (cursor.moveToNext())
        }
        cursor.close()
    }



}