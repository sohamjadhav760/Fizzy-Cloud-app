package com.example.data.email

import com.example.data.db.EmailTemplateEntity

object EmailTemplateDefaults {

    val ALL_TEMPLATES: List<EmailTemplateEntity> = listOf(
        // 1. User Registration Confirmation
        EmailTemplateEntity(
            templateType = EmailTemplateType.USER_REGISTRATION.name,
            name = "User Registration Confirmation",
            description = "Sent to new customers immediately upon account creation or registration confirmation.",
            subjectTemplate = "Welcome to Fizzy Cloud Hosting, {{user_name}}! Confirm your account",
            placeholders = "{{user_name}}, {{user_email}}, {{user_role}}, {{confirmation_link}}, {{current_year}}",
            textTemplate = """
                Welcome to Fizzy Cloud Hosting, {{user_name}}!
                
                Thank you for creating an account with us.
                Email: {{user_email}}
                Role: {{user_role}}
                
                Please confirm your email address by opening this link:
                {{confirmation_link}}
                
                Need help? Contact our 24/7 support team via our app portal.
                
                Best regards,
                Fizzy Cloud Hosting Team
            """.trimIndent(),
            htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background-color: #1e293b; border: 1px solid #334155; border-radius: 12px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #059669 0%, #0d9488 100%); padding: 30px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 26px; font-weight: 800; letter-spacing: -0.5px; }
                    .body { padding: 30px; line-height: 1.6; font-size: 15px; color: #cbd5e1; }
                    .button-container { text-align: center; margin: 30px 0; }
                    .button { background-color: #10b981; color: #000000; padding: 14px 28px; font-weight: 700; border-radius: 8px; text-decoration: none; display: inline-block; }
                    .info-box { background-color: #0f172a; border-left: 4px solid #10b981; padding: 16px; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #64748b; border-top: 1px solid #334155; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>Fizzy Cloud Hosting</h1>
                      <p style="margin: 5px 0 0; opacity: 0.9;">High-Performance Minecraft & VPS Servers</p>
                    </div>
                    <div class="body">
                      <h2 style="color: #f8fafc; margin-top: 0;">Welcome aboard, {{user_name}}!</h2>
                      <p>Your account has been created successfully on the Fizzy Cloud Hosting platform.</p>
                      
                      <div class="info-box">
                        <strong>Account Details:</strong><br>
                        Email: <span style="color: #34d399;">{{user_email}}</span><br>
                        Account Type: <span style="color: #34d399;">{{user_role}}</span>
                      </div>

                      <p>Please click the button below to verify your email address and activate full dashboard privileges:</p>

                      <div class="button-container">
                        <a href="{{confirmation_link}}" class="button">Confirm Email Account</a>
                      </div>

                      <p style="font-size: 13px; color: #94a3b8;">If the button above doesn't work, copy and paste this URL into your browser:<br>
                      <a href="{{confirmation_link}}" style="color: #34d399;">{{confirmation_link}}</a></p>
                    </div>
                    <div class="footer">
                      © {{current_year}} Fizzy Cloud Hosting Inc. All rights reserved. | 24/7 Support Desk
                    </div>
                  </div>
                </body>
                </html>
            """.trimIndent()
        ),

        // 2. Password Reset
        EmailTemplateEntity(
            templateType = EmailTemplateType.PASSWORD_RESET.name,
            name = "Password Reset Request",
            description = "Sent when a user requests a password reset token or magic link.",
            subjectTemplate = "Reset Your Password - Fizzy Cloud Hosting Security",
            placeholders = "{{user_name}}, {{user_email}}, {{reset_link}}, {{reset_token}}, {{ip_address}}, {{current_year}}",
            textTemplate = """
                Hello {{user_name}},
                
                We received a request to reset your password for {{user_email}}.
                
                Reset Security Code: {{reset_token}}
                Reset Link: {{reset_link}}
                Request IP Address: {{ip_address}}
                
                This code expires in 60 minutes. If you did not request a password reset, please ignore this email or contact support immediately.
                
                Fizzy Cloud Security Team
            """.trimIndent(),
            htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background-color: #1e293b; border: 1px solid #334155; border-radius: 12px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #d97706 0%, #b45309 100%); padding: 26px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 24px; font-weight: 800; }
                    .body { padding: 30px; line-height: 1.6; font-size: 15px; color: #cbd5e1; }
                    .token-box { background-color: #0f172a; border: 2px dashed #f59e0b; padding: 18px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 4px; color: #fbbf24; border-radius: 8px; margin: 20px 0; }
                    .button-container { text-align: center; margin: 24px 0; }
                    .button { background-color: #f59e0b; color: #000000; padding: 14px 28px; font-weight: 700; border-radius: 8px; text-decoration: none; display: inline-block; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #64748b; border-top: 1px solid #334155; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>🔒 Password Reset Request</h1>
                    </div>
                    <div class="body">
                      <p>Hello <strong>{{user_name}}</strong>,</p>
                      <p>We received a request to reset the password associated with account <strong>{{user_email}}</strong>.</p>
                      
                      <p>Your one-time verification code is:</p>
                      <div class="token-box">{{reset_token}}</div>

                      <div class="button-container">
                        <a href="{{reset_link}}" class="button">Reset Password Now</a>
                      </div>

                      <div style="background-color: #0f172a; padding: 12px 16px; border-radius: 6px; font-size: 13px; color: #94a3b8; margin-top: 20px;">
                        <strong>Security Information:</strong><br>
                        • IP Address: {{ip_address}}<br>
                        • Expiration: 60 minutes<br>
                        • If you didn't request this change, no action is needed and your account remains safe.
                      </div>
                    </div>
                    <div class="footer">
                      © {{current_year}} Fizzy Cloud Hosting | Automated Security Engine
                    </div>
                  </div>
                </body>
                </html>
            """.trimIndent()
        ),

        // 3. New Order Notification - Customer
        EmailTemplateEntity(
            templateType = EmailTemplateType.NEW_ORDER_CUSTOMER.name,
            name = "New Order Confirmation (Customer)",
            description = "Sent to customer right after successful payment / checkout completion.",
            subjectTemplate = "Order Confirmed - {{order_id}} | Fizzy Cloud Hosting",
            placeholders = "{{user_name}}, {{order_id}}, {{items_summary}}, {{subtotal}}, {{discount}}, {{tax}}, {{total_amount}}, {{payment_method}}, {{current_year}}",
            textTemplate = """
                Thank you for your order, {{user_name}}!
                
                Order ID: {{order_id}}
                Items Purchased: {{items_summary}}
                Payment Method: {{payment_method}}
                
                Subtotal: {{subtotal}}
                Discount: {{discount}}
                Tax: {{tax}}
                Total Amount Paid: {{total_amount}}
                
                Your service is automatically being provisioned. You can manage your servers in the Fizzy Cloud app dashboard.
                
                Fizzy Cloud Hosting Billing Team
            """.trimIndent(),
            htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background-color: #1e293b; border: 1px solid #334155; border-radius: 12px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #10b981 0%, #059669 100%); padding: 28px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 24px; font-weight: 800; }
                    .body { padding: 30px; line-height: 1.6; font-size: 15px; color: #cbd5e1; }
                    .receipt-table { width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 14px; }
                    .receipt-table th { background-color: #0f172a; color: #94a3b8; text-align: left; padding: 10px 14px; border-bottom: 2px solid #334155; }
                    .receipt-table td { padding: 12px 14px; border-bottom: 1px solid #334155; color: #e2e8f0; }
                    .total-row { font-weight: bold; font-size: 16px; color: #34d399; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #64748b; border-top: 1px solid #334155; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>🎉 Order Receipt & Confirmation</h1>
                      <p style="margin: 4px 0 0; opacity: 0.9;">Order #{{order_id}}</p>
                    </div>
                    <div class="body">
                      <p>Hi <strong>{{user_name}}</strong>,</p>
                      <p>We've received your order and payment! Here is your invoice receipt summary:</p>

                      <table class="receipt-table">
                        <thead>
                          <tr>
                            <th>Description</th>
                            <th style="text-align: right;">Details</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr>
                            <td>Items Purchased</td>
                            <td style="text-align: right; color: #6ee7b7;">{{items_summary}}</td>
                          </tr>
                          <tr>
                            <td>Payment Gateway</td>
                            <td style="text-align: right;">{{payment_method}}</td>
                          </tr>
                          <tr>
                            <td>Subtotal</td>
                            <td style="text-align: right;">{{subtotal}}</td>
                          </tr>
                          <tr>
                            <td>Discount Savings</td>
                            <td style="text-align: right; color: #f43f5e;">- {{discount}}</td>
                          </tr>
                          <tr>
                            <td>Taxes & Fees</td>
                            <td style="text-align: right;">{{tax}}</td>
                          </tr>
                          <tr class="total-row">
                            <td>Total Paid</td>
                            <td style="text-align: right;">{{total_amount}}</td>
                          </tr>
                        </tbody>
                      </table>

                      <p>🚀 Your server or domain service has been automatically provisioned and added to your active services list in the dashboard.</p>
                    </div>
                    <div class="footer">
                      © {{current_year}} Fizzy Cloud Hosting Inc. | Billing & Invoicing Department
                    </div>
                  </div>
                </body>
                </html>
            """.trimIndent()
        ),

        // 4. New Order Notification - Admin
        EmailTemplateEntity(
            templateType = EmailTemplateType.NEW_ORDER_ADMIN.name,
            name = "New Order Notification (Admin Alert)",
            description = "Alert email dispatched to the admin team whenever a new order is completed.",
            subjectTemplate = "🚨 [ADMIN ALERT] New Order {{order_id}} ({{total_amount}})",
            placeholders = "{{user_name}}, {{user_email}}, {{order_id}}, {{items_summary}}, {{total_amount}}, {{payment_method}}, {{current_year}}",
            textTemplate = """
                [ADMIN ALERT] New Order Received!
                
                Order ID: {{order_id}}
                Customer Name: {{user_name}}
                Customer Email: {{user_email}}
                Items: {{items_summary}}
                Total Revenue: {{total_amount}}
                Payment Gateway: {{payment_method}}
                
                Please log in to the Admin Panel to review service allocation and provisioning logs.
            """.trimIndent(),
            htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background-color: #1e293b; border: 1px solid #0284c7; border-radius: 12px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #0284c7 0%, #0369a1 100%); padding: 24px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 22px; font-weight: 800; }
                    .body { padding: 30px; line-height: 1.6; font-size: 15px; color: #cbd5e1; }
                    .alert-box { background-color: #0c4a6e; border-left: 4px solid #38bdf8; padding: 16px; border-radius: 6px; margin: 16px 0; }
                    .footer { text-align: center; padding: 18px; font-size: 12px; color: #64748b; border-top: 1px solid #334155; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>🚨 New Order Alert - Admin Panel</h1>
                    </div>
                    <div class="body">
                      <p>A new purchase has been processed on Fizzy Cloud Hosting.</p>
                      
                      <div class="alert-box">
                        <strong>Order Overview:</strong><br>
                        • Order ID: <span style="color: #38bdf8; font-weight: bold;">{{order_id}}</span><br>
                        • Customer: {{user_name}} ({{user_email}})<br>
                        • Purchased Items: {{items_summary}}<br>
                        • Total Revenue: <span style="color: #4ade80; font-weight: bold;">{{total_amount}}</span><br>
                        • Payment Method: {{payment_method}}
                      </div>

                      <p>View complete transaction details and node resource usage in your Admin Control Panel.</p>
                    </div>
                    <div class="footer">
                      Fizzy Cloud Internal Operations & Billing Dispatcher
                    </div>
                  </div>
                </body>
                </html>
            """.trimIndent()
        ),

        // 5. Service Activation
        EmailTemplateEntity(
            templateType = EmailTemplateType.SERVICE_ACTIVATION.name,
            name = "Service Activation Notification",
            description = "Sent when a Minecraft server, VPS, or domain service is activated and set to RUNNING status.",
            subjectTemplate = "🚀 Your Service {{service_name}} is Active & Ready!",
            placeholders = "{{user_name}}, {{service_name}}, {{service_type}}, {{ip_address}}, {{config_details}}, {{control_panel_url}}, {{current_year}}",
            textTemplate = """
                Hello {{user_name}},
                
                Great news! Your service '{{service_name}}' ({{service_type}}) is now fully provisioned and RUNNING!
                
                Connection Address: {{ip_address}}
                Configuration: {{config_details}}
                Control Panel URL: {{control_panel_url}}
                
                You can manage CPU, RAM, backups, and server console inside the app dashboard or Pterodactyl panel.
                
                Happy hosting!
                Fizzy Cloud Technical Support
            """.trimIndent(),
            htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background-color: #1e293b; border: 1px solid #334155; border-radius: 12px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%); padding: 28px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 24px; font-weight: 800; }
                    .body { padding: 30px; line-height: 1.6; font-size: 15px; color: #cbd5e1; }
                    .credentials-box { background-color: #0f172a; border: 1px solid #a78bfa; padding: 18px; border-radius: 8px; margin: 20px 0; }
                    .ip-badge { font-family: 'Courier New', monospace; background-color: #2e1065; color: #c084fc; padding: 6px 12px; border-radius: 4px; font-size: 16px; font-weight: bold; }
                    .button-container { text-align: center; margin: 24px 0; }
                    .button { background-color: #a78bfa; color: #000000; padding: 14px 28px; font-weight: 700; border-radius: 8px; text-decoration: none; display: inline-block; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #64748b; border-top: 1px solid #334155; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>🚀 Service Activation Complete</h1>
                      <p style="margin: 4px 0 0; opacity: 0.9;">{{service_name}} is ONLINE</p>
                    </div>
                    <div class="body">
                      <p>Hi <strong>{{user_name}}</strong>,</p>
                      <p>Your new server service is online and ready for connections.</p>

                      <div class="credentials-box">
                        <strong style="color: #ddd6fe; font-size: 16px;">Service Details:</strong><br><br>
                        • Service Name: <strong>{{service_name}}</strong><br>
                        • Service Type: {{service_type}}<br>
                        • Direct IP Address: <span class="ip-badge">{{ip_address}}</span><br>
                        • Resources: {{config_details}}
                      </div>

                      <div class="button-container">
                        <a href="{{control_panel_url}}" class="button">Access Pterodactyl Console</a>
                      </div>

                      <p style="font-size: 13px; color: #94a3b8;">Need assistance setting up plugins, modpacks, or DNS records? Open a support ticket anytime from our app portal.</p>
                    </div>
                    <div class="footer">
                      © {{current_year}} Fizzy Cloud Hosting Inc. | Infrastructure Provisioning Unit
                    </div>
                  </div>
                </body>
                </html>
            """.trimIndent()
        )
    )

    fun replacePlaceholders(templateStr: String, variables: Map<String, String>): String {
        var result = templateStr
        val defaultVars = mapOf(
            "current_year" to "2026",
            "control_panel_url" to "https://panel.fizzycloud.host",
            "confirmation_link" to "https://fizzycloud.host/activate?token=cnf_${(100000..999999).random()}",
            "reset_link" to "https://fizzycloud.host/reset-password?token=rst_${(100000..999999).random()}",
            "reset_token" to "${(100000..999999).random()}",
            "ip_address" to "192.168.1.${(10..250).random()}"
        )
        val allVars = defaultVars + variables

        for ((key, value) in allVars) {
            result = result.replace("{{$key}}", value)
            result = result.replace("{{\$$key}}", value)
        }
        return result
    }
}
