package com.bang.composable_graphs.composables.donut

import com.bang.composable_graphs.composables.donut.model.DonutSlice
import com.bang.composable_graphs.composables.donut.model.DonutData
import com.bang.composable_graphs.composables.donut.style.DonutChartType
import com.bang.composable_graphs.util.GraphHelper.convertPercentageToDegree

internal fun List<DonutData>.mapToDonutSliceList(type: DonutChartType): List<DonutSlice>{

    return when(type){
        is DonutChartType.Normal -> {
            this.mapToNormalDonutSliceList()
        }
        is DonutChartType.Progressive -> {
            this.mapToProgressiveDonutSliceList(totalProgress = type.totalProgress)
        }
    }
}

private fun List<DonutData>.mapToNormalDonutSliceList(): List<DonutSlice>{

    val total = this.map{ it.value }.sum()

    var startAngleTemp = 0F

    return this.map { donutData ->

        val percentage = (donutData.value / total) * 100F

        val startAngle = startAngleTemp
        val endAngle = startAngleTemp + convertPercentageToDegree(percentage)

        startAngleTemp = endAngle

        // return
        DonutSlice(
            id = donutData.id,
            startAngle = startAngle,
            endAngle = endAngle,
            color = donutData.color,
        )
    }
}

private fun List<DonutData>.mapToProgressiveDonutSliceList(totalProgress: Float): List<DonutSlice>{

    // sort the list in ascending sequence
    return this.sortedBy { it.value }.map { donutData ->

        val percentage = (donutData.value / totalProgress) * 100F

        val startAngle = 0F
        val endAngle = convertPercentageToDegree(percentage)

        // return
        DonutSlice(
            id = donutData.id,
            startAngle = startAngle,
            endAngle = endAngle,
            color = donutData.color
        )
    }
}