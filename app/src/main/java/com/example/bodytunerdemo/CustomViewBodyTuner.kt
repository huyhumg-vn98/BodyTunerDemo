package com.example.bodytunerdemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

@SuppressLint("ResourceAsColor")
class CustomViewBodyTuner(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var bitmapBody = BitmapFactory.decodeResource(resources, R.drawable.body)
    private var widthView = 0
    private var heightView = 0
    private var newHeight = 0
    private var newWidth = 0
    private var ratioBody = 0F
    private var paintDraw = Paint()
    private var paintLine = Paint()



    init {

        ratioBody = bitmapBody.width / bitmapBody.height.toFloat()
        paintLine.color = R.color.white
        paintLine.strokeWidth = 5f
        paintLine.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = widthView
        val height = heightView

        newWidth = widthView
        newHeight = (newWidth / ratioBody).roundToInt()

        if (newHeight > height) {
            newHeight = height
            newWidth = (newHeight * ratioBody).roundToInt()
        }
        Log.d("huy","width = $newWidth")
        Log.d("huy","height = $newHeight")

        val newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
//        setMeasuredDimension(width, height);
        super.onMeasure(newWidthSpec,newHeightSpec)
        bitmapBody = getResizedBitmap(bitmapBody,newHeight,newWidth)
    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }





    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmapBody!!,matrix,paintDraw)
        canvas?.drawLine(0f, bitmapBody.height / 3f ,bitmapBody.width.toFloat(), bitmapBody.height  / 3f ,paintLine)

        canvas?.drawLine(0f, bitmapBody.height * 2 / 3f ,bitmapBody.width.toFloat(), bitmapBody.height * 2 / 3f ,paintLine)


    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {






        return true
    }


    fun setSizeMaxView(width: Int, height: Int) {
        widthView = width
        heightView = height
        requestLayout()
    }

    private fun getResizedBitmap(bm: Bitmap?, newHeight: Int, newWidth: Int): Bitmap? {
        if (bm == null || newHeight <= 0 || newWidth <= 0) return bm
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
    }


}