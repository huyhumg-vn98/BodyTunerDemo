package g3.module.bodytunermagic

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class LibClickAnimUtils {
    companion object {
        @JvmStatic
        fun setScaleWhenClick(v: View) {
            com.github.florent37.viewanimator.ViewAnimator.animate(v).scale(0.9f, 1f).duration(200).start()
        }

        @JvmStatic
        fun setOnCustomTouchView(
            view: View,
            onCustomTouchListener: OnCustomTouchListener?
        ) {
            view.setOnTouchListener(object : OnTouchListener {
                private var rect: Rect? = null
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (onCustomTouchListener == null) return false
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        rect = Rect(v.left, v.top, v.right, v.bottom)
                        onCustomTouchListener.onCustomTouchDown(v, event)
                    } else if (rect != null && !rect!!.contains(v.left + event.x.toInt(), v.top + event.y.toInt())) {
                        onCustomTouchListener.onCustomTouchMoveOut(v, event)
                        return false
                    } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                        onCustomTouchListener.onCustomTouchUp(v, event)
                    } else {
                        onCustomTouchListener.onCustomTouchOther(v, event)
                    }
                    return true
                }
            })
        }


        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun setOnCustomTouchViewScale(
            view: View,
            customClickListener: OnCustomClickListener?
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                private fun setScale(scale: Float) {
                    view.scaleX = scale
                    view.scaleY = scale
                }

                override fun onCustomTouchDown(v: View?, event: MotionEvent?) {
                    setScale(0.9f)
                }

                override fun onCustomTouchMoveOut(v: View?, event: MotionEvent?) {
                    setScale(1f)
                }

                override fun onCustomTouchUp(v: View?, event: MotionEvent?) {
                    setScale(1f)
                    customClickListener?.onCustomClick(v, event)
                }

                override fun onCustomTouchOther(v: View?, event: MotionEvent?) {
                    setScale(1f)
                }
            })
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun setOnCustomTouchViewScaleNotOther(
            view: View,
            customClickListener: OnCustomClickListener?
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                private fun setScale(scale: Float) {
                    view.scaleX = scale
                    view.scaleY = scale
                }

                override fun onCustomTouchDown(v: View?, event: MotionEvent?) {
                    setScale(0.9f)
                }

                override fun onCustomTouchMoveOut(v: View?, event: MotionEvent?) {
                    setScale(1f)
                }

                override fun onCustomTouchUp(v: View?, event: MotionEvent?) {
                    setScale(1f)
                    customClickListener?.onCustomClick(v, event)
                }

                override fun onCustomTouchOther(v: View?, event: MotionEvent?) {
                }
            })
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun setOnCustomTouchViewScaleNotOtherWithUnit(
            view: View,
            customClickListener: OnCustomClickListener?,
            touchDown: () -> Unit,
            touchUp: () -> Unit,
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                private fun setScale(scale: Float) {
                    view.scaleX = scale
                    view.scaleY = scale
                }

                override fun onCustomTouchDown(v: View?, event: MotionEvent?) {
                    setScale(0.9f)
                    touchDown()
                }

                override fun onCustomTouchMoveOut(v: View?, event: MotionEvent?) {
                    setScale(1f)
                    touchUp()
                }

                override fun onCustomTouchUp(v: View?, event: MotionEvent?) {
                    setScale(1f)
                    touchUp()
                    customClickListener?.onCustomClick(v, event)
                }

                override fun onCustomTouchOther(v: View?, event: MotionEvent?) {
                }
            })
        }

        //----------------------------------------------------------------------------------------------

        @JvmStatic
        fun setOnCustomTouchViewAlphaNotOther(
            view: View,
            customClickListener: OnCustomClickListener?
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                var isTouchDown = false
                var isTouchMoveOutAndUp = false
                private fun setAlpha(alpha: Float) {
                    view.alpha = alpha
                }

                override fun onCustomTouchDown(v: View?, event: MotionEvent?) {
                    isTouchDown = true
                    setAlpha(0.7f)
                }

                override fun onCustomTouchMoveOut(v: View?, event: MotionEvent?) {
                    isTouchMoveOutAndUp = true
                    setAlpha(1f)
                }

                override fun onCustomTouchUp(v: View?, event: MotionEvent?) {
                    isTouchMoveOutAndUp = true
                    setAlpha(1f)
                    customClickListener?.onCustomClick(v, event)
                }

                override fun onCustomTouchOther(v: View?, event: MotionEvent?) {
                    if (!isTouchMoveOutAndUp && isTouchDown) setAlpha(1f)
                    isTouchDown = false
                    isTouchMoveOutAndUp = false
                }
            })
        }
    }
}

