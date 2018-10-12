package com.jiandanlangman.mapdemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFile = File(cacheDir, "demo.map")
        if (!mapFile.exists())
            extractMapFile()
        else
            toMapActivity()
    }

    private fun extractMapFile() {
        Thread {
            val `is` = assets.open("demo.map")
            val os = FileOutputStream(File(cacheDir, "demo.map"))
            val buffer = ByteArray(8192)
            var readLength: Int
            while (true) {
                readLength = `is`.read(buffer)
                if (readLength == -1)
                    break
                os.write(buffer, 0, readLength)
            }
            os.flush()
            os.close()
            `is`.close()
            runOnUiThread { toMapActivity() }
        }.start()
    }

    private fun toMapActivity() {
        finish()
        startActivity(Intent(this, MapActivity::class.java))
    }
}
