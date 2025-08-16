package org.babetech.borastock.presentation.screens.dashboard.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.*
import org.babetech.borastock.domain.models.ChartType
import org.jetbrains.compose.resources.painterResource

@Composable
fun GraphicSwitcherScreen() {
    var selectedChart by remember { mutableStateOf("Line") }

    val chartTypes = listOf(
        ChartType("Line", "Courbes", painterResource(Res.drawable.analytics), "Évolution temporelle"),
        ChartType("Bar", "Barres", painterResource(Res.drawable.BarChart), "Comparaisons"),
        ChartType("Pie", "Secteurs", painterResource(Res.drawable.PieChart), "Répartitions"),
        ChartType("Donut", "Anneau", painterResource(Res.drawable.DonutLarge), "Proportions"),
        ChartType("Radar", "Radar", painterResource(Res.drawable.analytics), "Multi-critères")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Options de graphique
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chartTypes) { chartType ->
                ElevatedCard(
                    onClick = { selectedChart = chartType.key },
                    modifier = Modifier.width(140.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selectedChart == chartType.key) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = chartType.icon,
                            contentDescription = chartType.title,
                            tint = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            chartType.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            chartType.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Affichage du graphique sélectionné
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            when (selectedChart) {
                "Line" -> LineChartScreen()
                "Bar" -> BarChartScreen()
                "Pie" -> PieChartScreen()
                "Donut" -> DonutChartScreen()
                "Radar" -> RadarChartScreen()
            }
        }
    }
}