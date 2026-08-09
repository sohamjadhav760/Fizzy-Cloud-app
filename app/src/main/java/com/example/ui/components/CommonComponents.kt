package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Surface(
        modifier = clickableModifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(16.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            content()
        }
    }
}

@Composable
fun BadgeChip(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.Black
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PrimaryGamingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .testTag("primary_btn_${text.lowercase().replace(" ", "_")}"),
        colors = ButtonDefaults.buttonColors(
            containerColor = EmeraldPrimary,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun SecondaryGamingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        border = BorderStroke(1.5.dp, EmeraldPrimary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: ImageVector,
    iconTint: Color = EmeraldPrimary,
    modifier: Modifier = Modifier
) {
    GlowCard(modifier = modifier, borderColor = iconTint.copy(alpha = 0.4f)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        color = iconTint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String = "",
    badge: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                BadgeChip(text = badge, containerColor = EmeraldPrimary, contentColor = Color.Black)
            }
        }
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

// --- SERVER STATUS INDICATOR SYSTEM ---

enum class ServerStatus(
    val rawKey: String,
    val displayLabel: String,
    val primaryColor: Color,
    val containerColor: Color,
    val icon: ImageVector,
    val description: String
) {
    ONLINE(
        rawKey = "RUNNING",
        displayLabel = "Online",
        primaryColor = Color(0xFF10B981), // Emerald Green
        containerColor = Color(0x2810B981),
        icon = Icons.Default.CheckCircle,
        description = "Server is active and accepting connections."
    ),
    OFFLINE(
        rawKey = "STOPPED",
        displayLabel = "Offline",
        primaryColor = Color(0xFFEF4444), // Red
        containerColor = Color(0x28EF4444),
        icon = Icons.Default.Cancel,
        description = "Server is stopped or powered off."
    ),
    MAINTENANCE(
        rawKey = "MAINTENANCE",
        displayLabel = "Maintenance",
        primaryColor = Color(0xFFF59E0B), // Amber / Orange
        containerColor = Color(0x28F59E0B),
        icon = Icons.Default.Build,
        description = "Scheduled maintenance or node upgrades in progress."
    );

    companion object {
        fun fromString(statusStr: String): ServerStatus {
            val normalized = statusStr.uppercase().trim()
            return when {
                normalized == "RUNNING" || normalized == "ONLINE" || normalized == "ACTIVE" -> ONLINE
                normalized == "STOPPED" || normalized == "OFFLINE" || normalized == "INACTIVE" -> OFFLINE
                normalized == "MAINTENANCE" || normalized == "SUSPENDED" || normalized == "MAINT" || normalized == "BUILDING" -> MAINTENANCE
                else -> OFFLINE
            }
        }
    }
}

@Composable
fun ServerStatusBadge(
    statusString: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    showPulsingDot: Boolean = true
) {
    val status = ServerStatus.fromString(statusString)
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val alphaAnimation by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Surface(
        modifier = modifier.testTag("server_status_badge_${status.name.lowercase()}"),
        color = status.containerColor,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, status.primaryColor.copy(alpha = 0.6f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            if (showPulsingDot && status == ServerStatus.ONLINE) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(status.primaryColor.copy(alpha = alphaAnimation))
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else if (showIcon) {
                Icon(
                    imageVector = status.icon,
                    contentDescription = status.displayLabel,
                    tint = status.primaryColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = status.displayLabel,
                color = status.primaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ServerStatusSummaryCard(
    onlineCount: Int,
    offlineCount: Int,
    maintenanceCount: Int,
    modifier: Modifier = Modifier
) {
    GlowCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = EmeraldPrimary.copy(alpha = 0.3f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "Server Network",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Server Infrastructure Network Status",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                ServerStatusBadge(
                    statusString = if (offlineCount == 0 && maintenanceCount == 0) "ONLINE" else if (maintenanceCount > 0) "MAINTENANCE" else "OFFLINE"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Online Summary Pill
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0x1F10B981),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$onlineCount Online",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // Offline Summary Pill
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0x1FEF4444),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$offlineCount Offline",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                // Maintenance Summary Pill
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0x1FF59E0B),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$maintenanceCount Maint.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }
        }
    }
}

