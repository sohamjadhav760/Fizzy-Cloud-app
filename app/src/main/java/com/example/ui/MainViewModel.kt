package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.email.*
import com.example.data.repository.HostingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HostingRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HostingRepository(database.hostingDao())
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // --- UI Theme & Navigation ---
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private val _currentTab = MutableStateFlow("home") // home, minecraft, domains, vps, cart, dashboard, admin
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    fun navigateTo(tab: String) {
        _currentTab.value = tab
    }

    // --- User Session ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Default to Alex Gamer as logged in customer, or Admin if switched
        viewModelScope.launch {
            repository.allUsers.collect { users ->
                if (_currentUser.value == null && users.isNotEmpty()) {
                    _currentUser.value = users.firstOrNull { it.email == "alex@gamer.com" } ?: users.first()
                }
            }
        }
    }

    fun switchUserRole(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
            }
        }
    }

    // --- Data Streams ---
    val minecraftPlans: StateFlow<List<MinecraftPlanEntity>> = repository.activeMinecraftPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMinecraftPlansAdmin: StateFlow<List<MinecraftPlanEntity>> = repository.allMinecraftPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vpsPlans: StateFlow<List<VpsPlanEntity>> = repository.activeVpsPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVpsPlansAdmin: StateFlow<List<VpsPlanEntity>> = repository.allVpsPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val domainTlds: StateFlow<List<DomainTldEntity>> = repository.activeDomainTlds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDomainTldsAdmin: StateFlow<List<DomainTldEntity>> = repository.allDomainTlds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<CouponEntity>> = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val websiteSettings: StateFlow<List<WebsiteSettingEntity>> = repository.websiteSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFaqs: StateFlow<List<FaqEntity>> = repository.allFaqs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTestimonials: StateFlow<List<TestimonialEntity>> = repository.allTestimonials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNavItems: StateFlow<List<NavItemEntity>> = repository.allNavItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFooterLinks: StateFlow<List<FooterLinkEntity>> = repository.allFooterLinks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPages: StateFlow<List<PageEntity>> = repository.allPages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- Email Notification Service State & Flows ---
    val emailLogs: StateFlow<List<EmailLogEntity>> = repository.allEmailLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emailTemplates: StateFlow<List<EmailTemplateEntity>> = repository.allEmailTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getActiveEmailConfig(): EmailConfig = repository.getActiveEmailConfig()

    private val _emailStatusToast = MutableStateFlow<String?>(null)
    val emailStatusToast: StateFlow<String?> = _emailStatusToast.asStateFlow()

    fun clearEmailStatusToast() {
        _emailStatusToast.value = null
    }

    fun sendUserRegistrationEmail(email: String, name: String, role: String = "CUSTOMER") {
        viewModelScope.launch {
            val req = EmailRequest(
                recipientEmail = email,
                recipientName = name,
                templateType = EmailTemplateType.USER_REGISTRATION,
                variables = mapOf(
                    "user_name" to name,
                    "user_email" to email,
                    "user_role" to role
                )
            )
            val (result, _) = repository.sendEmailNotification(req)
            val toast = when (result) {
                is EmailResult.Success -> "Registration email sent to $email (${result.provider})"
                is EmailResult.Error -> "Failed to send registration email: ${result.errorMessage}"
            }
            _emailStatusToast.value = toast
            repository.addNotification("Email Sent", "User registration confirmation email sent to $email", "ANNOUNCEMENT")
        }
    }

    fun sendPasswordResetEmail(email: String, name: String) {
        viewModelScope.launch {
            val req = EmailRequest(
                recipientEmail = email,
                recipientName = name,
                templateType = EmailTemplateType.PASSWORD_RESET,
                variables = mapOf(
                    "user_name" to name,
                    "user_email" to email
                )
            )
            val (result, _) = repository.sendEmailNotification(req)
            val toast = when (result) {
                is EmailResult.Success -> "Password reset email dispatched to $email (${result.provider})"
                is EmailResult.Error -> "Failed to send reset email: ${result.errorMessage}"
            }
            _emailStatusToast.value = toast
            repository.addNotification("Password Reset Sent", "Reset instructions mailed to $email", "SUPPORT")
        }
    }

    fun sendNewOrderEmails(order: OrderEntity) {
        viewModelScope.launch {
            // 1. Customer Order Confirmation Email
            val customerReq = EmailRequest(
                recipientEmail = order.customerEmail,
                recipientName = order.customerName,
                templateType = EmailTemplateType.NEW_ORDER_CUSTOMER,
                variables = mapOf(
                    "user_name" to order.customerName,
                    "order_id" to order.orderId,
                    "items_summary" to order.itemsSummary,
                    "subtotal" to "₹${"%.2f".format(order.subtotal)}",
                    "discount" to "₹${"%.2f".format(order.discount)}",
                    "tax" to "₹${"%.2f".format(order.tax)}",
                    "total_amount" to "₹${"%.2f".format(order.totalAmount)}",
                    "payment_method" to order.paymentMethod
                )
            )
            val (custResult, _) = repository.sendEmailNotification(customerReq)

            // 2. Admin Order Notification Email
            val config = repository.getActiveEmailConfig()
            val adminReq = EmailRequest(
                recipientEmail = config.adminNotificationEmail,
                recipientName = "Fizzy Cloud Admin Team",
                templateType = EmailTemplateType.NEW_ORDER_ADMIN,
                variables = mapOf(
                    "user_name" to order.customerName,
                    "user_email" to order.customerEmail,
                    "order_id" to order.orderId,
                    "items_summary" to order.itemsSummary,
                    "total_amount" to "₹${"%.2f".format(order.totalAmount)}",
                    "payment_method" to order.paymentMethod
                )
            )
            val (adminResult, _) = repository.sendEmailNotification(adminReq)

            _emailStatusToast.value = "Order confirmed! Emails sent to customer & admin."
        }
    }

    fun sendServiceActivationEmail(
        recipientEmail: String,
        recipientName: String,
        serviceName: String,
        serviceType: String,
        ipAddress: String,
        configDetails: String
    ) {
        viewModelScope.launch {
            val req = EmailRequest(
                recipientEmail = recipientEmail,
                recipientName = recipientName,
                templateType = EmailTemplateType.SERVICE_ACTIVATION,
                variables = mapOf(
                    "user_name" to recipientName,
                    "service_name" to serviceName,
                    "service_type" to serviceType,
                    "ip_address" to ipAddress,
                    "config_details" to configDetails
                )
            )
            val (result, _) = repository.sendEmailNotification(req)
            _emailStatusToast.value = "Service activation email sent for $serviceName"
        }
    }

    fun sendTestEmail(
        templateType: EmailTemplateType,
        recipientEmail: String,
        recipientName: String,
        testVars: Map<String, String> = emptyMap(),
        customSubject: String? = null
    ) {
        viewModelScope.launch {
            val req = EmailRequest(
                recipientEmail = recipientEmail,
                recipientName = recipientName,
                templateType = templateType,
                variables = testVars,
                customSubject = customSubject
            )
            val (result, rendered) = repository.sendEmailNotification(req)
            val msg = when (result) {
                is EmailResult.Success -> "Test email sent successfully! Provider: ${result.provider}, ID: ${result.messageId}"
                is EmailResult.Error -> "Test email failed: ${result.errorMessage}"
            }
            _emailStatusToast.value = msg
        }
    }

    fun saveEmailTemplate(template: EmailTemplateEntity) {
        viewModelScope.launch {
            repository.saveEmailTemplate(template)
            _emailStatusToast.value = "Updated template '${template.name}'"
        }
    }

    fun clearEmailLogs() {
        viewModelScope.launch {
            repository.clearEmailLogs()
            _emailStatusToast.value = "Cleared email logs"
        }
    }

    fun registerNewUserAccount(name: String, email: String, role: String = "CUSTOMER", phone: String = "") {
        viewModelScope.launch {
            val newUser = UserEntity(name = name, email = email, role = role, phone = phone)
            val id = repository.insertUser(newUser)
            _currentUser.value = newUser.copy(id = id.toInt())
            sendUserRegistrationEmail(email, name, role)
            repository.addNotification("Account Created", "Welcome $name! Account created.", "ANNOUNCEMENT")
        }
    }

    fun getTicketMessages(ticketId: String): Flow<List<TicketMessageEntity>> = repository.getTicketMessages(ticketId)


    // --- Applied Coupon State ---
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val coupon = repository.getCouponByCode(code.trim().uppercase())
            if (coupon != null && coupon.isActive) {
                _appliedCoupon.value = coupon
                _couponMessage.value = "Coupon '${coupon.code}' applied successfully!"
            } else {
                _appliedCoupon.value = null
                _couponMessage.value = "Invalid or expired coupon code."
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponMessage.value = null
    }

    // --- Cart Actions ---
    fun addToCart(item: CartItemEntity) {
        viewModelScope.launch {
            repository.addToCart(item)
            repository.addNotification("Item Added to Cart", "${item.productName} was added to your cart.", "ORDER")
        }
    }

    fun updateCartQuantity(item: CartItemEntity, newQty: Int) {
        viewModelScope.launch {
            if (newQty <= 0) {
                repository.removeCartItem(item)
            } else {
                repository.updateCartItem(item.copy(quantity = newQty))
            }
        }
    }

    fun removeCartItem(item: CartItemEntity) {
        viewModelScope.launch {
            repository.removeCartItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            _appliedCoupon.value = null
        }
    }

    // --- Checkout Flow ---
    fun processCheckout(customerName: String, customerEmail: String, paymentMethod: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val subtotal = items.sumOf { it.unitPrice * it.quantity }
            val coupon = _appliedCoupon.value
            val discount = if (coupon != null) {
                if (coupon.discountType == "PERCENTAGE") (subtotal * coupon.value / 100.0) else coupon.value
            } else 0.0
            val tax = (subtotal - discount) * 0.18 // 18% GST tax demo
            val total = (subtotal - discount + tax).coerceAtLeast(0.0)

            val orderId = "ORD-${(1000..9999).random()}"
            val user = currentUser.value
            val userId = user?.id ?: 1

            val itemsSummary = items.joinToString(", ") { "${it.productName} (${it.configSummary})" }

            val order = OrderEntity(
                orderId = orderId,
                userId = userId,
                customerName = customerName,
                customerEmail = customerEmail,
                itemsSummary = itemsSummary,
                subtotal = subtotal,
                discount = discount,
                tax = tax,
                totalAmount = total,
                status = "PAID",
                paymentMethod = paymentMethod
            )

            repository.createOrder(order)

            // Auto provision service & send activation emails
            items.forEach { cartItem ->
                val ip = "${(100..200).random()}.${(10..99).random()}.${(10..99).random()}.${(10..99).random()}:25565"
                repository.addService(
                    ServiceEntity(
                        userId = userId,
                        serviceType = cartItem.productType,
                        name = cartItem.productName,
                        ipAddress = ip,
                        status = "RUNNING",
                        configDetails = cartItem.configSummary
                    )
                )

                // Dispatch Service Activation Email
                sendServiceActivationEmail(
                    recipientEmail = customerEmail,
                    recipientName = customerName,
                    serviceName = cartItem.productName,
                    serviceType = cartItem.productType,
                    ipAddress = ip,
                    configDetails = cartItem.configSummary
                )
            }

            // Dispatch Order Confirmation Emails (Customer + Admin)
            sendNewOrderEmails(order)

            repository.clearCart()
            repository.addNotification("Order Confirmed!", "Order $orderId paid successfully via $paymentMethod.", "ORDER")
            onSuccess()
        }
    }

    // --- Domain Search ---
    private val _domainSearchQuery = MutableStateFlow("")
    val domainSearchQuery: StateFlow<String> = _domainSearchQuery.asStateFlow()

    fun updateDomainSearchQuery(query: String) {
        _domainSearchQuery.value = query
    }

    // --- Service Control Actions (Start, Stop, Restart) ---
    fun updateServiceStatus(serviceId: Int, newStatus: String) {
        viewModelScope.launch {
            val services = allServices.value
            val service = services.find { it.id == serviceId }
            if (service != null) {
                val updated = service.copy(status = newStatus)
                repository.updateService(updated)
                repository.addNotification("Service Updated", "${service.name} status changed to $newStatus.", "SERVICE")
            }
        }
    }

    // --- Support Ticket Actions ---
    fun createSupportTicket(subject: String, category: String, message: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val userId = user?.id ?: 1
            val userName = user?.name ?: "Customer"
            val ticketId = "TCK-${(1000..9999).random()}"

            val ticket = SupportTicketEntity(
                ticketId = ticketId,
                userId = userId,
                customerName = userName,
                category = category,
                subject = subject,
                priority = "MEDIUM",
                status = "OPEN"
            )
            repository.createTicket(ticket)

            val msg = TicketMessageEntity(
                ticketId = ticketId,
                senderName = userName,
                senderRole = user?.role ?: "CUSTOMER",
                message = message
            )
            repository.addTicketMessage(msg)
            repository.addNotification("Ticket Created", "Ticket $ticketId submitted to support team.", "SUPPORT")
        }
    }

    fun replyToTicket(ticketId: String, message: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val senderName = user?.name ?: "Staff"
            val senderRole = user?.role ?: "SUPPORT"

            val msg = TicketMessageEntity(
                ticketId = ticketId,
                senderName = senderName,
                senderRole = senderRole,
                message = message
            )
            repository.addTicketMessage(msg)

            val ticket = allTickets.value.find { it.ticketId == ticketId }
            if (ticket != null) {
                val newStatus = if (senderRole == "CUSTOMER") "OPEN" else "IN_PROGRESS"
                repository.updateTicket(ticket.copy(status = newStatus, lastUpdated = System.currentTimeMillis()))
            }
        }
    }

    fun updateTicketStatus(ticketId: String, newStatus: String) {
        viewModelScope.launch {
            val ticket = allTickets.value.find { it.ticketId == ticketId }
            if (ticket != null) {
                repository.updateTicket(ticket.copy(status = newStatus, lastUpdated = System.currentTimeMillis()))
            }
        }
    }

    // --- Admin Product CRUD Actions ---
    fun saveMinecraftPlan(plan: MinecraftPlanEntity) {
        viewModelScope.launch {
            repository.saveMinecraftPlan(plan)
            repository.addNotification("Product Saved", "Minecraft plan '${plan.name}' was saved.", "ANNOUNCEMENT")
        }
    }

    fun deleteMinecraftPlan(plan: MinecraftPlanEntity) {
        viewModelScope.launch {
            repository.deleteMinecraftPlan(plan)
        }
    }

    fun saveVpsPlan(plan: VpsPlanEntity) {
        viewModelScope.launch {
            repository.saveVpsPlan(plan)
            repository.addNotification("Product Saved", "VPS plan '${plan.name}' was saved.", "ANNOUNCEMENT")
        }
    }

    fun deleteVpsPlan(plan: VpsPlanEntity) {
        viewModelScope.launch {
            repository.deleteVpsPlan(plan)
        }
    }

    fun saveDomainTld(tld: DomainTldEntity) {
        viewModelScope.launch {
            repository.saveDomainTld(tld)
        }
    }

    fun deleteDomainTld(tld: DomainTldEntity) {
        viewModelScope.launch {
            repository.deleteDomainTld(tld)
        }
    }

    fun saveCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.saveCoupon(coupon)
        }
    }

    fun deleteCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.deleteCoupon(coupon)
        }
    }

    fun updateOrderStatus(orderId: String, status: String, notes: String = "") {
        viewModelScope.launch {
            val order = allOrders.value.find { it.orderId == orderId }
            if (order != null) {
                repository.updateOrder(order.copy(status = status, notes = notes))
                repository.addNotification("Order Updated", "Order $orderId status changed to $status.", "ORDER")
            }
        }
    }

    fun updateUserStatus(userId: Int, status: String) {
        viewModelScope.launch {
            val users = allUsers.value
            val user = users.find { it.id == userId }
            if (user != null) {
                repository.updateUser(user.copy(status = status))
            }
        }
    }

    fun saveWebsiteSetting(key: String, value: String) {
        viewModelScope.launch {
            val oldVal = websiteSettings.value.find { it.keyName == key }?.value ?: ""
            repository.saveWebsiteSetting(key, value)
            repository.addNotification("Website Setting Saved", "Updated setting for '$key'.", "ANNOUNCEMENT")
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "UPDATE_SETTING",
                targetItem = key,
                previousValue = oldVal,
                newValue = value
            )
        }
    }

    fun saveFaq(faq: FaqEntity) {
        viewModelScope.launch {
            repository.saveFaq(faq)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = if (faq.id == 0) "CREATE_FAQ" else "UPDATE_FAQ",
                targetItem = "FAQ #${faq.id}",
                newValue = faq.question
            )
        }
    }

    fun deleteFaq(faq: FaqEntity) {
        viewModelScope.launch {
            repository.deleteFaq(faq)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DELETE_FAQ",
                targetItem = "FAQ #${faq.id}",
                previousValue = faq.question
            )
        }
    }

    fun saveTestimonial(testimonial: TestimonialEntity) {
        viewModelScope.launch {
            repository.saveTestimonial(testimonial)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = if (testimonial.id == 0) "CREATE_TESTIMONIAL" else "UPDATE_TESTIMONIAL",
                targetItem = testimonial.authorName,
                newValue = testimonial.reviewText
            )
        }
    }

    fun deleteTestimonial(testimonial: TestimonialEntity) {
        viewModelScope.launch {
            repository.deleteTestimonial(testimonial)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DELETE_TESTIMONIAL",
                targetItem = testimonial.authorName
            )
        }
    }

    fun saveAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.saveAnnouncement(announcement)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = if (announcement.id == 0) "CREATE_ANNOUNCEMENT" else "UPDATE_ANNOUNCEMENT",
                targetItem = announcement.title,
                newValue = announcement.message
            )
        }
    }

    fun deleteAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DELETE_ANNOUNCEMENT",
                targetItem = announcement.title
            )
        }
    }

    fun saveNavItem(item: NavItemEntity) {
        viewModelScope.launch {
            repository.saveNavItem(item)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = if (item.id == 0) "CREATE_NAV_ITEM" else "UPDATE_NAV_ITEM",
                targetItem = item.label,
                newValue = item.destination
            )
        }
    }

    fun deleteNavItem(item: NavItemEntity) {
        viewModelScope.launch {
            repository.deleteNavItem(item)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DELETE_NAV_ITEM",
                targetItem = item.label
            )
        }
    }

    fun saveFooterLink(link: FooterLinkEntity) {
        viewModelScope.launch {
            repository.saveFooterLink(link)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = if (link.id == 0) "CREATE_FOOTER_LINK" else "UPDATE_FOOTER_LINK",
                targetItem = "${link.columnTitle} -> ${link.label}",
                newValue = link.destination
            )
        }
    }

    fun deleteFooterLink(link: FooterLinkEntity) {
        viewModelScope.launch {
            repository.deleteFooterLink(link)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DELETE_FOOTER_LINK",
                targetItem = "${link.columnTitle} -> ${link.label}"
            )
        }
    }

    fun savePage(page: PageEntity) {
        viewModelScope.launch {
            repository.savePage(page)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = if (page.id == 0) "CREATE_PAGE" else "UPDATE_PAGE",
                targetItem = page.title,
                newValue = "Published: ${page.isPublished}"
            )
        }
    }

    fun deletePage(page: PageEntity) {
        viewModelScope.launch {
            repository.deletePage(page)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DELETE_PAGE",
                targetItem = page.title
            )
        }
    }

    fun duplicateMinecraftPlan(plan: MinecraftPlanEntity) {
        viewModelScope.launch {
            val copyPlan = plan.copy(
                id = 0,
                name = "${plan.name} (Copy)"
            )
            repository.saveMinecraftPlan(copyPlan)
            repository.logAudit(
                adminName = currentUser.value?.name ?: "Admin",
                action = "DUPLICATE_MINECRAFT_PLAN",
                targetItem = plan.name,
                newValue = copyPlan.name
            )
        }
    }

    fun updateUserRole(userId: Int, newRole: String) {
        viewModelScope.launch {
            val user = allUsers.value.find { it.id == userId }
            if (user != null) {
                repository.updateUser(user.copy(role = newRole))
                repository.logAudit(
                    adminName = currentUser.value?.name ?: "Admin",
                    action = "UPDATE_USER_ROLE",
                    targetItem = user.email,
                    previousValue = user.role,
                    newValue = newRole
                )
            }
        }
    }
}

