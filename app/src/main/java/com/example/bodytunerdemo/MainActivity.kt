package com.example.bodytunerdemo

import android.annotation.SuppressLint
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), View.OnTouchListener, View.OnClickListener {
    private lateinit var bitmapOrigin: Bitmap
    private var wBitmapOrigin = 0
    private var hBitmapOrigin = 0
    private var newWidthBitmapOrigin = 0f
    private var newHeightBitmapOrigin = 0f
    private var listBitmap = ArrayList<Bitmap>()

    lateinit var bitmap1View: Bitmap
    lateinit var bitmap2View: Bitmap
    lateinit var bitmap3View: Bitmap
    lateinit var bitmapBodyNew: Bitmap
    lateinit var bitmapOriginDefault: Bitmap
    private var paint = Paint()
    private var maxHeightZoom = 0f
    private var hNew = 0
    private var progressMax = 0
    private var positionListBitmap = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bitmapOrigin = BitmapFactory.decodeResource(resources, R.drawable.wayner_rooney)
        wBitmapOrigin = bitmapOrigin.width
        hBitmapOrigin = bitmapOrigin.height

        rllCustomView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                maxHeightZoom = rllCustomView.height.toFloat()
                newWidthBitmapOrigin = rllCustomView.width.toFloat()
                newHeightBitmapOrigin =
                    newWidthBitmapOrigin * (hBitmapOrigin / wBitmapOrigin.toFloat())
                if (newHeightBitmapOrigin > rllCustomView.height.toFloat()) {
                    newHeightBitmapOrigin = rllCustomView.height * 0.8.toFloat()
                    newWidthBitmapOrigin =
                        newHeightBitmapOrigin * (wBitmapOrigin / hBitmapOrigin.toFloat())
                }
                bitmapOrigin = Bitmap.createScaledBitmap(
                    bitmapOrigin,
                    newWidthBitmapOrigin.toInt(),
                    newHeightBitmapOrigin.toInt(),
                    true
                )
                imageCustomView.setImageBitmap(bitmapOrigin)
                backGroundCutImage.translationY = newHeightBitmapOrigin * 0.3f
                setTranslateY(bitmapOrigin.width, bitmapOrigin.height)
                bitmapOriginDefault = bitmapOrigin
                bitmapBodyNew = bitmapOrigin
                listBitmap.add(bitmapOriginDefault)
                positionListBitmap++
                cutViewBitmap(rllTop, rllBottom)
                rllCustomView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        seekbarTranslate()
        onTouchView()
        onClick()
    }

    private fun seekbarTranslate() {
        seekbarBodyTuner.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p1 == 0) {
                    imageCustomView.setImageBitmap(bitmapOrigin)
                } else {
                    val bitmap2ViewNew = bitmapScale(bitmap2View, p1)
                    hNew = bitmap1View.height + bitmap2ViewNew.height + bitmap3View.height
                    if (hNew > maxHeightZoom) {
                        hNew = maxHeightZoom.toInt()
                        Log.d("huy", "to qua roi")
                        seekbarBodyTuner.progress = progressMax
                        return
                    }
                    progressMax = seekbarBodyTuner.progress
                    bitmapBodyNew =
                        Bitmap.createBitmap(bitmap1View.width, hNew, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmapBodyNew)
                    canvas.drawBitmap(bitmap1View, 0f, 0f, paint)
                    canvas.drawBitmap(bitmap2ViewNew, 0f, bitmap1View.height.toFloat(), paint)
                    canvas.drawBitmap(
                        bitmap3View,
                        0f,
                        bitmap2ViewNew.height + bitmap1View.height.toFloat(),
                        paint
                    )
                    hideView()
                    backGroundCutImage.layoutParams.height = bitmap2ViewNew.height
                    rllBottom.translationY = (bitmap1View.height + bitmap2ViewNew.height).toFloat() - covertDptoPx(20f)
                    viewBottom.translationY = rllBottom.translationY
                    imageCustomView.setImageBitmap(bitmapBodyNew)
                }
                txtSeekbar.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (positionListBitmap!=0 || positionListBitmap!=listBitmap.size-1){
                    val listBitmapClear = ArrayList<Bitmap>()
                    for (i in 0 until listBitmap.size){
                        if (i <= positionListBitmap){
                            listBitmapClear.add(listBitmap[i])
                        }
                    }

                    listBitmap.clear()
                    listBitmap.addAll(listBitmapClear)
                }

                visibleView()
                iconCompareBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                iconUndoBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                listBitmap.add(bitmapBodyNew)
                positionListBitmap++
            }
        })
    }

    private fun onClick() {
        iconUndoBodyTuner.setOnClickListener(this)
        iconRedoBodyTuner.setOnClickListener(this)

    }

    private fun onTouchView() {
        viewTop.setOnTouchListener(this)
        viewBottom.setOnTouchListener(this)
        iconCompareBodyTuner.setOnTouchListener(this)
    }

    private fun visibleView() {
        rllBottom.visibility = View.VISIBLE
        rllTop.visibility = View.VISIBLE
    }

    private fun hideView() {
        rllBottom.visibility = View.INVISIBLE
        rllTop.visibility = View.INVISIBLE
    }

    fun setTranslateY(widthBitmap: Int, heightBitmap: Int) {
        backGroundCutImage.layoutParams.height = (heightBitmap * 0.3f).toInt()
        backGroundCutImage.translationY = (heightBitmap * 0.3f)
        backGroundCutImage.layoutParams.width = widthBitmap
        rllTop.layoutParams.width = widthBitmap
        viewTop.layoutParams.width = widthBitmap
        viewBottom.layoutParams.width = widthBitmap
        rllTop.translationY = backGroundCutImage.translationY - covertDptoPx(20f)
        rllBottom.layoutParams.width = widthBitmap
        rllBottom.translationY = backGroundCutImage.translationY + backGroundCutImage.layoutParams.height - covertDptoPx(20f)
        viewTop.translationY = rllTop.translationY
        viewBottom.translationY = rllBottom.translationY
    }

    fun bitmapScale(bitmap: Bitmap, percent: Int): Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width,
            ((bitmap.height * (1 + percent * 0.01)).toInt()),
            true
        )
    }

    private fun cutViewBitmap(rllTop: View, rllBottom: View) {
        try {
            bitmap1View = Bitmap.createBitmap(
                bitmapOrigin,
                0,
                0,
                bitmapOrigin.width,
                backGroundCutImage.translationY.roundToInt()
            )

            bitmap2View = Bitmap.createBitmap(
                bitmapOrigin,
                0,
                backGroundCutImage.translationY.toInt(),
                bitmapOrigin.width,
                (rllBottom.translationY - rllTop.translationY).roundToInt()
            )

            val hBitmap3 = bitmapOrigin.height - bitmap1View.height - bitmap2View.height
            bitmap3View = Bitmap.createBitmap(
                bitmapOrigin,
                0,
                (rllBottom.translationY.toInt() + covertDptoPx(20f)).toInt(),
                bitmapOrigin.width, hBitmap3

            )
        }catch(Ex:Exception){
            Log.d("huy","lỗi rồi")
        }
    }

    private fun covertDptoPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

    private var pointRllTop = PointF(0f, 0f)
    private var pointRllBottom = PointF(0f, 0f)
    private var startTranslateTopY = 0f
    private var startTranslateBottomY = 0f
    private var distanceTopY = 0f
    private var distanceBottomY = 0f
    private var newTranslateTopY = 0f
    private var newTranslateBottomY = 0f
    private var hBgCutImage = 0f
    private var hNewCusImage = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return if (event!!.pointerCount > 1) {
            Log.d("huy", "unseleted mutiTouch")
            true
        } else {
            when (v!!) {
                viewTop -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d("huy", "${listBitmap.size}")
                            bitmapOrigin = bitmapBodyNew
                            backGroundCutImage.visibility = View.VISIBLE
                            startTranslateTopY = event.y
                            hBgCutImage = (backGroundCutImage.layoutParams.height).toFloat()
                            pointRllTop = PointF(rllTop.translationX, rllTop.translationY)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            distanceTopY = event.y - startTranslateTopY
                            newTranslateTopY = pointRllTop.y + distanceTopY

                            if (newTranslateTopY < covertDptoPx(-20f)) {
                                newTranslateTopY = covertDptoPx(-20f)
                                hNewCusImage = rllBottom.translationY + covertDptoPx(20f)
                            } else {
                                hNewCusImage = hBgCutImage - distanceTopY
                                if (newTranslateTopY + covertDptoPx(40f) > rllBottom.translationY) {
                                    newTranslateTopY = rllBottom.translationY - covertDptoPx(40f)
                                    hNewCusImage = covertDptoPx(40f)
                                }
                            }
                            Log.d("huy", "$distanceTopY ")
                            rllTop.translationY = newTranslateTopY
                            backGroundCutImage.translationY = newTranslateTopY + covertDptoPx(20f)

                            backGroundCutImage.layoutParams.height = hNewCusImage.toInt()
                            Log.d("huy", "${backGroundCutImage.height}")

                            backGroundCutImage.invalidate()
                            backGroundCutImage.requestLayout()
                        }
                        MotionEvent.ACTION_UP -> {
                            if (backGroundCutImage.translationY == 0f) {
                                backGroundCutImage.translationY = 1f
                            }
                            seekbarBodyTuner.progress = 0
                            backGroundCutImage.visibility = View.INVISIBLE
                            viewTop.translationY = rllTop.translationY
                            cutViewBitmap(rllTop, rllBottom)
                        }
                    }
                }
                viewBottom -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            bitmapOrigin = bitmapBodyNew
                            backGroundCutImage.visibility = View.VISIBLE
                            startTranslateBottomY = event.y
                            hBgCutImage = (backGroundCutImage.layoutParams.height).toFloat()
                            Log.d("huy", "hNewBgCutImage = $hBgCutImage")
                            pointRllBottom = PointF(rllBottom.translationX, rllBottom.translationY)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            distanceBottomY = event.y - startTranslateBottomY
                            Log.d("huy", "$distanceBottomY")
                            newTranslateBottomY = pointRllBottom.y + distanceBottomY
                            Log.d("huy", "$newTranslateBottomY")

                            if (newTranslateBottomY + covertDptoPx(20f) > bitmapOrigin.height) {
                                newTranslateBottomY =
                                    bitmapOrigin.height.toFloat() - covertDptoPx(21f)
                                hNewCusImage =
                                    bitmapOrigin.height - (rllTop.translationY + covertDptoPx(20f))
                            } else {
                                hNewCusImage = hBgCutImage + distanceBottomY
                                if (newTranslateBottomY - covertDptoPx(40f) < rllTop.translationY) {
                                    newTranslateBottomY = rllTop.translationY + covertDptoPx(40f)
                                    hNewCusImage = covertDptoPx(40f)
                                }
                            }
                            rllBottom.translationY = newTranslateBottomY
                            backGroundCutImage.layoutParams.height = hNewCusImage.toInt()
                            backGroundCutImage.invalidate()
                            backGroundCutImage.requestLayout()
                        }
                        MotionEvent.ACTION_UP -> {
                            seekbarBodyTuner.progress = 0
                            backGroundCutImage.visibility = View.INVISIBLE
                            viewBottom.translationY = rllBottom.translationY
                            cutViewBitmap(rllTop, rllBottom)
                        }
                    }
                }

                iconCompareBodyTuner -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            imageCustomView.setImageBitmap(bitmapOriginDefault)
                            hideView()
                        }
                        MotionEvent.ACTION_UP -> {
                            visibleView()
                            imageCustomView.setImageBitmap(bitmapBodyNew)
                        }
                    }
                }
            }
            true
        }
    }

    override fun onClick(v: View?) {
        when (v!!) {
            iconUndoBodyTuner -> {
                if (positionListBitmap > 0) {
                    positionListBitmap--
                    imageCustomView.setImageBitmap(listBitmap[positionListBitmap])

                    bitmapOrigin = listBitmap[positionListBitmap]
                    bitmapBodyNew = listBitmap[positionListBitmap]
                    seekbarBodyTuner.progress = 0
                    setTranslateY(
                        listBitmap[positionListBitmap].width,
                        listBitmap[positionListBitmap].height
                    )
                    cutViewBitmap(rllTop, rllBottom)
                    iconRedoBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                }
                if (positionListBitmap == 0){
                    iconUndoBodyTuner.setColorFilter(Color.parseColor("#8893cb"))
                    iconCompareBodyTuner.setColorFilter(Color.parseColor("#8893cb"))
                }
            }

            iconRedoBodyTuner -> {
                if (positionListBitmap < listBitmap.size - 1) {
                    positionListBitmap++
                    imageCustomView.setImageBitmap(listBitmap[positionListBitmap])

                    bitmapOrigin = listBitmap[positionListBitmap]
                    bitmapBodyNew = listBitmap[positionListBitmap]
                    seekbarBodyTuner.progress = 0
                    setTranslateY(
                        listBitmap[positionListBitmap].width,
                        listBitmap[positionListBitmap].height
                    )
                    cutViewBitmap(rllTop, rllBottom)
                    iconUndoBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                    iconCompareBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                }
                if (positionListBitmap == listBitmap.size -1){
                    iconRedoBodyTuner.setColorFilter(Color.parseColor("#8893cb"))
                }

            }
        }
    }
}