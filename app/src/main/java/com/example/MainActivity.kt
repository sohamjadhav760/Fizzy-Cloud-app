package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.components.AnnouncementBanner
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.TopBarNavbar
import com.example.ui.screens.*
import com.example.ui.theme.FizzyCloudTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()
            val cartItems by viewModel.cartItems.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val allUsers by viewModel.allUsers.collectAsState()
            val websiteSettings by viewModel.websiteSettings.collectAsState()

            val announcementText = websiteSettings.find { it.keyName == "announcement_banner" }?.value ?: ""

            FizzyCloudTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    topBar = {
                        Column {
                            TopBarNavbar(
                                currentTab = currentTab,
                                onNavigate = { viewModel.navigateTo(it) },
                                cartCount = cartItems.sumOf { it.quantity },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { viewModel.toggleTheme() },
                                currentUser = currentUser,
                                allUsers = allUsers,
                                onSwitchUser = { email -> viewModel.switchUserRole(email) }
                            )

                            AnnouncementBanner(text = announcementText)
                        }
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            currentTab = currentTab,
                            onNavigate = { viewModel.navigateTo(it) },
                            userRole = currentUser?.role
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                "home" -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                                "minecraft" -> MinecraftScreen(
                                    viewModel = viewModel,
                                    onNavigateToCart = { viewModel.navigateTo("cart") }
                                )
                                "domains" -> DomainScreen(
                                    viewModel = viewModel,
                                    onNavigateToCart = { viewModel.navigateTo("cart") }
                                )
                                "vps" -> VpsScreen(
                                    viewModel = viewModel,
                                    onNavigateToCart = { viewModel.navigateTo("cart") }
                                )
                                "cart" -> CartCheckoutScreen(
                                    viewModel = viewModel,
                                    onNavigateHome = { viewModel.navigateTo("minecraft") },
                                    onNavigateDashboard = { viewModel.navigateTo("dashboard") }
                                )
                                "dashboard" -> CustomerDashboardScreen(
                                    viewModel = viewModel
                                )
                                "admin" -> AdminDashboardScreen(
                                    viewModel = viewModel
                                )
                                else -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
