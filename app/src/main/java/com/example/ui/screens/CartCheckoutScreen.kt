package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CartItemEntity
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.EmeraldPrimary

@Composable
fun CartCheckoutScreen(
    viewModel: MainViewModel,
    onNavigateHome: () -> Unit,
    onNavigateDashboard: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponMessage by viewModel.couponMessage.collectAsState()

    var couponCodeInput by remember { mutableStateOf("") }
    var customerName by remember(currentUser) { mutableStateOf(currentUser?.name ?: "Alex Gamer") }
    var customerEmail by remember(currentUser) { mutableStateOf(currentUser?.email ?: "alex@gamer.com") }
    var customerPhone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "+91 9876543210") }
    var selectedPaymentMethod by remember { mutableStateOf("Razorpay UPI") }

    var showOrderSuccessDialog by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    val discount = if (appliedCoupon != null) {
        if (appliedCoupon!!.discountType == "PERCENTAGE") (subtotal * appliedCoupon!!.value / 100.0) else appliedCoupon!!.value
    } else 0.0
    val tax = (subtotal - discount) * 0.18
    val finalTotal = (subtotal - discount + tax).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cart_checkout_screen_column"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Shopping Cart & Checkout",
                subtitle = "Review your selected hosting plans and complete your order."
            )
        }

        if (cartItems.isEmpty()) {
            item {
                GlowCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Your cart is currently empty",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add a Minecraft server, domain, or VPS plan to get started.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryGamingButton(
                            text = "Browse Minecraft Plans",
                            onClick = onNavigateHome,
                            icon = Icons.Default.Dns
                        )
                    }
                }
            }
        } else {
            // Cart Items List
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onUpdateQty = { qty -> viewModel.updateCartQuantity(item, qty) },
                    onRemove = { viewModel.removeCartItem(item) }
                )
            }

            // Coupon Code Section
            item {
                GlowCard {
                    Column {
                        Text("Have a Coupon Code?", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponCodeInput,
                                onValueChange = { couponCodeInput = it },
                                placeholder = { Text("e.g. CRAFT10") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.applyCoupon(couponCodeInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                            ) {
                                Text("Apply", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (couponMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = couponMessage!!,
                                fontSize = 12.sp,
                                color = if (appliedCoupon != null) EmeraldPrimary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (appliedCoupon != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(onClick = { viewModel.removeCoupon() }) {
                                Text("Remove Applied Coupon (${appliedCoupon!!.code})", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Price Summary Card
            item {
                GlowCard(borderColor = EmeraldPrimary) {
                    Column {
                        Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        PriceRow(label = "Subtotal", amount = "₹${subtotal.toInt()}")
                        if (discount > 0) {
                            PriceRow(label = "Discount (${appliedCoupon?.code})", amount = "-₹${discount.toInt()}", color = EmeraldPrimary)
                        }
                        PriceRow(label = "Taxes (18% GST)", amount = "₹${tax.toInt()}")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount:", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("₹${finalTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 24.sp, color = EmeraldPrimary)
                        }
                    }
                }
            }

            // Checkout Form & Payment Selection
            item {
                GlowCard {
                    Column {
                        Text("Customer & Billing Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Select Payment Gateway:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        val paymentMethods = listOf("Razorpay UPI", "Credit/Debit Card", "Stripe Checkout", "Crypto (USDT)")
                        paymentMethods.forEach { method ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == method,
                                    onClick = { selectedPaymentMethod = method }
                                )
                                Text(text = method, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        PrimaryGamingButton(
                            text = "Pay ₹${finalTotal.toInt()} & Complete Order",
                            onClick = {
                                viewModel.processCheckout(
                                    customerName = customerName,
                                    customerEmail = customerEmail,
                                    paymentMethod = selectedPaymentMethod,
                                    onSuccess = { showOrderSuccessDialog = true }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Lock
                        )
                    }
                }
            }
        }
    }

    // Success Order Dialog
    if (showOrderSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment Successful!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Thank you for your order! Your Minecraft/VPS services have been provisioned instantly.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Check your customer dashboard to view your live IP addresses and server control panel.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOrderSuccessDialog = false
                        onNavigateDashboard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)
                ) {
                    Text("Go to My Services", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
    onUpdateQty: (Int) -> Unit,
    onRemove: () -> Unit
) {
    GlowCard(borderColor = MaterialTheme.colorScheme.outline) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.configSummary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${item.unitPrice.toInt()} (${item.billingCycle.lowercase()})",
                    fontWeight = FontWeight.Black,
                    color = EmeraldPrimary,
                    fontSize = 14.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onUpdateQty(item.quantity - 1) }) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = "${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                IconButton(onClick = { onUpdateQty(item.quantity + 1) }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = EmeraldPrimary)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, amount: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(text = amount, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
