package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserEntity
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNavbar(
    currentTab: String,
    onNavigate: (String) -> Unit,
    cartCount: Int,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    onSwitchUser: (String) -> Unit
) {
    var showUserMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigate("home") }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Fizzy Cloud Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fizzy Cloud",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            // Action Icons (Theme, Cart, Account Switcher)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Theme Toggle
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("theme_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Cart Icon with Badge
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.Black
                            ) {
                                Text(text = "$cartCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    IconButton(
                        onClick = { onNavigate("cart") },
                        modifier = Modifier.testTag("cart_nav_btn")
                    ) {
                        Icon(
                            imageVector = if (currentTab == "cart") Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = if (currentTab == "cart") EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // User Account / Role Switcher Menu
                Box {
                    IconButton(
                        onClick = { showUserMenu = true },
                        modifier = Modifier.testTag("user_menu_btn")
                    ) {
                        Icon(
                            imageVector = if (currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "MANAGER" || currentUser?.role == "SUPPORT") {
                                Icons.Filled.AdminPanelSettings
                            } else {
                                Icons.Filled.AccountCircle
                            },
                            contentDescription = "Account",
                            tint = if (currentUser?.role == "SUPER_ADMIN") EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showUserMenu,
                        onDismissRequest = { showUserMenu = false }
                    ) {
                        Text(
                            text = "Switch Persona / Role:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        HorizontalDivider()

                        allUsers.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = user.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${user.email} (${user.role})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onSwitchUser(user.email)
                                    showUserMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (user.role == "SUPER_ADMIN") Icons.Default.VerifiedUser else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (user.email == currentUser?.email) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: String,
    onNavigate: (String) -> Unit,
    userRole: String?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        val navItems = mutableListOf(
            NavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
            NavItem("minecraft", "Minecraft", Icons.Filled.Dns, Icons.Outlined.Dns),
            NavItem("domains", "Domains", Icons.Filled.Public, Icons.Outlined.Public),
            NavItem("vps", "VPS", Icons.Filled.Storage, Icons.Outlined.Storage),
            NavItem("dashboard", "Portal", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
        )

        // Show Admin tab for Admin/Manager/Support roles
        if (userRole == "SUPER_ADMIN" || userRole == "MANAGER" || userRole == "SUPPORT") {
            navItems.add(NavItem("admin", "Admin", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings))
        }

        navItems.forEach { item ->
            val isSelected = currentTab == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = EmeraldPrimary.copy(alpha = 0.2f)
                )
            )
        }
    }
}

private data class NavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
