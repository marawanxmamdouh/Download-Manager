package dev.marawanxmamdouh.downloadmanager

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dev.marawanxmamdouh.downloadmanager.databinding.ContentDetailBinding

private const val TAG = "DetailActivity"

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentDetailBinding.inflate(layoutInflater)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        var status = ""
        var downloadName = ""

        if (intent != null) {
            Log.i(TAG, "onCreate (line 26): intent is not null")
            status = intent.getStringExtra("status") ?: ""
            downloadName = intent.getStringExtra("downloadName") ?: ""
        }

        Log.i(TAG, "onCreate (line 27): $status")
        Log.i(TAG, "onCreate (line 28): $downloadName")

        if (status == "Success") {
            binding.downloadStatus.setTextColor(Color.GREEN)
        } else {
            binding.downloadStatus.setTextColor(Color.RED)
        }
        binding.downloadName.text = downloadName
        binding.downloadStatus.text = status

        setContentView(binding.root)
    }
}
