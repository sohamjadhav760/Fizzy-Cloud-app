package com.example.data.email

enum class EmailProviderType {
    SENDGRID,
    MAILGUN,
    SMTP,
    SIMULATED
}

enum class EmailTemplateType {
    USER_REGISTRATION,
    PASSWORD_RESET,
    NEW_ORDER_CUSTOMER,
    NEW_ORDER_ADMIN,
    SERVICE_ACTIVATION
}

data class EmailConfig(
    val provider: EmailProviderType = EmailProviderType.SIMULATED,
    val apiKey: String = "",
    val mailgunDomain: String = "mg.fizzycloud.host",
    val senderEmail: String = "noreply@fizzycloud.host",
    val senderName: String = "Fizzy Cloud Hosting",
    val adminNotificationEmail: String = "admin@fizzycloud.host",
    val smtpHost: String = "smtp.mailtrap.io",
    val smtpPort: Int = 587,
    val smtpUser: String = "",
    val smtpPass: String = ""
)

data class EmailRequest(
    val recipientEmail: String,
    val recipientName: String = "",
    val templateType: EmailTemplateType,
    val variables: Map<String, String> = emptyMap(),
    val customSubject: String? = null
)

sealed class EmailResult {
    data class Success(
        val messageId: String,
        val provider: EmailProviderType,
        val timestamp: Long = System.currentTimeMillis()
    ) : EmailResult()

    data class Error(
        val errorMessage: String,
        val errorCode: Int = 500,
        val provider: EmailProviderType,
        val timestamp: Long = System.currentTimeMillis()
    ) : EmailResult()
}
