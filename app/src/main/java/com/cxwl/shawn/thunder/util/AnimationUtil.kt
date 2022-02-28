package com.cxwl.shawn.thunder.util;

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation

object AnimationUtil {

    /**
     * 向上弹出动画
     * @param layout
     */
    fun toTopAnimation(layout: View) {
        if (layout.visibility == View.VISIBLE) {
            return
        }
        val animation = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f)
        animation.duration = 300
        layout.startAnimation(animation)
        layout.visibility = View.VISIBLE
    }

    /**
     * 向下隐藏动画
     * @param layout
     */
    fun toBottomAnimation(layout: View) {
        if (layout.visibility == View.GONE) {
            return
        }
        val animation = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1f)
        animation.duration = 300
        layout.startAnimation(animation)
        layout.visibility = View.GONE
    }

    /**
     * 隐藏或显示ListView的动画
     */
    fun hideOrShowListViewAnimator(view: View, startValue: Int, endValue: Int) { //1.设置属性的初始值和结束值
        val mAnimator = ValueAnimator.ofInt(0, 100)
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener { animation ->
            val animatorValue = animation.animatedValue as Int
            val fraction = animatorValue / 100f
            val mEvaluator = IntEvaluator()
            //3.使用IntEvaluator计算属性值并赋值给ListView的高
            view.layoutParams.height = mEvaluator.evaluate(fraction, startValue, endValue)
            view.requestLayout()
        }
        //4.为ValueAnimator设置LinearInterpolator
        mAnimator.interpolator = LinearInterpolator()
        //5.设置动画的持续时间
        mAnimator.duration = 200
        //6.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(view)
        mAnimator.start()
    }

}
