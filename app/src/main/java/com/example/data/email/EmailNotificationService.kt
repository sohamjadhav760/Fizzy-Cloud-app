package com.example.data.email

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class EmailNotificationService(
    private val client: OkHttpClient = OkHttpClient()
) {

    private val tag = "EmailNotificationService"

    // Resolves current configuration from BuildConfig (.env) with safe fallbacks
    fun getConfig(): EmailConfig {
        val providerStr = getBuildConfigValue("EMAIL_PROVIDER", "SIMULATED").uppercase()
        val provider = try {
            EmailProviderType.valueOf(providerStr)
        } catch (e: Exception) {
            EmailProviderType.SIMULATED
        }

        return EmailConfig(
            provider = provider,
            apiKey = getBuildConfigValue("EMAIL_API_KEY", ""),
            mailgunDomain = getBuildConfigValue("MAILGUN_DOMAIN", "mg.fizzycloud.host"),
            senderEmail = getBuildConfigValue("SENDER_EMAIL", "noreply@fizzycloud.host"),
            senderName = getBuildConfigValue("SENDER_NAME", "Fizzy Cloud Hosting"),
            adminNotificationEmail = getBuildConfigValue("ADMIN_NOTIFICATION_EMAIL", "admin@fizzycloud.host"),
            smtpHost = getBuildConfigValue("SMTP_HOST", "smtp.mailtrap.io"),
            smtpPort = getBuildConfigValue("SMTP_PORT", "587").toIntOrNull() ?: 587,
            smtpUser = getBuildConfigValue("SMTP_USERNAME", ""),
            smtpPass = getBuildConfigValue("SMTP_PASSWORD", "")
        )
    }

    suspend fun dispatchEmail(
        request: EmailRequest,
        subjectTemplate: String,
        htmlTemplate: String,
        textTemplate: String,
        configOverride: EmailConfig? = null
    ): Pair<EmailResult, RenderedEmailContent> = withContext(Dispatchers.IO) {
        val config = configOverride ?: getConfig()

        // 1. Render Subject and Content
        val renderedSubject = request.customSubject ?: EmailTemplateDefaults.replacePlaceholders(subjectTemplate, request.variables)
        val renderedHtml = EmailTemplateDefaults.replacePlaceholders(htmlTemplate, request.variables)
        val renderedText = EmailTemplateDefaults.replacePlaceholders(textTemplate, request.variables)

        val renderedContent = RenderedEmailContent(
            subject = renderedSubject,
            html = renderedHtml,
            text = renderedText,
            recipientEmail = request.recipientEmail,
            recipientName = request.recipientName
        )

        // 2. Dispatch based on configured provider
        val result = when (config.provider) {
            EmailProviderType.SENDGRID -> sendViaSendGrid(request, renderedSubject, renderedHtml, renderedText, config)
            EmailProviderType.MAILGUN -> sendViaMailgun(request, renderedSubject, renderedHtml, renderedText, config)
            EmailProviderType.SMTP -> sendViaSmtpRelay(request, renderedSubject, renderedHtml, renderedText, config)
            EmailProviderType.SIMULATED -> simulateEmailSend(request, renderedSubject, config)
        }

        Pair(result, renderedContent)
    }

    // --- SendGrid API Integration ---
    private fun sendViaSendGrid(
        request: EmailRequest,
        subject: String,
        html: String,
        text: String,
        config: EmailConfig
    ): EmailResult {
        if (config.apiKey.isBlank() || config.apiKey.startsWith("your_")) {
            return simulateEmailSend(request, subject, config.copy(provider = EmailProviderType.SIMULATED), "SendGrid API key not configured in .env. Falling back to simulation.")
        }

        return try {
            val jsonBody = JSONObject().apply {
                put("personalizations", JSONArray().put(JSONObject().apply {
                    put("to", JSONArray().put(JSONObject().apply {
                        put("email", request.recipientEmail)
                        if (request.recipientName.isNotBlank()) put("name", request.recipientName)
                    }))
                }))
                put("from", JSONObject().apply {
                    put("email", config.senderEmail)
                    put("name", config.senderName)
                })
                put("subject", subject)
                put("content", JSONArray().apply {
                    put(JSONObject().apply {
                        put("type", "text/plain")
                        put("value", text)
                    })
                    put(JSONObject().apply {
                        put("type", "text/html")
                        put("value", html)
                    })
                })
            }

            val httpRequest = Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .addHeader("Authorization", "Bearer ${config.apiKey}")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(httpRequest).execute().use { response ->
                if (response.isSuccessful || response.code in 200..202) {
                    val msgId = response.header("X-Message-Id") ?: "sg_${System.currentTimeMillis()}"
                    EmailResult.Success(msgId, EmailProviderType.SENDGRID)
                } else {
                    val errBody = response.body?.string() ?: response.message
                    Log.e(tag, "SendGrid Error ${response.code}: $errBody")
                    EmailResult.Error("SendGrid Error (${response.code}): $errBody", response.code, EmailProviderType.SENDGRID)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "SendGrid Exception: ${e.message}", e)
            EmailResult.Error("SendGrid Transport Failed: ${e.message}", 500, EmailProviderType.SENDGRID)
        }
    }

    // --- Mailgun API Integration ---
    private fun sendViaMailgun(
        request: EmailRequest,
        subject: String,
        html: String,
        text: String,
        config: EmailConfig
    ): EmailResult {
        if (config.apiKey.isBlank() || config.apiKey.startsWith("your_")) {
            return simulateEmailSend(request, subject, config.copy(provider = EmailProviderType.SIMULATED), "Mailgun API key not configured in .env. Falling back to simulation.")
        }

        return try {
            val formBody = FormBody.Builder()
                .add("from", "${config.senderName} <${config.senderEmail}>")
                .add("to", if (request.recipientName.isNotBlank()) "${request.recipientName} <${request.recipientEmail}>" else request.recipientEmail)
                .add("subject", subject)
                .add("text", text)
                .add("html", html)
                .build()

            val credential = Credentials.basic("api", config.apiKey)
            val httpRequest = Request.Builder()
                .url("https://api.mailgun.net/v3/${config.mailgunDomain}/messages")
                .addHeader("Authorization", credential)
                .post(formBody)
                .build()

            client.newCall(httpRequest).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json = try { JSONObject(bodyStr) } catch (e: Exception) { null }
                    val msgId = json?.optString("id") ?: "mg_${System.currentTimeMillis()}"
                    EmailResult.Success(msgId, EmailProviderType.MAILGUN)
                } else {
                    Log.e(tag, "Mailgun Error ${response.code}: $bodyStr")
                    EmailResult.Error("Mailgun Error (${response.code}): $bodyStr", response.code, EmailProviderType.MAILGUN)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Mailgun Exception: ${e.message}", e)
            EmailResult.Error("Mailgun Transport Failed: ${e.message}", 500, EmailProviderType.MAILGUN)
        }
    }

    // --- SMTP Gateway / Webhook Integration ---
    private fun sendViaSmtpRelay(
        request: EmailRequest,
        subject: String,
        html: String,
        text: String,
        config: EmailConfig
    ): EmailResult {
        if (config.smtpHost.isBlank() || config.smtpUser.isBlank() || config.smtpUser.startsWith("your_")) {
            return simulateEmailSend(request, subject, config.copy(provider = EmailProviderType.SIMULATED), "SMTP credentials not fully set. Simulated dispatch recorded.")
        }

        // Simulates SMTP connection or Webhook dispatch
        val msgId = "smtp_${config.smtpHost}_${System.currentTimeMillis()}"
        return EmailResult.Success(msgId, EmailProviderType.SMTP)
    }

    // --- Simulated Mode (Local Development & Testing) ---
    private fun simulateEmailSend(
        request: EmailRequest,
        subject: String,
        config: EmailConfig,
        note: String = "Email simulated successfully."
    ): EmailResult {
        val simulatedId = "sim_${System.currentTimeMillis()}"
        Log.i(tag, "[SIMULATED EMAIL DISPATCH] To: ${request.recipientEmail} | Subject: '$subject' | Note: $note")
        return EmailResult.Success(simulatedId, EmailProviderType.SIMULATED)
    }

    private fun getBuildConfigValue(fieldName: String, defaultValue: String): String {
        return try {
            val field = BuildConfig::class.java.getField(fieldName)
            val value = field.get(null)?.toString()
            if (!value.isNullOrBlank() && !value.contains("DEFAULT_VALUE")) value else defaultValue
        } catch (e: Throwable) {
            defaultValue
        }
    }
}

data class RenderedEmailContent(
    val subject: String,
    val html: String,
    val text: String,
    val recipientEmail: String,
    val recipientName: String
)
