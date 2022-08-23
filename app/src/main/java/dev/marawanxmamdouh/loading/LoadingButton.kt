package dev.marawanxmamdouh.loading

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f

    // Progress Circle
    private var progressCircleAnimator = ValueAnimator()
    private var currentSweepAngle = 0
    private var radius = 0f
    private var s1 = 0f
    private var s2 = 0f

    // Progress Rectangle
    private var progressRectangleAnimator = ValueAnimator()
    private var currentPosition = 0

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.BOLD)
    }


    init {

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(resources.getColor(R.color.colorPrimary))
        drawStartState(canvas)
        drawLoadingState(canvas)
    }

    private fun drawStartState(canvas: Canvas) {
        paint.color = resources.getColor(R.color.white)
        canvas.drawText(
            resources.getString(R.string.button_name),
            widthSize / 2 - paint.measureText(resources.getString(R.string.button_name)) / 2,
            heightSize / 2 + paint.descent(),
            paint
        )
    }

    private fun drawLoadingState(canvas: Canvas) {
        drawProgressRectangle(canvas)
        paint.color = resources.getColor(R.color.white)
        canvas.drawText(
            resources.getString(R.string.button_loading),
            widthSize / 2 - paint.measureText(resources.getString(R.string.button_loading)) / 2 - radius,
            heightSize / 2 + paint.textSize / 3,
            paint
        )
        drawProgressCircle(canvas)
    }

    private fun drawProgressRectangle(canvas: Canvas) {
        paint.color = resources.getColor(R.color.colorPrimaryDark)
        canvas.drawRect(
            0f,
            0f,
            currentPosition.toFloat(),
            heightSize,
            paint
        )
    }

    private fun drawProgressCircle(canvas: Canvas) {
        paint.color = resources.getColor(R.color.colorAccent)
        canvas.save()
        canvas.translate(s1, s2)
        canvas.drawArc(0f, 0f, radius, radius, -90f, currentSweepAngle.toFloat(), true, paint)
        canvas.restore()
    }

    private fun startProgressCircleAnimation() {
        progressCircleAnimator.cancel()
        progressCircleAnimator = ValueAnimator.ofInt(0, 360).apply {
            duration = 5000
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        progressCircleAnimator.start()
    }

    private fun startProgressRectangleAnimation() {
        progressRectangleAnimator.cancel()
        progressRectangleAnimator = ValueAnimator.ofInt(0, widthSize.roundToInt()).apply {
            duration = 5000
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentPosition = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        progressRectangleAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = min(w, h) / 2f
        s1 = widthSize / 2 + paint.measureText(resources.getString(R.string.button_loading)) / 4 + (radius/2)
        s2 = heightSize / 4f
        // TODO("until check if the button is completed")
        //canvas.drawColor(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.CLEAR)
        startProgressCircleAnimation()
        startProgressRectangleAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        radius = heightSize / 4

        setMeasuredDimension(w, h)
    }
}