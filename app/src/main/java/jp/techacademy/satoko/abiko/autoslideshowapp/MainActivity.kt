package jp.techacademy.satoko.abiko.AutoSlideshowApp

import android.content.ContentUris
import android.content.pm.PackageManager
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

    // APIレベルによって許可が必要なパーミッションを切り替える
    private val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else android.Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button1.setOnClickListener {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(readImagesPermission) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                Log.d("ANDROID", "許可されている")

            } else {
                Log.d("ANDROID", "許可されていない")
                // 許可されていないので許可ダイアログを表示する
                requestPermissionLauncher.launch(readImagesPermission)
            //拒否の場合の処理
                //？？
            }
        }
    }
    // 画像をボタンで表示する
    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

// 進むボタンの時、次の画像を表示（次の画像がない場合、movetonext）
// 戻るボタンの時、前の画像を表示（前の画像がない場合、movetofirst）
// 再生停止ボタンの時、再生か停止か判定
// 再生中なら停止して、停止中なら再生する

        binding.button2.setOnClickListener {
        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            binding.imageView.setImageURI(imageUri)
            }
        cursor.close()
        }
    }
}