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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserEntity
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold

@Composable
fun LoginScreen(
    users: List<UserEntity>,
    activeTab: String,
    onTabSelected: (String) -> Unit,
    onLogin: (UserEntity) -> Unit,
    onViewSchema: () -> Unit
) {
    val farmers = users.filter { it.role == "FARMER" }
    val buyers = users.filter { it.role == "BUYER" }

    var customName by remember { mutableStateOf("") }
    var customPhone by remember { mutableStateOf("") }
    var customLocation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header with Lush Agricultural Theme
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            FarmGreenPrimary,
                            Color(0xFF144A27)
                        )
                    )
                )
                .padding(top = 44.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // App Emblem
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🌾", fontSize = 38.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Farm Helper",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Direct Farm-to-Consumer Agricultural Platform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeatureBadge(text = "🔍 Meet & Inspect Quality")
                    FeatureBadge(text = "🛡️ Escrow Protection")
                }
            }
        }

        // Schema Flow Link Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewSchema() },
            color = HarvestGold.copy(alpha = 0.12f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = HarvestGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "View Architecture Code Flow & Database Schema",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = HarvestGold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = HarvestGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Role Tabs (Farmer Login Page vs Customer Login Page separately)
        TabRow(
            selectedTabIndex = if (activeTab == "FARMER") 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = FarmGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (activeTab == "FARMER") 0 else 1]
                    ),
                    color = FarmGreenPrimary
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = activeTab == "FARMER",
                onClick = { onTabSelected("FARMER") },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "👨‍🌾", fontSize = 18.sp)
                        Text(
                            text = "Farmer Login",
                            fontWeight = if (activeTab == "FARMER") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
            Tab(
                selected = activeTab == "BUYER",
                onClick = { onTabSelected("BUYER") },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🛒", fontSize = 18.sp)
                        Text(
                            text = "Consumer Login",
                            fontWeight = if (activeTab == "BUYER") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content for Selected Role
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (activeTab == "FARMER") {
                Text(
                    text = "Select Farmer Account to Sign In",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage your crop listings, receive in-person inspection appointments, verify quality, and receive Escrow payouts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                farmers.forEach { farmer ->
                    UserLoginCard(
                        user = farmer,
                        onSelect = { onLogin(farmer) }
                    )
                }
            } else {
                Text(
                    text = "Select Consumer / Buyer Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Search fresh produce directly from farmers, request quality assurance inspections before buying, and track deliveries in real time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                buyers.forEach { buyer ->
                    UserLoginCard(
                        user = buyer,
                        onSelect = { onLogin(buyer) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Or Quick Custom Sign In
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Or Sign In as New ${if (activeTab == "FARMER") "Farmer / FPO" else "Consumer"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Your Name") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = customPhone,
                        onValueChange = { customPhone = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = customLocation,
                        onValueChange = { customLocation = it },
                        label = { Text(if (activeTab == "FARMER") "Farm Location (e.g. Nashik)" else "Delivery City (e.g. Mumbai)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (customName.isNotBlank()) {
                                val newUser = UserEntity(
                                    username = customName.lowercase().replace(" ", "_"),
                                    name = customName,
                                    role = activeTab,
                                    phone = customPhone.ifBlank { "+91 99887 76655" },
                                    location = customLocation.ifBlank { "Maharashtra, India" },
                                    farmName = if (activeTab == "FARMER") "$customName Agro" else "",
                                    avatarInitials = customName.take(2).uppercase()
                                )
                                onLogin(newUser)
                            }
                        },
                        enabled = customName.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue to ${if (activeTab == "FARMER") "Farmer" else "Buyer"} Portal")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserLoginCard(
    user: UserEntity,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(FarmGreenPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.avatarInitials.ifBlank { user.name.take(2).uppercase() },
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenPrimary,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (user.farmName.isNotBlank()) {
                    Text(
                        text = user.farmName,
                        style = MaterialTheme.typography.bodySmall,
                        color = FarmGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "${user.location} • ${user.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                StarRatingBar(rating = user.rating, ratingCount = user.ratingCount)
            }

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
            ) {
                Text("Sign In")
            }
        }
    }
}

@Composable
fun FeatureBadge(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
