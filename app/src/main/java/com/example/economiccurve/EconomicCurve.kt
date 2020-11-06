package com.example.economiccurve

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView


class EconomicCurve @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), MobaEconomicCurve.onPointSelectListener {


    var mobaEconomicCurve: MobaEconomicCurve = MobaEconomicCurve(getContext())
    var tvPointTitle: TextView = TextView(getContext())

    init {
        var paramsCurve = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mobaEconomicCurve.layoutParams = paramsCurve
        this.addView(mobaEconomicCurve)
        this.addView(tvPointTitle)
        var pointTitle = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        tvPointTitle.layoutParams = pointTitle
        tvPointTitle.textSize = 10F
        tvPointTitle.setBackgroundResource(R.drawable.bg_curve_point_title)
        mobaEconomicCurve.setOnPointSelectListener(this)
    }

    override fun onPointSelect(
        blueTeam: String,
        redTeam: String,
        pointX: Float,
        pointY: Float,
        valueX: String,
        valueY: String,
        distanceX: Int
    ) {
        tvPointTitle.text =valueX + "时，"+blueTeam+"经济领先" +redTeam+valueY
        tvPointTitle.translationY = pointY - 60
        tvPointTitle.translationX = getTitleX(tvPointTitle.width, pointX, distanceX)
    }

    /**
     * 根据标题的长度和距离的长度
     */
    private fun getTitleX(titleWidth: Int, pointX: Float, distanceX: Int): Float {
        var transX = 0F
        var halfW = titleWidth / 2
        if (pointX >= halfW+10) {
            transX = (pointX - halfW)
        }
        if ((pointX+halfW)>=distanceX){
            transX= (distanceX-2*halfW).toFloat()
        }
        return transX
    }

    fun setData(
        datasList: List<MobaEconomicCurve.ActivityDetailPoint>,
        buleTeam: String,
        redTeam: String
    ) {
        mobaEconomicCurve.setData(datasList, buleTeam, redTeam)
    }

}
