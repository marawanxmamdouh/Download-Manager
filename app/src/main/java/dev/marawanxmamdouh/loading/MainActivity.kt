package dev.marawanxmamdouh.loading

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.marawanxmamdouh.loading.util.sendNotification


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    var downloadUrl = ""
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager

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
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starterr"
                R.id.radioButton3 -> downloadUrl = "https://github.com/square/retrofit"
                else -> {
                    Toast.makeText(this, "Please select a download option", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }
            Log.i(TAG, "onCreate (line 42): $downloadUrl")
            download()
        }

        createChannel("Downloading", "Download")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) getStatus()
        }
    }

    @SuppressLint("Range")
    private fun getStatus() {
        val query = DownloadManager.Query()
        query.setFilterById(downloadID)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra(
                "downloadName",
                downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
            )
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    Log.i(TAG, "getStatus (line 77): Download completed successfully")
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.sendNotification(
                        "Download completed Successfully",
                        applicationContext
                    )
                    intent.putExtra("status", DownloadManager.STATUS_SUCCESSFUL);
                }
                DownloadManager.STATUS_FAILED -> {
                    Log.i(TAG, "getStatus (line 86): Download failed")
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.sendNotification("Download failed", applicationContext)
                    intent.putExtra("status", DownloadManager.STATUS_FAILED)
                }
                DownloadManager.STATUS_PAUSED -> {
                    Log.i(TAG, "onReceive (line 92): STATUS_PAUSED")
                    intent.putExtra("status", DownloadManager.STATUS_PAUSED)
                }
                DownloadManager.STATUS_PENDING -> {
                    Log.i(TAG, "onReceive (line 94): STATUS_PENDING")
                    intent.putExtra("status", DownloadManager.STATUS_PENDING)
                }
                DownloadManager.STATUS_RUNNING -> {
                    Log.i(TAG, "onReceive (line 96): STATUS_RUNNING")
                    intent.putExtra("status", DownloadManager.STATUS_RUNNING)
                }
                else -> {
                    Log.i(TAG, "onReceive (line 102): STATUS_UNKNOWN")
                    intent.putExtra("status", -1)
                }
            }
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
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
                )

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Complete"

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}
