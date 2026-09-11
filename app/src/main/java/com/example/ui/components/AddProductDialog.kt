package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.theme.FarmGreenPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddProductDialog(
    farmerLocation: String,
    onDismiss: () -> Unit,
    onAddProduct: (
        title: String,
        category: String,
        description: String,
        quantity: Double,
        unit: String,
        pricePerUnit: Double,
        location: String,
        qualityGrade: String,
        harvestDate: String,
        isOrganic: Boolean,
        emoji: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val categories = listOf("Grains", "Fruits", "Vegetables", "Dairy", "Pulses", "Spices")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("10") }
    val units = listOf("quintal", "kg", "crate (24 pcs)", "liter")
    var selectedUnit by remember { mutableStateOf(units[0]) }
    var pricePerUnit by remember { mutableStateOf("2500") }
    var location by remember { mutableStateOf(farmerLocation) }
    val grades = listOf("Grade A+ Organic", "Grade A Export", "Standard Farm Fresh", "Grade A+ Lab Tested")
    var selectedGrade by remember { mutableStateOf(grades[0]) }
    var harvestDate by remember { mutableStateOf("08 Sep 2026") }
    var isOrganic by remember { mutableStateOf(true) }

    val emojiOptions = listOf("🌾", "🥭", "🍚", "🧅", "🥛", "🌽", "🍅", "🥔", "🫘", "🌶️")
    var selectedEmoji by remember { mutableStateOf(emojiOptions[0]) }

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "List New Farm Produce",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Add crop details for buyer inspection & purchase",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                // Emoji Picker
                Text(
                    text = "Select Produce Icon",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojiOptions.take(6).forEach { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) FarmGreenPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) FarmGreenPrimary else Color.LightGray,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                        }
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Produce Title (e.g. Organic Sharbati Wheat)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FarmGreenPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = FarmGreenPrimary
                            )
                        )
                    }
                }

                // Quantity & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity Available") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pricePerUnit,
                        onValueChange = { pricePerUnit = it },
                        label = { Text("Price per Unit (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Unit selection
                Text(
                    text = "Unit of Measure",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    units.forEach { u ->
                        FilterChip(
                            selected = selectedUnit == u,
                            onClick = { selectedUnit = u },
                            label = { Text(u, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Quality Grade
                Text(
                    text = "Quality Grade",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    grades.forEach { grade ->
                        FilterChip(
                            selected = selectedGrade == grade,
                            onClick = { selectedGrade = grade },
                            label = { Text(grade, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }

                // Harvest Date & Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = harvestDate,
                        onValueChange = { harvestDate = it },
                        label = { Text("Harvest Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Farm / Mandi Location") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Harvest Specifications") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                // Organic Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Organic Certified Produce",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Zero synthetic pesticides, certified natural",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                    Switch(
                        checked = isOrganic,
                        onCheckedChange = { isOrganic = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = FarmGreenPrimary)
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        val qty = quantity.toDoubleOrNull() ?: 10.0
                        val price = pricePerUnit.toDoubleOrNull() ?: 2000.0
                        val finalDesc = description.ifBlank {
                            "Direct from farm harvest. Premium quality batch open for buyer quality inspection."
                        }
                        onAddProduct(
                            title,
                            selectedCategory,
                            finalDesc,
                            qty,
                            selectedUnit,
                            price,
                            location,
                            selectedGrade,
                            harvestDate,
                            isOrganic,
                            selectedEmoji
                        )
                    },
                    enabled = title.isNotBlank() && quantity.isNotBlank() && pricePerUnit.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Produce to Marketplace")
                }
            }
        }
    }
}
