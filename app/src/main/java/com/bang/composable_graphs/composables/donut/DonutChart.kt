package com.bang.composable_graphs.composables.donut

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bang.composable_graphs.composables.donut.model.DonutData
import com.bang.composable_graphs.composables.donut.style.DonutChartStyle
import com.bang.composable_graphs.composables.donut.style.DonutChartType
import com.bang.composable_graphs.composables.donut.style.DonutSliceType
import com.bang.composable_graphs.util.DEFAULT_GRAPH_SIZE
import com.bang.composable_graphs.util.GraphHelper

import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * Donut Chart
 */
@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    type: DonutChartType = DonutChartType.Normal,
    data: List<DonutData>,
    style: DonutChartStyle = DonutChartStyle(),
    onSliceClick: ((DonutData)-> Unit)? = null
) {
    DonutChartImpl(
        modifier = modifier,
        type = type,
        data = data,
        style = style,
        onSliceClick = onSliceClick
    )
}

@Composable
private fun DonutChartImpl(
    modifier: Modifier,
    type: DonutChartType,
    data: List<DonutData>,
    style: DonutChartStyle,
    onSliceClick: ((DonutData) -> Unit)? = null,
) {

    val donutSliceList = remember { data.mapToDonutSliceList(type = type) }
    val ringSize = remember { style.thickness.value }
    var radiusF = remember { 0F }
    var centerF = remember { Offset(0F, 0F) }

    val tapDetectModifier = if (onSliceClick == null) Modifier else {
        Modifier
            .pointerInput(Unit) {
                detectTapGestures { p1: Offset ->

                    val isInsideCircle: Boolean = let {
                        val clickRadius = sqrt((p1.x - centerF.x).pow(2) + (p1.y - centerF.y).pow(2))
                        (clickRadius >= (radiusF - ringSize)) && (clickRadius <= radiusF)
                    }

                    if (isInsideCircle) {

                        var touchAngle = GraphHelper.getAngleFromOffset(
                            offset = p1,
                            radiusF
                        ) - 270F // -270 -> because we are shifting the chart by -90F in all places

                        while (touchAngle < 0F) {
                            touchAngle += 360.0F
                        }

                        val clickedSlice = donutSliceList.find { slice ->
                            (touchAngle >= slice.startAngle && touchAngle <= slice.endAngle)
                        }
                        val clickedDonutData = data.find { clickedSlice?.id == it.id }
                        clickedDonutData?.let { onSliceClick(it) }
                    }
                }
            }
    }

    val defaultModifier = Modifier
        .size(DEFAULT_GRAPH_SIZE)
        .then(tapDetectModifier)

    Canvas(
        modifier = modifier.then(defaultModifier),
        onDraw = {

            val minWidthAndHeight = listOf(this.size.width, this.size.height).minOf { it - ringSize }

            /**
             * Don't use [center] or [size] of canvas
             * Only use [donutCenter] and [donutSize] because we are maintaining same width and height
             * for the Pie
             */
            val donutSize = Size(minWidthAndHeight, minWidthAndHeight)
            val donutCenter = Offset(x = donutSize.width/2, y = donutSize.height/2) // TODO: Not needed, remove it in future after validation
            val radius = (minWidthAndHeight / 2) +  (ringSize/2)

            radiusF = radius
            centerF = center
            
            val cap = if (style.sliceType == DonutSliceType.Normal) StrokeCap.Butt else StrokeCap.Round

            /**
             * Draw inactive progress track when [type] is [DonutChartType.Progressive]
             */
            if (type is DonutChartType.Progressive){
                drawArc(
                    color =  type.progressInactiveColor,
                    size = donutSize,
                    topLeft = Offset(ringSize/2F, ringSize/2F),
                    startAngle = 0F,
                    sweepAngle = 360F,
                    useCenter = true,
                    style = Stroke(
                        width = ringSize,
                        cap = cap,
                    ),
                    blendMode = style.colors.blendMode,
                    colorFilter = style.colors.colorFilter
                )
            }

            /**
             * Draw arcs for each donut slices
             */
            donutSliceList.reversed().forEach { value ->
                drawArc(
                    color = value.color,
                    startAngle = - 90F, // -90F to rotate the arcs straight
                    sweepAngle = value.endAngle ,  // minus startAngle from endAngle to make sure we only draw the required arc and not from 0 for every arcs
                    useCenter = false,
                    style = Stroke(
                        width = ringSize,
                        cap = cap,
                    ),
                    size = donutSize,
                    topLeft = Offset(ringSize/2F, ringSize/2F),
                    blendMode = style.colors.blendMode,
                    colorFilter = style.colors.colorFilter
                )
            }
        }
    )
}

@Preview
@Composable
private fun DonutChartPreviewDark() {

    val previewData = remember {
        listOf(5, 10, 20, 40, 10).map {
            DonutData(value = it.toFloat())
        }
    }

    DonutChart(
        modifier = Modifier
            .size(300.dp)
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            ),
        data = previewData,
        style = DonutChartStyle(
            thickness = 60.dp,
            sliceType = DonutSliceType.Normal
        )
    )
}

