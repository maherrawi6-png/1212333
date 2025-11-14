package com.example.dashcamreceiver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var etStreamUrl: EditText
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var rvFiles: RecyclerView
    private lateinit var adapter: FileListAdapter
    private val outDir by lazy { FileManager.getOutputDir(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etStreamUrl = findViewById(R.id.etStreamUrl)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        rvFiles = findViewById(R.id.rvFiles)

        adapter = FileListAdapter(this) { file ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "video/mp4")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        rvFiles.layoutManager = LinearLayoutManager(this)
        rvFiles.adapter = adapter

        btnStart.setOnClickListener {
            val url = etStreamUrl.text.toString().trim()
            if (url.isEmpty()) { Toast.makeText(this, getString(R.string.enter_stream_url), Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val i = Intent(this, RecorderService::class.java)
            i.putExtra(RecorderService.EXTRA_STREAM_URL, url)
            startForegroundService(i)
            Toast.makeText(this, getString(R.string.recorder_started), Toast.LENGTH_SHORT).show()
        }

        btnStop.setOnClickListener {
            val i = Intent(this, RecorderService::class.java)
            stopService(i)
            Toast.makeText(this, getString(R.string.recorder_stopped), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateFiles(FileManager.listFiles(outDir))
    }
}
