package com.jiandanlangman.mapdemo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.jiandanlangman.maplibrary.displayer.Location
import com.jiandanlangman.maplibrary.displayer.Map
import com.jiandanlangman.maplibrary.displayer.MapViewContext
import com.jiandanlangman.maplibrary.displayer.layer.Layer
import com.jiandanlangman.maplibrary.displayer.layer.PointLayer
import com.jiandanlangman.maplibrary.displayer.widget.MapView
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File


class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapView = findViewById<MapView>(R.id.mapView)
        //设置加载时的动画时长，默认240ms，小于等于0表示无动画
//        mapView.setLoadAnimationDuration(240)
        val map = Map(File(cacheDir, "demo.map").absolutePath)
        mapView.setMap(map)
        val plusLevelButton = findViewById<View>(R.id.plus)
        plusLevelButton.setOnClickListener {
            mapView.plusLevel()
        }
        val reduceLevelButton = findViewById<View>(R.id.reduce)
        reduceLevelButton.setOnClickListener {
            mapView.reduceLevel()
        }
        mapView.addLayer(object : Layer {
            override fun onAttachToMapView(context: MapViewContext) {}

            override fun onDrawLayer(canvas: Canvas) {
                plusLevelButton.isEnabled = mapView.canPlusLevel()
                reduceLevelButton.isEnabled = mapView.canReduceLevel()
            }

            override fun onLongPress(location: Location) = false

            override fun onSingleTap(location: Location) = false

            override fun onUnAttachToMapView() {}

        })
        val pointLayer = PointLayer()
        pointLayer.setOnPointClickListener {
            val obj = it.obj as Array<*>
            if (obj[0] as Int == 1) {
                Toast.makeText(this, "onPointClick:$it", Toast.LENGTH_SHORT).show()
                true
            } else
                false
        }
        pointLayer.setOnPointLongPressedListener {
            val obj = it.obj as Array<*>
            if (obj[0] as Int == 1) {
                Toast.makeText(this, "onPointLongPressed:$it", Toast.LENGTH_SHORT).show()
                true
            } else
                false
        }
        mapView.addLayer(pointLayer)
        drawShops(pointLayer)
    }


    private fun drawShops(layer: PointLayer) {
        Thread {
            val ins = assets.open("shops.json")
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(8196)
            var redLength: Int
            while (true) {
                redLength = ins.read(buffer)
                if (redLength == -1)
                    break
                baos.write(buffer, 0, redLength)
            }
            baos.flush()
            val shops = JSONArray(String(baos.toByteArray()))
            baos.close()
            ins.close()
            val textSize = 12f * resources.displayMetrics.density
            val strokeWidth = 2f * resources.displayMetrics.density
            val paint = Paint()
            paint.textSize = textSize
            paint.isAntiAlias = true
            paint.textAlign = Paint.Align.CENTER
            val bitmapHeight = (textSize + strokeWidth * 2) * 1.4f
            val bitmapMinWidth = (textSize + strokeWidth * 2) * 3.2f
            val fontMetrics = paint.fontMetrics
            val top = fontMetrics.top
            val bottom = fontMetrics.bottom
            val baseLineY = bitmapHeight / 2f - top / 2f - bottom / 2f
            val points = ArrayList<PointLayer.Point>()
            for (i in 0 until shops.length()) {
                val shop = shops.optJSONObject(i)
                val shopName = shop.optString("name", "")
                val isVIP = shop.optInt("isVIP", 0)
                var width = Math.ceil(paint.measureText(shopName).toDouble() + strokeWidth * 2).toFloat()
                if (width < bitmapMinWidth)
                    width = bitmapMinWidth
                val bitmap = Bitmap.createBitmap(width.toInt(), bitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                paint.color = Color.WHITE
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                canvas.drawText(shopName, width / 2, baseLineY, paint)
                paint.color = if (isVIP == 1) Color.RED else Color.GRAY
                paint.style = Paint.Style.FILL
                paint.strokeWidth = 0f
                canvas.drawText(shopName, width / 2, baseLineY, paint)
                val obj = arrayOf(isVIP, shop.optString("category", ""))
                if (isVIP == 0)
                    points.add(0, PointLayer.Point(shopName, shop.optInt("x", 0).toFloat(), shop.optInt("y", 0).toFloat(), bitmap, obj))
                else
                    points.add(points.size, PointLayer.Point(shopName, shop.optInt("x", 0).toFloat(), shop.optInt("y", 0).toFloat(), bitmap, obj))
            }
            Handler(Looper.getMainLooper()).post { layer.setPoints(points) }
        }.start()

    }
}