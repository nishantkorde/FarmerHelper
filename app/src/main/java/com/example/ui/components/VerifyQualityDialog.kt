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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.entity.InspectionRequestEntity
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun VerifyQualityDialog(
    inspection: InspectionRequestEntity,
    onDismiss: () -> Unit,
    onConfirmVerification: (qualityGrade: String, notes: String, agreedPrice: Double) -> Unit
) {
    var qualityGrade by remember { mutableStateOf("Grade A+ Certified Verified") }
    var notes by remember {
        mutableStateOf("Moisture tested at 11.4%, 99.8% purity, zero pest damage. Sample inspected and certified with buyer.")
    }
    var agreedPrice by remember { mutableStateOf(inspection.originalPricePerUnit.toString()) }

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
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verify Quality",
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Verify Quality & Finalize Deal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Confirm inspection results with ${inspection.buyerName}",
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

                // Inspection Summary
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = FarmGreenPrimary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${inspection.productEmoji} ${inspection.productTitle}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Buyer: ${inspection.buyerName} (${inspection.buyerPhone})",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Inspection: ${inspection.inspectionType} • ${inspection.meetingLocation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Buyer Requested Qty: ${inspection.requestedQuantity} ${inspection.unit} @ ₹${inspection.originalPricePerUnit.toInt()}/${inspection.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Certified Grade Input
                OutlinedTextField(
                    value = qualityGrade,
                    onValueChange = { qualityGrade = it },
                    label = { Text("Quality Grade Verified") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Lab / Field Quality Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Quality Verification Test & Moisture Report") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4
                )

                // Agreed Price per Unit
                OutlinedTextField(
                    value = agreedPrice,
                    onValueChange = { agreedPrice = it },
                    label = { Text("Final Agreed Price per ${inspection.unit} (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Guidance Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🤝", fontSize = 20.sp)
                        Text(
                            text = "Confirming this will notify ${inspection.buyerName} that quality verification has passed and enable direct Escrow checkout for this produce.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Button(
                    onClick = {
                        val price = agreedPrice.toDoubleOrNull() ?: inspection.originalPricePerUnit
                        onConfirmVerification(qualityGrade, notes, price)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Quality & Deal Finalization")
                }
            }
        }
    }
}
