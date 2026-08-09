package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CartItemEntity
import com.example.data.db.MinecraftPlanEntity
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinecraftScreen(
    viewModel: MainViewModel,
    onNavigateToCart: () -> Unit
) {
    val plans by viewModel.minecraftPlans.collectAsState()
    var isYearly by remember { mutableStateOf(false) }
    var selectedPlanForConfig by remember { mutableStateOf<MinecraftPlanEntity?>(null) }
    var showCompareSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("minecraft_screen_column"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header & Billing Toggle ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                SectionHeader(
                    title = "Minecraft Server Hosting",
                    subtitle = "Powered by high clock-speed AMD Ryzen CPUs & NVMe SSD storage."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Monthly / Yearly Toggle
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { isYearly = false },
                            color = if (!isYearly) EmeraldPrimary else Color.Transparent,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Monthly",
                                color = if (!isYearly) Color.Black else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            onClick = { isYearly = true },
                            color = if (isYearly) EmeraldPrimary else Color.Transparent,
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Yearly (Save 17%)",
                                    color = if (isYearly) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showCompareSheet = true },
                    border = BorderStroke(1.dp, EmeraldPrimary)
                ) {
                    Icon(imageVector = Icons.Default.CompareArrows, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Compare All Plans", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // --- Plans Cards ---
        items(plans) { plan ->
            MinecraftPlanCard(
                plan = plan,
                isYearly = isYearly,
                onConfigure = { selectedPlanForConfig = plan }
            )
        }
    }

    // --- Configure Server Sheet/Dialog ---
    if (selectedPlanForConfig != null) {
        ConfigureMinecraftDialog(
            plan = selectedPlanForConfig!!,
            isYearly = isYearly,
            onDismiss = { selectedPlanForConfig = null },
            onAddToCart = { configItem ->
                viewModel.addToCart(configItem)
                selectedPlanForConfig = null
                onNavigateToCart()
            }
        )
    }

    // --- Compare Plans Sheet ---
    if (showCompareSheet) {
        CompareMinecraftSheet(
            plans = plans,
            onDismiss = { showCompareSheet = false }
        )
    }
}

@Composable
private fun MinecraftPlanCard(
    plan: MinecraftPlanEntity,
    isYearly: Boolean,
    onConfigure: () -> Unit
) {
    val price = if (isYearly) plan.priceYearly / 12.0 else plan.priceMonthly
    val billingCycleText = if (isYearly) "billed yearly (₹${plan.priceYearly.toInt()}/yr)" else "billed monthly"

    GlowCard(
        borderColor = if (plan.isPopular) EmeraldPrimary else MaterialTheme.colorScheme.outline
    ) {
        Column {
            if (plan.badge.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BadgeChip(text = plan.badge, containerColor = EmeraldPrimary, contentColor = Color.Black)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = plan.name,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = plan.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "₹${price.toInt()}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldPrimary
                )
                Text(
                    text = "/month",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }
            Text(
                text = billingCycleText,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecItem(icon = Icons.Default.Memory, label = "RAM", value = "${plan.ramGb} GB DDR5")
                SpecItem(icon = Icons.Default.Speed, label = "CPU", value = "${plan.cpuCores} Cores")
                SpecItem(icon = Icons.Default.Storage, label = "NVMe", value = "${plan.storageGb} GB")
                SpecItem(icon = Icons.Default.Group, label = "Players", value = plan.playerSlots)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feature Checklist
            val features = plan.features.split(",")
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature.trim(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryGamingButton(
                text = "Buy Now / Configure",
                onClick = onConfigure,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.ShoppingCart
            )
        }
    }
}

@Composable
private fun SpecItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigureMinecraftDialog(
    plan: MinecraftPlanEntity,
    isYearly: Boolean,
    onDismiss: () -> Unit,
    onAddToCart: (CartItemEntity) -> Unit
) {
    var software by remember { mutableStateOf("Paper") }
    var version by remember { mutableStateOf("1.20.4") }
    var serverName by remember { mutableStateOf("My Minecraft Server") }
    var location by remember { mutableStateOf("India (Mumbai)") }
    var dedicatedIpAddon by remember { mutableStateOf(false) }
    var dailyBackupAddon by remember { mutableStateOf(false) }

    val basePrice = if (isYearly) plan.priceYearly else plan.priceMonthly
    var finalPrice = basePrice
    if (dedicatedIpAddon) finalPrice += (if (isYearly) 1000.0 else 100.0)
    if (dailyBackupAddon) finalPrice += (if (isYearly) 500.0 else 50.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Configure ${plan.name} Server", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = serverName,
                    onValueChange = { serverName = it },
                    label = { Text("Server Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Software Selector
                Text(text = "Server Software:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Paper", "Spigot", "Purpur", "Forge", "Fabric").forEach { sw ->
                        FilterChip(
                            selected = software == sw,
                            onClick = { software = sw },
                            label = { Text(sw, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location Selector
                Text(text = "Server Region:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("India", "Singapore", "Germany", "US East").forEach { loc ->
                        FilterChip(
                            selected = location.contains(loc),
                            onClick = { location = loc },
                            label = { Text(loc, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Addons
                Text(text = "Optional Addons:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = dedicatedIpAddon, onCheckedChange = { dedicatedIpAddon = it })
                    Text("Dedicated IP (+₹100/mo)", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = dailyBackupAddon, onCheckedChange = { dailyBackupAddon = it })
                    Text("Automated Daily Snapshots (+₹50/mo)", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Configured Price:", fontWeight = FontWeight.Bold)
                        Text(
                            "₹${finalPrice.toInt()} / ${if (isYearly) "year" else "month"}",
                            fontWeight = FontWeight.Black,
                            color = EmeraldPrimary,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val summary = "$software $version | $location | $serverName"
                    val item = CartItemEntity(
                        productType = "MINECRAFT",
                        productId = plan.id,
                        productName = "Minecraft ${plan.name} (${plan.ramGb}GB)",
                        billingCycle = if (isYearly) "YEARLY" else "MONTHLY",
                        configSummary = summary,
                        unitPrice = finalPrice,
                        quantity = 1
                    )
                    onAddToCart(item)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Add to Cart", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CompareMinecraftSheet(
    plans: List<MinecraftPlanEntity>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Compare Minecraft Plans", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(plans) { p ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("${p.name} - ₹${p.priceMonthly.toInt()}/mo", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Text("RAM: ${p.ramGb}GB | CPU: ${p.cpuCores} Cores | Storage: ${p.storageGb}GB NVMe", fontSize = 12.sp)
                            Text("Slots: ${p.playerSlots} | Features: ${p.features}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
