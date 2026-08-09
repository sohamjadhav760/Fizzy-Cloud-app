package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// User Account
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val role: String = "CUSTOMER", // SUPER_ADMIN, MANAGER, SUPPORT, CUSTOMER
    val status: String = "ACTIVE", // ACTIVE, SUSPENDED
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// Minecraft Hosting Plans
@Entity(tableName = "minecraft_plans")
data class MinecraftPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "High performance server plan",
    val priceMonthly: Double,
    val priceYearly: Double = priceMonthly * 10,
    val ramGb: Int = 4,
    val cpuCores: Int = 2,
    val storageGb: Int = 20,
    val playerSlots: String = "20 Slots", // e.g. "5 Slots", "Unlimited"
    val features: String = "DDoS Protection, Instant Setup, NVMe Storage", // Comma separated features
    val badge: String = "", // "POPULAR", "BEST VALUE", ""
    val isPopular: Boolean = false,
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val locations: String = "India, Singapore, Germany, US East",
    val supportedSoftware: String = "Paper, Spigot, Purpur, Vanilla, Fabric, Forge"
)

// VPS Hosting Plans
@Entity(tableName = "vps_plans")
data class VpsPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val priceMonthly: Double,
    val cpuCores: Int,
    val ramGb: Int,
    val storageNvmeGb: Int,
    val bandwidthTb: Double,
    val portSpeed: String = "1 Gbps",
    val features: String,
    val badge: String = "",
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val operatingSystems: String = "Ubuntu 22.04, Debian 12, AlmaLinux 9, Windows Server"
)

// Domain TLD Pricing
@Entity(tableName = "domain_tlds")
data class DomainTldEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tld: String, // e.g. ".com", ".in", ".net", ".org", ".xyz", ".online"
    val registerPrice: Double,
    val renewPrice: Double,
    val transferPrice: Double,
    val description: String = "",
    val isActive: Boolean = true
)

// Discount Coupons
@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String, // e.g., CRAFT10
    val discountType: String, // PERCENTAGE, FIXED
    val value: Double, // e.g. 10.0 for 10% or 50.0 for ₹50
    val minOrderValue: Double = 0.0,
    val maxUses: Int = 100,
    val usedCount: Int = 0,
    val isActive: Boolean = true,
    val expiryDate: String = "2028-12-31"
)

// Shopping Cart Items
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productType: String, // MINECRAFT, VPS, DOMAIN
    val productId: Int,
    val productName: String,
    val billingCycle: String = "MONTHLY", // MONTHLY, YEARLY
    val configSummary: String, // e.g., "Paper 1.20.4 | Singapore | IP Addon"
    val unitPrice: Double,
    val quantity: Int = 1
)

// Customer Orders
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String, // e.g. "ORD-1092"
    val userId: Int,
    val customerName: String,
    val customerEmail: String,
    val itemsSummary: String,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val totalAmount: Double,
    val status: String = "PAID", // PAID, PENDING, CANCELLED, REFUNDED
    val paymentMethod: String = "Razorpay",
    val createdAt: Long = System.currentTimeMillis(),
    val renewalDate: Long = System.currentTimeMillis() + 30L * 24 * 3600 * 1000,
    val notes: String = ""
)

// Active Hosted Services (Server/Domain Management)
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val serviceType: String, // MINECRAFT, VPS, DOMAIN
    val name: String,
    val ipAddress: String = "192.168.1.100:25565",
    val status: String = "RUNNING", // RUNNING, STOPPED, SUSPENDED
    val cpuUsagePercent: Int = 18,
    val ramUsageMb: Int = 1420,
    val maxRamMb: Int = 4096,
    val configDetails: String,
    val renewalDate: String = "2026-09-08"
)

// Support Tickets
@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val ticketId: String, // e.g. "TCK-8821"
    val userId: Int,
    val customerName: String,
    val category: String, // Billing, Technical, Minecraft Config, General
    val subject: String,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, URGENT
    val status: String = "OPEN", // OPEN, IN_PROGRESS, CLOSED
    val lastUpdated: Long = System.currentTimeMillis()
)

// Ticket Messages
@Entity(tableName = "ticket_messages")
data class TicketMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: String,
    val senderName: String,
    val senderRole: String, // CUSTOMER, SUPPORT, ADMIN
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Dynamic Website Settings (Controlled via Admin)
@Entity(tableName = "website_settings")
data class WebsiteSettingEntity(
    @PrimaryKey val keyName: String,
    val value: String
)

// System & Customer Notifications
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // ORDER, SERVICE, SUPPORT, ANNOUNCEMENT
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// Email Service Outbox Logs
@Entity(tableName = "email_logs")
data class EmailLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientEmail: String,
    val recipientName: String = "",
    val templateType: String, // USER_REGISTRATION, PASSWORD_RESET, NEW_ORDER_CUSTOMER, NEW_ORDER_ADMIN, SERVICE_ACTIVATION
    val subject: String,
    val bodyPreview: String,
    val fullHtml: String,
    val providerUsed: String, // SENDGRID, MAILGUN, SMTP, SIMULATED
    val status: String, // SENT, SIMULATED, FAILED
    val errorMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Email Templates (Stored in Room for dynamic editing via Admin)
@Entity(tableName = "email_templates")
data class EmailTemplateEntity(
    @PrimaryKey val templateType: String, // USER_REGISTRATION, PASSWORD_RESET, NEW_ORDER_CUSTOMER, NEW_ORDER_ADMIN, SERVICE_ACTIVATION
    val name: String,
    val description: String,
    val subjectTemplate: String,
    val htmlTemplate: String,
    val textTemplate: String,
    val placeholders: String, // Comma separated, e.g. "{{user_name}}, {{order_id}}"
    val isActive: Boolean = true,
    val lastModified: Long = System.currentTimeMillis()
)

// FAQs CMS
@Entity(tableName = "faqs")
data class FaqEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val category: String = "General",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

// Testimonials CMS
@Entity(tableName = "testimonials")
data class TestimonialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorRole: String,
    val avatarUrl: String = "",
    val rating: Int = 5,
    val reviewText: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

// Announcement Banner System
@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val buttonText: String = "Claim Offer",
    val buttonUrl: String = "",
    val displayLocation: String = "HEADER_BANNER",
    val isActive: Boolean = true
)

// Header Navigation Menu Editor
@Entity(tableName = "nav_items")
data class NavItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val destination: String,
    val iconName: String = "Home",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

// Footer Links Editor
@Entity(tableName = "footer_links")
data class FooterLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val columnTitle: String,
    val label: String,
    val destination: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

// Pages Management CMS
@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val slug: String,
    val content: String,
    val isPublished: Boolean = true,
    val lastModified: Long = System.currentTimeMillis()
)

// Audit Log / Activity Logs
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminName: String,
    val action: String,
    val targetItem: String,
    val previousValue: String = "",
    val newValue: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


