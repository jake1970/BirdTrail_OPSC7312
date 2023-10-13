package com.example.birdtrail_opsc7312

import android.animation.ObjectAnimator
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Size
import android.view.View

class AnimationHandler {

    fun animateX (componentToAnimate: View, positionToAnimateTo: Float) {

        ObjectAnimator.ofFloat(componentToAnimate, "translationX", positionToAnimateTo).apply {
            duration = 300
            start()
        }

    }


    fun getPositionInRelationToScreen(componentToAnimate: View) : Point
    {
        val displayMetrics = DisplayMetrics()
        val width = displayMetrics.widthPixels
        val height = displayMetrics.widthPixels

        val moveToPositionX = (width - componentToAnimate.width)
        val moveToPositionY = (height - componentToAnimate.height)

        val moveToPosition = Point(moveToPositionX, moveToPositionY)

        return moveToPosition

    }


}