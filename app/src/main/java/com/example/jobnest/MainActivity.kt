package com.example.jobnest

import com.example.jobnest.viewmodel.LocationViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobnest.auth.*
import com.example.jobnest.main.owner.MapPickerScreen
import com.example.jobnest.main.owner.MyJobsScreen
import com.example.jobnest.main.owner.PostJobScreen
import com.example.jobnest.main.screens.*
import com.example.jobnest.ui.theme.JobnestTheme
import com.example.jobnest.viewmodel.AuthViewModel
import com.example.jobnest.viewmodel.JobViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

// Premium Color Palette
private val PrimaryBlue = Color(0xFF2E5BFF)
private val LightBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val AccentGreen = Color(0xFF10B981)
private val InactiveGray = Color(0xFF9CA3AF)
private val SoftBackground = Color(0xFFF8F9FD)

class MainActivity : ComponentActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken!!
            authViewModel.signInWithGoogle(idToken)
        } catch (e: ApiException) {
            Log.w("MainActivity", "Google sign in failed", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasLocationPermission()) {
            requestLocationPermission()
        }

        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        enableEdgeToEdge()
        setContent {
            JobnestTheme {
                JobNestApp(
                    modifier = Modifier.fillMaxSize(),
                    onGoogleSignInClicked = {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                )
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}

@Composable
fun JobNestApp(
    modifier: Modifier = Modifier,
    onGoogleSignInClicked: () -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val jobViewModel: JobViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    var jobPostRefreshTrigger by remember { mutableStateOf(0) }

    NavHost(navController, startDestination = "splash", modifier = modifier) {

        composable("splash") {
            val authState by authViewModel.authState.collectAsState()
            SplashScreen(
                onSplashComplete = {
                    val destination = if (authState.currentUserData != null) "main" else "login"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onSignUpClicked = { navController.navigate("signup") },
                onStudentLoginSuccess = { navController.navigate("main?userType=student") { popUpTo("login") { inclusive = true } } },
                onOwnerLoginSuccess = { navController.navigate("main?userType=owner") { popUpTo("login") { inclusive = true } } },
                onForgotPasswordClicked = { navController.navigate("forgot_password") },
                onGoogleSignInClicked = onGoogleSignInClicked,
                viewModel = authViewModel
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignInClicked = { navController.navigate("login") },
                onStudentSignUpSuccess = { navController.navigate("main?userType=student") { popUpTo(0) { inclusive = true } } },
                onJobCoordinatorSignUpSuccess = { navController.navigate("main?userType=owner") { popUpTo(0) { inclusive = true } } },
                viewModel = authViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onSendClicked = { navController.navigateUp() },
                onBackToLoginClicked = { navController.navigateUp() }
            )
        }

        composable("notifications") {
            NotificationsScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }

        composable("about") {
            AboutScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }

        composable("main?userType={userType}") { entry ->
            val userType = entry.arguments?.getString("userType")

            val shouldRefresh by entry.savedStateHandle
                .getStateFlow("should_refresh_jobs", false)
                .collectAsState()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    jobPostRefreshTrigger++
                    entry.savedStateHandle["should_refresh_jobs"] = false
                }
            }

            MainScreen(
                initialUserType = userType,
                jobViewModel = jobViewModel,
                refreshTrigger = jobPostRefreshTrigger,
                onNavigateToPostJob = { navController.navigate("post_job") },
                onNavigateToEditProfile = { navController.navigate("edit_profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSecurity = { navController.navigate("security") },
                onNavigateToAbout = { navController.navigate("about") },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate("login") { popUpTo("main") { inclusive = true } }
                }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                onBackPressed = { navController.navigateUp() },
                onOpenMapPicker = { navController.navigate("map_picker_profile") },
                selectedAddress = "",
                selectedLatLng = null
            )
        }

        composable("map_picker_profile") {
            MapPickerScreen(
                onLocationSelected = { _, _ -> navController.navigateUp() },
                onBackPressed = { navController.navigateUp() }
            )
        }

        composable("post_job") {
            val locationState by locationViewModel.locationState.collectAsState()

            val savedStateHandle = it.savedStateHandle
            val selectedAddress = savedStateHandle.get<String>("selected_address") ?: ""
            val selectedLat = savedStateHandle.get<Double>("selected_lat")
            val selectedLng = savedStateHandle.get<Double>("selected_lng")
            val selectedLatLng = if (selectedLat != null && selectedLng != null) {
                LatLng(selectedLat, selectedLng)
            } else null

            PostJobScreen(
                selectedAddress = selectedAddress,
                selectedLatLng = selectedLatLng,
                onOpenMapPicker = { navController.navigate("map_picker") },
                onBackPressed = { navController.navigateUp() },
                onJobPostedSuccessfully = {
                    locationViewModel.clearLocation()
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("should_refresh_jobs", true)
                    navController.navigateUp()
                }
            )
        }

        composable("map_picker") {
            MapPickerScreen(
                onLocationSelected = { address, latLng ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("selected_address", address)
                        set("selected_lat", latLng.latitude)
                        set("selected_lng", latLng.longitude)
                    }
                    locationViewModel.setLocation(address, latLng)
                    navController.navigateUp()
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}

@Composable
fun MainScreen(
    initialUserType: String?,
    jobViewModel: JobViewModel,
    refreshTrigger: Int,
    onNavigateToPostJob: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isOwner = initialUserType == "owner" || authState.currentUserData?.userType == "owner"
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = SoftBackground,
        bottomBar = {
            PremiumBottomNavigationBar(
                isOwner = isOwner,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToPostJob = onNavigateToPostJob
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isOwner) {
                when (selectedTab) {
                    0 -> MyJobsScreen(
                        onSwitchView = {},
                        refreshTrigger = refreshTrigger
                    )
                    1 -> ProfileScreen(
                        onLogout = onLogout,
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToSecurity = onNavigateToSecurity,
                        onNavigateToAbout = onNavigateToAbout,
                        isOwnerView = true,
                        onSwitchView = {}
                    )
                }
            } else {
                when (selectedTab) {
                    0 -> HomeScreen(
                        onSwitchView = {},
                        viewModel = jobViewModel
                    )
                    1 -> SavedScreen(
                        onSwitchView = {},
                        viewModel = jobViewModel
                    )
                    2 -> SearchScreen(
                        onBackClick = { selectedTab = 0 },
                        onSwitchView = {},
                        viewModel = jobViewModel
                    )
                    3 -> ProfileScreen(
                        onLogout = onLogout,
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToSecurity = onNavigateToSecurity,
                        onNavigateToAbout = onNavigateToAbout,
                        isOwnerView = false,
                        onSwitchView = {}
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumBottomNavigationBar(
    isOwner: Boolean,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToPostJob: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Main navigation container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(34.dp),
                color = Color.White,
                shadowElevation = 16.dp,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isOwner) {
                        // Owner navigation items
                        PremiumNavItem(
                            icon = Icons.Default.Work,
                            label = "My Jobs",
                            selected = selectedTab == 0,
                            onClick = { onTabSelected(0) }
                        )

                        // Center spacer for FAB
                        Spacer(modifier = Modifier.width(72.dp))

                        PremiumNavItem(
                            icon = Icons.Default.Person,
                            label = "Profile",
                            selected = selectedTab == 1,
                            onClick = { onTabSelected(1) }
                        )
                    } else {
                        // Student navigation items
                        PremiumNavItem(
                            icon = Icons.Default.Home,
                            label = "Home",
                            selected = selectedTab == 0,
                            onClick = { onTabSelected(0) }
                        )

                        PremiumNavItem(
                            icon = Icons.Default.Bookmark,
                            label = "Saved",
                            selected = selectedTab == 1,
                            onClick = { onTabSelected(1) }
                        )

                        PremiumNavItem(
                            icon = Icons.Default.Search,
                            label = "Search",
                            selected = selectedTab == 2,
                            onClick = { onTabSelected(2) }
                        )

                        PremiumNavItem(
                            icon = Icons.Default.Person,
                            label = "Profile",
                            selected = selectedTab == 3,
                            onClick = { onTabSelected(3) }
                        )
                    }
                }
            }

            // Floating Action Button (Owner only)
            if (isOwner) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-16).dp)
                ) {
                    PremiumFloatingActionButton(
                        onClick = onNavigateToPostJob
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "navItemScale"
    )

    val iconSize by animateDpAsState(
        targetValue = if (selected) 26.dp else 22.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconSize"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (selected) {
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, AccentPurple)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else InactiveGray,
                modifier = Modifier.size(iconSize)
            )
        }

        // Label (always visible, changes color based on selection)
        Text(
            text = label,
            fontSize = if (selected) 11.sp else 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else InactiveGray
        )

        // Active indicator dot
        if (selected) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(AccentGreen)
            )
        } else {
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun PremiumFloatingActionButton(
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "fabScale"
    )

    val rotation by rememberInfiniteTransition(label = "rotate").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .size(64.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = PrimaryBlue.copy(alpha = 0.3f),
                spotColor = AccentPurple.copy(alpha = 0.3f)
            )
    ) {
        // Rotating gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlue,
                            LightBlue,
                            AccentPurple
                        )
                    )
                )
                .clickable(
                    onClick = {
                        pressed = true
                        onClick()
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Post Job",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        // Subtle pulse ring effect
        val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}