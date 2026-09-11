package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.InspectionRequestEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.RatingEntity
import com.example.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmHelperDao {

    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE farmerId = :farmerId ORDER BY id DESC")
    fun getProductsByFarmer(farmerId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: Long)

    // --- Inspections ---
    @Query("SELECT * FROM inspection_requests ORDER BY createdAt DESC")
    fun getAllInspections(): Flow<List<InspectionRequestEntity>>

    @Query("SELECT * FROM inspection_requests WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getInspectionsForFarmer(farmerId: Long): Flow<List<InspectionRequestEntity>>

    @Query("SELECT * FROM inspection_requests WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun getInspectionsForBuyer(buyerId: Long): Flow<List<InspectionRequestEntity>>

    @Query("SELECT * FROM inspection_requests WHERE id = :id LIMIT 1")
    suspend fun getInspectionById(id: Long): InspectionRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: InspectionRequestEntity): Long

    @Update
    suspend fun updateInspection(inspection: InspectionRequestEntity)

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getOrdersForFarmer(farmerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun getOrdersForBuyer(buyerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationCount(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Long)

    // --- Ratings ---
    @Query("SELECT * FROM ratings WHERE toUserId = :userId ORDER BY createdAt DESC")
    fun getRatingsForUser(userId: Long): Flow<List<RatingEntity>>

    @Query("SELECT * FROM ratings ORDER BY createdAt DESC")
    fun getAllRatings(): Flow<List<RatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: RatingEntity): Long
}
