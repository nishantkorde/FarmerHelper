package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.entity.OrderEntity
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.GoldStar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RatingDialog(
    order: OrderEntity,
    currentRole: String,
    onDismiss: () -> Unit,
    onSubmit: (stars: Int, tags: String, feedback: String) -> Unit
) {
    var stars by remember { mutableIntStateOf(5) }
    val availableTags = if (currentRole == "BUYER") {
        listOf(
            "Super Fresh 🌿",
            "Accurate Weight ⚖️",
            "Pesticide Free 🛡️",
            "Great Packaging 📦",
            "Fair Price 💰",
            "Fast Dispatch 🚚"
        )
    } else {
        listOf(
            "Prompt Payment 💳",
            "Clear Communication 💬",
            "Respectful Buyer 🤝",
            "Smooth Handover 🚛"
        )
    }

    var selectedTags by remember { mutableStateOf(setOf(availableTags[0], availableTags[1])) }
    var feedback by remember {
        mutableStateOf(
            if (currentRole == "BUYER") {
                "Excellent produce quality! The inspection matched the delivered goods perfectly. Highly trustworthy farmer."
            } else {
                "Smooth transaction and prompt escrow clearance. Delighted to do business!"
            }
        )
    }

    val targetName = if (currentRole == "BUYER") order.farmerName else order.buyerName
    val ratingLabel = when (stars) {
        5 -> "Exceptional (5/5) 🌟"
        4 -> "Very Good (4/5) 👍"
        3 -> "Average (3/5)"
        2 -> "Poor (2/5)"
        else -> "Unsatisfactory (1/5)"
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
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rate Completed Transaction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Order #${order.orderNumber} • ${order.productTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                Text(
                    text = "How was your experience with $targetName?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Interactive Star Bar
                InteractiveStarRating(
                    rating = stars,
                    onRatingChanged = { stars = it }
                )

                Text(
                    text = ratingLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldStar
                )

                // Tags flow
                Text(
                    text = "Select Experience Highlights",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                            },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FarmGreenPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = FarmGreenPrimary
                            )
                        )
                    }
                }

                // Written Feedback
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Written Review (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                // Community Trust Impact
                Surface(
                    color = FarmGreenPrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🤝", fontSize = 18.sp)
                        Text(
                            text = "Automated ratings build verified trust in the farm-to-consumer ecosystem.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FarmGreenPrimary
                        )
                    }
                }

                Button(
                    onClick = {
                        val tagsString = selectedTags.joinToString(", ")
                        onSubmit(stars, tagsString, feedback)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Community Rating")
                }
            }
        }
    }
}
