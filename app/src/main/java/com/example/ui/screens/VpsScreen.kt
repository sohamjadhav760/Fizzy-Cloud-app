package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CartItemEntity
import com.example.data.db.VpsPlanEntity
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpsScreen(
    viewModel: MainViewModel,
    onNavigateToCart: () -> Unit
) {
    val vpsPlans by viewModel.vpsPlans.collectAsState()
    var selectedVpsPlanForConfig by remember { mutableStateOf<VpsPlanEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vps_screen_column"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "VPS Hosting",
                subtitle = "High Performance KVM Virtual Private Servers with dedicated NVMe storage and root access."
            )
        }

        // VPS Plans
        items(vpsPlans) { plan ->
            VpsPlanCard(
                plan = plan,
                onConfigure = { selectedVpsPlanForConfig = plan }
            )
        }
    }

    // Configurator Dialog
    if (selectedVpsPlanForConfig != null) {
        ConfigureVpsDialog(
            plan = selectedVpsPlanForConfig!!,
            onDismiss = { selectedVpsPlanForConfig = null },
            onAddToCart = { cartItem ->
                viewModel.addToCart(cartItem)
                selectedVpsPlanForConfig = null
                onNavigateToCart()
            }
        )
    }
}

@Composable
private fun VpsPlanCard(
    plan: VpsPlanEntity,
    onConfigure: () -> Unit
) {
    GlowCard(borderColor = if (plan.badge.isNotEmpty()) AmberAccent else MaterialTheme.colorScheme.outline) {
        Column {
            if (plan.badge.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    BadgeChip(text = plan.badge, containerColor = AmberAccent, contentColor = Color.Black)
                }
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
                    text = "₹${plan.priceMonthly.toInt()}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberAccent
                )
                Text(
                    text = "/month",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VpsSpecItem(icon = Icons.Default.Speed, label = "vCPU", value = "${plan.cpuCores} Cores")
                VpsSpecItem(icon = Icons.Default.Memory, label = "RAM", value = "${plan.ramGb} GB")
                VpsSpecItem(icon = Icons.Default.Storage, label = "NVMe", value = "${plan.storageNvmeGb} GB")
                VpsSpecItem(icon = Icons.Default.SwapVert, label = "Bandwidth", value = "${plan.bandwidthTb} TB")
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryGamingButton(
                text = "Configure VPS",
                onClick = onConfigure,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Settings
            )
        }
    }
}

@Composable
private fun VpsSpecItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigureVpsDialog(
    plan: VpsPlanEntity,
    onDismiss: () -> Unit,
    onAddToCart: (CartItemEntity) -> Unit
) {
    var selectedOs by remember { mutableStateOf("Ubuntu 22.04") }
    var selectedLocation by remember { mutableStateOf("India (Mumbai)") }
    var enableBackup by remember { mutableStateOf(false) }

    var finalPrice = plan.priceMonthly
    if (enableBackup) finalPrice += 100.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure ${plan.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Operating System:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column {
                    listOf("Ubuntu 22.04 LTS", "Debian 12", "AlmaLinux 9", "Windows Server 2022").forEach { os ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedOs == os, onClick = { selectedOs = os })
                            Text(os, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Data Center Region:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("India", "Singapore", "Germany", "US East").forEach { loc ->
                        FilterChip(
                            selected = selectedLocation.contains(loc),
                            onClick = { selectedLocation = loc },
                            label = { Text(loc, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = enableBackup, onCheckedChange = { enableBackup = it })
                    Text("Automatic Daily Server Backups (+₹100/mo)", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = AmberAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Monthly Total:", fontWeight = FontWeight.Bold)
                        Text("₹${finalPrice.toInt()} / mo", fontWeight = FontWeight.Black, color = AmberAccent, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val summary = "$selectedOs | $selectedLocation | Port ${plan.portSpeed}"
                    val cartItem = CartItemEntity(
                        productType = "VPS",
                        productId = plan.id,
                        productName = plan.name,
                        billingCycle = "MONTHLY",
                        configSummary = summary,
                        unitPrice = finalPrice,
                        quantity = 1
                    )
                    onAddToCart(cartItem)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = Color.Black)
            ) {
                Text("Add to Cart", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
