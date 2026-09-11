package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.RatingEntity
import com.example.data.entity.UserEntity
import com.example.ui.components.QualityGradeBadge
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.VerifyQualityDialog
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusInTransit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDashboardScreen(
    farmer: UserEntity,
    farmerProducts: List<ProductEntity>,
    inspections: List<InspectionRequestEntity>,
    orders: List<OrderEntity>,
    ratings: List<RatingEntity>,
    unreadNotifs: Int,
    onAddProductClick: () -> Unit,
    onAcceptInspection: (InspectionRequestEntity) -> Unit,
    onVerifyQualityAndConfirmDeal: (InspectionRequestEntity, String, String, Double) -> Unit,
    onUpdateOrderStatus: (OrderEntity, String) -> Unit,
    onOpenNotifications: () -> Unit,
    onSwitchPortal: () -> Unit,
    onLogout: () -> Unit,
    onViewSchema: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Produce (${farmerProducts.size})", "Inspections (${inspections.size})", "Orders (${orders.size})", "Trust & Ratings")
    var inspectionToVerify by remember { mutableStateOf<InspectionRequestEntity?>(null) }

    val pendingInspections = inspections.filter { it.status == "PENDING" }
    val totalEscrowEarnings = orders.sumOf { it.totalAmount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "👨‍🌾", fontSize = 22.sp)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = farmer.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Producer",
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = farmer.farmName.ifBlank { "Farmer / FPO Dashboard" },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onViewSchema) {
                        Icon(Icons.Default.Code, contentDescription = "View Schema Flow", tint = HarvestGold)
                    }
                    IconButton(onClick = onOpenNotifications) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(containerColor = Color.Red) {
                                        Text(unreadNotifs.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = onSwitchPortal) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch to Consumer Portal")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Stats Row Banner
            FarmerMetricsBanner(
                productsCount = farmerProducts.size,
                pendingInspectionsCount = pendingInspections.size,
                ordersCount = orders.size,
                escrowEarnings = totalEscrowEarnings,
                rating = farmer.rating,
                ratingCount = farmer.ratingCount,
                onAddProduceClick = onAddProductClick
            )

            // Primary Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = FarmGreenPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> FarmerProduceTab(
                    products = farmerProducts,
                    onAddProduceClick = onAddProductClick
                )
                1 -> FarmerInspectionsTab(
                    inspections = inspections,
                    onAccept = onAcceptInspection,
                    onOpenVerifyDialog = { inspectionToVerify = it }
                )
                2 -> FarmerOrdersTab(
                    orders = orders,
                    onUpdateStatus = onUpdateOrderStatus
                )
                3 -> FarmerTrustTab(
                    farmer = farmer,
                    ratings = ratings
                )
            }
        }
    }

    // Inspection Verification Dialog
    inspectionToVerify?.let { insp ->
        VerifyQualityDialog(
            inspection = insp,
            onDismiss = { inspectionToVerify = null },
            onConfirmVerification = { grade, notes, agreedPrice ->
                onVerifyQualityAndConfirmDeal(insp, grade, notes, agreedPrice)
                inspectionToVerify = null
            }
        )
    }
}

@Composable
fun FarmerMetricsBanner(
    productsCount: Int,
    pendingInspectionsCount: Int,
    ordersCount: Int,
    escrowEarnings: Double,
    rating: Float,
    ratingCount: Int,
    onAddProduceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = FarmGreenPrimary.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Producer Hub",
                        style = MaterialTheme.typography.labelSmall,
                        color = FarmGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${escrowEarnings.toInt()} Escrow Volume",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Button(
                    onClick = onAddProduceClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Produce", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = FarmGreenPrimary.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricItem(label = "Active Crops", value = productsCount.toString())
                MetricItem(
                    label = "Inspections",
                    value = if (pendingInspectionsCount > 0) "$pendingInspectionsCount New" else "0 New",
                    highlight = pendingInspectionsCount > 0
                )
                MetricItem(label = "Total Orders", value = ordersCount.toString())
                MetricItem(label = "Trust Rating", value = "★ ${String.format("%.1f", rating)}")
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Color(0xFFEA580C) else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            fontSize = 11.sp
        )
    }
}

@Composable
fun FarmerProduceTab(
    products: List<ProductEntity>,
    onAddProduceClick: () -> Unit
) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🌱", fontSize = 48.sp)
                Text(
                    text = "No produce listed yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "List your fresh grains, fruits, vegetables, or dairy so buyers can inspect and purchase directly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = onAddProduceClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Your First Produce")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                FarmerProductCard(product = product)
            }
        }
    }
}

@Composable
fun FarmerProductCard(product: ProductEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FarmGreenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = product.emojiIcon, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    QualityGradeBadge(grade = product.qualityGrade)
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${product.category} • Harvested ${product.harvestDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Text(
                    text = "Stock: ${product.quantityAvailable} ${product.unit} available",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (product.quantityAvailable > 0) FarmGreenPrimary else Color.Red
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Direct Price: ₹${product.pricePerUnit.toInt()} / ${product.unit}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenPrimary
                )
            }
        }
    }
}

@Composable
fun FarmerInspectionsTab(
    inspections: List<InspectionRequestEntity>,
    onAccept: (InspectionRequestEntity) -> Unit,
    onOpenVerifyDialog: (InspectionRequestEntity) -> Unit
) {
    if (inspections.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No quality inspection requests yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(inspections) { insp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = insp.productEmoji, fontSize = 22.sp)
                                Text(
                                    text = insp.productTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            StatusBadge(status = insp.status)
                        }

                        Text(
                            text = "Buyer: ${insp.buyerName} (${insp.buyerPhone})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Inspection: ${insp.inspectionType} • ${insp.preferredDate} at ${insp.preferredTime}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Location: ${insp.meetingLocation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        if (insp.buyerNotes.isNotBlank()) {
                            Text(
                                text = "Buyer Checklist: \"${insp.buyerNotes}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }

                        if (insp.status == "VERIFIED_PASSED") {
                            Surface(
                                color = StatusCompleted.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "✅ Verification Confirmed: ${insp.qualityGradeGiven}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusCompleted
                                    )
                                    Text(
                                        text = insp.qualityVerificationNotes,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Deal Confirmed Price: ₹${insp.finalAgreedPrice.toInt()} / ${insp.unit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusCompleted
                                    )
                                }
                            }
                        }

                        // Actions based on state
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (insp.status == "PENDING") {
                                Button(
                                    onClick = { onAccept(insp) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Accept Meeting")
                                }
                            } else if (insp.status == "ACCEPTED") {
                                Button(
                                    onClick = { onOpenVerifyDialog(insp) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted)
                                ) {
                                    Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Quality & Finalize Deal")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FarmerOrdersTab(
    orders: List<OrderEntity>,
    onUpdateStatus: (OrderEntity, String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No customer orders yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order #${order.orderNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(status = order.orderStatus)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = order.productEmoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "${order.quantity} ${order.unit} • ${order.productTitle}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Buyer: ${order.buyerName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Text(
                            text = "Delivery Address: ${order.deliveryAddress}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Surface(
                            color = FarmGreenPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Payment: ${order.paymentStatus}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenPrimary
                                )
                                Text(
                                    text = "₹${order.totalAmount.toInt()}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenPrimary
                                )
                            }
                        }

                        // Dispatch Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (order.orderStatus) {
                                "PLACED", "QUALITY_VERIFIED" -> {
                                    Button(
                                        onClick = { onUpdateStatus(order, "DISPATCHED") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Dispatched")
                                    }
                                }
                                "DISPATCHED" -> {
                                    Button(
                                        onClick = { onUpdateStatus(order, "IN_TRANSIT") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusInTransit)
                                    ) {
                                        Text("Set In Transit")
                                    }
                                }
                                "IN_TRANSIT" -> {
                                    Button(
                                        onClick = { onUpdateStatus(order, "DELIVERED") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted)
                                    ) {
                                        Text("Confirm Handover (Delivered)")
                                    }
                                }
                                "DELIVERED", "COMPLETED" -> {
                                    Surface(
                                        color = StatusCompleted.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "✅ Delivered & Escrow Released",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StatusCompleted,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FarmerTrustTab(
    farmer: UserEntity,
    ratings: List<RatingEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Farmer Community Trust Score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("%.1f", farmer.rating),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = GoldStar
                )
                StarRatingBar(rating = farmer.rating, ratingCount = farmer.ratingCount)
                Text(
                    text = "Based on ${farmer.ratingCount} verified crop inspections & transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Text(
            text = "Buyer Reviews & Experience Testimonials",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        if (ratings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No reviews received yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(ratings) { rating ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rating.fromUserName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                StarRatingBar(rating = rating.stars.toFloat())
                            }
                            if (rating.tags.isNotBlank()) {
                                Text(
                                    text = "Tags: ${rating.tags}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FarmGreenPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "\"${rating.comment}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}
