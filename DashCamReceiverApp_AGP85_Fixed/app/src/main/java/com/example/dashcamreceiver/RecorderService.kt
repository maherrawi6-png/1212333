package com.example.dashcamreceiver

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

class RecorderService : Service() {
    companion object {
        const val EXTRA_STREAM_URL = "stream_url"
        const val CHANNEL_ID = "dashcam_rec"
    }
    private var streamUrl: String? = null

    override fun onCreate() { super.onCreate(); createNotificationChannel() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        streamUrl = intent?.getStringExtra(EXTRA_STREAM_URL)
        startForeground(1, buildNotification())
        streamUrl?.let { startRecording(it) }
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${'$'}{getString(com.example.dashcamreceiver.R.string.app_name)}")
            .setContentText("Recording service running")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(CHANNEL_ID, "DashCam Rec", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(chan)
        }
    }

    private fun startRecording(url: String) {
        val out = FileManager.getOutputDir(this)
        val outPattern = File(out, "seg_%Y%m%d_%H%M%S.mp4").absolutePath
        val cmd = "-rtsp_transport tcp -i \"$url\" -c copy -f segment -segment_time 60 -reset_timestamps 1 -strftime 1 \"$outPattern\""
        Thread {
            val session = FFmpegKit.execute(cmd)
            val returnCode = session.returnCode
            if (ReturnCode.isSuccess(returnCode)) {
                // success
            } else {
                // handle failure
            }
        }.start()

        Thread {
            while (true) {
                FileManager.cleanupOld(out, 5)
                Thread.sleep(10_000)
            }
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
