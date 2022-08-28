package dev.marawanxmamdouh.loading

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    var downloadUrl = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val customButton = findViewById<LoadingButton>(R.id.custom_button)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        customButton.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButton1 -> downloadUrl = "https://github.com/bumptech/glide"
                R.id.radioButton2 -> downloadUrl =
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                R.id.radioButton3 -> downloadUrl = "https://github.com/square/retrofit"
            }
            Log.i(TAG, "onCreate (line 42): $downloadUrl")
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}
