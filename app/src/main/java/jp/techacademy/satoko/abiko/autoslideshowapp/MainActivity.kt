package jp.techacademy.satoko.abiko.AutoSlideshowApp

import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Handler
import android.os.Looper
import jp.techacademy.satoko.abiko.AutoSlideshowApp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("ANDROID", "許可された")
            } else {
                Log.d("ANDROID", "許可されなかった")
            }
        }
//パーミッションはボタン押下すると許可したものとみなされる（拒否がない）
    // APIレベルによって許可が必要なパーミッションを切り替える
    private val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else android.Manifest.permission.READ_EXTERNAL_STORAGE
    private var cursor: Cursor? = null
    private var playing = false
    private var timer: Timer? = null

    // タイマー用の時間のための変数
    private var handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
           null, // 項目（null = 全項目）
           null, // フィルタ条件（null = フィルタなし）
           null, // フィルタ用パラメータ
           null // ソート (nullソートなし）
        )

        binding.button1.setOnClickListener {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(readImagesPermission) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                Log.d("ANDROID", "許可されている")

            } else {
                Log.d("ANDROID", "許可されていない")
                // 許可されていないので許可ダイアログを表示する
                requestPermissionLauncher.launch(readImagesPermission)
            }
        }
        //再生ボタン　
        binding.startButton.setOnClickListener {
        //画像の操作
          if (playing) {
              playing = false // 再生中フラグを落とす
              if (timer == null) {
                  timer = Timer()
                  timer!!.schedule(object : TimerTask() {
                      override fun run() {
                          handler.post {
                              val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                              val id = cursor!!.getLong(fieldIndex)
                              val imageUri =
                                  ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                              binding.imageView.setImageURI(imageUri)
                          }
                      }
                  }, 200, 200) // 最初に始動させるまで200ミリ秒、ループの間隔を200ミリ秒 に設定
              }

          } else {
              playing = true // 再生中フラグを上げる
              // 停止ボタンの処理
                 if (timer != null){
                     timer!!.cancel()
                     timer = null
                 }
          }
        }

        //進むボタン　
        binding.nextButton.setOnClickListener {
            if (cursor!!.moveToNext()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                binding.imageView.setImageURI(imageUri)
            }
            //最後の場合、最初を表示する
            else if (cursor!!.moveToFirst()) {
                     val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                     val id = cursor!!.getLong(fieldIndex)
                     val imageUri =
                         ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                     binding.imageView.setImageURI(imageUri)
            }
        }
        //戻るボタン
        binding.previousButton.setOnClickListener {
        if (cursor!!.moveToPrevious()) {
           // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            binding.imageView.setImageURI(imageUri)

            }
          //最初の場合、最後を表示
          else  if (cursor!!.moveToLast() ) {
              val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
              val id = cursor!!.getLong(fieldIndex)
              val imageUri =
                  ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

              binding.imageView.setImageURI(imageUri)
              }
          }
    }
}