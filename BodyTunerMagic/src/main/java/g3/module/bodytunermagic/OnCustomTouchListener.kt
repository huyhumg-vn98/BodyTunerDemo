package g3.module.bodytunermagic

import android.view.MotionEvent
import android.view.View

interface OnCustomTouchListener {
    fun onCustomTouchDown(v: View?, event: MotionEvent?)
    fun onCustomTouchMoveOut(v: View?, event: MotionEvent?)
    fun onCustomTouchUp(v: View?, event: MotionEvent?)
    fun onCustomTouchOther(v: View?, event: MotionEvent?)
}