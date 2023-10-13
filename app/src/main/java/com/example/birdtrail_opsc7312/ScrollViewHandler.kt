package com.example.birdtrail_opsc7312

import android.widget.LinearLayout
import android.widget.Space
import androidx.fragment.app.FragmentActivity

class ScrollViewHandler {

    fun generateSpacer(activityLayout: LinearLayout, thisFragment: FragmentActivity, spaceSize: Int)
    {
        val scale = thisFragment.resources.displayMetrics.density
        val pixels = (spaceSize * scale + 0.5f)
        val spacer = Space(thisFragment)
        spacer.minimumHeight = pixels.toInt()
        activityLayout.addView(spacer)
    }

}