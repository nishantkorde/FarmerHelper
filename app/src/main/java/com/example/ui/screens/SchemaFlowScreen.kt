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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusInTransit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemaFlowScreen(
    onBack: () -> Unit
) {
    var selectedView by remember { mutableStateOf("FLOW") } // "FLOW" or "SCHEMA"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Architecture & Schema",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Visual Code Flow & Room Database ER",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // View Switcher Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = selectedView == "FLOW",
                    onClick = { selectedView = "FLOW" },
                    label = { Text("Code Flow Diagram (User Spec)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FarmGreenPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = FarmGreenPrimary
                    )
                )

                FilterChip(
                    selected = selectedView == "SCHEMA",
                    onClick = { selectedView = "SCHEMA" },
                    label = { Text("Room Database Schema (6 Tables)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FarmGreenPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = FarmGreenPrimary
                    )
                )
            }

            HorizontalDivider()

            if (selectedView == "FLOW") {
                CodeFlowDiagramView()
            } else {
                DatabaseSchemaTableView()
            }
        }
    }
}

@Composable
fun CodeFlowDiagramView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Step 1: Root Hub
        WorkflowNodeCard(
            title = "FARMHELPER",
            subtitle = "Central Smart Direct-Agri Platform",
            badge = "Core System Hub",
            icon = Icons.Default.Inventory2,
            accentColor = Color(0xFF5E35B1),
            bgColor = Color(0xFFEDE7F6)
        )

        WorkflowArrow()

        // Step 2: Dual Portals
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Branch: Farmer
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WorkflowNodeCard(
                    title = "FARMER / FPO",
                    subtitle = "Dedicated Producer Portal",
                    badge = "Role 1",
                    icon = Icons.Default.Person,
                    accentColor = FarmGreenPrimary,
                    bgColor = FarmGreenPrimary.copy(alpha = 0.12f)
                )
                WorkflowArrow()
                WorkflowNodeCard(
                    title = "Add Products",
                    subtitle = "List title, price, qty, grade, photo & organic cert",
                    badge = "Step 1",
                    icon = Icons.Default.AddCircle,
                    accentColor = FarmGreenPrimary,
                    bgColor = FarmGreenPrimary.copy(alpha = 0.08f)
                )
            }

            // Right Branch: Buyer
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WorkflowNodeCard(
                    title = "BUYER / CONSUMER",
                    subtitle = "Dedicated Consumer Portal",
                    badge = "Role 2",
                    icon = Icons.Default.ShoppingCart,
                    accentColor = Color(0xFF0277BD),
                    bgColor = Color(0xFFE1F5FE)
                )
                WorkflowArrow()
                WorkflowNodeCard(
                    title = "Search Products",
                    subtitle = "Find by category, location, grade & price",
                    badge = "Step 1",
                    icon = Icons.Default.Search,
                    accentColor = Color(0xFF0277BD),
                    bgColor = Color(0xFFE1F5FE).copy(alpha = 0.8f)
                )
            }
        }

        WorkflowArrow()

        // Step 3: Product Node (Central Hub)
        WorkflowNodeCard(
            title = "Product Listing",
            subtitle = "Available for direct purchase or quality inspection selection",
            badge = "Shared Marketplace Item",
            icon = Icons.Default.ShoppingBag,
            accentColor = HarvestGold,
            bgColor = HarvestGold.copy(alpha = 0.14f)
        )

        WorkflowArrow()

        // Step 4: Meet / Inspect
        WorkflowNodeCard(
            title = "Meet / Inspect",
            subtitle = "Discuss, check product in person (Farm Visit / Mandi Meet / Video Call)",
            badge = "Quality Assurance Step 2",
            icon = Icons.Default.Handshake,
            accentColor = Color(0xFF00838F),
            bgColor = Color(0xFFE0F7FA)
        )

        WorkflowArrow()

        // Step 5: Quality Verification
        WorkflowNodeCard(
            title = "Quality Verification",
            subtitle = "Farmer & Buyer check moisture %, grain purity & authenticity",
            badge = "Verification Step 3",
            icon = Icons.Default.VerifiedUser,
            accentColor = StatusCompleted,
            bgColor = StatusCompleted.copy(alpha = 0.12f)
        )

        WorkflowArrow()

        // Step 6: Deal Confirmation
        WorkflowNodeCard(
            title = "Deal Confirmation",
            subtitle = "Agree on certified grade, bulk pricing, delivery terms & finalize",
            badge = "Mutual Agreement Step 4",
            icon = Icons.Default.FactCheck,
            accentColor = Color(0xFFC2185B),
            bgColor = Color(0xFFFCE4EC)
        )

        WorkflowArrow()

        // Step 7: Farmer <-> Buyer Transaction
        WorkflowNodeCard(
            title = "Farmer ↔ Buyer Transaction",
            subtitle = "Secure Escrow payment, live tracking, delivery & mutual rating",
            badge = "Final Execution Step 5",
            icon = Icons.Default.AccountBalance,
            accentColor = FarmGreenPrimary,
            bgColor = FarmGreenPrimary.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun WorkflowNodeCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    bgColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun WorkflowArrow() {
    Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = "Flow direction",
        tint = FarmGreenPrimary,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun DatabaseSchemaTableView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Entity Relationship Schema (Room SQLite)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "6 normalized tables modeling users, listings, inspections, escrow transactions, logistics notifications, and community ratings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        SchemaTableCard(
            tableName = "users",
            description = "Farmers, FPOs, and Buyers profiles with community trust score",
            columns = listOf(
                "id (Long)" to "PRIMARY KEY [Auto]",
                "username (String)" to "Unique account handle",
                "name (String)" to "Full display name",
                "role (String)" to "'FARMER' | 'BUYER'",
                "phone (String)" to "Contact & SMS alerts",
                "location (String)" to "Mandi / Farm district",
                "farmName (String)" to "Registered FPO / Agro Farm",
                "rating (Float)" to "Automated average score (1.0 - 5.0)",
                "ratingCount (Int)" to "Total completed ratings",
                "isVerified (Boolean)" to "Identity verified badge"
            )
        )

        SchemaTableCard(
            tableName = "products",
            description = "Agricultural produce listed by farmers with quality grades",
            columns = listOf(
                "id (Long)" to "PRIMARY KEY [Auto]",
                "farmerId (Long)" to "FOREIGN KEY -> users.id",
                "farmerName (String)" to "Denormalized for quick display",
                "title (String)" to "Crop / produce name",
                "category (String)" to "Grains | Fruits | Veg | Pulses | Dairy",
                "quantityAvailable (Double)" to "Available batch volume",
                "unit (String)" to "kg | quintal | crate | liter",
                "pricePerUnit (Double)" to "Direct farm price (₹)",
                "qualityGrade (String)" to "Grade A+ Organic | Export Quality",
                "allowsInspection (Boolean)" to "Eligible for Meet/Inspect"
            )
        )

        SchemaTableCard(
            tableName = "inspection_requests",
            description = "In-person / video quality inspection meeting & verification record",
            columns = listOf(
                "id (Long)" to "PRIMARY KEY [Auto]",
                "productId (Long)" to "FOREIGN KEY -> products.id",
                "farmerId (Long)" to "FOREIGN KEY -> users.id",
                "buyerId (Long)" to "FOREIGN KEY -> users.id",
                "inspectionType (String)" to "Farm Visit | Mandi Meet | Video Call",
                "preferredDate (String)" to "Scheduled inspection date",
                "meetingLocation (String)" to "On-site farm / depot location",
                "status (String)" to "PENDING | ACCEPTED | VERIFIED_PASSED",
                "qualityGradeGiven (String)" to "Quality cert post-inspection",
                "qualityVerificationNotes (String)" to "Moisture %, purity, testing",
                "finalAgreedPrice (Double)" to "Negotiated deal price per unit"
            )
        )

        SchemaTableCard(
            tableName = "orders",
            description = "Escrow orders placed directly or post-inspection",
            columns = listOf(
                "id (Long)" to "PRIMARY KEY [Auto]",
                "orderNumber (String)" to "Unique tracking ref (e.g. FH-2026-9021)",
                "productId (Long)" to "FOREIGN KEY -> products.id",
                "farmerId (Long)" to "FOREIGN KEY -> users.id",
                "buyerId (Long)" to "FOREIGN KEY -> users.id",
                "quantity (Double)" to "Ordered volume",
                "totalAmount (Double)" to "Final escrow payable (₹)",
                "paymentStatus (String)" to "ESCROW_PAID | RELEASED_TO_FARMER",
                "orderStatus (String)" to "PLACED | QUALITY_VERIFIED | IN_TRANSIT | DELIVERED",
                "trackingPartner (String)" to "Logistics carrier name",
                "trackingNumber (String)" to "Real-time airway bill number",
                "isQualityVerified (Boolean)" to "Quality checked prior to order"
            )
        )

        SchemaTableCard(
            tableName = "notifications",
            description = "Real-time updates delivered to farmers and buyers",
            columns = listOf(
                "id (Long)" to "PRIMARY KEY [Auto]",
                "userId (Long)" to "FOREIGN KEY -> users.id",
                "role (String)" to "'FARMER' | 'BUYER'",
                "title (String)" to "Notification headline",
                "message (String)" to "Detailed real-time text",
                "type (String)" to "INSPECTION | ORDER | PAYMENT | DELIVERY | RATING",
                "isRead (Boolean)" to "Read/Unread flag",
                "timestamp (Long)" to "Epoch millis"
            )
        )

        SchemaTableCard(
            tableName = "ratings",
            description = "Automated post-delivery community trust reviews",
            columns = listOf(
                "id (Long)" to "PRIMARY KEY [Auto]",
                "orderId (Long)" to "FOREIGN KEY -> orders.id",
                "fromUserId (Long)" to "Reviewer ID",
                "toUserId (Long)" to "Recipient ID",
                "stars (Int)" to "1 to 5 stars",
                "tags (String)" to "Super Fresh, Accurate Weight, etc.",
                "comment (String)" to "Written testimonial"
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SchemaTableCard(
    tableName: String,
    description: String,
    columns: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = tableName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = FarmGreenPrimary
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                columns.forEach { (col, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = col,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
