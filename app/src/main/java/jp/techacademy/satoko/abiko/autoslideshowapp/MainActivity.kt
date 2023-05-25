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
//パーミッションはボタン押下すると許可したものとみなされる（拒否がない）
    // APIレベルによって許可が必要なパーミッションを切り替える
    private val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else android.Manifest.permission.READ_EXTERNAL_STORAGE

//再生・進むなどのボタンが表示されない
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
 //   private var cursor: Cursor? = null
    private fun getContentsInfo() {
        // 画像の情報を取得する(cursol の初期化)
        val resolver = contentResolver
  //Q：もとはvalだが、cursorは変数扱いとみなしたほうがよいか？ボタンによって内容が変わる？
        var cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

       // 進むボタンクリック時の処理（次の画像がない場合、movetonext）
       // cursol を使って moveToNext() し、画像を表示する
   //Q： 代入の書き方、カーソルとmovetoの記述
        binding.startBotton.setOnClickListener {
            cursor!!.moveToNext()
        }

        binding.resetButton.setOnClickListener {
            // 戻るボタンクリック時の処理
            // cursol を使って moveToPrevious() し、画像を表示する
            // val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            cursor!!.moveToPrevious()
        }

        binding.resetButton.setOnClickListener {
            // 再生停止ボタンクリック時の処理
            // cursol を使って movetoFirst() し、画像の再表示する（onCreate？）
        }
    }
//カーソルクローズ：カーソルフェッチと対？
   //     cursor.close()
}