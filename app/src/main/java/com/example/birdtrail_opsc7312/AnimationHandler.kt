package com.example.birdtrail_opsc7312

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout

class AnimationHandler {

    //---------------------------------------------------------------------------------------------
    //method to animate a component on the x axis
    //---------------------------------------------------------------------------------------------
    fun animateX (componentToAnimate: View, positionToAnimateTo: Float) {

        //animate the components movement
        ObjectAnimator.ofFloat(componentToAnimate, "translationX", positionToAnimateTo).apply {

            //set the length of the animation
            duration = 300
            start()
        }
    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to get the current ui elements position on the screen
    //---------------------------------------------------------------------------------------------
    fun getPositionInRelationToScreen(componentToAnimate: View) : Point
    {
        //get current display metrics
        val displayMetrics = DisplayMetrics()

        //get screen width
        val width = displayMetrics.widthPixels

        //get the screen height
        val height = displayMetrics.widthPixels

        //get the components new X position on the screen
        val moveToPositionX = (width - componentToAnimate.width)

        //get the components new Y position on the screen
        val moveToPositionY = (height - componentToAnimate.height)

        //return the components new position as a point
        return Point(moveToPositionX, moveToPositionY)

    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to animate the change in size of component
    //---------------------------------------------------------------------------------------------
    fun animateMenu(menuRelativeLayout: RelativeLayout, startInt: Int, endInt: Int)
    {
        //new value animator
        val va = ValueAnimator.ofInt(startInt, endInt)

        //set the animation duration
        va.duration = 300

        //set update listener to modify the size of the menu
        va.addUpdateListener { animation ->
            //get the animation value
            val value = animation.animatedValue as Int

            //define the menus new height
            menuRelativeLayout.layoutParams.height = value

            //set the menus new height
            menuRelativeLayout.requestLayout()
        }

        //start the menu expansion animation
        va.start()
    }
    //---------------------------------------------------------------------------------------------
}