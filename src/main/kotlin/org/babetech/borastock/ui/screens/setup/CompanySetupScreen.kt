package org.babetech.borastock.ui.screens.setup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import borastock.composeapp.generated.resources.*
import org.babetech.borastock.ui.components.CompottieAnimation
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.coil.KmpFileFetcher
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.babetech.borastock.ui.screens.setup.viewmodel.CompanySetupViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.babetech.borastock.ui.screens.setup.viewmodel.CompanySetupUiState

// Constants pour les animations
private const val ANIM_DURATION_MILLIS = 600
private const val SPRING_DAMPING = 0.8f
private const val SPRING_STIFFNESS = 300f

// Énumération des étapes
enum class CompanySetupStep {
    GENERAL_AND_TAX_INFO,
    CONTACT_INFO,
    CONFIRMATION
}

/**
 * Écran principal de configuration de l'entreprise avec design moderne et animations fluides
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CompanySetupScreen(
    onFinish: () -> Unit,
    companySetupViewModel: CompanySetupViewModel = koinViewModel()
) {
    var currentStep by remember { mutableStateOf(CompanySetupStep.GENERAL_AND_TAX_INFO) }
    val uiState by companySetupViewModel.uiState.collectAsState()
    var logoFile: KmpFile? by remember { mutableStateOf(null) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Animation d'entrée pour l'écran entier
    var screenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        screenVisible = true
    }

    LaunchedEffect(uiState.logoUri) {
        if (uiState.logoUri != null && logoFile == null) {
            // Logique de conversion URI vers KmpFile si nécessaire
        }
    }

    val navigator = rememberSupportingPaneScaffoldNavigator()

    AnimatedVisibility(
        visible = screenVisible,
        enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
            animationSpec = tween(800, easing = EaseOutCubic),
            initialOffsetY = { it / 3 }
        )
    ) {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                AnimatedPane {
                    Scaffold(
                        topBar = {
                            CompanySetupTopBar(currentStep = currentStep) {
                                currentStep = CompanySetupStep.values()[currentStep.ordinal - 1]
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                AnimatedContent(
                                    targetState = currentStep,
                                    transitionSpec = {
                                        slideIntoContainer(
                                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = spring(
                                                dampingRatio = SPRING_DAMPING,
                                                stiffness = SPRING_STIFFNESS
                                            )
                                        ) + fadeIn(
                                            animationSpec = tween(ANIM_DURATION_MILLIS)
                                        ) with slideOutOfContainer(
                                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = spring(
                                                dampingRatio = SPRING_DAMPING,
                                                stiffness = SPRING_STIFFNESS
                                            )
                                        ) + fadeOut(
                                            animationSpec = tween(ANIM_DURATION_MILLIS)
                                        )
                                    }, 
                                    label = "step_transition"
                                ) { targetStep ->
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Animation Lottie avec effet de rebond
                                        val lottiePath = when (targetStep) {
                                            CompanySetupStep.GENERAL_AND_TAX_INFO -> "drawable/animations/Welcome.json"
                                            CompanySetupStep.CONTACT_INFO -> "drawable/animations/Support.json"
                                            CompanySetupStep.CONFIRMATION -> if (showSuccessAnimation) "drawable/animations/Confetti.json" else "drawable/animations/Welcome.json"
                                        }
                                        
                                        AnimatedLottieSection(lottiePath = lottiePath)

                                        // Contenu de l'étape avec animation d'apparition
                                        AnimatedStepContent(
                                            currentStep = targetStep,
                                            uiState = uiState,
                                            logoFile = logoFile,
                                            onCompanyNameChange = companySetupViewModel::updateCompanyName,
                                            onMainActivityChange = companySetupViewModel::updateMainActivity,
                                            onIdNatChange = companySetupViewModel::updateIdNat,
                                            onRccmNrcChange = companySetupViewModel::updateRccmNrc,
                                            onTaxNumberChange = companySetupViewModel::updateTaxNumber,
                                            onVatRateChange = companySetupViewModel::updateVatRate,
                                            onLogoFileChange = {
                                                logoFile = it
                                                companySetupViewModel.updateLogoUri(it?.file.toString())
                                            },
                                            onAddressChange = companySetupViewModel::updateAddress,
                                            onCityChange = companySetupViewModel::updateCity,
                                            onProvinceChange = companySetupViewModel::updateProvince,
                                            onCountryChange = companySetupViewModel::updateCountry,
                                            onPhone1Change = companySetupViewModel::updatePhone1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                CompanySetupNavigationButtons(
                                    currentStep = currentStep,
                                    onPreviousClick = { 
                                        currentStep = CompanySetupStep.values()[currentStep.ordinal - 1] 
                                    },
                                    onNextClick = {
                                        if (companySetupViewModel.validateCurrentStep(currentStep)) {
                                            if (currentStep.ordinal < CompanySetupStep.values().size - 1) {
                                                currentStep = CompanySetupStep.values()[currentStep.ordinal + 1]
                                            } else {
                                                companySetupViewModel.saveCompanyInfo()
                                                showSuccessAnimation = true
                                                onFinish()
                                            }
                                        }
                                    },
                                    onRestartClick = {
                                        companySetupViewModel.resetSetup()
                                        currentStep = CompanySetupStep.GENERAL_AND_TAX_INFO
                                        logoFile = null
                                        showSuccessAnimation = false
                                    },
                                    onImportClick = {
                                        println("Action: Importer un fichier d'entreprise existant")
                                    }
                                )
                            }
                        }
                    }
                }
            },
            supportingPane = {
                AnimatedPane {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CompottieAnimation(
                            lottiePath = "drawable/animations/Welcome.json",
                            modifier = Modifier.size(300.dp)
                        )
                    }
                }
            }
        )
    }
}

/**
 * Section Lottie animée avec effet de rebond
 */
@Composable
private fun AnimatedLottieSection(lottiePath: String) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(lottiePath) {
        visible = false
        delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = 200f
            )
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Card(
            modifier = Modifier
                .size(200.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CompottieAnimation(
                    lottiePath = lottiePath,
                    modifier = Modifier.size(160.dp)
                )
            }
        }
    }
}

/**
 * Contenu d'étape animé
 */
@Composable
private fun AnimatedStepContent(
    currentStep: CompanySetupStep,
    uiState: CompanySetupUiState,
    logoFile: KmpFile?,
    onCompanyNameChange: (String) -> Unit,
    onMainActivityChange: (String) -> Unit,
    onIdNatChange: (String) -> Unit,
    onRccmNrcChange: (String) -> Unit,
    onTaxNumberChange: (String) -> Unit,
    onVatRateChange: (String) -> Unit,
    onLogoFileChange: (KmpFile?) -> Unit,
    onAddressChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onProvinceChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onPhone1Change: (String) -> Unit
) {
    var contentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(currentStep) {
        contentVisible = false
        delay(200)
        contentVisible = true
    }

    AnimatedVisibility(
        visible = contentVisible,
        enter = slideInVertically(
            animationSpec = spring(
                dampingRatio = 0.8f,
                stiffness = 300f
            ),
            initialOffsetY = { it / 2 }
        ) + fadeIn(animationSpec = tween(500))
    ) {
        when (currentStep) {
            CompanySetupStep.GENERAL_AND_TAX_INFO -> GeneralAndTaxInfoStep(
                companyName = uiState.companyName, onCompanyNameChange = onCompanyNameChange,
                mainActivity = uiState.mainActivity, onMainActivityChange = onMainActivityChange,
                idNat = uiState.idNat, onIdNatChange = onIdNatChange,
                rccmNrc = uiState.rccmNrc, onRccmNrcChange = onRccmNrcChange,
                taxNumber = uiState.taxNumber, onTaxNumberChange = onTaxNumberChange,
                vatRate = uiState.vatRate, onVatRateChange = onVatRateChange,
                logoFile = logoFile, onLogoFileChange = onLogoFileChange,
                isCompanyNameError = uiState.isCompanyNameError,
                isMainActivityError = uiState.isMainActivityError,
                isIdNatError = uiState.isIdNatError,
                isRccmNrcError = uiState.isRccmNrcError,
                isTaxNumberError = uiState.isTaxNumberError,
                isVatRateError = uiState.isVatRateError
            )
            CompanySetupStep.CONTACT_INFO -> ContactInfoSetupStep(
                address = uiState.address, onAddressChange = onAddressChange,
                city = uiState.city, onCityChange = onCityChange,
                province = uiState.province, onProvinceChange = onProvinceChange,
                country = uiState.country, onCountryChange = onCountryChange,
                phone1 = uiState.phone1, onPhone1Change = onPhone1Change,
                isAddressError = uiState.isAddressError,
                isCityError = uiState.isCityError,
                isProvinceError = uiState.isProvinceError,
                isCountryError = uiState.isCountryError,
                isPhone1Error = uiState.isPhone1Error
            )
            CompanySetupStep.CONFIRMATION -> ConfirmationStep(
                companyName = uiState.companyName,
                mainActivity = uiState.mainActivity,
                idNat = uiState.idNat,
                rccmNrc = uiState.rccmNrc,
                address = uiState.address,
                city = uiState.city,
                province = uiState.province,
                country = uiState.country,
                phone1 = uiState.phone1,
                taxNumber = uiState.taxNumber,
                vatRate = uiState.vatRate,
                logoUri = uiState.logoUri
            )
        }
    }
}

/**
 * Barre supérieure moderne avec indicateur de progression fluide
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySetupTopBar(currentStep: CompanySetupStep, onBackClick: () -> Unit) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Configuration de l'entreprise",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Étape ${currentStep.ordinal + 1} / ${CompanySetupStep.values().size}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            navigationIcon = {
                if (currentStep != CompanySetupStep.GENERAL_AND_TAX_INFO) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_arrow_back), 
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        )
        
        // Indicateur de progression moderne
        ModernProgressIndicator(currentStep = currentStep)
    }
}

/**
 * Indicateur de progression moderne avec animations fluides
 */
@Composable
private fun ModernProgressIndicator(currentStep: CompanySetupStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompanySetupStep.values().forEachIndexed { index, step ->
            val isCompleted = index < currentStep.ordinal
            val isCurrent = index == currentStep.ordinal
            val isUpcoming = index > currentStep.ordinal

            // Animation des couleurs
            val backgroundColor by animateColorAsState(
                targetValue = when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    isCurrent -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                animationSpec = tween(ANIM_DURATION_MILLIS),
                label = "background_color"
            )

            val contentColor by animateColorAsState(
                targetValue = when {
                    isCompleted -> MaterialTheme.colorScheme.onPrimary
                    isCurrent -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(ANIM_DURATION_MILLIS),
                label = "content_color"
            )

            // Animation de la taille
            val scale by animateFloatAsState(
                targetValue = if (isCurrent) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = 300f
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .border(
                        width = if (isCurrent) 3.dp else 0.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        painterResource(Res.drawable.ic_check_circle),
                        contentDescription = "Étape complétée",
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor
                    )
                }
            }

            // Ligne de connexion entre les étapes
            if (index < CompanySetupStep.values().size - 1) {
                val lineProgress by animateFloatAsState(
                    targetValue = if (isCompleted) 1f else 0f,
                    animationSpec = tween(ANIM_DURATION_MILLIS),
                    label = "line_progress"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(lineProgress)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

/**
 * Boutons de navigation avec design moderne et animations
 */
@Composable
fun CompanySetupNavigationButtons(
    currentStep: CompanySetupStep,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onRestartClick: () -> Unit,
    onImportClick: () -> Unit
) {
    var buttonsVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(currentStep) {
        buttonsVisible = false
        delay(300)
        buttonsVisible = true
    }

    AnimatedVisibility(
        visible = buttonsVisible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = 0.8f),
            initialOffsetY = { it / 2 }
        ) + fadeIn()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep != CompanySetupStep.GENERAL_AND_TAX_INFO) {
                    ModernButton(
                        text = "Précédent",
                        onClick = onPreviousClick,
                        modifier = Modifier.weight(1f),
                        isPrimary = false
                    )
                } else {
                    ModernButton(
                        text = "Importer",
                        onClick = onImportClick,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        icon = Res.drawable.ic_upload
                    )
                }

                ModernButton(
                    text = if (currentStep == CompanySetupStep.CONFIRMATION) "Terminer" else "Suivant",
                    onClick = onNextClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = true,
                    icon = if (currentStep == CompanySetupStep.CONFIRMATION) Res.drawable.ic_check_circle else Res.drawable.ic_arrow_forward
                )
            }

            if (currentStep == CompanySetupStep.CONFIRMATION) {
                TextButton(
                    onClick = onRestartClick,
                    modifier = Modifier.semantics { 
                        contentDescription = "Redémarrer la configuration" 
                    }
                ) {
                    Icon(
                        painterResource(Res.drawable.Refresh),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Redémarrer la configuration",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

/**
 * Bouton moderne réutilisable avec animations
 */
@Composable
private fun ModernButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    icon: DrawableResource? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "button_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(150),
        label = "button_elevation"
    )

    if (isPrimary) {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = modifier
                .height(56.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(100)
                    isPressed = false
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = modifier
                .height(56.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp
            )
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(100)
                    isPressed = false
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/**
 * Animation de secousse améliorée
 */
@Composable
fun Modifier.shake(shakeState: Boolean): Modifier {
    val shakeTranslationX by animateFloatAsState(
        targetValue = if (shakeState) 0f else 0f,
        animationSpec = keyframes {
            durationMillis = 400
            0f at 0
            -15f at 50
            15f at 100
            -10f at 150
            10f at 200
            -5f at 250
            5f at 300
            0f at 400
        },
        label = "shake_animation"
    )

    return this.graphicsLayer {
        translationX = shakeTranslationX
    }
}

/**
 * Étape 1 améliorée avec animations d'apparition séquentielles
 */
@Composable
fun GeneralAndTaxInfoStep(
    companyName: String, onCompanyNameChange: (String) -> Unit,
    mainActivity: String, onMainActivityChange: (String) -> Unit,
    idNat: String, onIdNatChange: (String) -> Unit,
    rccmNrc: String, onRccmNrcChange: (String) -> Unit,
    taxNumber: String, onTaxNumberChange: (String) -> Unit,
    vatRate: String, onVatRateChange: (String) -> Unit,
    logoFile: KmpFile?, onLogoFileChange: (KmpFile?) -> Unit,
    isCompanyNameError: Boolean,
    isMainActivityError: Boolean,
    isIdNatError: Boolean,
    isRccmNrcError: Boolean,
    isTaxNumberError: Boolean,
    isVatRateError: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        AnimatedStepTitle(
            title = "Informations générales et fiscales",
            subtitle = "Configurez les détails de votre entreprise"
        )

        CompanyLogoSection(logoFile = logoFile, onLogoFileChange = onLogoFileChange)

        // Champs avec animation séquentielle
        val fields = listOf(
            Triple(companyName, onCompanyNameChange, "Nom de l'entreprise") to Triple(Res.drawable.ic_business, isCompanyNameError, "Le nom de l'entreprise est requis"),
            Triple(mainActivity, onMainActivityChange, "Secteur d'activité") to Triple(Res.drawable.ic_work, isMainActivityError, "Le secteur d'activité est requis"),
            Triple(idNat, onIdNatChange, "Numéro d'identification national (ID.NAT)") to Triple(Res.drawable.ic_badge, isIdNatError, "Le numéro ID.NAT est requis"),
            Triple(rccmNrc, onRccmNrcChange, "Numéro RCCM / NRC") to Triple(Res.drawable.ic_article, isRccmNrcError, "Le numéro RCCM/NRC est requis"),
            Triple(taxNumber, onTaxNumberChange, "Numéro d'impôt / TVA") to Triple(Res.drawable.ic_attach_money, isTaxNumberError, "Le numéro d'impôt/TVA est requis"),
            Triple(vatRate, onVatRateChange, "Taux de TVA (%)") to Triple(Res.drawable.ic_percent, isVatRateError, "Le taux de TVA est requis et doit être un nombre")
        )

        fields.forEachIndexed { index, (fieldData, errorData) ->
            AnimatedTextField(
                value = fieldData.first,
                onValueChange = fieldData.second,
                label = fieldData.third,
                leadingIconRes = errorData.first,
                isError = errorData.second,
                errorMessage = errorData.third,
                keyboardType = if (fieldData.third.contains("TVA")) KeyboardType.Number else KeyboardType.Text,
                delay = index * 100L
            )
        }
    }
}

/**
 * Titre d'étape animé
 */
@Composable
private fun AnimatedStepTitle(title: String, subtitle: String) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(title) {
        visible = false
        delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = 0.8f),
            initialOffsetY = { -it / 2 }
        ) + fadeIn()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Champ de texte animé
 */
@Composable
private fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIconRes: DrawableResource,
    isError: Boolean,
    errorMessage: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    delay: Long = 0L
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = 0.8f),
            initialOffsetY = { it / 3 }
        ) + fadeIn()
    ) {
        CompanyInfoTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            leadingIconRes = leadingIconRes,
            keyboardType = keyboardType,
            isError = isError,
            errorMessage = errorMessage
        )
    }
}

/**
 * Section logo améliorée
 */
@Composable
fun CompanyLogoSection(logoFile: KmpFile?, onLogoFileChange: (KmpFile?) -> Unit) {
    val scope = rememberCoroutineScope()
    val imageLoader = ImageLoader.Builder(coil3.compose.LocalPlatformContext.current)
        .components { add(KmpFileFetcher.Factory()) }
        .build()

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { onLogoFileChange(it) }
            }
        }
    )

    var sectionVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(200)
        sectionVisible = true
    }

    AnimatedVisibility(
        visible = sectionVisible,
        enter = scaleIn(
            animationSpec = spring(dampingRatio = 0.7f)
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Logo de l'entreprise",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (logoFile != null) Color.Transparent 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (logoFile != null) {
                        AsyncImage(
                            imageLoader = imageLoader,
                            model = logoFile,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Logo de l'entreprise sélectionné"
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.ic_image),
                            contentDescription = "Aucun logo sélectionné",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    if (logoFile != null) "Logo sélectionné" else "Aucun logo sélectionné",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ModernButton(
                    text = if (logoFile != null) "Changer le logo" else "Téléverser le logo",
                    onClick = { pickerLauncher.launch() },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = false,
                    icon = Res.drawable.ic_image
                )
            }
        }
    }
}

/**
 * Champ de texte amélioré avec design moderne
 */
@Composable
fun CompanyInfoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIconRes: DrawableResource,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var triggerShake by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isError) {
        if (isError) {
            triggerShake = true
        }
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = tween(300),
        label = "border_color"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                if (triggerShake) triggerShake = false
            },
            label = { 
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                ) 
            },
            leadingIcon = { 
                Icon(
                    painterResource(leadingIconRes), 
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error 
                           else if (isFocused) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .shake(triggerShake),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        
        AnimatedVisibility(
            visible = isError,
            enter = slideInVertically(
                animationSpec = spring(dampingRatio = 0.8f),
                initialOffsetY = { -it }
            ) + fadeIn()
        ) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Étape 2 améliorée
 */
@Composable
fun ContactInfoSetupStep(
    address: String, onAddressChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    province: String, onProvinceChange: (String) -> Unit,
    country: String, onCountryChange: (String) -> Unit,
    phone1: String, onPhone1Change: (String) -> Unit,
    isAddressError: Boolean,
    isCityError: Boolean,
    isProvinceError: Boolean,
    isCountryError: Boolean,
    isPhone1Error: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        AnimatedStepTitle(
            title = "Coordonnées",
            subtitle = "Renseignez les informations de contact"
        )

        val fields = listOf(
            Triple(address, onAddressChange, "Adresse physique") to Triple(Res.drawable.ic_home, isAddressError, "L'adresse est requise"),
            Triple(city, onCityChange, "Ville") to Triple(Res.drawable.ic_location_city, isCityError, "La ville est requise"),
            Triple(province, onProvinceChange, "Province / Région") to Triple(Res.drawable.ic_public, isProvinceError, "La province/région est requise"),
            Triple(country, onCountryChange, "Pays") to Triple(Res.drawable.ic_flag, isCountryError, "Le pays est requis"),
            Triple(phone1, onPhone1Change, "Téléphone 1") to Triple(Res.drawable.ic_phone, isPhone1Error, "Le numéro de téléphone est requis")
        )

        fields.forEachIndexed { index, (fieldData, errorData) ->
            AnimatedTextField(
                value = fieldData.first,
                onValueChange = fieldData.second,
                label = fieldData.third,
                leadingIconRes = errorData.first,
                isError = errorData.second,
                errorMessage = errorData.third,
                keyboardType = if (fieldData.third.contains("Téléphone")) KeyboardType.Phone else KeyboardType.Text,
                delay = index * 100L
            )
        }
    }
}

/**
 * Étape de confirmation améliorée
 */
@Composable
fun ConfirmationStep(
    companyName: String,
    mainActivity: String,
    idNat: String,
    rccmNrc: String,
    address: String,
    city: String,
    province: String,
    country: String,
    phone1: String,
    taxNumber: String,
    vatRate: String,
    logoUri: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp), 
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedStepTitle(
            title = "Résumé de la configuration",
            subtitle = "Vérifiez vos informations avant de finaliser"
        )

        CompanySummaryCard(
            companyName = companyName,
            mainActivity = mainActivity,
            idNat = idNat,
            rccmNrc = rccmNrc,
            address = address,
            city = city,
            province = province,
            country = country,
            phone1 = phone1,
            taxNumber = taxNumber,
            vatRate = vatRate,
            logoUri = logoUri
        )
    }
}

/**
 * Carte de résumé moderne avec animations
 */
@Composable
fun CompanySummaryCard(
    companyName: String,
    mainActivity: String,
    idNat: String,
    rccmNrc: String,
    address: String,
    city: String,
    province: String,
    country: String,
    phone1: String,
    taxNumber: String,
    vatRate: String,
    logoUri: String?
) {
    val imageLoader = ImageLoader.Builder(coil3.compose.LocalPlatformContext.current)
        .components { add(KmpFileFetcher.Factory()) }
        .build()

    var cardVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        cardVisible = true
    }

    AnimatedVisibility(
        visible = cardVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = 0.7f,
                stiffness = 200f
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section logo avec animation
                if (logoUri != null) {
                    AsyncImage(
                        imageLoader = imageLoader,
                        model = logoUri,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Logo de l'entreprise"
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_business),
                            contentDescription = "Aucun logo d'entreprise",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Nom et activité
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        companyName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        mainActivity,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                // Sections d'informations avec animations séquentielles
                AnimatedSummarySection(
                    title = "Informations légales",
                    delay = 100L
                ) {
                    InfoRow(label = "ID.NAT:", value = idNat)
                    InfoRow(label = "RCCM/NRC:", value = rccmNrc)
                    InfoRow(label = "N° d'impôt/TVA:", value = taxNumber)
                    InfoRow(label = "Taux de TVA:", value = "$vatRate%")
                }

                AnimatedSummarySection(
                    title = "Coordonnées",
                    delay = 200L
                ) {
                    InfoRow(label = "Adresse:", value = address)
                    InfoRow(label = "Ville:", value = city)
                    InfoRow(label = "Province:", value = province)
                    InfoRow(label = "Pays:", value = country)
                    InfoRow(label = "Téléphone:", value = phone1)
                }
            }
        }
    }
}

/**
 * Section de résumé animée
 */
@Composable
private fun AnimatedSummarySection(
    title: String,
    delay: Long = 0L,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = 0.8f),
            initialOffsetY = { it / 2 }
        ) + fadeIn()
    ) {
        SummaryCard(title = title, content = content)
    }
}

/**
 * Carte de résumé moderne
 */
@Composable
fun SummaryCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp), 
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                title, 
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

/**
 * Ligne d'information améliorée
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, 
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ), 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value, 
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}