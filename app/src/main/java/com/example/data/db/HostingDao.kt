package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HostingDao {

    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Minecraft Plans ---
    @Query("SELECT * FROM minecraft_plans ORDER BY displayOrder ASC")
    fun getAllMinecraftPlans(): Flow<List<MinecraftPlanEntity>>

    @Query("SELECT * FROM minecraft_plans WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveMinecraftPlans(): Flow<List<MinecraftPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMinecraftPlan(plan: MinecraftPlanEntity): Long

    @Update
    suspend fun updateMinecraftPlan(plan: MinecraftPlanEntity)

    @Delete
    suspend fun deleteMinecraftPlan(plan: MinecraftPlanEntity)

    // --- VPS Plans ---
    @Query("SELECT * FROM vps_plans ORDER BY displayOrder ASC")
    fun getAllVpsPlans(): Flow<List<VpsPlanEntity>>

    @Query("SELECT * FROM vps_plans WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveVpsPlans(): Flow<List<VpsPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVpsPlan(plan: VpsPlanEntity): Long

    @Update
    suspend fun updateVpsPlan(plan: VpsPlanEntity)

    @Delete
    suspend fun deleteVpsPlan(plan: VpsPlanEntity)

    // --- Domain TLDs ---
    @Query("SELECT * FROM domain_tlds")
    fun getAllDomainTlds(): Flow<List<DomainTldEntity>>

    @Query("SELECT * FROM domain_tlds WHERE isActive = 1")
    fun getActiveDomainTlds(): Flow<List<DomainTldEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomainTld(tld: DomainTldEntity): Long

    @Update
    suspend fun updateDomainTld(tld: DomainTldEntity)

    @Delete
    suspend fun deleteDomainTld(tld: DomainTldEntity)

    // --- Coupons ---
    @Query("SELECT * FROM coupons")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity): Long

    @Update
    suspend fun updateCoupon(coupon: CouponEntity)

    @Delete
    suspend fun deleteCoupon(coupon: CouponEntity)

    // --- Cart ---
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersForUser(userId: Int): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // --- Services ---
    @Query("SELECT * FROM services WHERE userId = :userId")
    fun getServicesForUser(userId: Int): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Update
    suspend fun updateService(service: ServiceEntity)

    // --- Support Tickets ---
    @Query("SELECT * FROM support_tickets ORDER BY lastUpdated DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun getTicketsForUser(userId: Int): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)

    @Update
    suspend fun updateTicket(ticket: SupportTicketEntity)

    // --- Ticket Messages ---
    @Query("SELECT * FROM ticket_messages WHERE ticketId = :ticketId ORDER BY timestamp ASC")
    fun getTicketMessages(ticketId: String): Flow<List<TicketMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketMessage(message: TicketMessageEntity)

    // --- Website Settings ---
    @Query("SELECT * FROM website_settings")
    fun getAllWebsiteSettings(): Flow<List<WebsiteSettingEntity>>

    @Query("SELECT value FROM website_settings WHERE keyName = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: WebsiteSettingEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    // --- Email Logs ---
    @Query("SELECT * FROM email_logs ORDER BY timestamp DESC")
    fun getAllEmailLogs(): Flow<List<EmailLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmailLog(log: EmailLogEntity): Long

    @Query("DELETE FROM email_logs")
    suspend fun clearEmailLogs()

    // --- Email Templates ---
    @Query("SELECT * FROM email_templates")
    fun getAllEmailTemplates(): Flow<List<EmailTemplateEntity>>

    @Query("SELECT * FROM email_templates WHERE templateType = :type LIMIT 1")
    suspend fun getEmailTemplate(type: String): EmailTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmailTemplate(template: EmailTemplateEntity): Long

    // --- FAQs ---
    @Query("SELECT * FROM faqs ORDER BY displayOrder ASC")
    fun getAllFaqs(): Flow<List<FaqEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaq(faq: FaqEntity): Long

    @Update
    suspend fun updateFaq(faq: FaqEntity)

    @Delete
    suspend fun deleteFaq(faq: FaqEntity)

    // --- Testimonials ---
    @Query("SELECT * FROM testimonials ORDER BY displayOrder ASC")
    fun getAllTestimonials(): Flow<List<TestimonialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestimonial(testimonial: TestimonialEntity): Long

    @Update
    suspend fun updateTestimonial(testimonial: TestimonialEntity)

    @Delete
    suspend fun deleteTestimonial(testimonial: TestimonialEntity)

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Update
    suspend fun updateAnnouncement(announcement: AnnouncementEntity)

    @Delete
    suspend fun deleteAnnouncement(announcement: AnnouncementEntity)

    // --- Nav Items ---
    @Query("SELECT * FROM nav_items ORDER BY displayOrder ASC")
    fun getAllNavItems(): Flow<List<NavItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNavItem(navItem: NavItemEntity): Long

    @Update
    suspend fun updateNavItem(navItem: NavItemEntity)

    @Delete
    suspend fun deleteNavItem(navItem: NavItemEntity)

    // --- Footer Links ---
    @Query("SELECT * FROM footer_links ORDER BY displayOrder ASC")
    fun getAllFooterLinks(): Flow<List<FooterLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFooterLink(link: FooterLinkEntity): Long

    @Update
    suspend fun updateFooterLink(link: FooterLinkEntity)

    @Delete
    suspend fun deleteFooterLink(link: FooterLinkEntity)

    // --- Pages ---
    @Query("SELECT * FROM pages ORDER BY lastModified DESC")
    fun getAllPages(): Flow<List<PageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity): Long

    @Update
    suspend fun updatePage(page: PageEntity)

    @Delete
    suspend fun deletePage(page: PageEntity)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long
}


