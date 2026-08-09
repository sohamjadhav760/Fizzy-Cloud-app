package com.example.ui.screens

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.db.EmailLogEntity
import com.example.data.db.EmailTemplateEntity
import com.example.data.email.*
import com.example.ui.MainViewModel
import com.example.ui.components.BadgeChip
import com.example.ui.components.GlowCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmailSection(viewModel: MainViewModel) {
    val emailLogs by viewModel.emailLogs.collectAsState()
    val emailTemplates by viewModel.emailTemplates.collectAsState()
    val emailConfig: EmailConfig = remember { viewModel.getActiveEmailConfig() }
    val emailStatusToast by viewModel.emailStatusToast.collectAsState()

    var showSetupGuideDialog by remember { mutableStateOf(false) }
    var showTestConsoleDialog by remember { mutableStateOf(false) }
    var selectedLogForModal by remember { mutableStateOf<EmailLogEntity?>(null) }
    var selectedTemplateForEdit by remember { mutableStateOf<EmailTemplateEntity?>(null) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_email_section")
    ) {

        // --- Toast Status Banner ---
        emailStatusToast?.let { toastMsg ->
            GlowCard(borderColor = EmeraldPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary)
                        Text(toastMsg, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { viewModel.clearEmailStatusToast() }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // --- 1. Provider Status Header Card ---
        GlowCard(borderColor = CyanSecondary) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = CyanSecondary)
                            Text("Email Notification Service", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Text("SMTP & API Transports (SendGrid, Mailgun, SMTP, Simulation)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    val providerColor = when (emailConfig.provider) {
                        EmailProviderType.SENDGRID -> Color(0xFF0284C7)
                        EmailProviderType.MAILGUN -> Color(0xFFEA580C)
                        EmailProviderType.SMTP -> AmberAccent
                        EmailProviderType.SIMULATED -> EmeraldPrimary
                    }
                    BadgeChip(
                        text = "PROVIDER: ${emailConfig.provider.name}",
                        containerColor = providerColor,
                        contentColor = Color.White
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sender Email:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${emailConfig.senderName} <${emailConfig.senderEmail}>", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Admin Alert Recipient:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(emailConfig.adminNotificationEmail, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showTestConsoleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Send Test Email")
                    }

                    OutlinedButton(
                        onClick = { showSetupGuideDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Setup Guide")
                    }
                }
            }
        }

        // --- 2. Email Templates Management Card ---
        GlowCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Configured Email Templates (${emailTemplates.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("HTML & Plain text notification templates for key events", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                emailTemplates.forEach { tpl ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (tpl.templateType) {
                                            "USER_REGISTRATION" -> Icons.Default.PersonAdd
                                            "PASSWORD_RESET" -> Icons.Default.LockReset
                                            "NEW_ORDER_CUSTOMER" -> Icons.Default.Receipt
                                            "NEW_ORDER_ADMIN" -> Icons.Default.NotificationImportant
                                            else -> Icons.Default.RocketLaunch
                                        },
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(tpl.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(tpl.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                TextButton(onClick = { isExpanded = !isExpanded }) {
                                    Text(if (isExpanded) "Hide" else "Edit / View")
                                    Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                                }
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Placeholders Available:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanSecondary)
                                    Text(tpl.placeholders, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)

                                    var editSubject by remember(tpl) { mutableStateOf(tpl.subjectTemplate) }
                                    var editHtml by remember(tpl) { mutableStateOf(tpl.htmlTemplate) }

                                    OutlinedTextField(
                                        value = editSubject,
                                        onValueChange = { editSubject = it },
                                        label = { Text("Subject Template") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = editHtml,
                                        onValueChange = { editHtml = it },
                                        label = { Text("HTML Code Template") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 120.dp, max = 220.dp),
                                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.saveEmailTemplate(
                                                    tpl.copy(
                                                        subjectTemplate = editSubject,
                                                        htmlTemplate = editHtml,
                                                        lastModified = System.currentTimeMillis()
                                                    )
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Save Template Changes")
                                        }

                                        OutlinedButton(
                                            onClick = { selectedTemplateForEdit = tpl },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Preview Live HTML")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Email Outbox & Audit Logs Card ---
        GlowCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sent Email Outbox Logs (${emailLogs.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Live transmission history & delivery status", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (emailLogs.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearEmailLogs() }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Clear Logs", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                if (emailLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No emails dispatched yet. Click 'Send Test Email' above to test!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    emailLogs.take(15).forEach { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLogForModal = log }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(log.recipientEmail, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("• ${log.templateType}", fontSize = 11.sp, color = CyanSecondary)
                                    }
                                    Text(log.subject, fontSize = 12.sp, maxLines = 1)
                                    Text(dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(Modifier.width(8.dp))

                                val badgeColor = when (log.status) {
                                    "SENT" -> EmeraldPrimary
                                    "SIMULATED" -> CyanSecondary
                                    else -> MaterialTheme.colorScheme.error
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    BadgeChip(text = log.status, containerColor = badgeColor, contentColor = Color.Black)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Via ${log.providerUsed}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOG 1: Setup & Credentials Guide Modal ---
    if (showSetupGuideDialog) {
        AlertDialog(
            onDismissRequest = { showSetupGuideDialog = false },
            title = { Text("Email Service Setup & Credentials Guide", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Configure environment variables in .env or via the Secrets panel in AI Studio to enable real email deliveries:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    GlowCard(borderColor = CyanSecondary) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("SendGrid Integration:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldPrimary)
                            Text("1. Set EMAIL_PROVIDER=SENDGRID", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("2. Set EMAIL_API_KEY=SG.your_sendgrid_key", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    GlowCard(borderColor = AmberAccent) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Mailgun Integration:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AmberAccent)
                            Text("1. Set EMAIL_PROVIDER=MAILGUN", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("2. Set EMAIL_API_KEY=key-your_mailgun_key", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("3. Set MAILGUN_DOMAIN=mg.yourdomain.com", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    GlowCard {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("SMTP Gateway Integration:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyanSecondary)
                            Text("1. Set EMAIL_PROVIDER=SMTP", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("2. Set SMTP_HOST=smtp.mailtrap.io", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text("3. Set SMTP_PORT=587, SMTP_USERNAME, SMTP_PASSWORD", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Text("In SIMULATED mode, emails are rendered locally and saved to the outbox log without requiring external network API keys.", fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showSetupGuideDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }

    // --- DIALOG 2: Send Test Email Console Modal ---
    if (showTestConsoleDialog) {
        var testTemplateType by remember { mutableStateOf(EmailTemplateType.USER_REGISTRATION) }
        var testRecipientEmail by remember { mutableStateOf("alex@gamer.com") }
        var testRecipientName by remember { mutableStateOf("Alex Gamer") }
        var testCustomSubject by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTestConsoleDialog = false },
            title = { Text("Send Test Email", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a template event to trigger a test dispatch:", fontSize = 12.sp)

                    Text("Email Event Template:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    EmailTemplateType.values().forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { testTemplateType = type }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = testTemplateType == type, onClick = { testTemplateType = type })
                            Spacer(Modifier.width(8.dp))
                            Text(type.name, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = testRecipientEmail,
                        onValueChange = { testRecipientEmail = it },
                        label = { Text("Recipient Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = testRecipientName,
                        onValueChange = { testRecipientName = it },
                        label = { Text("Recipient Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = testCustomSubject,
                        onValueChange = { testCustomSubject = it },
                        label = { Text("Custom Subject (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val testVars = mapOf(
                            "user_name" to testRecipientName,
                            "user_email" to testRecipientEmail,
                            "user_role" to "CUSTOMER",
                            "order_id" to "ORD-9982",
                            "items_summary" to "Minecraft Starter Plan (2GB RAM), .com Domain",
                            "subtotal" to "₹199.00",
                            "discount" to "₹20.00",
                            "tax" to "₹32.22",
                            "total_amount" to "₹211.22",
                            "payment_method" to "Razorpay UPI",
                            "service_name" to "DragonCraft SMP",
                            "service_type" to "MINECRAFT",
                            "ip_address" to "142.93.201.88:25565",
                            "config_details" to "Paper 1.20.4 | Singapore Node"
                        )
                        viewModel.sendTestEmail(
                            templateType = testTemplateType,
                            recipientEmail = testRecipientEmail,
                            recipientName = testRecipientName,
                            testVars = testVars,
                            customSubject = if (testCustomSubject.isNotBlank()) testCustomSubject else null
                        )
                        showTestConsoleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                ) {
                    Text("Dispatch Test Email")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTestConsoleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- DIALOG 3: View Rendered Email Outbox Log Modal ---
    selectedLogForModal?.let { log ->
        AlertDialog(
            onDismissRequest = { selectedLogForModal = null },
            title = {
                Column {
                    Text("Outbox Log #${log.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(log.subject, fontSize = 13.sp, color = EmeraldPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(350.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("To: ${log.recipientEmail}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        BadgeChip(text = log.status, containerColor = if (log.status == "FAILED") MaterialTheme.colorScheme.error else EmeraldPrimary, contentColor = Color.Black)
                    }
                    Text("Provider: ${log.providerUsed} | Date: ${dateFormat.format(Date(log.timestamp))}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (log.errorMessage.isNotBlank()) {
                        Text("Error: ${log.errorMessage}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }

                    HorizontalDivider()

                    Text("Rendered HTML Preview:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    val context = LocalContext.current
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = false
                                loadDataWithBaseURL(null, log.fullHtml, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    )
                }
            },
            confirmButton = {
                Button(onClick = { selectedLogForModal = null }) {
                    Text("Close")
                }
            }
        )
    }

    // --- DIALOG 4: View Live HTML Template Preview Modal ---
    selectedTemplateForEdit?.let { tpl ->
        AlertDialog(
            onDismissRequest = { selectedTemplateForEdit = null },
            title = { Text("Template Preview: ${tpl.name}", fontWeight = FontWeight.Bold) },
            text = {
                val dummyRendered = remember(tpl) {
                    EmailTemplateDefaults.replacePlaceholders(
                        tpl.htmlTemplate,
                        mapOf(
                            "user_name" to "Alex Gamer",
                            "user_email" to "alex@gamer.com",
                            "user_role" to "CUSTOMER",
                            "order_id" to "ORD-1092",
                            "items_summary" to "Minecraft Pro Server (4GB RAM)",
                            "subtotal" to "₹199.00",
                            "discount" to "₹19.90",
                            "tax" to "₹32.20",
                            "total_amount" to "₹211.30",
                            "payment_method" to "Razorpay UPI",
                            "service_name" to "DragonCraft SMP",
                            "service_type" to "MINECRAFT",
                            "ip_address" to "142.93.201.88:25565",
                            "config_details" to "Paper 1.20.4 | Singapore Location"
                        )
                    )
                }
                Column(modifier = Modifier.height(350.dp)) {
                    Text("Sample Rendered View:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = false
                                loadDataWithBaseURL(null, dummyRendered, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    )
                }
            },
            confirmButton = {
                Button(onClick = { selectedTemplateForEdit = null }) {
                    Text("Close Preview")
                }
            }
        )
    }
}
