package com.example.economiccurve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var pointDayList: MutableList<MobaEconomicCurve.ActivityDetailPoint> = ArrayList()
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "05:00",
                -2300f
            )
        )
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "10:00",
                3500f
            )
        )
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "15:00",
                -1500f
            )
        )
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "20:00",
                -1000f
            )
        )
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "25:00",
                3000f
            )
        )
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "30:00",
                3500f
            )
        )
        pointDayList!!.add(
            MobaEconomicCurve.ActivityDetailPoint(
                "35:00",
                2000f
            )
        )
        findViewById<EconomicCurve>(R.id.economic_curve).setData(pointDayList, "EDG", "WE")
    }
}