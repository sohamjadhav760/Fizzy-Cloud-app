package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.db.DomainTldEntity
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.EmeraldPrimary

@Composable
fun DomainScreen(
    viewModel: MainViewModel,
    onNavigateToCart: () -> Unit
) {
    val tlds by viewModel.domainTlds.collectAsState()
    var searchDomainQuery by remember { mutableStateOf("") }
    var searchResultDomain by remember { mutableStateOf<String?>(null) }
    var isAvailable by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("domain_screen_column"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header & Domain Search ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                SectionHeader(
                    title = "Domain Name Registration",
                    subtitle = "Claim your custom IP address for your Minecraft server or brand."
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlowCard(borderColor = CyanSecondary) {
                    Column {
                        Text("Search Domain Availability", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = searchDomainQuery,
                            onValueChange = { searchDomainQuery = it },
                            placeholder = { Text("e.g. dragoncraft.in") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PrimaryGamingButton(
                            text = "Search Domain",
                            onClick = {
                                if (searchDomainQuery.isNotBlank()) {
                                    val cleaned = searchDomainQuery.trim().lowercase()
                                    val domainToTest = if (cleaned.contains(".")) cleaned else "$cleaned.com"
                                    searchResultDomain = domainToTest
                                    isAvailable = !domainToTest.contains("minecraft") && !domainToTest.contains("hypixel")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Public
                        )
                    }
                }
            }
        }

        // --- Search Result Box ---
        if (searchResultDomain != null) {
            item {
                val domainName = searchResultDomain!!
                val tldExt = "." + domainName.substringAfterLast(".", "com")
                val matchedTld = tlds.find { it.tld == tldExt } ?: tlds.firstOrNull()
                val price = matchedTld?.registerPrice ?: 799.0

                GlowCard(borderColor = if (isAvailable) EmeraldPrimary else MaterialTheme.colorScheme.error) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (isAvailable) EmeraldPrimary else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = domainName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAvailable) "Domain is available for immediate registration!" else "Sorry, domain is taken. Try another name.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isAvailable) {
                            Button(
                                onClick = {
                                    val cartItem = CartItemEntity(
                                        productType = "DOMAIN",
                                        productId = matchedTld?.id ?: 1,
                                        productName = "Domain: $domainName",
                                        billingCycle = "YEARLY",
                                        configSummary = "1 Year Registration + Free DNS",
                                        unitPrice = price,
                                        quantity = 1
                                    )
                                    viewModel.addToCart(cartItem)
                                    onNavigateToCart()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                            ) {
                                Text("Add (₹${price.toInt()}/yr)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- TLD Pricing Grid ---
        item {
            Text("Popular TLD Pricing", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        items(tlds) { tld ->
            GlowCard(borderColor = MaterialTheme.colorScheme.outline) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = tld.tld,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = CyanSecondary
                        )
                        Text(
                            text = tld.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${tld.registerPrice.toInt()}/yr",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "Renew ₹${tld.renewPrice.toInt()} | Transfer ₹${tld.transferPrice.toInt()}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = {
                                val exampleDomain = "myserver${(10..99).random()}${tld.tld}"
                                val item = CartItemEntity(
                                    productType = "DOMAIN",
                                    productId = tld.id,
                                    productName = "Domain Registration ($exampleDomain)",
                                    billingCycle = "YEARLY",
                                    configSummary = "TLD ${tld.tld} | Free DNS Management",
                                    unitPrice = tld.registerPrice,
                                    quantity = 1
                                )
                                viewModel.addToCart(item)
                                onNavigateToCart()
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Register", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
