package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusInTransit
import com.example.ui.theme.StatusPending

@Composable
fun QualityGradeBadge(grade: String, modifier: Modifier = Modifier) {
    Surface(
        color = FarmGreenPrimary.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "🛡️", fontSize = 12.sp)
            Text(
                text = grade,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = FarmGreenPrimary
            )
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        "PENDING" -> Triple(StatusPending.copy(alpha = 0.15f), StatusPending, "Pending Inspection")
        "ACCEPTED" -> Triple(StatusInTransit.copy(alpha = 0.15f), StatusInTransit, "Inspection Scheduled")
        "VERIFIED_PASSED" -> Triple(StatusCompleted.copy(alpha = 0.15f), StatusCompleted, "Quality Verified")
        "PLACED" -> Triple(HarvestGold.copy(alpha = 0.15f), HarvestGold, "Order Placed")
        "QUALITY_VERIFIED" -> Triple(StatusCompleted.copy(alpha = 0.15f), StatusCompleted, "Quality Confirmed")
        "DISPATCHED" -> Triple(StatusInTransit.copy(alpha = 0.15f), StatusInTransit, "Farm Dispatched")
        "IN_TRANSIT" -> Triple(StatusInTransit.copy(alpha = 0.15f), StatusInTransit, "In Transit")
        "DELIVERED", "COMPLETED" -> Triple(StatusCompleted.copy(alpha = 0.15f), StatusCompleted, "Delivered")
        else -> Triple(Color.Gray.copy(alpha = 0.15f), Color.DarkGray, status)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StarRatingBar(
    rating: Float,
    ratingCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Rating Star",
            tint = GoldStar,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (ratingCount != null) {
            Text(
                text = "($ratingCount)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun InteractiveStarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GoldStar.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable { onRatingChanged(i) }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "$i Star",
                    tint = if (isSelected) GoldStar else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
