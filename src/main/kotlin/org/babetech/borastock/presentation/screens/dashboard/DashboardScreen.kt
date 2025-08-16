package org.babetech.borastock.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.babetech.borastock.domain.models.MetricData
import org.babetech.borastock.domain.models.Movement
import org.babetech.borastock.presentation.components.dashboard.DashboardMetricsGrid
import org.babetech.borastock.presentation.components.dashboard.QuickActionsSection
import org.babetech.borastock.presentation.components.dashboard.RecentMovementsList
import org.babetech.borastock.presentation.screens.dashboard.charts.GraphicSwitcherScreen
import org.babetech.borastock.presentation.viewmodel.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Écran principal du tableau de bord
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StockViewModel = koinViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
    val paneState = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
    val showSupporting = paneState != PaneAdaptedValue.Hidden

    // États observables
    val stockStatistics by viewModel.stockStatistics.collectAsStateWithLifecycle()
    val entryStatistics by viewModel.entryStatistics.collectAsStateWithLifecycle()
    val exitStatistics by viewModel.exitStatistics.collectAsStateWithLifecycle()

    // Données de démonstration pour les métriques
    val sampleMetrics = remember {
        listOf(
            MetricData(
                title = "Produits",
                value = "128",
                trend = "+12%",
                trendUp = true,
                icon = Res.drawable.inventory,
                color = androidx.compose.ui.graphics.Color(0xFF3B82F6)
            ),
            MetricData(
                title = "Fournisseurs",
                value = "24",
                trend = "+3%",
                trendUp = true,
                icon = Res.drawable.group,
                color = androidx.compose.ui.graphics.Color(0xFF10B981)
            ),
            MetricData(
                title = "Stock Total",
                value = "2,350",
                trend = "-5%",
                trendUp = false,
                icon = Res.drawable.Warehouse,
                color = androidx.compose.ui.graphics.Color(0xFFF59E0B)
            ),
            MetricData(
                title = "Commandes",
                value = "89",
                trend = "+18%",
                trendUp = true,
                icon = Res.drawable.ShoppingCart,
                color = androidx.compose.ui.graphics.Color(0xFFEF4444)
            ),
            MetricData(
                title = "Clients",
                value = "1,250",
                trend = "+6%",
                trendUp = true,
                icon = Res.drawable.Person,
                color = androidx.compose.ui.graphics.Color(0xFF8B5CF6)
            ),
            MetricData(
                title = "Ventes du mois",
                value = "€15,800",
                trend = "+9%",
                trendUp = true,
                icon = Res.drawable.TrendingUp,
                color = androidx.compose.ui.graphics.Color(0xFF06B6D4)
            )
        )
    }

    val sampleMovements = remember {
        listOf(
            Movement("Ajout 20 unités - Produit A", "Il y a 2h", true),
            Movement("Sortie 5 unités - Produit B", "Il y a 4h", false),
            Movement("Ajout 50 unités - Produit C", "Il y a 6h", true),
            Movement("Sortie 12 unités - Produit D", "Il y a 8h", false)
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                AnimatedPane {
                    MainDashboardPane(
                        metrics = sampleMetrics,
                        movements = sampleMovements,
                        showChartButton = !showSupporting,
                        onToggleChart = {
                            scope.launch {
                                navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                            }
                        },
                        onAddProductClick = { /* TODO */ },
                        onScanCodeClick = { /* TODO */ },
                        onViewAnalyticsClick = { /* TODO */ }
                    )
                }
            },
            supportingPane = {
                if (showSupporting) {
                    AnimatedPane {
                        SupportingChartPane(
                            onBack = {
                                scope.launch { navigator.navigateBack() }
                            }
                        )
                    }
                }
            }
        )
    }
}

/**
 * Volet principal du tableau de bord
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainDashboardPane(
    metrics: List<MetricData>,
    movements: List<Movement>,
    showChartButton: Boolean,
    onToggleChart: () -> Unit,
    onAddProductClick: () -> Unit,
    onScanCodeClick: () -> Unit,
    onViewAnalyticsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                    )
                )
            )
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Tableau de bord",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Aperçu de votre activité",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { 
                DashboardMetricsGrid(metrics = metrics) 
            }
            
            item { 
                RecentMovementsList(movements = movements) 
            }
            
            item { 
                QuickActionsSection(
                    onAddProductClick = onAddProductClick,
                    onScanCodeClick = onScanCodeClick,
                    onViewAnalyticsClick = onViewAnalyticsClick
                ) 
            }
            
            if (showChartButton) {
                item {
                    Button(
                        onClick = onToggleChart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Afficher les graphiques",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Volet de support avec les graphiques
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SupportingChartPane(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()

    Box(modifier = modifier.fillMaxSize().padding(24.dp)) {
        if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Expanded) 60.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Analyse des performances",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            GraphicSwitcherScreen()
        }
    }
}