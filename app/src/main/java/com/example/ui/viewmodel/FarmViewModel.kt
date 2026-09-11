package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.RatingEntity
import com.example.data.entity.UserEntity
import com.example.data.repository.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FarmViewModel(private val repository: FarmRepository) : ViewModel() {

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Logged in user & active role
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Current role filter / login tab: "FARMER" or "BUYER"
    private val _activeRoleTab = MutableStateFlow("FARMER")
    val activeRoleTab: StateFlow<String> = _activeRoleTab.asStateFlow()

    // App Navigation / View Mode: "LOGIN", "DASHBOARD", "SCHEMA_FLOW"
    private val _currentScreen = MutableStateFlow("LOGIN")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Dashboard Sub-tabs: "OVERVIEW", "PRODUCTS", "INSPECTIONS", "ORDERS", "COMMUNITY_RATINGS"
    private val _dashboardTab = MutableStateFlow("OVERVIEW")
    val dashboardTab: StateFlow<String> = _dashboardTab.asStateFlow()

    // Search and category filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // All products
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered products for buyers
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        searchQuery,
        selectedCategory
    ) { products, query, cat ->
        products.filter { p ->
            val matchesCategory = (cat == "All" || p.category.equals(cat, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                p.title.contains(query, ignoreCase = true) ||
                p.location.contains(query, ignoreCase = true) ||
                p.farmerName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Farmer's own products
    val farmerProducts: StateFlow<List<ProductEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "FARMER") {
            repository.getProductsByFarmer(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Inspections for current user
    val userInspections: StateFlow<List<InspectionRequestEntity>> = _currentUser.flatMapLatest { user ->
        when (user?.role) {
            "FARMER" -> repository.getInspectionsForFarmer(user.id)
            "BUYER" -> repository.getInspectionsForBuyer(user.id)
            else -> repository.allInspections
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders for current user
    val userOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        when (user?.role) {
            "FARMER" -> repository.getOrdersForFarmer(user.id)
            "BUYER" -> repository.getOrdersForBuyer(user.id)
            else -> repository.allOrders
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for current user
    val userNotifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getNotificationsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unread count
    val unreadNotificationsCount: StateFlow<Int> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getUnreadNotificationCount(user.id)
        } else {
            flowOf(0)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Ratings
    val allRatings: StateFlow<List<RatingEntity>> = repository.allRatings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & Action States
    val showAddProductDialog = MutableStateFlow(false)
    val showNotificationDialog = MutableStateFlow(false)
    val activeInspectionProduct = MutableStateFlow<ProductEntity?>(null)
    val activeCheckoutProduct = MutableStateFlow<ProductEntity?>(null)
    val activeVerifiedInspectionForCheckout = MutableStateFlow<InspectionRequestEntity?>(null)
    val activeRatingOrder = MutableStateFlow<OrderEntity?>(null)
    val snackbarMessage = MutableStateFlow<String?>(null)

    fun setActiveRoleTab(role: String) {
        _activeRoleTab.value = role
    }

    fun loginAs(user: UserEntity) {
        _currentUser.value = user
        _activeRoleTab.value = user.role
        _currentScreen.value = "DASHBOARD"
        _dashboardTab.value = "OVERVIEW"
        snackbarMessage.value = "Welcome back, ${user.name}! (${user.role} Portal)"
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "LOGIN"
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setDashboardTab(tab: String) {
        _dashboardTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

    // --- Farmer Actions ---
    fun addProduct(
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
    ) {
        val farmer = _currentUser.value ?: return
        viewModelScope.launch {
            val product = ProductEntity(
                farmerId = farmer.id,
                farmerName = farmer.name,
                farmerPhone = farmer.phone,
                title = title,
                category = category,
                description = description,
                quantityAvailable = quantity,
                unit = unit,
                pricePerUnit = pricePerUnit,
                location = location.ifBlank { farmer.location },
                qualityGrade = qualityGrade,
                harvestDate = harvestDate,
                isOrganicCertified = isOrganic,
                allowsInspection = true,
                emojiIcon = emoji
            )
            repository.insertProduct(product)
            showAddProductDialog.value = false
            snackbarMessage.value = "Product '$title' listed successfully!"
        }
    }

    fun acceptInspection(inspection: InspectionRequestEntity) {
        viewModelScope.launch {
            repository.acceptInspection(inspection)
            snackbarMessage.value = "Inspection meeting accepted for ${inspection.preferredDate}!"
        }
    }

    fun verifyQualityAndConfirmDeal(
        inspection: InspectionRequestEntity,
        qualityGrade: String,
        notes: String,
        agreedPrice: Double
    ) {
        viewModelScope.launch {
            repository.verifyQualityAndConfirmDeal(inspection, qualityGrade, notes, agreedPrice)
            snackbarMessage.value = "Quality verified & deal confirmed for ${inspection.buyerName}!"
        }
    }

    fun updateOrderStatus(order: OrderEntity, nextStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(order, nextStatus)
            snackbarMessage.value = "Order #${order.orderNumber} updated to $nextStatus"
        }
    }

    // --- Buyer Actions ---
    fun requestInspection(
        product: ProductEntity,
        inspectionType: String,
        date: String,
        time: String,
        location: String,
        notes: String,
        quantity: Double
    ) {
        val buyer = _currentUser.value ?: return
        viewModelScope.launch {
            repository.requestInspection(
                product = product,
                buyer = buyer,
                inspectionType = inspectionType,
                preferredDate = date,
                preferredTime = time,
                meetingLocation = location,
                notes = notes,
                requestedQuantity = quantity
            )
            activeInspectionProduct.value = null
            snackbarMessage.value = "Quality inspection request sent to ${product.farmerName}!"
        }
    }

    fun completeCheckout(
        product: ProductEntity,
        quantity: Double,
        unitPrice: Double,
        deliveryAddress: String,
        paymentMethod: String,
        isQualityVerified: Boolean,
        inspectionId: Long?
    ) {
        val buyer = _currentUser.value ?: return
        viewModelScope.launch {
            repository.placeOrderWithPayment(
                product = product,
                buyer = buyer,
                quantity = quantity,
                unitPrice = unitPrice,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod,
                isQualityVerified = isQualityVerified,
                inspectionId = inspectionId
            )
            activeCheckoutProduct.value = null
            activeVerifiedInspectionForCheckout.value = null
            snackbarMessage.value = "Payment confirmed! Order placed and secured in Escrow."
            _dashboardTab.value = "ORDERS"
        }
    }

    fun submitRating(
        order: OrderEntity,
        stars: Int,
        tags: String,
        feedback: String
    ) {
        val currentUser = _currentUser.value ?: return
        viewModelScope.launch {
            // Find target user
            val targetUserId = if (currentUser.role == "BUYER") order.farmerId else order.buyerId
            val targetUser = repository.getUserById(targetUserId)
            if (targetUser != null) {
                repository.submitRating(order, currentUser, targetUser, stars, tags, feedback)
                activeRatingOrder.value = null
                snackbarMessage.value = "Thank you! Rating submitted to build community trust."
            }
        }
    }

    fun markNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
        }
    }
}
