package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.ProductEntity
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentGatewayDialog(
    product: ProductEntity,
    verifiedInspection: InspectionRequestEntity? = null,
    onDismiss: () -> Unit,
    onPaymentSuccess: (quantity: Double, unitPrice: Double, address: String, paymentMethod: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var quantity by remember {
        mutableStateOf(
            if (verifiedInspection != null) verifiedInspection.requestedQuantity.toString() else "1"
        )
    }
    val unitPrice = verifiedInspection?.finalAgreedPrice ?: product.pricePerUnit
    var address by remember { mutableStateOf("Flat 402, Green Meadows, Model Colony, Pune - 411016") }
    var selectedMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe / Paytm)") }
    var upiId by remember { mutableStateOf("priya@okhdfcbank") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentComplete by remember { mutableStateOf(false) }

    val qtyNum = quantity.toDoubleOrNull() ?: 1.0
    val totalAmount = qtyNum * unitPrice

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FarmGreenPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Secure Escrow",
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "FarmPay Escrow Checkout",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "100% Buyer & Farmer Protected",
                                style = MaterialTheme.typography.bodySmall,
                                color = FarmGreenPrimary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, enabled = !isProcessing) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                if (paymentComplete) {
                    // Success View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Payment Successful",
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Payment Secured in Escrow!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenPrimary
                        )
                        Text(
                            text = "₹${String.format("%.2f", totalAmount)} safely held. Farmer ${product.farmerName} has been notified to dispatch.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    // Product Summary Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = product.emojiIcon, fontSize = 36.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "From: ${product.farmerName} • ${product.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                if (verifiedInspection != null) {
                                    Surface(
                                        color = FarmGreenPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = "✅ Quality Verified (${verifiedInspection.qualityGradeGiven})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FarmGreenPrimary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "₹${unitPrice.toInt()}/${product.unit}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarmGreenPrimary
                            )
                        }
                    }

                    // Quantity Input
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity to Purchase (${product.unit})") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = {
                            Text("Available in farm stock: ${product.quantityAvailable} ${product.unit}")
                        }
                    )

                    // Delivery Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Delivery Address") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )

                    // Payment Method selector
                    Text(
                        text = "Select Secure Payment Method",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    val paymentMethods = listOf(
                        "UPI (Google Pay / PhonePe / Paytm)",
                        "RuPay / Visa / Mastercard Debit/Credit",
                        "Net Banking (Instant Escrow Clearance)"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        paymentMethods.forEach { method ->
                            val isSelected = selectedMethod == method
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedMethod = method },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FarmGreenPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) FarmGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isSelected) FarmGreenPrimary else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = method,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    if (selectedMethod.startsWith("UPI")) {
                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { upiId = it },
                            label = { Text("Virtual Payment Address (VPA / UPI ID)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Escrow Protection Notice Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HarvestGold.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🛡️", fontSize = 18.sp)
                            Text(
                                text = "Escrow Guarantee: Your funds remain securely in third-party escrow until you receive and confirm product quality upon delivery.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Price Breakdown
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Produce Subtotal ($qtyNum ${product.unit} × ₹${unitPrice.toInt()}):", style = MaterialTheme.typography.bodySmall)
                                Text("₹${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Escrow Protection & Mandi Cess:", style = MaterialTheme.typography.bodySmall)
                                Text("₹0 (Waived)", color = FarmGreenPrimary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Escrow Payable:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    "₹${String.format("%.2f", totalAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FarmGreenPrimary
                                )
                            }
                        }
                    }

                    // Pay Button
                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                delay(1200) // Realistic secure gateway simulation
                                isProcessing = false
                                paymentComplete = true
                                delay(800)
                                onPaymentSuccess(
                                    qtyNum,
                                    unitPrice,
                                    address,
                                    selectedMethod
                                )
                            }
                        },
                        enabled = !isProcessing && qtyNum > 0 && address.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Securing Funds in Escrow...")
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay Securely ₹${String.format("%.2f", totalAmount)}")
                        }
                    }
                }
            }
        }
    }
}
