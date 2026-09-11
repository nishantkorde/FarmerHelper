package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusInTransit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboardScreen(
    buyer: UserEntity,
    products: List<ProductEntity>,
    inspections: List<InspectionRequestEntity>,
    orders: List<OrderEntity>,
    ratings: List<RatingEntity>,
    unreadNotifs: Int,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onRequestInspection: (ProductEntity) -> Unit,
    onBuyDirect: (ProductEntity) -> Unit,
    onPlaceOrderFromVerifiedInspection: (InspectionRequestEntity, ProductEntity) -> Unit,
    onOpenRating: (OrderEntity) -> Unit,
    onOpenNotifications: () -> Unit,
    onSwitchPortal: () -> Unit,
    onLogout: () -> Unit,
    onViewSchema: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Marketplace (${products.size})",
        "Inspections (${inspections.size})",
        "My Orders (${orders.size})",
        "Trust & Reviews"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🛒", fontSize = 22.sp)
                        Column {
                            Text(
                                text = buyer.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${buyer.location} • Consumer Portal",
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
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch to Farmer Portal")
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
                0 -> BuyerMarketplaceTab(
                    products = products,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    onSearchChange = onSearchChange,
                    onCategoryChange = onCategoryChange,
                    onRequestInspection = onRequestInspection,
                    onBuyDirect = onBuyDirect
                )
                1 -> BuyerInspectionsTab(
                    inspections = inspections,
                    allProducts = products,
                    onCheckoutVerified = onPlaceOrderFromVerifiedInspection
                )
                2 -> BuyerOrdersTab(
                    orders = orders,
                    onRateOrder = onOpenRating
                )
                3 -> BuyerTrustTab(ratings = ratings)
            }
        }
    }
}

@Composable
fun BuyerMarketplaceTab(
    products: List<ProductEntity>,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onRequestInspection: (ProductEntity) -> Unit,
    onBuyDirect: (ProductEntity) -> Unit
) {
    val categories = listOf("All", "Grains", "Fruits", "Vegetables", "Dairy", "Pulses")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search crops, farmer names, or locations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategoryChange(cat) },
                    label = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FarmGreenPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = FarmGreenPrimary
                    )
                )
            }
        }

        // Informative Escrow & Inspection Callout
        Surface(
            color = HarvestGold.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🛡️", fontSize = 18.sp)
                Text(
                    text = "Request Quality Inspection before paying, or buy directly with 100% Escrow Protection.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }

        // Product List
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No matching farm produce found.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products) { product ->
                    BuyerProductCard(
                        product = product,
                        onRequestInspection = { onRequestInspection(product) },
                        onBuyDirect = { onBuyDirect(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun BuyerProductCard(
    product: ProductEntity,
    onRequestInspection: () -> Unit,
    onBuyDirect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(FarmGreenPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = product.emojiIcon, fontSize = 30.sp)
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
                        text = "Farmer: ${product.farmerName} • ${product.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 2
                    )
                }
            }

            // Price & Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${product.pricePerUnit.toInt()} / ${product.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = FarmGreenPrimary
                    )
                    Text(
                        text = "Available: ${product.quantityAvailable} ${product.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                if (product.isOrganicCertified) {
                    Surface(
                        color = FarmGreenPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🌱 Certified Organic",
                            style = MaterialTheme.typography.labelSmall,
                            color = FarmGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Two Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Meet / Inspect Button
                OutlinedButton(
                    onClick = onRequestInspection,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Meet / Inspect", color = FarmGreenPrimary, fontSize = 12.sp)
                }

                // Buy Direct (Escrow)
                Button(
                    onClick = onBuyDirect,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buy (Escrow)", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BuyerInspectionsTab(
    inspections: List<InspectionRequestEntity>,
    allProducts: List<ProductEntity>,
    onCheckoutVerified: (InspectionRequestEntity, ProductEntity) -> Unit
) {
    if (inspections.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🔍", fontSize = 42.sp)
                Text(
                    text = "No quality inspections yet",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "You can request an in-person farm visit or mandi inspection for any produce before paying.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
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
                                Text(text = insp.productEmoji, fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = insp.productTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Farmer: ${insp.farmerName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            StatusBadge(status = insp.status)
                        }

                        Text(
                            text = "Meeting Mode: ${insp.inspectionType} • ${insp.preferredDate} (${insp.preferredTime})",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Location: ${insp.meetingLocation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Verified Status banner & Checkout button!
                        if (insp.status == "VERIFIED_PASSED") {
                            Surface(
                                color = StatusCompleted.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "✅ Quality Verified (${insp.qualityGradeGiven})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusCompleted
                                    )
                                    Text(
                                        text = insp.qualityVerificationNotes,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Agreed Final Price: ₹${insp.finalAgreedPrice.toInt()} / ${insp.unit} (Quantity: ${insp.requestedQuantity} ${insp.unit})",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusCompleted
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Direct Checkout Button with Quality Guarantee
                                    Button(
                                        onClick = {
                                            val matchedProduct = allProducts.find { it.id == insp.productId }
                                                ?: ProductEntity(
                                                    id = insp.productId,
                                                    farmerId = insp.farmerId,
                                                    farmerName = insp.farmerName,
                                                    farmerPhone = "+91 98220 11223",
                                                    title = insp.productTitle,
                                                    category = "Verified",
                                                    description = "Direct farm produce verified through physical inspection.",
                                                    quantityAvailable = insp.requestedQuantity,
                                                    unit = insp.unit,
                                                    pricePerUnit = insp.finalAgreedPrice,
                                                    location = insp.meetingLocation,
                                                    qualityGrade = insp.qualityGradeGiven,
                                                    harvestDate = "Fresh Harvest",
                                                    emojiIcon = insp.productEmoji
                                                )
                                            onCheckoutVerified(insp, matchedProduct)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted)
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Place Order (Quality Guaranteed)")
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
fun BuyerOrdersTab(
    orders: List<OrderEntity>,
    onRateOrder: (OrderEntity) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No active or past orders.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = order.productEmoji, fontSize = 28.sp)
                            Column {
                                Text(
                                    text = "${order.quantity} ${order.unit} • ${order.productTitle}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "From: ${order.farmerName} • ₹${order.totalAmount.toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FarmGreenPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Order Stepper Visualizer
                        OrderLifecycleStepper(currentStatus = order.orderStatus)

                        // Tracking & Delivery Details
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = StatusInTransit,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${order.trackingPartner}: ${order.trackingNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Destination: ${order.deliveryAddress}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Payment Status: ${order.paymentStatus} (FarmPay Escrow)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FarmGreenPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Rating Trigger if Delivered
                        if (order.orderStatus == "DELIVERED" || order.orderStatus == "COMPLETED") {
                            if (!order.buyerRated) {
                                Button(
                                    onClick = { onRateOrder(order) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldStar)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Rate Quality & Build Community Trust", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    color = GoldStar.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "⭐", fontSize = 16.sp)
                                        Text(
                                            text = "Thank you! You rated this order and boosted farmer reliability.",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
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
fun OrderLifecycleStepper(currentStatus: String) {
    val steps = listOf("Placed", "Verified", "Dispatched", "In Transit", "Delivered")
    val currentIndex = when (currentStatus) {
        "PLACED" -> 0
        "QUALITY_VERIFIED" -> 1
        "DISPATCHED" -> 2
        "IN_TRANSIT" -> 3
        "DELIVERED", "COMPLETED" -> 4
        else -> 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isPassed = index <= currentIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isPassed) FarmGreenPrimary else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = if (isPassed) FarmGreenPrimary else Color.Gray,
                    fontWeight = if (isPassed) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun BuyerTrustTab(ratings: List<RatingEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FarmGreenPrimary.copy(alpha = 0.08f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🌱 Transparent Community Trust",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenPrimary
                )
                Text(
                    text = "Every transaction is backed by physical or video quality inspection, escrow funds security, and mutual verified ratings to ensure long-term community reliability.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }

        Text(
            text = "Community Ratings & Feedback Feed",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ratings) { rating ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
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
                                text = "${rating.fromUserName} → ${rating.toUserName}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            StarRatingBar(rating = rating.stars.toFloat())
                        }
                        if (rating.tags.isNotBlank()) {
                            Text(
                                text = "Highlights: ${rating.tags}",
                                style = MaterialTheme.typography.labelSmall,
                                color = FarmGreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "\"${rating.comment}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
