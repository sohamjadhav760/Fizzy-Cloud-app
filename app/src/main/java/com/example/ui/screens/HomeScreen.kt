package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val settings by viewModel.websiteSettings.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val dbFaqs by viewModel.allFaqs.collectAsState()
    val dbTestimonials by viewModel.allTestimonials.collectAsState()
    val dbAnnouncements by viewModel.allAnnouncements.collectAsState()

    val heroHeadline = settings.find { it.keyName == "hero_headline" }?.value ?: "Powerful Hosting. Built for Gamers."
    val heroSubheading = settings.find { it.keyName == "hero_subheading" }?.value ?: "Reliable Minecraft Servers, Domains and VPS Hosting at affordable prices."
    val heroBtnText = settings.find { it.keyName == "hero_button_text" }?.value ?: "Browse Minecraft Plans"
    val featuresRaw = settings.find { it.keyName == "features_json" }?.value ?: "High Performance,24/7 Support,DDoS Protection,Instant Setup,99.9% Uptime,Affordable Pricing"

    val featuresList = featuresRaw.split(",").filter { it.isNotBlank() }
    val activeAnnouncements = dbAnnouncements.filter { it.isActive }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // --- 1. HERO SECTION ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    BadgeChip(text = "⚡ NEXT-GEN MINECRAFT & VPS HOSTING", containerColor = EmeraldPrimary, contentColor = Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = heroHeadline,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = heroSubheading,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrimaryGamingButton(
                            text = "Browse Minecraft Plans",
                            onClick = { onNavigate("minecraft") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Dns
                        )
                        SecondaryGamingButton(
                            text = "View VPS Plans",
                            onClick = { onNavigate("vps") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Storage
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stylized Gaming Canvas Illustration Banner
                    HeroGamingCanvas()
                }
            }
        }

        // --- 2. HOSTING CATEGORIES ---
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                SectionHeader(
                    title = "Hosting Categories",
                    subtitle = "Select the right server infrastructure for your Minecraft world or business"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Minecraft Card
                CategoryCard(
                    title = "Minecraft Servers",
                    description = "High-performance Paper, Purpur & Fabric nodes with zero lag.",
                    buttonText = "View MC Plans",
                    icon = Icons.Default.Dns,
                    accentColor = EmeraldPrimary,
                    onClick = { onNavigate("minecraft") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Domains Card
                CategoryCard(
                    title = "Domains",
                    description = "Register your perfect server domain (.com, .in, .net, .org, .xyz).",
                    buttonText = "Search Domain",
                    icon = Icons.Default.Public,
                    accentColor = CyanSecondary,
                    onClick = { onNavigate("domains") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // VPS Card
                CategoryCard(
                    title = "VPS Hosting",
                    description = "Powerful Virtual Private Servers with full root access & NVMe storage.",
                    buttonText = "View VPS Plans",
                    icon = Icons.Default.Storage,
                    accentColor = AmberAccent,
                    onClick = { onNavigate("vps") }
                )
            }
        }

        // --- LIVE SERVER INFRASTRUCTURE STATUS ---
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                val onlineCount = services.count { ServerStatus.fromString(it.status) == ServerStatus.ONLINE }
                val offlineCount = services.count { ServerStatus.fromString(it.status) == ServerStatus.OFFLINE }
                val maintenanceCount = services.count { ServerStatus.fromString(it.status) == ServerStatus.MAINTENANCE }

                SectionHeader(
                    title = "System Status & Nodes",
                    subtitle = "Real-time network node operational health across global regions"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ServerStatusSummaryCard(
                    onlineCount = onlineCount.coerceAtLeast(12),
                    offlineCount = offlineCount,
                    maintenanceCount = maintenanceCount
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Datacenter region indicators
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val datacenterNodes = listOf(
                        Triple("Singapore Gaming Node (SG-1)", "ONLINE", "12 ms"),
                        Triple("Mumbai VPS Cluster (IN-1)", "ONLINE", "18 ms"),
                        Triple("Frankfurt MC Node (DE-2)", "MAINTENANCE", "Upgrades"),
                        Triple("US-East High RAM Node (US-1)", "ONLINE", "42 ms")
                    )

                    datacenterNodes.forEach { (nodeName, statusStr, ping) ->
                        val nodeStatus = ServerStatus.fromString(statusStr)
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, nodeStatus.primaryColor.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(nodeStatus.primaryColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = nodeName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = nodeStatus.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = ping,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = nodeStatus.primaryColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    ServerStatusBadge(statusString = statusStr, showPulsingDot = true)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. WHY CHOOSE US? ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                SectionHeader(
                    title = "Why Choose Us?",
                    subtitle = "Engineered for maximum uptime, lowest ping, and total reliability"
                )

                Spacer(modifier = Modifier.height(16.dp))

                val featureIcons = listOf(
                    Icons.Default.Speed to "High Performance",
                    Icons.Default.HeadsetMic to "24/7 Gaming Support",
                    Icons.Default.Shield to "Enterprise DDoS Protection",
                    Icons.Default.FlashOn to "Instant 60s Setup",
                    Icons.Default.CheckCircle to "99.9% Uptime Guarantee",
                    Icons.Default.AttachMoney to "Affordable Pricing"
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    featureIcons.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { (icon, label) ->
                                FeatureBox(
                                    icon = icon,
                                    label = label,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. TESTIMONIALS ---
        val activeTestimonials = dbTestimonials.filter { it.isActive }
        if (activeTestimonials.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionHeader(
                        title = "Community Feedback",
                        subtitle = "Trusted by thousands of Minecraft server owners and gaming networks"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(activeTestimonials) { item ->
                            GlowCard(
                                modifier = Modifier.width(260.dp),
                                borderColor = EmeraldPrimary.copy(alpha = 0.3f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.authorName,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp
                                        )
                                        if (item.authorRole.isNotBlank()) {
                                            Text(
                                                text = item.authorRole,
                                                fontSize = 11.sp,
                                                color = EmeraldPrimary
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.reviewText,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 5. FREQUENTLY ASKED QUESTIONS ---
        val activeFaqs = dbFaqs.filter { it.isActive }
        if (activeFaqs.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp)
                ) {
                    SectionHeader(
                        title = "Frequently Asked Questions",
                        subtitle = "Everything you need to know about setting up your Minecraft server"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    activeFaqs.forEach { faq ->
                        FaqAccordionItem(question = faq.question, answer = faq.answer)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }


        // --- 6. FINAL CTA BANNER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(EmeraldDark, EmeraldPrimary)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ready to Build Your Minecraft Kingdom?",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Instant setup within 60 seconds. Full panel control and 24/7 support.",
                        color = Color.Black.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigate("minecraft") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Get Started Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    description: String,
    buttonText: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlowCard(borderColor = accentColor.copy(alpha = 0.5f), onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buttonText,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureBox(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FaqAccordionItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    GlowCard(
        borderColor = if (isExpanded) EmeraldPrimary else MaterialTheme.colorScheme.outline,
        onClick = { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = EmeraldPrimary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun HeroGamingCanvas() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Background grid lines
            val step = 30f
            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0x2210B981),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += step
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0x2210B981),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += step
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "ULTRA NVMe MC NODES",
                    fontWeight = FontWeight.Black,
                    color = EmeraldGlow,
                    fontSize = 16.sp
                )
                Text(
                    text = "Ryzen 9 7950X3D • DDR5 RAM • 1.2 Tbps DDoS Guard",
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            }
        }
    }
}
