package com.example.grampet.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navDeepLink
import com.example.grampet.viewmodel.AuthViewModel
import com.example.grampet.viewmodel.PostsViewModel
import com.example.grampet.viewmodel.Post
import com.example.grampet.viewmodel.PostsViewModelFactory

// --- Las definiciones de las pantallas internas (DashboardScreen) no cambian ---
sealed class DashboardScreen(val route: String, val label: String, val icon: ImageVector) {
    data object Feed : DashboardScreen("feed", "Feed", Icons.Default.Home)
    data object Upload : DashboardScreen("upload", "Subir", Icons.Default.AddCircle)
    data object Notifications : DashboardScreen("notifications", "Avisos", Icons.Default.Notifications)
    data object Profile : DashboardScreen("profile", "Perfil", Icons.Default.Person)
}
val dashboardScreens = listOf(DashboardScreen.Feed, DashboardScreen.Upload, DashboardScreen.Notifications, DashboardScreen.Profile)


@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val postsViewModel: PostsViewModel = viewModel(factory = PostsViewModelFactory())

    val posts by postsViewModel.posts.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState.isUserLoggedIn) {
        if (!authState.isUserLoggedIn && authState.email.isEmpty()) {
            onLogoutSuccess()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                dashboardScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = DashboardScreen.Feed.route, Modifier.padding(innerPadding)) {
            composable(DashboardScreen.Feed.route) { FeedScreen(posts = posts) }

            composable(DashboardScreen.Upload.route) {
                UploadScreen(onPostPublished = { bitmap, description ->
                    postsViewModel.addPost(bitmap, description)
                    navController.navigate(DashboardScreen.Feed.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                    }
                })
            }

            composable(
                route = DashboardScreen.Profile.route,
                deepLinks = listOf(navDeepLink { uriPattern = "android-app://androidx.navigation/profile" })
            ) {
                ProfileScreen(
                    allPosts = posts,
                    onLogoutClicked = { authViewModel.logout() }
                )
            }
            
            composable(DashboardScreen.Notifications.route) {
                NotificationsScreen()
            }
        }
    }
}


// (El resto de los Composables de MainScreen.kt, como UploadScreen, no necesitan cambios)

@Composable
fun UploadScreen(onPostPublished: (Bitmap, String) -> Unit) {
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { newBitmap: Bitmap? -> bitmap = newBitmap }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) cameraLauncher.launch(null)
        }
    )

    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    bitmap = currentBitmap.asImageBitmap(),
                    contentDescription = "Foto capturada",
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Añade una descripción...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onPostPublished(currentBitmap, description) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar Foto")
                }
            }
        } else {
            CameraButton(onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> cameraLauncher.launch(null)
                    else -> permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            })
        }
    }
}

@Composable
private fun CameraButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(Icons.Outlined.CameraAlt, contentDescription = "Tomar foto")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Tomar una Foto")
    }
}