package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.ProductEntity
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun InspectionRequestDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSubmitRequest: (
        inspectionType: String,
        date: String,
        time: String,
        location: String,
        notes: String,
        quantity: Double
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf("Farm Visit") }
    var preferredDate by remember { mutableStateOf("Tomorrow, 10:00 AM") }
    var preferredTime by remember { mutableStateOf("10:00 AM - 12:00 PM") }
    var meetingLocation by remember { mutableStateOf(product.location) }
    var requestedQty by remember { mutableStateOf("5") }
    var buyerNotes by remember {
        mutableStateOf("Interested in inspecting grain purity, moisture level, and aroma before final purchase.")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
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
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Quality Inspection",
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Request Quality Inspection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Inspect produce before committing payment",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                // Product Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = FarmGreenPrimary.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = product.emojiIcon, fontSize = 32.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Farmer: ${product.farmerName} (${product.qualityGrade})",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Location: ${product.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "₹${product.pricePerUnit.toInt()}/${product.unit}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenPrimary
                        )
                    }
                }

                // Inspection Type Selection
                Text(
                    text = "Inspection Method",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Farm Visit", "Mandi Meet", "Live Video Call").forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type, style = MaterialTheme.typography.bodySmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FarmGreenPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = FarmGreenPrimary
                            )
                        )
                    }
                }

                // Preferred Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = preferredDate,
                        onValueChange = { preferredDate = it },
                        label = { Text("Preferred Date") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = preferredTime,
                        onValueChange = { preferredTime = it },
                        label = { Text("Time Window") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Meeting Location
                OutlinedTextField(
                    value = meetingLocation,
                    onValueChange = { meetingLocation = it },
                    label = { Text("Meeting Location / Farm Address") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Quantity intending to purchase
                OutlinedTextField(
                    value = requestedQty,
                    onValueChange = { requestedQty = it },
                    label = { Text("Quantity Intending to Buy (${product.unit})") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Notes
                OutlinedTextField(
                    value = buyerNotes,
                    onValueChange = { buyerNotes = it },
                    label = { Text("Quality Check Checklist / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                // Quality Assurance Explainer
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💡", fontSize = 20.sp)
                        Text(
                            text = "After you meet and verify product quality, the farmer marks verification complete. Then you can confirm the deal & checkout with complete peace of mind!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        val qty = requestedQty.toDoubleOrNull() ?: 1.0
                        onSubmitRequest(
                            selectedType,
                            preferredDate,
                            preferredTime,
                            meetingLocation,
                            buyerNotes,
                            qty
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Quality Inspection Request")
                }
            }
        }
    }
}
