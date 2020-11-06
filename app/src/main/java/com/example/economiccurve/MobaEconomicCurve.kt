package com.example.economiccurve

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*


class MobaEconomicCurve @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var distanceW: Int = 0
    private var distanceH: Int = 0
    private var lineStartX: Int = 0
    private var layoutRect = Rect()
    private var showPadding = 0
    private val horizontalNum = 13      //水平位置数量
    private var tempPaddingStartX: Int = 0
    private var subtractPadding: Int = 0
    private val pointList = ArrayList<Point>()
    private val dataLists = ArrayList<ActivityDetailPoint>()
    private val lineNumY = 9
    private var keyBase = 0
    private var minValue = 0
    private var baseZeroy = 0F
    private var mTimeTextPaint: Paint = Paint()
    private var mValueLinePaint: Paint? = null
    private var mTextRectPaint: Paint? = null
    private var currentDescArray: MutableList<String> = ArrayList()
    private var txtYArray: MutableList<Float> = ArrayList()    //y轴的坐标
    private var showTypeUnit = ""
    private var mPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private var mLinePaint: Paint = Paint()
    private var mPointLine: Paint = Paint(ANTI_ALIAS_FLAG)
    private val showTypeYValueArray = 100
    private var linex: Int = 0
    private var rate: Float = 0.toFloat()
    private val yleftWidth: Float = 100f
    private val path = Path()
    private var mode = NONE

    private var prevx: Float = 0.toFloat()
    private var curx: Float = 0.toFloat()
    private var prevd: Float = 0.toFloat()

    private var buleTeam: String = ""
    private var redTeam: String = ""
    private var mRectPaint: Paint = Paint()


    // 文字的高度
    private
    val textHeight: Float
        get() {
            val fm = mTimeTextPaint.fontMetrics  //TODO
            return Math.ceil((fm.descent - fm.ascent).toDouble()).toFloat() - 6F
        }

    init {
        initPaints()
    }


    private fun initPaints() {
        val textSize = 30f
        mPaint = Paint(ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG)
        mPaint.style = Paint.Style.FILL
        mPaint.textSize = textSize
        mPaint.textAlign = Paint.Align.LEFT

        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.strokeWidth = 0.5f
        mLinePaint.color = resources.getColor(R.color.color_54627F)

        mTimeTextPaint = Paint(ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG)
        mTimeTextPaint.isAntiAlias = true
        mTimeTextPaint.textSize = textSize
        mTimeTextPaint.color = Color.GRAY

        mPaint = Paint(ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG)
        mPaint.style = Paint.Style.FILL
        mPaint.textSize = textSize
        mPaint.textAlign = Paint.Align.LEFT
        linex = -1

        mValueLinePaint = Paint()
        mValueLinePaint?.isAntiAlias = true

        mTextRectPaint = Paint()
        mTextRectPaint?.isAntiAlias = true
        mTextRectPaint?.textAlign = Paint.Align.CENTER
        mTextRectPaint?.textSize = 20.toFloat()
        mTextRectPaint?.color = resources.getColor(R.color.colorAccent)
        mPointLine.color = resources.getColor(R.color.color_team_red)
        mPointLine.strokeWidth = 3f
        mPointLine.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
        mRectPaint.style = Paint.Style.FILL
        mRectPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        var resultWidth = sizeWidth
        var resultHeight = sizeHeight
        // 考虑内边距对尺寸的影响
        resultWidth += paddingLeft + paddingRight
        resultHeight += paddingTop + paddingBottom
        // 考虑父容器对尺寸的影响
        resultWidth = resolveMeasure(sizeWidth, resultWidth)
        distanceW = resultWidth
        resultHeight = resolveMeasure(sizeHeight, resultHeight)
        distanceH = resultHeight
        setMeasuredDimension(resultWidth, resultHeight)


        val num = horizontalNum
        showPadding = distanceW / num
        lineStartX = (distanceW - showPadding * (num - 3)) / 2
        tempPaddingStartX = lineStartX / 8
        subtractPadding = showPadding
        val rectBottom = distanceH
        layoutRect = Rect(0, 0, distanceW, rectBottom)
        init()
    }

    private fun init() {
        if (pointList.size > 0)
            pointList.clear()
        getZeroBaseY()
        convertData2Point()
        postInvalidate()
    }

    fun setData(
        datasList: List<ActivityDetailPoint>,
        buleTeam: String,
        redTeam: String
    ) {
        if (this.dataLists.size > 0) this.dataLists.clear()
        if (this.pointList.size > 0) this.pointList.clear()
        this.buleTeam = buleTeam
        this.redTeam = redTeam
        var maxYPointValue = 0f
        if (datasList.isNotEmpty()) {
            this.dataLists.addAll(datasList)
            val yValueList = ArrayList<Float>()
            for (point in datasList) {
                yValueList.add(point.avg)
            }
            maxYPointValue = Collections.max(yValueList)
        }
        datasList.forEach {
            currentDescArray.add(it.timeStamp)
        }
        maxYPointValue =
            if (maxYPointValue == 0f) (showTypeYValueArray * lineNumY).toFloat() else maxYPointValue
        var keyBase: Int = (maxYPointValue / lineNumY).toInt()
        if (keyBase * lineNumY - maxYPointValue < keyBase)
            keyBase = ((maxYPointValue + keyBase) / lineNumY).toInt()
        this.showTypeUnit = ""
        this.minValue = 0

        this.linex = 0
        this.keyBase = keyBase
        rate = 1f
        txtYArray.add(4000F)
        txtYArray.add(3000F)
        txtYArray.add(2000F)
        txtYArray.add(1000F)
        txtYArray.add(0F)
        txtYArray.add(-1000F)
        txtYArray.add(-2000F)
        txtYArray.add(-3000F)
        txtYArray.add(-4000F)
        init()
    }

    private var itemValue = 1000f

    private fun convertData2Point() {
        if (dataLists.size > 0) {
            val rate1 = returnRate()
            val keyValueY = distanceH / (lineNumY + 1)
            for (i in 0 until dataLists.size) {
                pointList.add(
                    Point(
                        (rate1 * i) + lineStartX.toFloat() + tempPaddingStartX.toFloat(),
                        returnDataYValue(dataLists[i].avg, keyValueY.toFloat())
                    )
                )
            }
        }
    }


    //控制x坐标之间的间隔
    private fun returnRate(): Float {
        var dex = 25f
        dex =
            (distanceW.toFloat() - tempPaddingStartX.toFloat() - showPadding) / currentDescArray.size
        return dex
    }
    //每个刻度的

    //通过y值来获取y的高度
    private fun returnDataYValue(value: Float, keyValueY: Float): Float {
        var y = 0F
        var itemHeight = keyValueY / itemValue
        if (value > 0) {
            y = baseZeroy - (itemHeight) * value
        } else {
            y = baseZeroy + Math.abs(itemHeight * value)
        }
        return y
    }

    fun resolveMeasure(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val specSize = View.MeasureSpec.getSize(measureSpec)
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> result = Math.min(specSize, defaultSize)
        }
        return result
    }

    /**
     * 获取基线的y的位置
     */
    private fun getZeroBaseY() {
        if (dataLists.size > 0) {
            val keyValueY = (distanceH / (lineNumY + 1)).toFloat()
            var yValue: Float      //记录y的值
            if (txtYArray.isEmpty()) return
            for (i in 0 until lineNumY) {
                yValue = keyValueY * (i + 2) - subtractPadding
                if (txtYArray[i] == 0F) {
                    baseZeroy = yValue
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mode = DRAG
                prevx = event.x
            }

            MotionEvent.ACTION_UP -> {
                linex = getClosestValueIndex(prevx)
                invalidate()
            }

        }
        return true
    }

    //获取最近的Index值
    private fun getClosestValueIndex(x: Float): Int {
        var res = -1
        if (pointList.size > 0) {
            var dif = (distanceW / pointList.size).toFloat()
            var tmp: Float
            for (i in pointList.indices) {
                tmp = Math.abs(x - pointList[i].px)
                if (tmp < dif) {
                    dif = tmp
                    res = i
                }
            }
        }
        return res
    }


    override fun onDraw(canvas: Canvas) {
        drawXAxis(canvas)
        drawYAxis(canvas)
        drawBeizer(canvas)
        drawTeamName(canvas)
        drawTouch(canvas)
    }

    fun setOnPointSelectListener(listener: onPointSelectListener) {
        this.listener = listener
    }

    private var listener: onPointSelectListener? = null

    interface onPointSelectListener {
        fun onPointSelect(
            blueTeam: String,
            redTeam: String,
            pointX: Float,
            pointY: Float,
            valueX: String,
            valueY: String,
            distanceX: Int
        )
    }


    // 画x轴
    private fun drawXAxis(canvas: Canvas) {
        val rate = returnRate()
        val y = layoutRect.bottom - textHeight
        mTimeTextPaint.textAlign = Paint.Align.CENTER
        mTimeTextPaint.strokeWidth = 2f
        mTimeTextPaint.style = Paint.Style.FILL

        val showNum = currentDescArray.size
        for (i in 0 until showNum) {
            val x =
                (rate * i) + lineStartX.toFloat() + tempPaddingStartX.toFloat()
            canvas.drawText(currentDescArray[i], x, y, mTimeTextPaint)
        }
    }

    //用于记录最高线段的高和最低线段的高
    private var topLineY = 0f
    private var btmLinY = 0f
    // 画Y轴及线
    private fun drawYAxis(canvas: Canvas) {
        if (txtYArray.isEmpty()) return
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 5f
        mPaint.color = Color.BLACK
        val keyValueY = (distanceH / (lineNumY + 1)).toFloat()
        var yValue: Float      //记录y的值
        var showStr: String?
        for (i in 0 until lineNumY) {
            yValue = keyValueY * (i + 2) - subtractPadding
            if (txtYArray[i] > 0) {
                showStr = (txtYArray[i] / 1000).toString() + "k"
                mPaint.color = resources.getColor(R.color.color_team_blue)
                canvas.drawText(
                    showStr,
                    (lineStartX - showPadding).toFloat(),
                    yValue + 0.25f * textHeight,
                    mPaint
                )
            } else if (txtYArray[i] == 0F) {
                showStr = txtYArray[i].toString()
                mPaint.color = resources.getColor(R.color.color_999)
                canvas.drawText(
                    showStr,
                    (lineStartX - showPadding).toFloat(),
                    yValue + 0.25f * textHeight,
                    mPaint
                )
            } else {
                showStr = (Math.abs(txtYArray[i] / 1000)).toString() + "k"
                mPaint.color = resources.getColor(R.color.color_team_red)
                canvas.drawText(
                    showStr,
                    (lineStartX - showPadding).toFloat(),
                    yValue + 0.25f * textHeight,
                    mPaint
                )
            }
            canvas.drawLine(
                (lineStartX - showPadding).toFloat() + yleftWidth / 2 + 20,
                yValue,
                (distanceW - lineStartX + showPadding).toFloat(),
                yValue,
                mLinePaint
            )
            if (i == 0) {
                topLineY = yValue
            }
            if (i == lineNumY - 1) {
                btmLinY = yValue
            }
        }

    }

    //画队伍名称
    private fun drawTeamName(canvas: Canvas) {
        var left = (lineStartX - showPadding).toFloat() + yleftWidth / 2 + 20
        var top1 = 80f
        val oval1 = RectF(left, top1, left + 50, top1 + 20)// 设置个新的长方形
        mRectPaint.color = resources.getColor(R.color.color_team_blue)
        mPaint.color = resources.getColor(R.color.color_team_blue)
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 2F
        canvas.drawRoundRect(oval1, 20F, 20F, mRectPaint)
        canvas.drawText(buleTeam, left + 60, top1 + 20, mPaint)

        var top2 = distanceH.toFloat() - 140
        val oval2 = RectF(left, top2, left + 50, top2 + 20)// 设置个新的长方形
        mRectPaint.color = resources.getColor(R.color.color_team_red)
        mPaint.color = resources.getColor(R.color.color_team_red)
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 2F
        canvas.drawRoundRect(oval2, 20F, 20F, mRectPaint)
        canvas.drawText(redTeam, left + 60, top2 + 20, mPaint)
    }

    private fun drawBeizer(canvas: Canvas) {
        if (pointList.isNotEmpty()) {
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = 8F
            mPaint.color = resources.getColor(R.color.color_team_red)
            var startp: Point
            var endp: Point
            for (i in 0 until pointList.size - 1) {
                startp = pointList[i]
                endp = pointList[i + 1]
                //两个点同时在基线上
                if (startp.py < baseZeroy && endp.py < baseZeroy) {
                    mPaint.color = resources.getColor(R.color.color_team_blue)
                    val wt = (startp.px + endp.px) / 2
                    path.reset()
                    path.moveTo(startp.px, startp.py)
                    path.cubicTo(wt, startp.py, wt, endp.py, endp.px, endp.py)
                    canvas.drawPath(path, mPaint)
                }
                //两个点同时在基线下
                else if (startp.py > baseZeroy && endp.py > baseZeroy) {
                    mPaint.color = resources.getColor(R.color.color_team_red)
                    val wt = (startp.px + endp.px) / 2
                    path.reset()
                    path.moveTo(startp.px, startp.py)
                    path.cubicTo(wt, startp.py, wt, endp.py, endp.px, endp.py)
                    canvas.drawPath(path, mPaint)
                }
                //一个点在基线上，一个点在基线下,此时分成两段去绘制
                else {
                    //当前基线的与该线段较短的x坐标
                    var baseZeroX = getZeroX(startp.px, endp.px, startp.py, endp.py)
                    //起点在基线一下
                    if (startp.py > baseZeroy) {
                        mPaint.color = resources.getColor(R.color.color_team_red)
                        val wtleft = (startp.px + baseZeroX) / 2
                        val htLeft= (startp.py + baseZeroy) / 2
                        var leftRateX=(baseZeroX-startp.px)/4
                        var leftRateY=(baseZeroy-startp.py)/4
                        path.reset()
                        path.moveTo(startp.px, startp.py)
                        path.cubicTo(wtleft, startp.py, wtleft+leftRateX, htLeft-leftRateY, baseZeroX, baseZeroy)
                        canvas.drawPath(path, mPaint)

                        mPaint.color = resources.getColor(R.color.color_team_blue)
                        val wtRight = (baseZeroX + endp.px) / 2
                        val htRight= (baseZeroy + endp.py) / 2
                        var rightRateX=(endp.px-baseZeroX)/4
                        var rightRateY=(endp.py-baseZeroy)/4
                        path.reset()
                        path.moveTo(baseZeroX, baseZeroy)   //为了保持裁点时曲线的平滑
                        path.cubicTo(wtRight-rightRateX, htRight+rightRateY, wtRight, endp.py, endp.px, endp.py)
                        canvas.drawPath(path, mPaint)
                    } else {
                        mPaint.color = resources.getColor(R.color.color_team_blue)
                        val wtleft = (startp.px + baseZeroX) / 2
                        val htLeft= (startp.py + baseZeroy) / 2
                        var leftRateX=(baseZeroX-startp.px)/4
                        var leftRateY=(baseZeroy-startp.py)/4
                        path.reset()
                        path.moveTo(startp.px, startp.py)
                        path.cubicTo(wtleft, startp.py, wtleft+leftRateX, htLeft-leftRateY, baseZeroX, baseZeroy)
                        canvas.drawPath(path, mPaint)


                        mPaint.color = resources.getColor(R.color.color_team_red)
                        val wtRight = (baseZeroX + endp.px) / 2
                        val htRight= (baseZeroy + endp.py) / 2
                        var rightRateX=(endp.px-baseZeroX)/4
                        var rightRateY=(endp.py-baseZeroy)/4
                        path.reset()
                        path.moveTo(baseZeroX, baseZeroy)
                        path.cubicTo(wtRight-rightRateX, htRight+rightRateY, wtRight, endp.py, endp.px, endp.py)
                        canvas.drawPath(path, mPaint)
                    }
                }
            }
        }
    }


    private fun getZeroX(x1: Float, x2: Float, y1: Float, y2: Float): Float {
        var k = (y2 - y1) / (x2 - x1)
        var b = y1 - k * x1
        return (baseZeroy - b) / k
    }

    private fun drawTouch(canvas: Canvas) {
        if (pointList.size > 0 && linex >= 0 && linex < pointList.size) {
            var currentPoint = pointList[linex]
            //基线一下
            if (currentPoint.py >= baseZeroy) {
                mValueLinePaint?.color = resources.getColor(R.color.color_team_red)
                canvas.drawCircle(
                    pointList[linex].px,
                    pointList[linex].py,
                    14F,
                    mValueLinePaint!!
                )// 小圆
                mValueLinePaint?.isAntiAlias = true
                mPointLine.color = resources.getColor(R.color.color_team_red)
                canvas.drawLine(
                    pointList[linex].px,
                    topLineY,
                    pointList[linex].px,
                    btmLinY,
                    mPointLine
                )

            } else {
                mValueLinePaint?.color = resources.getColor(R.color.color_team_blue)
                canvas.drawCircle(
                    pointList[linex].px,
                    pointList[linex].py,
                    14F,
                    mValueLinePaint!!
                )// 小圆
                mValueLinePaint?.isAntiAlias = true
                mPointLine.color = resources.getColor(R.color.color_team_blue)
                canvas.drawLine(
                    pointList[linex].px,
                    topLineY,
                    pointList[linex].px,
                    btmLinY,
                    mPointLine
                )
            }


            if (listener == null) return
            dataLists[linex].avg.toString()
            listener?.onPointSelect(
                buleTeam,
                redTeam,
                currentPoint.px,
                currentPoint.py,
                dataLists[linex].timeStamp,
                dataLists[linex].avg.toString(),
                distanceW
            )
        }
    }

    private inner class Point internal constructor(internal var px: Float, internal var py: Float) {

        override fun toString(): String {
            return "px:$px, py:$py"
        }
    }

    class ActivityDetailPoint(var timeStamp: String, var avg: Float)


    companion object {

        private val NONE = 0
        private val DRAG = 1
    }
}