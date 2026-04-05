package com.example.photogalleryapp

import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.sqrt

class FullscreenActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RES_ID = "extra_res_id"
    }

    private lateinit var imageView: ImageView
    private val matrix = Matrix()
    private val savedMatrix = Matrix()

    private var mode = NONE

    private val start = FloatArray(2)
    private val mid = FloatArray(2)
    private var oldDist = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        imageView = findViewById(R.id.ivFull)
        imageView.scaleType = ImageView.ScaleType.MATRIX

        val resId = intent.getIntExtra(EXTRA_RES_ID, 0)
        if (resId != 0) imageView.setImageResource(resId)

        findViewById<FloatingActionButton>(R.id.fabBack).setOnClickListener {
            finish()
        }

        imageView.setOnTouchListener { _, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(matrix)
                    start[0] = event.x
                    start[1] = event.y
                    mode = DRAG
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(event)
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix)
                        midPoint(mid, event)
                        mode = ZOOM
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        matrix.set(savedMatrix)
                        val dx = event.x - start[0]
                        val dy = event.y - start[1]
                        matrix.postTranslate(dx, dy)
                    } else if (mode == ZOOM) {
                        val newDist = spacing(event)
                        if (newDist > 10f) {
                            matrix.set(savedMatrix)
                            val scale = newDist / oldDist
                            matrix.postScale(scale, scale, mid[0], mid[1])
                        }
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                }
            }

            imageView.imageMatrix = matrix
            true
        }
    }

    private fun spacing(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun midPoint(point: FloatArray, event: MotionEvent) {
        if (event.pointerCount < 2) return
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[0] = x / 2f
        point[1] = y / 2f
    }

    private companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
}