package g3.module.bodytunermagic

import android.graphics.Bitmap

interface BodyTunerListener {
    fun onCloseBodyTuner()
    fun onApplyBodyTuner(bitmap: Bitmap)
}