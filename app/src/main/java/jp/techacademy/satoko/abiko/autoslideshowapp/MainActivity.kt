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
import android.view.View
import jp.techacademy.satoko.abiko.AutoSlideshowApp.databinding.ActivityMainBinding

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
    //再生・進むなどのボタンが表示されない
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
        //再生ボタン（タイマーで動作する）
        binding.startBotton.setOnClickListener {
//            Log.d("ANDROID", "start")   timer
//            cursor!!.moveToNext()
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
          else  if (cursor!!.moveToFirst()) {
              val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
              val id = cursor!!.getLong(fieldIndex)
              val imageUri =
                  ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

              binding.imageView.setImageURI(imageUri)
          }
        }
    }
}