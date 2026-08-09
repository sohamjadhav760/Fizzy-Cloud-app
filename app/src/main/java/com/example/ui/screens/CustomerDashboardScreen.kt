package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.OrderEntity
import com.example.data.db.ServiceEntity
import com.example.data.db.SupportTicketEntity
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    viewModel: MainViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val tickets by viewModel.allTickets.collectAsState()

    var activeSubTab by remember { mutableStateOf("services") } // services, orders, tickets, profile

    var selectedTicketForThread by remember { mutableStateOf<SupportTicketEntity?>(null) }
    var showCreateTicketDialog by remember { mutableStateOf(false) }

    val userServices = services.filter { currentUser == null || it.userId == currentUser?.id }
    val userOrders = orders.filter { currentUser == null || it.userId == currentUser?.id }
    val userTickets = tickets.filter { currentUser == null || it.userId == currentUser?.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customer_dashboard_screen_column"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header & Greeting ---
        item {
            Column {
                SectionHeader(
                    title = "Welcome back, ${currentUser?.name ?: "Customer"}!",
                    subtitle = "Manage your active servers, domains, billing invoices, and support tickets."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Active Services",
                        value = "${userServices.size}",
                        icon = Icons.Default.Dns,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Orders",
                        value = "${userOrders.size}",
                        icon = Icons.Default.Receipt,
                        iconTint = CyanSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // --- Sub Tab Navigation ---
        item {
            TabRow(
                selectedTabIndex = listOf("services", "orders", "tickets", "profile").indexOf(activeSubTab),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                Tab(
                    selected = activeSubTab == "services",
                    onClick = { activeSubTab = "services" },
                    text = { Text("Services (${userServices.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == "orders",
                    onClick = { activeSubTab = "orders" },
                    text = { Text("Orders", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == "tickets",
                    onClick = { activeSubTab = "tickets" },
                    text = { Text("Tickets", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == "profile",
                    onClick = { activeSubTab = "profile" },
                    text = { Text("Profile & Email", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }


        // --- TAB 1: SERVICES ---
        if (activeSubTab == "services") {
            if (userServices.isEmpty()) {
                item {
                    GlowCard {
                        Text("No active hosting services yet. Buy a Minecraft server or domain to get started!")
                    }
                }
            } else {
                val onlineCount = userServices.count { ServerStatus.fromString(it.status) == ServerStatus.ONLINE }
                val offlineCount = userServices.count { ServerStatus.fromString(it.status) == ServerStatus.OFFLINE }
                val maintenanceCount = userServices.count { ServerStatus.fromString(it.status) == ServerStatus.MAINTENANCE }

                item {
                    ServerStatusSummaryCard(
                        onlineCount = onlineCount,
                        offlineCount = offlineCount,
                        maintenanceCount = maintenanceCount
                    )
                }

                items(userServices) { service ->
                    ServiceControlCard(
                        service = service,
                        onUpdateStatus = { newStatus -> viewModel.updateServiceStatus(service.id, newStatus) }
                    )
                }
            }
        }

        // --- TAB 2: ORDERS & INVOICES ---
        if (activeSubTab == "orders") {
            if (userOrders.isEmpty()) {
                item {
                    GlowCard { Text("No order history found.") }
                }
            } else {
                items(userOrders) { order ->
                    OrderInvoiceCard(order = order)
                }
            }
        }

        // --- TAB 3: SUPPORT TICKETS ---
        if (activeSubTab == "tickets") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Support Tickets", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Button(
                        onClick = { showCreateTicketDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open Ticket", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (userTickets.isEmpty()) {
                item { GlowCard { Text("No support tickets open.") } }
            } else {
                items(userTickets) { ticket ->
                    SupportTicketRowCard(
                        ticket = ticket,
                        onClick = { selectedTicketForThread = ticket }
                    )
                }
            }
        }

        // --- TAB 4: PROFILE & EMAIL NOTIFICATIONS ---
        if (activeSubTab == "profile") {
            item {
                val emailStatusToast by viewModel.emailStatusToast.collectAsState()

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    emailStatusToast?.let { toastMsg ->
                        GlowCard(borderColor = EmeraldPrimary) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(toastMsg, fontSize = 12.sp, color = EmeraldPrimary)
                                IconButton(onClick = { viewModel.clearEmailStatusToast() }) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    GlowCard(borderColor = CyanSecondary) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CyanSecondary)
                                Text("Account Profile Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            HorizontalDivider()
                            Text("Name: ${currentUser?.name ?: "Customer"}", fontWeight = FontWeight.SemiBold)
                            Text("Email: ${currentUser?.email ?: "alex@gamer.com"}", fontSize = 13.sp)
                            Text("Account Role: ${currentUser?.role ?: "CUSTOMER"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    GlowCard(borderColor = EmeraldPrimary) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = EmeraldPrimary)
                                Text("Email Notification Services", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Text(
                                "Request automated templated emails to be dispatched to your registered email address:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    viewModel.sendPasswordResetEmail(
                                        email = currentUser?.email ?: "alex@gamer.com",
                                        name = currentUser?.name ?: "Customer"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Request Password Reset Email")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.sendUserRegistrationEmail(
                                        email = currentUser?.email ?: "alex@gamer.com",
                                        name = currentUser?.name ?: "Customer",
                                        role = currentUser?.role ?: "CUSTOMER"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.MarkEmailRead, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Resend Registration Welcome Email")
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Create Ticket Dialog ---
    if (showCreateTicketDialog) {
        CreateTicketDialog(
            onDismiss = { showCreateTicketDialog = false },
            onSubmit = { subject, category, message ->
                viewModel.createSupportTicket(subject, category, message)
                showCreateTicketDialog = false
            }
        )
    }

    // --- Ticket Thread Dialog ---
    if (selectedTicketForThread != null) {
        TicketThreadDialog(
            ticket = selectedTicketForThread!!,
            viewModel = viewModel,
            onDismiss = { selectedTicketForThread = null }
        )
    }
}

@Composable
private fun ServiceControlCard(
    service: ServiceEntity,
    onUpdateStatus: (String) -> Unit
) {
    var isConsoleExpanded by remember { mutableStateOf(false) }
    val currentStatus = ServerStatus.fromString(service.status)

    val displayedCpu = when (currentStatus) {
        ServerStatus.ONLINE -> service.cpuUsagePercent
        ServerStatus.OFFLINE -> 0
        ServerStatus.MAINTENANCE -> 2
    }

    val displayedRam = when (currentStatus) {
        ServerStatus.ONLINE -> service.ramUsageMb
        ServerStatus.OFFLINE -> 0
        ServerStatus.MAINTENANCE -> 120
    }

    GlowCard(borderColor = currentStatus.primaryColor.copy(alpha = 0.5f)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = service.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text(text = service.configDetails, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(8.dp))
                ServerStatusBadge(statusString = service.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // IP Address Bar
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = currentStatus.primaryColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "IP: ${service.ipAddress}", fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Copy", fontSize = 11.sp, color = currentStatus.primaryColor, fontWeight = FontWeight.Bold)
                }
            }

            // Maintenance Banner if applicable
            if (currentStatus == ServerStatus.MAINTENANCE) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0x1FF59E0B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scheduled maintenance or node upgrades in progress. Services may be temporarily unavailable.",
                            fontSize = 11.sp,
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (currentStatus == ServerStatus.OFFLINE) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0x1FEF4444),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Server is currently powered off. Click 'Online' to boot up instances.",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Usage Gauges (CPU / RAM)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CPU Usage ($displayedCpu%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { displayedCpu / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                        color = currentStatus.primaryColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("RAM Usage ($displayedRam MB / ${service.maxRamMb}MB)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { displayedRam.toFloat() / service.maxRamMb },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                        color = CyanSecondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Selector / Control Buttons
            Text(
                text = "Server Power & Status Control:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onUpdateStatus("RUNNING") },
                    enabled = currentStatus != ServerStatus.ONLINE,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Online", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onUpdateStatus("STOPPED") },
                    enabled = currentStatus != ServerStatus.OFFLINE,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Offline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onUpdateStatus("MAINTENANCE") },
                    enabled = currentStatus != ServerStatus.MAINTENANCE,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Maint.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Console Terminal Toggle
            TextButton(
                onClick = { isConsoleExpanded = !isConsoleExpanded },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isConsoleExpanded) "Hide Live Console" else "View Live Console", fontSize = 12.sp)
            }

            AnimatedVisibility(visible = isConsoleExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    val logText = when (currentStatus) {
                        ServerStatus.ONLINE -> "[INFO] Starting Minecraft server version 1.20.4...\n[INFO] Loading properties from server.properties\n[INFO] Preparing level 'world'\n[INFO] Done (2.102s)! For help, type \"help\"\n[INFO] Player Alex joined the game (142.93.201.88)"
                        ServerStatus.OFFLINE -> "[SYSTEM] Server power state: SHUTDOWN COMPLETE\n[SYSTEM] Container process killed gracefully.\n[SYSTEM] Awaiting boot command from control panel..."
                        ServerStatus.MAINTENANCE -> "[MAINTENANCE] Kernel upgrades in progress on Node-SG1.\n[MAINTENANCE] Storage sync: 100% completed.\n[MAINTENANCE] Node health check underway..."
                    }
                    Text(
                        text = logText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = currentStatus.primaryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderInvoiceCard(order: OrderEntity) {
    GlowCard(borderColor = MaterialTheme.colorScheme.outline) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = order.orderId, fontWeight = FontWeight.Black, fontSize = 16.sp, color = EmeraldPrimary)
                BadgeChip(text = order.status, containerColor = EmeraldPrimary, contentColor = Color.Black)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = order.itemsSummary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = "Paid via ${order.paymentMethod}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                OutlinedButton(onClick = { }, modifier = Modifier.height(32.dp)) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF Invoice", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun SupportTicketRowCard(ticket: SupportTicketEntity, onClick: () -> Unit) {
    GlowCard(borderColor = MaterialTheme.colorScheme.outline, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ticket.ticketId, fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 13.sp)
                Text(text = ticket.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "Category: ${ticket.category} | Priority: ${ticket.priority}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            BadgeChip(
                text = ticket.status,
                containerColor = if (ticket.status == "OPEN") CyanSecondary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTicketDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technical Support") }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open Support Ticket", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Describe your issue...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (subject.isNotBlank()) onSubmit(subject, category, message) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Submit Ticket", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketThreadDialog(
    ticket: SupportTicketEntity,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val messages by viewModel.getTicketMessages(ticket.ticketId).collectAsState(initial = emptyList())
    var replyText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${ticket.ticketId}: ${ticket.subject}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(messages) { msg ->
                        Surface(
                            color = if (msg.senderRole == "CUSTOMER") MaterialTheme.colorScheme.surfaceVariant else EmeraldPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "${msg.senderName} (${msg.senderRole})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldPrimary)
                                Text(text = msg.message, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Type reply...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                viewModel.replyToTicket(ticket.ticketId, replyText)
                                replyText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = EmeraldPrimary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
