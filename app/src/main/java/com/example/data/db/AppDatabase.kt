package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        MinecraftPlanEntity::class,
        VpsPlanEntity::class,
        DomainTldEntity::class,
        CouponEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        ServiceEntity::class,
        SupportTicketEntity::class,
        TicketMessageEntity::class,
        WebsiteSettingEntity::class,
        NotificationEntity::class,
        EmailLogEntity::class,
        EmailTemplateEntity::class,
        FaqEntity::class,
        TestimonialEntity::class,
        AnnouncementEntity::class,
        NavItemEntity::class,
        FooterLinkEntity::class,
        PageEntity::class,
        AuditLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hostingDao(): HostingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fizzycloud_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
