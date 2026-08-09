package com.example.data.repository

import com.example.data.db.*
import com.example.data.email.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HostingRepository(private val dao: HostingDao) {

    private val emailNotificationService = EmailNotificationService()

    // --- Flows ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allMinecraftPlans: Flow<List<MinecraftPlanEntity>> = dao.getAllMinecraftPlans()
    val activeMinecraftPlans: Flow<List<MinecraftPlanEntity>> = dao.getActiveMinecraftPlans()
    val allVpsPlans: Flow<List<VpsPlanEntity>> = dao.getAllVpsPlans()
    val activeVpsPlans: Flow<List<VpsPlanEntity>> = dao.getActiveVpsPlans()
    val allDomainTlds: Flow<List<DomainTldEntity>> = dao.getAllDomainTlds()
    val activeDomainTlds: Flow<List<DomainTldEntity>> = dao.getActiveDomainTlds()
    val allCoupons: Flow<List<CouponEntity>> = dao.getAllCoupons()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    val allTickets: Flow<List<SupportTicketEntity>> = dao.getAllTickets()
    val websiteSettings: Flow<List<WebsiteSettingEntity>> = dao.getAllWebsiteSettings()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val allEmailLogs: Flow<List<EmailLogEntity>> = dao.getAllEmailLogs()
    val allEmailTemplates: Flow<List<EmailTemplateEntity>> = dao.getAllEmailTemplates()
    val allFaqs: Flow<List<FaqEntity>> = dao.getAllFaqs()
    val allTestimonials: Flow<List<TestimonialEntity>> = dao.getAllTestimonials()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()
    val allNavItems: Flow<List<NavItemEntity>> = dao.getAllNavItems()
    val allFooterLinks: Flow<List<FooterLinkEntity>> = dao.getAllFooterLinks()
    val allPages: Flow<List<PageEntity>> = dao.getAllPages()
    val allAuditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()

    fun getActiveEmailConfig(): EmailConfig = emailNotificationService.getConfig()

    fun getOrdersForUser(userId: Int): Flow<List<OrderEntity>> = dao.getOrdersForUser(userId)
    fun getServicesForUser(userId: Int): Flow<List<ServiceEntity>> = dao.getServicesForUser(userId)
    fun getTicketsForUser(userId: Int): Flow<List<SupportTicketEntity>> = dao.getTicketsForUser(userId)
    fun getTicketMessages(ticketId: String): Flow<List<TicketMessageEntity>> = dao.getTicketMessages(ticketId)

    // --- Actions & CRUD ---
    suspend fun getUserByEmail(email: String) = withContext(Dispatchers.IO) { dao.getUserByEmail(email) }
    suspend fun insertUser(user: UserEntity) = withContext(Dispatchers.IO) { dao.insertUser(user) }
    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) { dao.updateUser(user) }

    suspend fun saveMinecraftPlan(plan: MinecraftPlanEntity) = withContext(Dispatchers.IO) {
        if (plan.id == 0) dao.insertMinecraftPlan(plan) else dao.updateMinecraftPlan(plan)
    }
    suspend fun deleteMinecraftPlan(plan: MinecraftPlanEntity) = withContext(Dispatchers.IO) { dao.deleteMinecraftPlan(plan) }

    suspend fun saveVpsPlan(plan: VpsPlanEntity) = withContext(Dispatchers.IO) {
        if (plan.id == 0) dao.insertVpsPlan(plan) else dao.updateVpsPlan(plan)
    }
    suspend fun deleteVpsPlan(plan: VpsPlanEntity) = withContext(Dispatchers.IO) { dao.deleteVpsPlan(plan) }

    suspend fun saveDomainTld(tld: DomainTldEntity) = withContext(Dispatchers.IO) {
        if (tld.id == 0) dao.insertDomainTld(tld) else dao.updateDomainTld(tld)
    }
    suspend fun deleteDomainTld(tld: DomainTldEntity) = withContext(Dispatchers.IO) { dao.deleteDomainTld(tld) }

    suspend fun saveCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) {
        if (coupon.id == 0) dao.insertCoupon(coupon) else dao.updateCoupon(coupon)
    }
    suspend fun deleteCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) { dao.deleteCoupon(coupon) }
    suspend fun getCouponByCode(code: String) = withContext(Dispatchers.IO) { dao.getCouponByCode(code) }

    suspend fun addToCart(item: CartItemEntity) = withContext(Dispatchers.IO) { dao.insertCartItem(item) }
    suspend fun updateCartItem(item: CartItemEntity) = withContext(Dispatchers.IO) { dao.updateCartItem(item) }
    suspend fun removeCartItem(item: CartItemEntity) = withContext(Dispatchers.IO) { dao.deleteCartItem(item) }
    suspend fun clearCart() = withContext(Dispatchers.IO) { dao.clearCart() }

    suspend fun createOrder(order: OrderEntity) = withContext(Dispatchers.IO) { dao.insertOrder(order) }
    suspend fun updateOrder(order: OrderEntity) = withContext(Dispatchers.IO) { dao.updateOrder(order) }

    suspend fun addService(service: ServiceEntity) = withContext(Dispatchers.IO) { dao.insertService(service) }
    suspend fun updateService(service: ServiceEntity) = withContext(Dispatchers.IO) { dao.updateService(service) }

    suspend fun createTicket(ticket: SupportTicketEntity) = withContext(Dispatchers.IO) { dao.insertTicket(ticket) }
    suspend fun updateTicket(ticket: SupportTicketEntity) = withContext(Dispatchers.IO) { dao.updateTicket(ticket) }
    suspend fun addTicketMessage(message: TicketMessageEntity) = withContext(Dispatchers.IO) { dao.insertTicketMessage(message) }

    suspend fun saveWebsiteSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        dao.saveSetting(WebsiteSettingEntity(key, value))
    }
    suspend fun getSettingValue(key: String): String? = withContext(Dispatchers.IO) { dao.getSettingValue(key) }

    suspend fun addNotification(title: String, message: String, type: String) = withContext(Dispatchers.IO) {
        dao.insertNotification(NotificationEntity(title = title, message = message, type = type))
    }

    // --- FAQs ---
    suspend fun saveFaq(faq: FaqEntity) = withContext(Dispatchers.IO) {
        if (faq.id == 0) dao.insertFaq(faq) else dao.updateFaq(faq)
    }
    suspend fun deleteFaq(faq: FaqEntity) = withContext(Dispatchers.IO) { dao.deleteFaq(faq) }

    // --- Testimonials ---
    suspend fun saveTestimonial(testimonial: TestimonialEntity) = withContext(Dispatchers.IO) {
        if (testimonial.id == 0) dao.insertTestimonial(testimonial) else dao.updateTestimonial(testimonial)
    }
    suspend fun deleteTestimonial(testimonial: TestimonialEntity) = withContext(Dispatchers.IO) { dao.deleteTestimonial(testimonial) }

    // --- Announcements ---
    suspend fun saveAnnouncement(announcement: AnnouncementEntity) = withContext(Dispatchers.IO) {
        if (announcement.id == 0) dao.insertAnnouncement(announcement) else dao.updateAnnouncement(announcement)
    }
    suspend fun deleteAnnouncement(announcement: AnnouncementEntity) = withContext(Dispatchers.IO) { dao.deleteAnnouncement(announcement) }

    // --- Nav Items ---
    suspend fun saveNavItem(item: NavItemEntity) = withContext(Dispatchers.IO) {
        if (item.id == 0) dao.insertNavItem(item) else dao.updateNavItem(item)
    }
    suspend fun deleteNavItem(item: NavItemEntity) = withContext(Dispatchers.IO) { dao.deleteNavItem(item) }

    // --- Footer Links ---
    suspend fun saveFooterLink(link: FooterLinkEntity) = withContext(Dispatchers.IO) {
        if (link.id == 0) dao.insertFooterLink(link) else dao.updateFooterLink(link)
    }
    suspend fun deleteFooterLink(link: FooterLinkEntity) = withContext(Dispatchers.IO) { dao.deleteFooterLink(link) }

    // --- Pages ---
    suspend fun savePage(page: PageEntity) = withContext(Dispatchers.IO) {
        if (page.id == 0) dao.insertPage(page) else dao.updatePage(page)
    }
    suspend fun deletePage(page: PageEntity) = withContext(Dispatchers.IO) { dao.deletePage(page) }

    // --- Audit Log ---
    suspend fun logAudit(
        adminName: String,
        action: String,
        targetItem: String,
        previousValue: String = "",
        newValue: String = ""
    ) = withContext(Dispatchers.IO) {
        dao.insertAuditLog(
            AuditLogEntity(
                adminName = adminName,
                action = action,
                targetItem = targetItem,
                previousValue = previousValue,
                newValue = newValue
            )
        )
    }


    // --- Email Service Actions ---
    suspend fun sendEmailNotification(
        request: EmailRequest,
        configOverride: EmailConfig? = null
    ): Pair<EmailResult, RenderedEmailContent> = withContext(Dispatchers.IO) {
        // Retrieve template from DB or fallback to default
        val templateFromDb = dao.getEmailTemplate(request.templateType.name)
        val defaultTemplate = EmailTemplateDefaults.ALL_TEMPLATES.find { it.templateType == request.templateType.name }

        val subjectTpl = request.customSubject ?: templateFromDb?.subjectTemplate ?: defaultTemplate?.subjectTemplate ?: "Notification from Fizzy Cloud"
        val htmlTpl = templateFromDb?.htmlTemplate ?: defaultTemplate?.htmlTemplate ?: "<p>Hello {{user_name}}</p>"
        val textTpl = templateFromDb?.textTemplate ?: defaultTemplate?.textTemplate ?: "Hello {{user_name}}"

        val (result, rendered) = emailNotificationService.dispatchEmail(
            request = request,
            subjectTemplate = subjectTpl,
            htmlTemplate = htmlTpl,
            textTemplate = textTpl,
            configOverride = configOverride
        )

        val (statusStr, errorStr, providerStr) = when (result) {
            is EmailResult.Success -> Triple(if (result.provider == EmailProviderType.SIMULATED) "SIMULATED" else "SENT", "", result.provider.name)
            is EmailResult.Error -> Triple("FAILED", result.errorMessage, result.provider.name)
        }

        val logEntity = EmailLogEntity(
            recipientEmail = request.recipientEmail,
            recipientName = request.recipientName,
            templateType = request.templateType.name,
            subject = rendered.subject,
            bodyPreview = rendered.text.take(160),
            fullHtml = rendered.html,
            providerUsed = providerStr,
            status = statusStr,
            errorMessage = errorStr
        )
        dao.insertEmailLog(logEntity)

        Pair(result, rendered)
    }

    suspend fun saveEmailTemplate(template: EmailTemplateEntity) = withContext(Dispatchers.IO) {
        dao.insertEmailTemplate(template)
    }

    suspend fun clearEmailLogs() = withContext(Dispatchers.IO) {
        dao.clearEmailLogs()
    }

    suspend fun seedEmailTemplatesIfEmpty() = withContext(Dispatchers.IO) {
        for (tpl in EmailTemplateDefaults.ALL_TEMPLATES) {
            val existing = dao.getEmailTemplate(tpl.templateType)
            if (existing == null) {
                dao.insertEmailTemplate(tpl)
            }
        }
    }

    // --- Seeding Database with Initial Demo Content ---
    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        seedEmailTemplatesIfEmpty()

        val existingUser = dao.getUserByEmail("admin@fizzycloud.gg")
        if (existingUser != null) return@withContext


        // Seed Users
        val adminId = dao.insertUser(
            UserEntity(name = "Owner (Super Admin)", email = "admin@fizzycloud.gg", role = "SUPER_ADMIN")
        ).toInt()
        dao.insertUser(
            UserEntity(name = "Support Agent", email = "support@fizzycloud.gg", role = "SUPPORT")
        )
        val customerId = dao.insertUser(
            UserEntity(name = "Alex Gamer", email = "alex@gamer.com", role = "CUSTOMER", phone = "+91 9876543210")
        ).toInt()

        // Seed Minecraft Plans
        dao.insertMinecraftPlan(
            MinecraftPlanEntity(
                name = "Starter",
                description = "Ideal for small friend groups & test servers",
                priceMonthly = 99.0,
                priceYearly = 990.0,
                ramGb = 2,
                cpuCores = 2,
                storageGb = 10,
                playerSlots = "5 Slots",
                features = "DDoS Protection, Instant Setup, Vanilla/Paper, Modpack Support, 1 Gbps Port",
                displayOrder = 1
            )
        )
        dao.insertMinecraftPlan(
            MinecraftPlanEntity(
                name = "Pro",
                description = "Best performance for active survival & SMP communities",
                priceMonthly = 199.0,
                priceYearly = 1990.0,
                ramGb = 4,
                cpuCores = 3,
                storageGb = 25,
                playerSlots = "15 Slots",
                features = "DDoS Protection, Instant Setup, Automated Daily Backups, Free Subdomain, Modpack Installer",
                badge = "POPULAR",
                isPopular = true,
                displayOrder = 2
            )
        )
        dao.insertMinecraftPlan(
            MinecraftPlanEntity(
                name = "Premium",
                description = "Maximum speed for high player count & plugin heavy servers",
                priceMonthly = 399.0,
                priceYearly = 3990.0,
                ramGb = 8,
                cpuCores = 4,
                storageGb = 50,
                playerSlots = "Unlimited",
                features = "DDoS Protection, Priority Support, Dedicated IP, Free MySQL Database, Unlimited Backups",
                badge = "BEST VALUE",
                displayOrder = 3
            )
        )
        dao.insertMinecraftPlan(
            MinecraftPlanEntity(
                name = "Ultra Enterprise",
                description = "Custom dedicated thread allocation for network hub servers",
                priceMonthly = 799.0,
                priceYearly = 7990.0,
                ramGb = 16,
                cpuCores = 8,
                storageGb = 120,
                playerSlots = "Unlimited",
                features = "Ultra Low Latency, 24/7 VIP Discord Agent, BungeeCord Proxy Ready, Custom Java Flags",
                displayOrder = 4
            )
        )

        // Seed VPS Plans
        dao.insertVpsPlan(
            VpsPlanEntity(
                name = "VPS Basic",
                description = "Fast virtual private server for light gaming or web hosting",
                priceMonthly = 299.0,
                cpuCores = 2,
                ramGb = 4,
                storageNvmeGb = 50,
                bandwidthTb = 1.0,
                portSpeed = "1 Gbps",
                features = "Root Access, 1 IPv4, Instant Provisioning, Linux Distros",
                displayOrder = 1
            )
        )
        dao.insertVpsPlan(
            VpsPlanEntity(
                name = "VPS Pro",
                description = "High power node for game networks and databases",
                priceMonthly = 599.0,
                cpuCores = 4,
                ramGb = 8,
                storageNvmeGb = 100,
                bandwidthTb = 2.0,
                portSpeed = "1 Gbps",
                features = "Root Access, 1 IPv4, Daily Snapshot, Automated DDoS Firewall",
                badge = "BEST VALUE",
                displayOrder = 2
            )
        )
        dao.insertVpsPlan(
            VpsPlanEntity(
                name = "VPS Enterprise",
                description = "Heavy workload virtual server with dedicated core pinning",
                priceMonthly = 999.0,
                cpuCores = 8,
                ramGb = 16,
                storageNvmeGb = 200,
                bandwidthTb = 4.0,
                portSpeed = "1 Gbps",
                features = "Root Access, 2 IPv4, 1-Click OS Reload, VIP Node Monitoring",
                displayOrder = 3
            )
        )

        // Seed Domain TLDs
        dao.insertDomainTld(DomainTldEntity(tld = ".com", registerPrice = 799.0, renewPrice = 999.0, transferPrice = 799.0, description = "Most recognized worldwide domain"))
        dao.insertDomainTld(DomainTldEntity(tld = ".in", registerPrice = 399.0, renewPrice = 599.0, transferPrice = 399.0, description = "Great choice for Indian server communities"))
        dao.insertDomainTld(DomainTldEntity(tld = ".net", registerPrice = 899.0, renewPrice = 1099.0, transferPrice = 899.0, description = "Popular for network hubs"))
        dao.insertDomainTld(DomainTldEntity(tld = ".org", registerPrice = 949.0, renewPrice = 1149.0, transferPrice = 949.0, description = "Trusted organization TLD"))
        dao.insertDomainTld(DomainTldEntity(tld = ".xyz", registerPrice = 199.0, renewPrice = 899.0, transferPrice = 199.0, description = "Modern & affordable gaming TLD"))
        dao.insertDomainTld(DomainTldEntity(tld = ".online", registerPrice = 149.0, renewPrice = 999.0, transferPrice = 149.0, description = "Perfect for active online communities"))

        // Seed Coupons
        dao.insertCoupon(CouponEntity(code = "FIZZY10", discountType = "PERCENTAGE", value = 10.0, minOrderValue = 0.0))
        dao.insertCoupon(CouponEntity(code = "MINECRAFT50", discountType = "FIXED", value = 50.0, minOrderValue = 199.0))
        dao.insertCoupon(CouponEntity(code = "WELCOME20", discountType = "PERCENTAGE", value = 20.0, minOrderValue = 0.0))

        // Seed Website Settings
        dao.saveSetting(WebsiteSettingEntity("website_name", "Fizzy Cloud Hosting"))
        dao.saveSetting(WebsiteSettingEntity("website_logo", "Fizzy Cloud"))
        dao.saveSetting(WebsiteSettingEntity("hero_headline", "Powerful Cloud Hosting. Built for Gamers."))
        dao.saveSetting(WebsiteSettingEntity("hero_subheading", "Reliable Minecraft Servers, Domains, and VPS Hosting at affordable prices."))
        dao.saveSetting(WebsiteSettingEntity("hero_button_text", "Explore Minecraft Plans"))
        dao.saveSetting(WebsiteSettingEntity("announcement_banner", "⚡ Summer Sale! Use code FIZZY10 for 10% off all server plans."))
        dao.saveSetting(WebsiteSettingEntity("contact_email", "support@fizzycloud.gg"))
        dao.saveSetting(WebsiteSettingEntity("contact_phone", "+91 98765 43210"))
        dao.saveSetting(WebsiteSettingEntity("contact_discord", "discord.gg/fizzycloud"))
        dao.saveSetting(WebsiteSettingEntity("currency_symbol", "₹"))
        dao.saveSetting(WebsiteSettingEntity("currency_code", "INR"))
        dao.saveSetting(WebsiteSettingEntity("primary_color_hex", "#10B981"))
        dao.saveSetting(WebsiteSettingEntity("secondary_color_hex", "#06B6D4"))

        // Seed FAQs
        dao.insertFaq(FaqEntity(question = "How fast is server setup?", answer = "Your server is provisioned automatically within 60 seconds after payment confirmation.", category = "Hosting", displayOrder = 1))
        dao.insertFaq(FaqEntity(question = "Can I upgrade my plan later?", answer = "Yes! You can upgrade your RAM and storage anytime from your customer dashboard with zero data loss.", category = "Hosting", displayOrder = 2))
        dao.insertFaq(FaqEntity(question = "Do you offer DDoS protection?", answer = "All Minecraft and VPS servers include automated 1.2 Tbps DDoS mitigation at no extra charge.", category = "Security", displayOrder = 3))
        dao.insertFaq(FaqEntity(question = "What payment methods are supported?", answer = "We accept Razorpay, UPI, Credit/Debit Cards, NetBanking, Stripe, and Crypto.", category = "Billing", displayOrder = 4))

        // Seed Testimonials
        dao.insertTestimonial(TestimonialEntity(authorName = "Minecraft India Community", authorRole = "SMP Network", rating = 5, reviewText = "Fizzy Cloud runs our 100+ player SMP with zero lag and instant support!", displayOrder = 1))
        dao.insertTestimonial(TestimonialEntity(authorName = "Mark S.", authorRole = "VPS Server Owner", rating = 5, reviewText = "Switched from another host to Fizzy Cloud VPS. 10/10 performance and price.", displayOrder = 2))
        dao.insertTestimonial(TestimonialEntity(authorName = "Apex Build Team", authorRole = "Development Team", rating = 5, reviewText = "The automated backups and instant subdomains saved us hours of work.", displayOrder = 3))

        // Seed Announcements
        dao.insertAnnouncement(AnnouncementEntity(title = "Summer Host Promo", message = "⚡ Summer Sale! Use code FIZZY10 for 10% off all server plans.", buttonText = "Claim Coupon", buttonUrl = "coupons", isActive = true))

        // Seed Nav Items
        dao.insertNavItem(NavItemEntity(label = "Home", destination = "home", iconName = "Home", displayOrder = 1))
        dao.insertNavItem(NavItemEntity(label = "Minecraft", destination = "minecraft", iconName = "Dns", displayOrder = 2))
        dao.insertNavItem(NavItemEntity(label = "VPS Hosting", destination = "vps", iconName = "Storage", displayOrder = 3))
        dao.insertNavItem(NavItemEntity(label = "Domains", destination = "domain", iconName = "Language", displayOrder = 4))
        dao.insertNavItem(NavItemEntity(label = "Contact", destination = "tickets", iconName = "SupportAgent", displayOrder = 5))

        // Seed Footer Links
        dao.insertFooterLink(FooterLinkEntity(columnTitle = "Products", label = "Minecraft Hosting", destination = "minecraft", displayOrder = 1))
        dao.insertFooterLink(FooterLinkEntity(columnTitle = "Products", label = "VPS Servers", destination = "vps", displayOrder = 2))
        dao.insertFooterLink(FooterLinkEntity(columnTitle = "Products", label = "Domain Search", destination = "domain", displayOrder = 3))
        dao.insertFooterLink(FooterLinkEntity(columnTitle = "Company", label = "About Us", destination = "about", displayOrder = 1))
        dao.insertFooterLink(FooterLinkEntity(columnTitle = "Company", label = "Privacy Policy", destination = "privacy", displayOrder = 2))
        dao.insertFooterLink(FooterLinkEntity(columnTitle = "Company", label = "Terms of Service", destination = "terms", displayOrder = 3))

        // Seed Pages
        dao.insertPage(PageEntity(title = "About Fizzy Cloud", slug = "about", content = "Fizzy Cloud Hosting is a high-performance cloud hosting provider specializing in low-latency Minecraft servers, enterprise VPS hosting, and domain management across global datacenters.", isPublished = true))
        dao.insertPage(PageEntity(title = "Terms of Service", slug = "terms", content = "By using Fizzy Cloud Hosting services, you agree to comply with our Acceptable Use Policy and fair resource usage guidelines.", isPublished = true))
        dao.insertPage(PageEntity(title = "Privacy Policy", slug = "privacy", content = "Your data privacy and server security are our highest priority. We do not sell or expose user personal information.", isPublished = true))

        // Seed Audit Logs
        dao.insertAuditLog(AuditLogEntity(adminName = "Super Admin", action = "INITIALIZED_SYSTEM", targetItem = "Database", newValue = "Seeded core products and default CMS settings"))


        // Seed Services for Customer Alex
        dao.insertService(
            ServiceEntity(
                userId = customerId,
                serviceType = "MINECRAFT",
                name = "DragonCraft SMP",
                ipAddress = "142.93.201.88:25565",
                status = "RUNNING",
                cpuUsagePercent = 22,
                ramUsageMb = 1840,
                maxRamMb = 4096,
                configDetails = "Paper 1.20.4 | Singapore Location | 4GB RAM"
            )
        )
        dao.insertService(
            ServiceEntity(
                userId = customerId,
                serviceType = "DOMAIN",
                name = "dragoncraft.in",
                ipAddress = "A Record -> 142.93.201.88",
                status = "RUNNING",
                configDetails = "Auto-Renew Enabled | DNS Managed"
            )
        )

        // Seed Customer Order
        dao.insertOrder(
            OrderEntity(
                orderId = "ORD-1092",
                userId = customerId,
                customerName = "Alex Gamer",
                customerEmail = "alex@gamer.com",
                itemsSummary = "Minecraft Pro Plan (4GB RAM) - Paper 1.20.4",
                subtotal = 199.0,
                discount = 19.9,
                tax = 32.2,
                totalAmount = 211.3,
                status = "PAID",
                paymentMethod = "Razorpay UPI"
            )
        )

        // Seed Support Ticket
        val ticketId = "TCK-8821"
        dao.insertTicket(
            SupportTicketEntity(
                ticketId = ticketId,
                userId = customerId,
                customerName = "Alex Gamer",
                category = "Technical Support",
                subject = "How to install GeyserMC Bedrock plugin?",
                priority = "MEDIUM",
                status = "OPEN"
            )
        )
        dao.insertTicketMessage(
            TicketMessageEntity(
                ticketId = ticketId,
                senderName = "Alex Gamer",
                senderRole = "CUSTOMER",
                message = "Hi! I bought the Pro plan for DragonCraft SMP. How do I enable Bedrock crossplay via GeyserMC?"
            )
        )
        dao.insertTicketMessage(
            TicketMessageEntity(
                ticketId = ticketId,
                senderName = "Support Agent",
                senderRole = "SUPPORT",
                message = "Hello Alex! You can install GeyserMC directly with 1-click in your server file manager, or drop Geyser-Spigot.jar into /plugins folder!"
            )
        )

        // Initial Notification
        dao.insertNotification(
            NotificationEntity(
                title = "Welcome to Fizzy Cloud Hosting!",
                message = "Your account is set up. Explore Minecraft servers, VPS, and Domain registrations.",
                type = "ANNOUNCEMENT"
            )
        )
    }
}
