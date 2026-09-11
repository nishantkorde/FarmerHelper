package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddProductDialog
import com.example.ui.components.InspectionRequestDialog
import com.example.ui.components.NotificationSheet
import com.example.ui.components.PaymentGatewayDialog
import com.example.ui.components.RatingDialog
import com.example.ui.screens.BuyerDashboardScreen
import com.example.ui.screens.FarmerDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SchemaFlowScreen
import com.example.ui.theme.FarmHelperTheme
import com.example.ui.viewmodel.FarmViewModel
import com.example.ui.viewmodel.FarmViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FarmHelperTheme {
        FarmHelperApp()
      }
    }
  }
}

@Composable
fun FarmHelperApp() {
  val viewModel: FarmViewModel = viewModel(factory = FarmViewModelFactory(androidx.compose.ui.platform.LocalContext.current))

  val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val activeRoleTab by viewModel.activeRoleTab.collectAsStateWithLifecycle()
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

  val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
  val farmerProducts by viewModel.farmerProducts.collectAsStateWithLifecycle()
  val userInspections by viewModel.userInspections.collectAsStateWithLifecycle()
  val userOrders by viewModel.userOrders.collectAsStateWithLifecycle()
  val userNotifications by viewModel.userNotifications.collectAsStateWithLifecycle()
  val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
  val allRatings by viewModel.allRatings.collectAsStateWithLifecycle()

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

  val showAddProduct by viewModel.showAddProductDialog.collectAsStateWithLifecycle()
  val showNotifications by viewModel.showNotificationDialog.collectAsStateWithLifecycle()
  val activeInspectionProduct by viewModel.activeInspectionProduct.collectAsStateWithLifecycle()
  val activeCheckoutProduct by viewModel.activeCheckoutProduct.collectAsStateWithLifecycle()
  val activeVerifiedInspectionForCheckout by viewModel.activeVerifiedInspectionForCheckout.collectAsStateWithLifecycle()
  val activeRatingOrder by viewModel.activeRatingOrder.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
      snackbarHostState.showSnackbar(
        message = it,
        duration = SnackbarDuration.Short
      )
      viewModel.clearSnackbar()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    val contentModifier = Modifier.padding(innerPadding)

    when {
      currentScreen == "SCHEMA_FLOW" -> {
        SchemaFlowScreen(
          onBack = {
            viewModel.setScreen(if (currentUser != null) "DASHBOARD" else "LOGIN")
          }
        )
      }

      currentScreen == "LOGIN" || currentUser == null -> {
        LoginScreen(
          users = allUsers,
          activeTab = activeRoleTab,
          onTabSelected = { viewModel.setActiveRoleTab(it) },
          onLogin = { viewModel.loginAs(it) },
          onViewSchema = { viewModel.setScreen("SCHEMA_FLOW") }
        )
      }

      currentUser?.role == "FARMER" -> {
        FarmerDashboardScreen(
          farmer = currentUser!!,
          farmerProducts = farmerProducts,
          inspections = userInspections,
          orders = userOrders,
          ratings = allRatings.filter { it.toUserId == currentUser!!.id },
          unreadNotifs = unreadNotificationsCount,
          onAddProductClick = { viewModel.showAddProductDialog.value = true },
          onAcceptInspection = { viewModel.acceptInspection(it) },
          onVerifyQualityAndConfirmDeal = { insp, grade, notes, price ->
            viewModel.verifyQualityAndConfirmDeal(insp, grade, notes, price)
          },
          onUpdateOrderStatus = { order, status -> viewModel.updateOrderStatus(order, status) },
          onOpenNotifications = { viewModel.showNotificationDialog.value = true },
          onSwitchPortal = {
            val buyerUser = allUsers.firstOrNull { it.role == "BUYER" }
            if (buyerUser != null) {
              viewModel.loginAs(buyerUser)
            } else {
              viewModel.logout()
            }
          },
          onLogout = { viewModel.logout() },
          onViewSchema = { viewModel.setScreen("SCHEMA_FLOW") }
        )
      }

      currentUser?.role == "BUYER" -> {
        BuyerDashboardScreen(
          buyer = currentUser!!,
          products = filteredProducts,
          inspections = userInspections,
          orders = userOrders,
          ratings = allRatings,
          unreadNotifs = unreadNotificationsCount,
          searchQuery = searchQuery,
          selectedCategory = selectedCategory,
          onSearchChange = { viewModel.setSearchQuery(it) },
          onCategoryChange = { viewModel.setSelectedCategory(it) },
          onRequestInspection = { viewModel.activeInspectionProduct.value = it },
          onBuyDirect = {
            viewModel.activeVerifiedInspectionForCheckout.value = null
            viewModel.activeCheckoutProduct.value = it
          },
          onPlaceOrderFromVerifiedInspection = { insp, product ->
            viewModel.activeVerifiedInspectionForCheckout.value = insp
            viewModel.activeCheckoutProduct.value = product
          },
          onOpenRating = { viewModel.activeRatingOrder.value = it },
          onOpenNotifications = { viewModel.showNotificationDialog.value = true },
          onSwitchPortal = {
            val farmerUser = allUsers.firstOrNull { it.role == "FARMER" }
            if (farmerUser != null) {
              viewModel.loginAs(farmerUser)
            } else {
              viewModel.logout()
            }
          },
          onLogout = { viewModel.logout() },
          onViewSchema = { viewModel.setScreen("SCHEMA_FLOW") }
        )
      }
    }
  }

  // --- Dialogs ---

  // 1. Farmer: Add Product Dialog
  if (showAddProduct && currentUser != null) {
    AddProductDialog(
      farmerLocation = currentUser!!.location,
      onDismiss = { viewModel.showAddProductDialog.value = false },
      onAddProduct = { title, cat, desc, qty, unit, price, loc, grade, harvestDate, organic, emoji ->
        viewModel.addProduct(
          title = title,
          category = cat,
          description = desc,
          quantity = qty,
          unit = unit,
          pricePerUnit = price,
          location = loc,
          qualityGrade = grade,
          harvestDate = harvestDate,
          isOrganic = organic,
          emoji = emoji
        )
      }
    )
  }

  // 2. Buyer: Request Quality Inspection Dialog
  activeInspectionProduct?.let { product ->
    InspectionRequestDialog(
      product = product,
      onDismiss = { viewModel.activeInspectionProduct.value = null },
      onSubmitRequest = { type, date, time, location, notes, quantity ->
        viewModel.requestInspection(
          product = product,
          inspectionType = type,
          date = date,
          time = time,
          location = location,
          notes = notes,
          quantity = quantity
        )
      }
    )
  }

  // 3. Buyer: Secure Escrow Payment Gateway Dialog
  activeCheckoutProduct?.let { product ->
    PaymentGatewayDialog(
      product = product,
      verifiedInspection = activeVerifiedInspectionForCheckout,
      onDismiss = {
        viewModel.activeCheckoutProduct.value = null
        viewModel.activeVerifiedInspectionForCheckout.value = null
      },
      onPaymentSuccess = { qty, unitPrice, address, method ->
        val inspection = activeVerifiedInspectionForCheckout
        viewModel.completeCheckout(
          product = product,
          quantity = qty,
          unitPrice = unitPrice,
          deliveryAddress = address,
          paymentMethod = method,
          isQualityVerified = inspection != null,
          inspectionId = inspection?.id
        )
      }
    )
  }

  // 4. Completed Order Rating Dialog
  activeRatingOrder?.let { order ->
    val role = currentUser?.role ?: "BUYER"
    RatingDialog(
      order = order,
      currentRole = role,
      onDismiss = { viewModel.activeRatingOrder.value = null },
      onSubmit = { stars, tags, feedback ->
        viewModel.submitRating(order, stars, tags, feedback)
      }
    )
  }

  // 5. Notifications Sheet
  if (showNotifications) {
    NotificationSheet(
      notifications = userNotifications,
      onDismiss = { viewModel.showNotificationDialog.value = false },
      onMarkAllRead = { viewModel.markNotificationsRead() }
    )
  }
}

