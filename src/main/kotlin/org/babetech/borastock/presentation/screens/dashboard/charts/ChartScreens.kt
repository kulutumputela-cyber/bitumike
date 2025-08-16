package org.babetech.borastock.presentation.screens.dashboard.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle

@Composable
fun LineChartScreen() {
    val testLineParameters: List<LineParameters> = listOf(
        LineParameters(
            label = "Revenue",
            data = listOf(70.0, 80.0, 50.33, 40.0, 100.500, 50.0),
            lineColor = Color.Gray,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        ),
        LineParameters(
            label = "Earnings",
            data = listOf(60.0, 80.6, 40.33, 86.232, 88.0, 90.0),
            lineColor = Color(0xFFFF7F50),
            lineType = LineType.DEFAULT_LINE,
            lineShadow = true
        ),
        LineParameters(
            label = "Profits",
            data = listOf(1.0, 40.0, 11.33, 55.23, 1.0, 100.0),
            lineColor = Color(0xFF81BE88),
            lineType = LineType.CURVED_LINE,
            lineShadow = false,
        )
    )

    Box(Modifier.fillMaxSize()) {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            linesParameters = testLineParameters,
            isGrid = true,
            gridColor = Color.Blue,
            xAxisData = listOf("2019", "2020", "2021", "2022", "2023", "2024"),
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
            ),
            xAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400
            ),
            yAxisRange = 14,
            oneLineChart = false,
            gridOrientation = GridOrientation.VERTICAL
        )
    }
}

@Composable
fun BarChartScreen() {
    val testBarParameters: List<BarParameters> = listOf(
        BarParameters(
            dataName = "Entrées",
            data = listOf(60.0, 70.6, 80.0, 50.6, 44.0, 100.6, 10.0),
            barColor = Color(0xFF6C3428)
        ),
        BarParameters(
            dataName = "Sorties",
            data = listOf(50.0, 30.6, 77.0, 69.6, 50.0, 30.6, 80.0),
            barColor = Color(0xFFBA704F),
        ),
        BarParameters(
            dataName = "Stock",
            data = listOf(100.0, 99.6, 60.0, 80.6, 10.0, 100.6, 55.99),
            barColor = Color(0xFFDFA878),
        ),
    )

    Box(Modifier.fillMaxSize()) {
        BarChart(
            chartParameters = testBarParameters,
            gridColor = Color.DarkGray,
            xAxisData = listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul"),
            isShowGrid = true,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.DarkGray,
            ),
            xAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.W400
            ),
            yAxisRange = 15,
            barWidth = 20.dp
        )
    }
}

@Composable
fun PieChartScreen() {
    val testPieChartData: List<PieChartData> = listOf(
        PieChartData(
            partName = "Électronique",
            data = 500.0,
            color = Color(0xFF22A699),
        ),
        PieChartData(
            partName = "Informatique",
            data = 700.0,
            color = Color(0xFFF2BE22),
        ),
        PieChartData(
            partName = "Accessoires",
            data = 500.0,
            color = Color(0xFFF29727),
        ),
        PieChartData(
            partName = "Autres",
            data = 100.0,
            color = Color(0xFFF24C3D),
        ),
    )

    PieChart(
        modifier = Modifier.fillMaxSize(),
        pieChartData = testPieChartData,
        ratioLineColor = Color.LightGray,
        textRatioStyle = TextStyle(color = Color.Gray),
    )
}

@Composable
fun DonutChartScreen() {
    val testPieChartData: List<PieChartData> = listOf(
        PieChartData(
            partName = "En stock",
            data = 500.0,
            color = Color(0xFF0B666A),
        ),
        PieChartData(
            partName = "Stock faible",
            data = 700.0,
            color = Color(0xFF35A29F),
        ),
        PieChartData(
            partName = "Rupture",
            data = 500.0,
            color = Color(0xFF97FEED),
        ),
        PieChartData(
            partName = "Surstock",
            data = 100.0,
            color = Color(0xFF071952),
        ),
    )

    DonutChart(
        modifier = Modifier.fillMaxSize(),
        pieChartData = testPieChartData,
        centerTitle = "Stock",
        centerTitleStyle = TextStyle(color = Color(0xFF071952)),
        outerCircularColor = Color.LightGray,
        innerCircularColor = Color.Gray,
        ratioLineColor = Color.LightGray,
    )
}

@Composable
fun RadarChartScreen() {
    val radarLabels = listOf(
        "Qualité", "Prix", "Délai", "Service", "Fiabilité", "Innovation"
    )
    val values2 = listOf(120.0, 160.0, 110.0, 112.0, 200.0, 120.0)
    val values = listOf(180.0, 180.0, 165.0, 135.0, 120.0, 150.0)
    
    val labelsStyle = TextStyle(
        color = Color.Black,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )

    val scalarValuesStyle = TextStyle(
        color = Color.Black,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )

    RadarChart(
        modifier = Modifier.fillMaxSize(),
        radarLabels = radarLabels,
        labelsStyle = labelsStyle,
        netLinesStyle = NetLinesStyle(
            netLineColor = Color(0x90ffD3CFD3),
            netLinesStrokeWidth = 2f,
            netLinesStrokeCap = StrokeCap.Butt
        ),
        scalarSteps = 2,
        scalarValue = 200.0,
        scalarValuesStyle = scalarValuesStyle,
        polygons = listOf(
            Polygon(
                values = values,
                unit = "%",
                style = PolygonStyle(
                    fillColor = Color(0xffc2ff86),
                    fillColorAlpha = 0.5f,
                    borderColor = Color(0xffe6ffd6),
                    borderColorAlpha = 0.5f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Butt,
                )
            ),
            Polygon(
                values = values2,
                unit = "%",
                style = PolygonStyle(
                    fillColor = Color(0xffFFDBDE),
                    fillColorAlpha = 0.5f,
                    borderColor = Color(0xffFF8B99),
                    borderColorAlpha = 0.5f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Butt
                )
            )
        )
    )
}