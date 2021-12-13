package g3.module.bodytunermagic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.layout_body_tuner_magic.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.roundToInt

class BodyTunerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs),
    View.OnTouchListener {
    private var bitmapOrigin: Bitmap? = null
    private var bitmapRealDefault: Bitmap? = null
    private var wBitmapOrigin = 0
    private var hBitmapOrigin = 0
    private var newWidthBitmapOrigin = 0f
    private var newHeightBitmapOrigin = 0f
    private var wBitmapReal = 0
    private var hBitmapReal = 0
    private var listBitmapView = ArrayList<Bitmap>()
    private var listBitmapReal = ArrayList<Bitmap>()
    private lateinit var bitmap1View: Bitmap
    private lateinit var bitmap2View: Bitmap
    private lateinit var bitmap3View: Bitmap
    private lateinit var bitmap1Real: Bitmap
    private lateinit var bitmap2Real: Bitmap
    private lateinit var bitmap3Real: Bitmap
    private lateinit var bitmapBodyNewView: Bitmap
    private lateinit var bitmapBodyNewReal: Bitmap
    private lateinit var bitmap2ViewNew: Bitmap
    private lateinit var bitmap2NewReal: Bitmap
    private lateinit var bitmapOriginDefault: Bitmap
    private var bodyTunerListener: BodyTunerListener? = null
    private var paint = Paint()
    private var maxHeightZoomView = 0f
    private var maxHeightZoomReal = 0f
    private var hNewView = 0
    private var hNewReal = 0
    private var progressMax = 0
    private var positionListBitmap = -1
    private var hIcon = 0
    private var wIcon = 0
    private var progressNew = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_body_tuner_magic, this, true)
    }

    fun setBitmap(bitmap: Bitmap?) {
        bitmapOrigin = bitmap
        bitmapRealDefault = bitmap
        wBitmapOrigin = bitmapOrigin!!.width
        hBitmapOrigin = bitmapOrigin!!.height
        wBitmapReal = wBitmapOrigin
        hBitmapReal = hBitmapOrigin
        Log.d("huy", "$wBitmapReal")
        Log.d("huy", "$hBitmapReal")
        getSizeViewDraw()
        seekbarTranslate()
        onTouchView()
        onClick()
    }

    private fun getSizeViewDraw() {
        rllCustomView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                maxHeightZoomView = rllCustomView.height.toFloat()
                newWidthBitmapOrigin = rllCustomView.width.toFloat()
                newHeightBitmapOrigin =
                    newWidthBitmapOrigin * (hBitmapOrigin / wBitmapOrigin.toFloat())
                if (newHeightBitmapOrigin > rllCustomView.height.toFloat()) {
                    newHeightBitmapOrigin = rllCustomView.height * 0.9.toFloat()
                    newWidthBitmapOrigin =
                        newHeightBitmapOrigin * (wBitmapOrigin / hBitmapOrigin.toFloat())
                }
                bitmapOrigin = bitmapOrigin?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        newWidthBitmapOrigin.toInt(),
                        newHeightBitmapOrigin.toInt(),
                        true
                    )
                }
                bitmapOriginDefault = bitmapOrigin!!
                seekbarBodyTuner.progress = 50
                defaultTranslateView()
                rllCustomView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun seekbarTranslate() {
        seekbarBodyTuner.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                if (progress == 50) {
                    progressNew = 0
                    imageCustomView.setImageBitmap(bitmapOrigin)
                } else {
                    progressNew = (50 - progress) * (-1)
                    bitmapDraw(progressNew)
                }
                txtSeekbar.text = progressNew.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (positionListBitmap != 0 || positionListBitmap != listBitmapView.size - 1) {
                    val listBitmapClearView = ArrayList<Bitmap>()
                    val listBitmapClearReal = ArrayList<Bitmap>()
                    for (i in 0 until listBitmapView.size) {
                        if (i <= positionListBitmap) {
                            listBitmapClearView.add(listBitmapView[i])
                            listBitmapClearReal.add(listBitmapReal[i])
                        }
                    }
                    listBitmapView.clear()
                    listBitmapReal.clear()
                    listBitmapView.addAll(listBitmapClearView)
                    listBitmapReal.addAll(listBitmapClearReal)
                }

                visibleView()
                txtMaxSize.visibility = View.GONE
                iconCompareBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                iconUndoBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                listBitmapView.add(bitmapBodyNewView)
                listBitmapReal.add(bitmapBodyNewReal)
                positionListBitmap++
            }
        })
    }


    private fun bitmapDraw(progress: Int) {
        bitmap2ViewNew = bitmapScale(bitmap2View, progress)
        bitmap2NewReal = bitmapScale(bitmap2Real, progress)
        hNewView = bitmap1View.height + bitmap2ViewNew.height + bitmap3View.height
        hNewReal = bitmap1Real.height + bitmap2NewReal.height + bitmap3Real.height

        if (hNewView > maxHeightZoomView) {
            hNewView = maxHeightZoomView.toInt()
            hNewReal = maxHeightZoomReal.toInt()
            seekbarBodyTuner.progress = progressMax
            txtMaxSize.visibility = View.VISIBLE
            return
        }
        progressMax = seekbarBodyTuner.progress
        bitmapBodyNewView =
            Bitmap.createBitmap(bitmapOrigin!!.width, hNewView, Bitmap.Config.ARGB_8888)
        bitmapBodyNewReal = Bitmap.createBitmap(
            bitmapRealDefault!!.width,
            hNewReal,
            Bitmap.Config.ARGB_8888
        )
        drawBitmapView(bitmapBodyNewView)
        GlobalScope.launch {
            drawBitmapReal(bitmapBodyNewReal)
        }
        hideView()
        backGroundCutImage.layoutParams.height = bitmap2ViewNew.height
        viewBackgroundCutImage.layoutParams.height = bitmap2ViewNew.height
        rllBottom.translationY =
            (bitmap1View.height + bitmap2ViewNew.height).toFloat() - (hIcon / 2)
        viewBottom.translationY = rllBottom.translationY
        imageCustomView.setImageBitmap(bitmapBodyNewView)
    }


    private fun defaultTranslateView() {
        hIcon = (rllCustomView.width.toFloat() * 0.1).toInt()
        wIcon = (rllCustomView.width.toFloat() * 0.1).toInt()
        iconTop.layoutParams.width = wIcon
        iconTop.layoutParams.height = hIcon
        iconTop.requestLayout()
        iconBottom.layoutParams.width = wIcon
        iconBottom.layoutParams.height = hIcon
        iconBottom.requestLayout()
        viewTop.layoutParams.height = hIcon
        viewBottom.layoutParams.height = hIcon
        Log.d("huy", "hIco = $hIcon")
        Log.d("huy", "wIco = $wIcon")
        imageCustomView.setImageBitmap(bitmapOrigin)
        setTranslateY(bitmapOrigin!!.width, bitmapOrigin!!.height)
        bitmapBodyNewView = bitmapOrigin!!
        bitmapBodyNewReal = bitmapRealDefault!!
        listBitmapView.add(bitmapOriginDefault)
        listBitmapReal.add(bitmapRealDefault!!)
        positionListBitmap++
        cutViewBitmap(rllTop, rllBottom)
        cutViewReal()
        bitmap2ViewNew = bitmap2View
        bitmap2NewReal = bitmap2Real
    }

    private fun drawBitmapReal(bitmap: Bitmap) {
        val canvasBitmapReal = Canvas(bitmap)
        canvasBitmapReal.drawBitmap(bitmap1Real, 0f, 0f, paint)
        canvasBitmapReal.drawBitmap(bitmap2NewReal, 0f, bitmap1Real.height.toFloat(), paint)
        canvasBitmapReal.drawBitmap(
            bitmap3Real,
            0f,
            bitmap2NewReal.height + bitmap1Real.height.toFloat(),
            paint
        )
    }

    private fun drawBitmapView(bitmap: Bitmap) {
        val canvasBitmapView = Canvas(bitmap)
        canvasBitmapView.drawBitmap(bitmap1View, 0f, 0f, paint)
        canvasBitmapView.drawBitmap(bitmap2ViewNew, 0f, bitmap1View.height.toFloat(), paint)
        canvasBitmapView.drawBitmap(
            bitmap3View,
            0f,
            bitmap2ViewNew.height + bitmap1View.height.toFloat(),
            paint
        )
    }

    private fun onClick() {
        LibClickAnimUtils.setOnCustomTouchViewScale(
            iconUndoBodyTuner,
            object : OnCustomClickListener {
                override fun onCustomClick(v: View?, event: MotionEvent?) {
                    if (positionListBitmap > 0) {
                        positionListBitmap--
                        imageCustomView.setImageBitmap(listBitmapView[positionListBitmap])

                        bitmapOrigin = listBitmapView[positionListBitmap]
                        bitmapBodyNewView = listBitmapView[positionListBitmap]
                        bitmapRealDefault = listBitmapReal[positionListBitmap]
                        bitmapBodyNewReal = listBitmapReal[positionListBitmap]
                        seekbarBodyTuner.progress = 50
                        setTranslateY(
                            listBitmapView[positionListBitmap].width,
                            listBitmapView[positionListBitmap].height
                        )
                        GlobalScope.launch {
                            cutViewBitmap(rllTop, rllBottom)
                            cutViewReal()
                        }
                        iconRedoBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                    }
                    if (positionListBitmap == 0) {
                        iconUndoBodyTuner.setColorFilter(Color.parseColor("#8893cb"))
                        iconCompareBodyTuner.setColorFilter(Color.parseColor("#8893cb"))
                    }
                }
            })

        LibClickAnimUtils.setOnCustomTouchViewScale(
            iconRedoBodyTuner,
            object : OnCustomClickListener {
                override fun onCustomClick(v: View?, event: MotionEvent?) {
                    if (positionListBitmap < listBitmapView.size - 1) {
                        positionListBitmap++
                        imageCustomView.setImageBitmap(listBitmapView[positionListBitmap])

                        bitmapOrigin = listBitmapView[positionListBitmap]
                        bitmapBodyNewView = listBitmapView[positionListBitmap]
                        bitmapRealDefault = listBitmapReal[positionListBitmap]
                        bitmapBodyNewReal = listBitmapReal[positionListBitmap]
                        seekbarBodyTuner.progress = 50
                        setTranslateY(
                            listBitmapView[positionListBitmap].width,
                            listBitmapView[positionListBitmap].height
                        )
                        GlobalScope.launch {
                            cutViewBitmap(rllTop, rllBottom)
                            cutViewReal()
                        }
                        iconUndoBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                        iconCompareBodyTuner.setColorFilter(Color.parseColor("#8167fd"))
                    }
                    if (positionListBitmap == listBitmapView.size - 1) {
                        iconRedoBodyTuner.setColorFilter(Color.parseColor("#8893cb"))
                    }
                }
            })

        LibClickAnimUtils.setOnCustomTouchViewScale(
            iconSaveBodyTunerMagic,
            object : OnCustomClickListener {
                override fun onCustomClick(v: View?, event: MotionEvent?) {
                    FileUtilsEditor(context) {
                        Log.d("huy", "sucesss")
                    }.saveExternal(
                        bitmapBodyNewReal,
                        System.currentTimeMillis().toString() + "",
                        ".jpg",
                        "image/jpeg",
                        "huy"
                    ) { path: String? ->

                    }
                    Log.d("huy", "click")
                }
            })

        LibClickAnimUtils.setOnCustomTouchViewScale(
            iconCloseBodyTunerMagic,
            object : OnCustomClickListener {
                override fun onCustomClick(v: View?, event: MotionEvent?) {
                    bodyTunerListener?.onCloseBodyTuner()
                }
            })

    }

    private fun onTouchView() {
        viewBackgroundCutImage.setOnTouchListener(this)
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
        backGroundCutImage.layoutParams.height = (heightBitmap * 0.2f).toInt()
        viewBackgroundCutImage.layoutParams.height = (heightBitmap * 0.2f).toInt()
        backGroundCutImage.translationY = (heightBitmap * 0.2f)
        viewBackgroundCutImage.translationY = backGroundCutImage.translationY
        backGroundCutImage.layoutParams.width = widthBitmap
        viewBackgroundCutImage.layoutParams.width = widthBitmap
        rllTop.layoutParams.width = widthBitmap
        viewTop.layoutParams.width = widthBitmap
        viewBottom.layoutParams.width = widthBitmap
        rllTop.translationY = backGroundCutImage.translationY - (hIcon / 2f)
        rllBottom.layoutParams.width = widthBitmap
        rllBottom.translationY =
            backGroundCutImage.translationY + backGroundCutImage.layoutParams.height - (hIcon / 2f)
        viewTop.translationY = rllTop.translationY
        viewBottom.translationY = rllBottom.translationY
    }

    private fun bitmapScale(bitmap: Bitmap, percent: Int): Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width,
            ((bitmap.height * (1 + percent * 0.01)).toInt()),
            true
        )
    }

    private fun bitmapRealScale(bitmap: Bitmap, percent: Int): Bitmap {
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
                bitmapOrigin!!,
                0,
                0,
                bitmapOrigin!!.width,
                backGroundCutImage.translationY.roundToInt()
            )

            bitmap2View = Bitmap.createBitmap(
                bitmapOrigin!!,
                0,
                backGroundCutImage.translationY.toInt(),
                bitmapOrigin!!.width,
                (rllBottom.translationY - rllTop.translationY).roundToInt()
            )

            val hBitmap3 = bitmapOrigin!!.height - bitmap1View.height - bitmap2View.height
            bitmap3View = Bitmap.createBitmap(
                bitmapOrigin!!,
                0,
                (rllBottom.translationY.toInt() + (hIcon / 2)),
                bitmapOrigin!!.width, hBitmap3

            )
        } catch (Ex: Exception) {
            Log.d("huy", "lỗi rồi")
        }
    }

    private fun cutViewReal() {
        try {
            val ratioBitmap1View = bitmap1View.width / bitmap1View.height.toFloat()
            val hBitmap1Real = wBitmapReal / ratioBitmap1View
            val ratioBitmap2View = bitmap2View.width / bitmap2View.height.toFloat()
            val hBitmap2Real = wBitmapReal / ratioBitmap2View

            bitmap1Real = Bitmap.createBitmap(
                bitmapRealDefault!!,
                0,
                0,
                bitmapRealDefault!!.width,
                hBitmap1Real.roundToInt()
            )
            bitmap2Real = Bitmap.createBitmap(
                bitmapRealDefault!!,
                0,
                bitmap1Real.height,
                bitmapRealDefault!!.width,
                hBitmap2Real.roundToInt()
            )
            val hBitmap3Real = bitmapRealDefault!!.height - bitmap1Real.height - bitmap2Real.height
            bitmap3Real = Bitmap.createBitmap(
                bitmapRealDefault!!,
                0,
                bitmap1Real.height + bitmap2Real.height,
                bitmapRealDefault!!.width,
                hBitmap3Real
            )
        } catch (EX: Exception) {
            Log.d("huy", "lỗi rồi")
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
    private var pointBackGroundCutImage = PointF(0f, 0f)
    private var startTranslateTopY = 0f
    private var startTranslateBottomY = 0f
    private var distanceTopY = 0f
    private var distanceBottomY = 0f
    private var newTranslateTopY = 0f
    private var newTranslateBottomY = 0f
    private var hBgCutImage = 0f
    private var hNewCusImage = 0f
    private var startTranslateBackGroundCutImageY = 0f
    private var translateBackGroundCutImageY = 0f
    private var newTranslateBackGroundCutImageY = 0f


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
                            Log.d("huy", "${listBitmapView.size}")
                            bitmapOrigin = bitmapBodyNewView
                            bitmapRealDefault = bitmapBodyNewReal
                            backGroundCutImage.visibility = View.VISIBLE
                            startTranslateTopY = event.y
                            hBgCutImage = (backGroundCutImage.layoutParams.height).toFloat()
                            pointRllTop = PointF(rllTop.translationX, rllTop.translationY)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            distanceTopY = event.y - startTranslateTopY
                            newTranslateTopY = pointRllTop.y + distanceTopY

                            if (newTranslateTopY < (-hIcon / 2)) {
                                newTranslateTopY = -hIcon / 2f
                                hNewCusImage = rllBottom.translationY + (hIcon / 2)
                            } else {
                                hNewCusImage = hBgCutImage - distanceTopY
                                if (newTranslateTopY + hIcon > rllBottom.translationY) {
                                    newTranslateTopY = rllBottom.translationY - hIcon
                                    hNewCusImage = hIcon.toFloat()
                                }
                            }
                            rllTop.translationY = newTranslateTopY
                            backGroundCutImage.translationY = newTranslateTopY + (hIcon / 2)
                            backGroundCutImage.layoutParams.height = hNewCusImage.toInt()
                            viewBackgroundCutImage.translationY = backGroundCutImage.translationY
                            viewBackgroundCutImage.layoutParams.height = hNewCusImage.toInt()
                            Log.d("huy", "${backGroundCutImage.height}")
                            rllTop.invalidate()
                            rllTop.requestLayout()
                            backGroundCutImage.invalidate()
                            backGroundCutImage.requestLayout()
                        }
                        MotionEvent.ACTION_UP -> {
                            if (backGroundCutImage.translationY == 0f) {
                                backGroundCutImage.translationY = 1f
                            }
                            seekbarBodyTuner.progress = 50
                            backGroundCutImage.visibility = View.INVISIBLE
                            viewTop.translationY = rllTop.translationY
                            GlobalScope.launch {
                                cutViewBitmap(rllTop, rllBottom)
                                cutViewReal()
                            }
                        }
                    }
                }
                viewBottom -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            bitmapOrigin = bitmapBodyNewView
                            bitmapRealDefault = bitmapBodyNewReal
                            backGroundCutImage.visibility = View.VISIBLE
                            startTranslateBottomY = event.y
                            hBgCutImage = (backGroundCutImage.layoutParams.height).toFloat()
                            pointRllBottom = PointF(rllBottom.translationX, rllBottom.translationY)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            distanceBottomY = event.y - startTranslateBottomY
                            Log.d("huy", "$distanceBottomY")
                            newTranslateBottomY = pointRllBottom.y + distanceBottomY
                            Log.d("huy", "$newTranslateBottomY")

                            if (newTranslateBottomY + (hIcon / 2) > bitmapOrigin!!.height) {
                                newTranslateBottomY =
                                    bitmapOrigin!!.height.toFloat() - (hIcon / 2 + covertDptoPx(1f))
                                hNewCusImage =
                                    bitmapOrigin!!.height - (rllTop.translationY + (hIcon / 2))
                            } else {
                                hNewCusImage = hBgCutImage + distanceBottomY
                                if (newTranslateBottomY - hIcon < rllTop.translationY) {
                                    newTranslateBottomY = rllTop.translationY + hIcon
                                    hNewCusImage = hIcon.toFloat()
                                }
                            }
                            rllBottom.translationY = newTranslateBottomY
                            backGroundCutImage.layoutParams.height = hNewCusImage.toInt()
                            viewBackgroundCutImage.layoutParams.height = hNewCusImage.toInt()
                            rllBottom.invalidate()
                            rllBottom.requestLayout()
                            backGroundCutImage.invalidate()
                            backGroundCutImage.requestLayout()
                        }
                        MotionEvent.ACTION_UP -> {
                            seekbarBodyTuner.progress = 50
                            backGroundCutImage.visibility = View.INVISIBLE
                            viewBottom.translationY = rllBottom.translationY
                            GlobalScope.launch {
                                cutViewBitmap(rllTop, rllBottom)
                                cutViewReal()
                            }

                        }
                    }
                }

                viewBackgroundCutImage -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            bitmapOrigin = bitmapBodyNewView
                            bitmapRealDefault = bitmapBodyNewReal
                            backGroundCutImage.visibility = View.VISIBLE
                            rllTop.visibility = View.INVISIBLE
                            rllBottom.visibility = View.INVISIBLE
                            hBgCutImage = (backGroundCutImage.layoutParams.height).toFloat()
                            pointBackGroundCutImage = PointF(
                                backGroundCutImage.translationX,
                                backGroundCutImage.translationY
                            )
                            startTranslateBackGroundCutImageY = event.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            translateBackGroundCutImageY =
                                event.y - startTranslateBackGroundCutImageY
                            newTranslateBackGroundCutImageY =
                                pointBackGroundCutImage.y + translateBackGroundCutImageY

                            if (newTranslateBackGroundCutImageY <= 0f) {
                                newTranslateBackGroundCutImageY = 1f
                            }
                            if (newTranslateBackGroundCutImageY + hBgCutImage >= bitmapOrigin!!.height) {
                                newTranslateBackGroundCutImageY =
                                    bitmapOrigin!!.height - hBgCutImage
                            }
                            backGroundCutImage.translationY = newTranslateBackGroundCutImageY
                            rllTop.translationY = backGroundCutImage.translationY - (hIcon / 2)
                            viewTop.translationY = rllTop.translationY
                            rllBottom.translationY =
                                backGroundCutImage.translationY - (hIcon / 2 + covertDptoPx(1f)) + backGroundCutImage.layoutParams.height
                            viewBottom.translationY = rllBottom.translationY
                        }
                        MotionEvent.ACTION_UP -> {
                            seekbarBodyTuner.progress = 50
                            rllTop.visibility = View.VISIBLE
                            rllBottom.visibility = View.VISIBLE
                            cutViewBitmap(rllTop, rllBottom)
                            cutViewReal()
                            viewBackgroundCutImage.translationY = backGroundCutImage.translationY
                            backGroundCutImage.visibility = View.INVISIBLE
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
                            imageCustomView.setImageBitmap(bitmapBodyNewView)
                        }
                    }
                }
            }
            true
        }
    }
}