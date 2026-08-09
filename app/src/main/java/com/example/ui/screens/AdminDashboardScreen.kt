package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.db.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val minecraftPlans by viewModel.allMinecraftPlansAdmin.collectAsState()
    val vpsPlans by viewModel.allVpsPlansAdmin.collectAsState()
    val domainTlds by viewModel.allDomainTldsAdmin.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val tickets by viewModel.allTickets.collectAsState()
    val settings by viewModel.websiteSettings.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val faqs by viewModel.allFaqs.collectAsState()
    val testimonials by viewModel.allTestimonials.collectAsState()
    val announcements by viewModel.allAnnouncements.collectAsState()
    val navItems by viewModel.allNavItems.collectAsState()
    val footerLinks by viewModel.allFooterLinks.collectAsState()
    val pages by viewModel.allPages.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()

    var activeAdminTab by remember { mutableStateOf("overview") }
    var globalSearchQuery by remember { mutableStateOf("") }

    val userRole = currentUser?.role ?: "SUPER_ADMIN"
    val isSuperAdmin = userRole == "SUPER_ADMIN"
    val isManager = userRole == "MANAGER" || isSuperAdmin
    val isSupport = userRole == "SUPPORT" || isManager

    val tabList = listOf(
        "overview" to "Overview",
        "settings" to "Site Settings",
        "homepage" to "Homepage Editor",
        "minecraft" to "Minecraft",
        "vps" to "VPS Hosting",
        "domains" to "Domains",
        "coupons" to "Coupons",
        "orders" to "Orders",
        "customers" to "Customers",
        "services" to "Services",
        "tickets" to "Tickets",
        "cms" to "CMS Content",
        "nav_footer" to "Nav & Footer",
        "email" to "Email Service",
        "integrations" to "Integrations",
        "audit" to "Activity Logs"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen_column"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(
                        title = "Super Admin Control Center",
                        subtitle = "Manage products, pricing, users, CMS, orders, and integrations in real time."
                    )
                    BadgeChip(text = "ROLE: $userRole", containerColor = EmeraldPrimary, contentColor = Color.Black)
                }

                // Global Search Bar
                OutlinedTextField(
                    value = globalSearchQuery,
                    onValueChange = { globalSearchQuery = it },
                    placeholder = { Text("Search orders, customers, plans, audit logs...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (globalSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { globalSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // --- Admin Navigation Bar ---
        item {
            ScrollableTabRow(
                selectedTabIndex = tabList.indexOfFirst { it.first == activeAdminTab }.coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                edgePadding = 0.dp
            ) {
                tabList.forEach { (tabKey, tabLabel) ->
                    Tab(
                        selected = activeAdminTab == tabKey,
                        onClick = { activeAdminTab = tabKey },
                        text = { Text(tabLabel, fontWeight = if (activeAdminTab == tabKey) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        // --- 1. OVERVIEW TAB ---
        if (activeAdminTab == "overview") {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val onlineCount = services.count { ServerStatus.fromString(it.status) == ServerStatus.ONLINE }
                    val offlineCount = services.count { ServerStatus.fromString(it.status) == ServerStatus.OFFLINE }
                    val maintenanceCount = services.count { ServerStatus.fromString(it.status) == ServerStatus.MAINTENANCE }

                    ServerStatusSummaryCard(
                        onlineCount = onlineCount,
                        offlineCount = offlineCount,
                        maintenanceCount = maintenanceCount
                    )

                    val totalRev = orders.sumOf { it.totalAmount }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(title = "Total Revenue", value = "₹${totalRev.toInt()}", icon = Icons.Default.AttachMoney, iconTint = EmeraldPrimary, modifier = Modifier.weight(1f))
                        StatCard(title = "Total Orders", value = "${orders.size}", icon = Icons.Default.ShoppingCart, iconTint = CyanSecondary, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(title = "Total Customers", value = "${users.size}", icon = Icons.Default.People, iconTint = AmberAccent, modifier = Modifier.weight(1f))
                        StatCard(title = "Support Tickets", value = "${tickets.size}", icon = Icons.Default.ConfirmationNumber, iconTint = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
                    }

                    // Live Audit Preview
                    GlowCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Recent System Activity", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (auditLogs.isEmpty()) {
                                Text("No recent activity recorded yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                auditLogs.take(5).forEach { log ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${log.adminName}: ${log.action} on ${log.targetItem}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                            if (log.newValue.isNotBlank()) {
                                                Text("Val: ${log.newValue}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp)), fontSize = 11.sp, color = EmeraldPrimary)
                                    }
                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. WEBSITE SETTINGS TAB ---
        if (activeAdminTab == "settings") {
            item {
                AdminWebsiteSettingsSection(settings = settings, viewModel = viewModel)
            }
        }

        // --- 3. HOMEPAGE EDITOR TAB ---
        if (activeAdminTab == "homepage") {
            item {
                AdminHomepageEditorSection(settings = settings, viewModel = viewModel)
            }
        }

        // --- 4. MINECRAFT PLANS CRUD TAB ---
        if (activeAdminTab == "minecraft") {
            item {
                AdminMinecraftPlansSection(plans = minecraftPlans, viewModel = viewModel)
            }
        }

        // --- 5. VPS PLANS CRUD TAB ---
        if (activeAdminTab == "vps") {
            item {
                AdminVpsPlansSection(plans = vpsPlans, viewModel = viewModel)
            }
        }

        // --- 6. DOMAIN TLDS TAB ---
        if (activeAdminTab == "domains") {
            item {
                AdminDomainTldsSection(tlds = domainTlds, viewModel = viewModel)
            }
        }

        // --- 7. COUPONS TAB ---
        if (activeAdminTab == "coupons") {
            item {
                AdminCouponsSection(coupons = coupons, viewModel = viewModel)
            }
        }

        // --- 8. ORDERS MANAGEMENT TAB ---
        if (activeAdminTab == "orders") {
            item {
                AdminOrdersSection(orders = orders, searchQuery = globalSearchQuery, viewModel = viewModel)
            }
        }

        // --- 9. CUSTOMERS MANAGEMENT TAB ---
        if (activeAdminTab == "customers") {
            item {
                AdminCustomersSection(users = users, searchQuery = globalSearchQuery, viewModel = viewModel)
            }
        }

        // --- 10. SERVICES MANAGEMENT TAB ---
        if (activeAdminTab == "services") {
            item {
                AdminServerNodesSection(services = services, viewModel = viewModel)
            }
        }

        // --- 11. SUPPORT TICKETS TAB ---
        if (activeAdminTab == "tickets") {
            item {
                AdminTicketsSection(tickets = tickets, viewModel = viewModel)
            }
        }

        // --- 12. CMS CONTENT (FAQs, Testimonials, Announcements, Pages) ---
        if (activeAdminTab == "cms") {
            item {
                AdminCmsSection(
                    faqs = faqs,
                    testimonials = testimonials,
                    announcements = announcements,
                    pages = pages,
                    viewModel = viewModel
                )
            }
        }

        // --- 13. NAVIGATION & FOOTER EDITOR ---
        if (activeAdminTab == "nav_footer") {
            item {
                AdminNavFooterSection(navItems = navItems, footerLinks = footerLinks, viewModel = viewModel)
            }
        }

        // --- 14. EMAIL SERVICE TAB ---
        if (activeAdminTab == "email") {
            item {
                AdminEmailSection(viewModel = viewModel)
            }
        }

        // --- 15. INTEGRATIONS & GATEWAYS TAB ---
        if (activeAdminTab == "integrations") {
            item {
                AdminIntegrationsSection(settings = settings, viewModel = viewModel)
            }
        }

        // --- 16. AUDIT / ACTIVITY LOGS TAB ---
        if (activeAdminTab == "audit") {
            item {
                AdminAuditLogsSection(auditLogs = auditLogs, searchQuery = globalSearchQuery)
            }
        }
    }
}

// --- SUB-SECTIONS ---

@Composable
private fun AdminWebsiteSettingsSection(settings: List<WebsiteSettingEntity>, viewModel: MainViewModel) {
    var siteName by remember(settings) { mutableStateOf(settings.find { it.keyName == "website_name" }?.value ?: "Fizzy Cloud Hosting") }
    var siteLogo by remember(settings) { mutableStateOf(settings.find { it.keyName == "website_logo" }?.value ?: "Fizzy Cloud") }
    var contactEmail by remember(settings) { mutableStateOf(settings.find { it.keyName == "contact_email" }?.value ?: "support@fizzycloud.gg") }
    var contactPhone by remember(settings) { mutableStateOf(settings.find { it.keyName == "contact_phone" }?.value ?: "+91 98765 43210") }
    var contactDiscord by remember(settings) { mutableStateOf(settings.find { it.keyName == "contact_discord" }?.value ?: "discord.gg/fizzycloud") }
    var currencySymbol by remember(settings) { mutableStateOf(settings.find { it.keyName == "currency_symbol" }?.value ?: "₹") }
    var primaryColorHex by remember(settings) { mutableStateOf(settings.find { it.keyName == "primary_color_hex" }?.value ?: "#10B981") }

    GlowCard(borderColor = EmeraldPrimary) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Website Global Brand Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Updating these values dynamically configures header, footer, and emails across the app.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(value = siteName, onValueChange = { siteName = it }, label = { Text("Website Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = siteLogo, onValueChange = { siteLogo = it }, label = { Text("Logo Text / Icon Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactEmail, onValueChange = { contactEmail = it }, label = { Text("Support Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactPhone, onValueChange = { contactPhone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactDiscord, onValueChange = { contactDiscord = it }, label = { Text("Discord Invite URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = currencySymbol, onValueChange = { currencySymbol = it }, label = { Text("Currency Symbol") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = primaryColorHex, onValueChange = { primaryColorHex = it }, label = { Text("Primary Theme Hex") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    viewModel.saveWebsiteSetting("website_name", siteName)
                    viewModel.saveWebsiteSetting("website_logo", siteLogo)
                    viewModel.saveWebsiteSetting("contact_email", contactEmail)
                    viewModel.saveWebsiteSetting("contact_phone", contactPhone)
                    viewModel.saveWebsiteSetting("contact_discord", contactDiscord)
                    viewModel.saveWebsiteSetting("currency_symbol", currencySymbol)
                    viewModel.saveWebsiteSetting("primary_color_hex", primaryColorHex)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Save Website Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminHomepageEditorSection(settings: List<WebsiteSettingEntity>, viewModel: MainViewModel) {
    var heroHeadline by remember(settings) { mutableStateOf(settings.find { it.keyName == "hero_headline" }?.value ?: "Powerful Hosting. Built for Gamers.") }
    var heroSubheading by remember(settings) { mutableStateOf(settings.find { it.keyName == "hero_subheading" }?.value ?: "Reliable Minecraft Servers, Domains and VPS Hosting at affordable prices.") }
    var heroBtnText by remember(settings) { mutableStateOf(settings.find { it.keyName == "hero_button_text" }?.value ?: "Browse Minecraft Plans") }
    var announcement by remember(settings) { mutableStateOf(settings.find { it.keyName == "announcement_banner" }?.value ?: "⚡ Summer Sale! Use code FIZZY10 for 10% off.") }

    GlowCard(borderColor = EmeraldPrimary) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Homepage Hero & Banner Content Editor", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            OutlinedTextField(value = heroHeadline, onValueChange = { heroHeadline = it }, label = { Text("Hero Headline") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = heroSubheading, onValueChange = { heroSubheading = it }, label = { Text("Hero Subheading") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            OutlinedTextField(value = heroBtnText, onValueChange = { heroBtnText = it }, label = { Text("Hero CTA Button Label") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = announcement, onValueChange = { announcement = it }, label = { Text("Global Header Announcement Banner") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    viewModel.saveWebsiteSetting("hero_headline", heroHeadline)
                    viewModel.saveWebsiteSetting("hero_subheading", heroSubheading)
                    viewModel.saveWebsiteSetting("hero_button_text", heroBtnText)
                    viewModel.saveWebsiteSetting("announcement_banner", announcement)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Publish Homepage Changes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminMinecraftPlansSection(plans: List<MinecraftPlanEntity>, viewModel: MainViewModel) {
    var showAddMcDialog by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<MinecraftPlanEntity?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Minecraft Plans (${plans.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(
                onClick = { showAddMcDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("+ Add MC Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        plans.forEach { plan ->
            GlowCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (plan.badge.isNotBlank()) BadgeChip(text = plan.badge, containerColor = AmberAccent, contentColor = Color.Black)
                        }
                        Text("Price: ₹${plan.priceMonthly.toInt()}/mo | RAM: ${plan.ramGb}GB | CPU: ${plan.cpuCores} Cores | Storage: ${plan.storageGb}GB", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row {
                        IconButton(onClick = { viewModel.duplicateMinecraftPlan(plan) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = CyanSecondary)
                        }
                        IconButton(onClick = { editingPlan = plan }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EmeraldPrimary)
                        }
                        IconButton(onClick = { viewModel.deleteMinecraftPlan(plan) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (showAddMcDialog || editingPlan != null) {
            MinecraftPlanDialog(
                existingPlan = editingPlan,
                onDismiss = {
                    showAddMcDialog = false
                    editingPlan = null
                },
                onSave = { updatedPlan ->
                    viewModel.saveMinecraftPlan(updatedPlan)
                    showAddMcDialog = false
                    editingPlan = null
                }
            )
        }
    }
}

@Composable
private fun MinecraftPlanDialog(
    existingPlan: MinecraftPlanEntity?,
    onDismiss: () -> Unit,
    onSave: (MinecraftPlanEntity) -> Unit
) {
    var name by remember { mutableStateOf(existingPlan?.name ?: "") }
    var price by remember { mutableStateOf(existingPlan?.priceMonthly?.toInt()?.toString() ?: "299") }
    var ram by remember { mutableStateOf(existingPlan?.ramGb?.toString() ?: "6") }
    var cpu by remember { mutableStateOf(existingPlan?.cpuCores?.toString() ?: "3") }
    var storage by remember { mutableStateOf(existingPlan?.storageGb?.toString() ?: "30") }
    var badge by remember { mutableStateOf(existingPlan?.badge ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingPlan == null) "Add Minecraft Plan" else "Edit Minecraft Plan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Plan Name") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Monthly Price (₹)") })
                OutlinedTextField(value = ram, onValueChange = { ram = it }, label = { Text("RAM (GB)") })
                OutlinedTextField(value = cpu, onValueChange = { cpu = it }, label = { Text("CPU Cores") })
                OutlinedTextField(value = storage, onValueChange = { storage = it }, label = { Text("NVMe Storage (GB)") })
                OutlinedTextField(value = badge, onValueChange = { badge = it }, label = { Text("Badge Label (e.g., POPULAR)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            (existingPlan ?: MinecraftPlanEntity(name = name, priceMonthly = 299.0)).copy(
                                name = name,
                                priceMonthly = price.toDoubleOrNull() ?: 299.0,
                                priceYearly = (price.toDoubleOrNull() ?: 299.0) * 10,
                                ramGb = ram.toIntOrNull() ?: 6,
                                cpuCores = cpu.toIntOrNull() ?: 3,
                                storageGb = storage.toIntOrNull() ?: 30,
                                badge = badge
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) { Text("Save Plan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AdminVpsPlansSection(plans: List<VpsPlanEntity>, viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("VPS Hosting Plans (${plans.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("+ Add VPS Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        plans.forEach { plan ->
            GlowCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Price: ₹${plan.priceMonthly.toInt()}/mo | vCPU: ${plan.cpuCores} | RAM: ${plan.ramGb}GB | Storage: ${plan.storageNvmeGb}GB NVMe", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.deleteVpsPlan(plan) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (showAddDialog) {
            var name by remember { mutableStateOf("") }
            var price by remember { mutableStateOf("499") }
            var ram by remember { mutableStateOf("8") }
            var cpu by remember { mutableStateOf("4") }
            var storage by remember { mutableStateOf("80") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add VPS Plan", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Plan Name") })
                        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Monthly Price (₹)") })
                        OutlinedTextField(value = ram, onValueChange = { ram = it }, label = { Text("RAM (GB)") })
                        OutlinedTextField(value = cpu, onValueChange = { cpu = it }, label = { Text("vCPU Cores") })
                        OutlinedTextField(value = storage, onValueChange = { storage = it }, label = { Text("NVMe Storage (GB)") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.saveVpsPlan(
                                    VpsPlanEntity(
                                        name = name,
                                        description = "High performance virtual dedicated core node",
                                        priceMonthly = price.toDoubleOrNull() ?: 499.0,
                                        ramGb = ram.toIntOrNull() ?: 8,
                                        cpuCores = cpu.toIntOrNull() ?: 4,
                                        storageNvmeGb = storage.toIntOrNull() ?: 80,
                                        bandwidthTb = 2.0,
                                        portSpeed = "1 Gbps",
                                        features = "Root Access, 1 IPv4, Instant Reload"
                                    )
                                )
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                    ) { Text("Save VPS Plan") }
                },
                dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun AdminDomainTldsSection(tlds: List<DomainTldEntity>, viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Domain TLD Pricing (${tlds.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("+ Add TLD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        tlds.forEach { item ->
            GlowCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(item.tld, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldPrimary)
                        Text("Register: ₹${item.registerPrice.toInt()} | Renew: ₹${item.renewPrice.toInt()} | Transfer: ₹${item.transferPrice.toInt()}", fontSize = 12.sp)
                    }
                    IconButton(onClick = { viewModel.deleteDomainTld(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (showAddDialog) {
            var tldExt by remember { mutableStateOf(".gg") }
            var regPrice by remember { mutableStateOf("1299") }
            var renewPrice by remember { mutableStateOf("1499") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Domain TLD", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = tldExt, onValueChange = { tldExt = it }, label = { Text("TLD Extension (e.g. .gg)") })
                        OutlinedTextField(value = regPrice, onValueChange = { regPrice = it }, label = { Text("Registration Price (₹)") })
                        OutlinedTextField(value = renewPrice, onValueChange = { renewPrice = it }, label = { Text("Renewal Price (₹)") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tldExt.isNotBlank()) {
                                viewModel.saveDomainTld(
                                    DomainTldEntity(
                                        tld = tldExt,
                                        registerPrice = regPrice.toDoubleOrNull() ?: 1299.0,
                                        renewPrice = renewPrice.toDoubleOrNull() ?: 1499.0,
                                        transferPrice = regPrice.toDoubleOrNull() ?: 1299.0,
                                        description = "Gaming and community TLD"
                                    )
                                )
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                    ) { Text("Save TLD") }
                },
                dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun AdminCouponsSection(coupons: List<CouponEntity>, viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Active Discount Coupons (${coupons.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("+ Create Coupon", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        coupons.forEach { coupon ->
            GlowCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        BadgeChip(text = coupon.code, containerColor = AmberAccent, contentColor = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (coupon.discountType == "PERCENTAGE") "${coupon.value.toInt()}% OFF" else "₹${coupon.value.toInt()} OFF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = { viewModel.deleteCoupon(coupon) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (showAddDialog) {
            var code by remember { mutableStateOf("SUPER20") }
            var valStr by remember { mutableStateOf("20") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Create Discount Coupon", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Coupon Code") })
                        OutlinedTextField(value = valStr, onValueChange = { valStr = it }, label = { Text("Discount % or Fixed Amount") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (code.isNotBlank()) {
                                viewModel.saveCoupon(
                                    CouponEntity(
                                        code = code.uppercase(),
                                        discountType = "PERCENTAGE",
                                        value = valStr.toDoubleOrNull() ?: 10.0,
                                        minOrderValue = 0.0
                                    )
                                )
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                    ) { Text("Create Coupon") }
                },
                dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun AdminOrdersSection(orders: List<OrderEntity>, searchQuery: String, viewModel: MainViewModel) {
    val filteredOrders = orders.filter {
        searchQuery.isBlank() ||
                it.orderId.contains(searchQuery, ignoreCase = true) ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.customerEmail.contains(searchQuery, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Orders & Invoices (${filteredOrders.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        filteredOrders.forEach { order ->
            GlowCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(order.orderId, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EmeraldPrimary)
                        BadgeChip(
                            text = order.status,
                            containerColor = when (order.status) {
                                "PAID" -> EmeraldPrimary
                                "PENDING" -> AmberAccent
                                else -> MaterialTheme.colorScheme.error
                            },
                            contentColor = Color.Black
                        )
                    }
                    Text("Customer: ${order.customerName} (${order.customerEmail})", fontSize = 12.sp)
                    Text("Items: ${order.itemsSummary}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold)

                        Row {
                            if (order.status != "PAID") {
                                TextButton(onClick = { viewModel.updateOrderStatus(order.orderId, "PAID", "Marked paid by Admin") }) {
                                    Text("Mark Paid", fontSize = 11.sp, color = EmeraldPrimary)
                                }
                            }
                            if (order.status != "CANCELLED") {
                                TextButton(onClick = { viewModel.updateOrderStatus(order.orderId, "CANCELLED", "Cancelled by Admin") }) {
                                    Text("Cancel Order", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCustomersSection(users: List<UserEntity>, searchQuery: String, viewModel: MainViewModel) {
    val filteredUsers = users.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Customer Accounts (${filteredUsers.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        filteredUsers.forEach { user ->
            GlowCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            BadgeChip(text = user.role, containerColor = CyanSecondary, contentColor = Color.Black)
                        }
                        Text(user.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Status: ${user.status}", fontSize = 11.sp, color = if (user.status == "ACTIVE") EmeraldPrimary else MaterialTheme.colorScheme.error)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        if (user.status == "ACTIVE") {
                            TextButton(onClick = { viewModel.updateUserStatus(user.id, "SUSPENDED") }) {
                                Text("Suspend", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        } else {
                            TextButton(onClick = { viewModel.updateUserStatus(user.id, "ACTIVE") }) {
                                Text("Activate", fontSize = 11.sp, color = EmeraldPrimary)
                            }
                        }

                        if (user.role == "CUSTOMER") {
                            TextButton(onClick = { viewModel.updateUserRole(user.id, "MANAGER") }) {
                                Text("+ Make Manager", fontSize = 10.sp, color = AmberAccent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTicketsSection(tickets: List<SupportTicketEntity>, viewModel: MainViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Support Desk Queue (${tickets.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        tickets.forEach { ticket ->
            GlowCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Ticket #${ticket.ticketId}: ${ticket.subject}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        BadgeChip(
                            text = ticket.status,
                            containerColor = when (ticket.status) {
                                "OPEN" -> AmberAccent
                                "IN_PROGRESS" -> CyanSecondary
                                else -> EmeraldPrimary
                            },
                            contentColor = Color.Black
                        )
                    }
                    Text("Category: ${ticket.category} | Priority: ${ticket.priority}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AdminCmsSection(
    faqs: List<FaqEntity>,
    testimonials: List<TestimonialEntity>,
    announcements: List<AnnouncementEntity>,
    pages: List<PageEntity>,
    viewModel: MainViewModel
) {
    var cmsSubTab by remember { mutableStateOf("faqs") }
    var showAddFaqDialog by remember { mutableStateOf(false) }
    var showAddTestimonialDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = cmsSubTab == "faqs", onClick = { cmsSubTab = "faqs" }, label = { Text("FAQs (${faqs.size})") })
            FilterChip(selected = cmsSubTab == "testimonials", onClick = { cmsSubTab = "testimonials" }, label = { Text("Reviews (${testimonials.size})") })
            FilterChip(selected = cmsSubTab == "pages", onClick = { cmsSubTab = "pages" }, label = { Text("Pages (${pages.size})") })
        }

        if (cmsSubTab == "faqs") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Frequently Asked Questions", fontWeight = FontWeight.Bold)
                Button(onClick = { showAddFaqDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)) {
                    Text("+ Add FAQ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            faqs.forEach { faq ->
                GlowCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(faq.question, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(faq.answer, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteFaq(faq) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (cmsSubTab == "testimonials") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Customer Reviews", fontWeight = FontWeight.Bold)
                Button(onClick = { showAddTestimonialDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)) {
                    Text("+ Add Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            testimonials.forEach { item ->
                GlowCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${item.authorName} (${item.authorRole})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("\"${item.reviewText}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteTestimonial(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (cmsSubTab == "pages") {
            pages.forEach { page ->
                GlowCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${page.title} (/${page.slug})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(page.content.take(80) + "...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        BadgeChip(text = if (page.isPublished) "PUBLISHED" else "DRAFT", containerColor = if (page.isPublished) EmeraldPrimary else AmberAccent, contentColor = Color.Black)
                    }
                }
            }
        }

        if (showAddFaqDialog) {
            var q by remember { mutableStateOf("") }
            var a by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddFaqDialog = false },
                title = { Text("Add FAQ Entry", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Question") })
                        OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("Answer") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (q.isNotBlank()) {
                                viewModel.saveFaq(FaqEntity(question = q, answer = a))
                                showAddFaqDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                    ) { Text("Save FAQ") }
                },
                dismissButton = { TextButton(onClick = { showAddFaqDialog = false }) { Text("Cancel") } }
            )
        }

        if (showAddTestimonialDialog) {
            var author by remember { mutableStateOf("") }
            var role by remember { mutableStateOf("") }
            var review by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddTestimonialDialog = false },
                title = { Text("Add Testimonial", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author Name") })
                        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role / Guild Name") })
                        OutlinedTextField(value = review, onValueChange = { review = it }, label = { Text("Review Text") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (author.isNotBlank()) {
                                viewModel.saveTestimonial(TestimonialEntity(authorName = author, authorRole = role, reviewText = review))
                                showAddTestimonialDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                    ) { Text("Save Review") }
                },
                dismissButton = { TextButton(onClick = { showAddTestimonialDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun AdminNavFooterSection(navItems: List<NavItemEntity>, footerLinks: List<FooterLinkEntity>, viewModel: MainViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlowCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Header Navigation Links (${navItems.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                navItems.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("${item.label} -> ${item.destination}", fontSize = 13.sp)
                        IconButton(onClick = { viewModel.deleteNavItem(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        GlowCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Footer Category Links (${footerLinks.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                footerLinks.forEach { link ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("[${link.columnTitle}] ${link.label} -> ${link.destination}", fontSize = 13.sp)
                        IconButton(onClick = { viewModel.deleteFooterLink(link) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminIntegrationsSection(settings: List<WebsiteSettingEntity>, viewModel: MainViewModel) {
    var rzpKey by remember(settings) { mutableStateOf(settings.find { it.keyName == "razorpay_key" }?.value ?: "rzp_test_981273918237") }
    var stripeKey by remember(settings) { mutableStateOf(settings.find { it.keyName == "stripe_key" }?.value ?: "pk_test_51239812739817") }
    var pteroUrl by remember(settings) { mutableStateOf(settings.find { it.keyName == "ptero_url" }?.value ?: "https://panel.fizzycloud.gg") }
    var discordWebhook by remember(settings) { mutableStateOf(settings.find { it.keyName == "discord_webhook" }?.value ?: "https://discord.com/api/webhooks/102938102938") }

    GlowCard(borderColor = EmeraldPrimary) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("API Keys & Provider Integrations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("All secret tokens are securely stored and evaluated on the backend server.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(value = rzpKey, onValueChange = { rzpKey = it }, label = { Text("Razorpay Key ID") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stripeKey, onValueChange = { stripeKey = it }, label = { Text("Stripe Publishable Key") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = pteroUrl, onValueChange = { pteroUrl = it }, label = { Text("Pterodactyl Daemon URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = discordWebhook, onValueChange = { discordWebhook = it }, label = { Text("Discord Audit Webhook") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    viewModel.saveWebsiteSetting("razorpay_key", rzpKey)
                    viewModel.saveWebsiteSetting("stripe_key", stripeKey)
                    viewModel.saveWebsiteSetting("ptero_url", pteroUrl)
                    viewModel.saveWebsiteSetting("discord_webhook", discordWebhook)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
            ) {
                Text("Save API Credentials", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminServerNodesSection(services: List<ServiceEntity>, viewModel: MainViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Active Infrastructure Nodes & Services (${services.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        services.forEach { service ->
            GlowCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(service.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("IP: ${service.ipAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        ServerStatusBadge(statusString = service.status)
                    }

                    LinearProgressIndicator(
                        progress = (service.cpuUsagePercent / 100f).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = EmeraldPrimary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("RAM: ${service.ramUsageMb}MB / ${service.maxRamMb}MB | CPU: ${service.cpuUsagePercent}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row {
                            TextButton(onClick = { viewModel.updateServiceStatus(service.id, "RUNNING") }) {
                                Text("Online", fontSize = 11.sp, color = EmeraldPrimary)
                            }
                            TextButton(onClick = { viewModel.updateServiceStatus(service.id, "MAINTENANCE") }) {
                                Text("Maint", fontSize = 11.sp, color = AmberAccent)
                            }
                            TextButton(onClick = { viewModel.updateServiceStatus(service.id, "STOPPED") }) {
                                Text("Offline", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAuditLogsSection(auditLogs: List<AuditLogEntity>, searchQuery: String) {

    val filteredLogs = auditLogs.filter {
        searchQuery.isBlank() ||
                it.adminName.contains(searchQuery, ignoreCase = true) ||
                it.action.contains(searchQuery, ignoreCase = true) ||
                it.targetItem.contains(searchQuery, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("System Audit Logs (${filteredLogs.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        filteredLogs.forEach { log ->
            GlowCard {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("${log.adminName} -> ${log.action}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldPrimary)
                        Text(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Target: ${log.targetItem}", fontSize = 12.sp)
                    if (log.newValue.isNotBlank()) {
                        Text("Value: ${log.newValue}", fontSize = 11.sp, color = CyanSecondary)
                    }
                }
            }
        }
    }
}
