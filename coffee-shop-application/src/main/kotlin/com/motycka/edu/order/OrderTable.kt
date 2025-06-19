package com.motycka.edu.order

// TODO implement OrderTable and OrderDAO
import com.motycka.edu.customer.CustomerTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object OrderTable : LongIdTable("orders") { // Changed table name to "orders"
    val customerId = long("customer_id").references(CustomerTable.id)
    val status = enumerationByName("status", 50, OrderStatus::class)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    // Storing the calculated price is a good practice for historical accuracy
    val totalPrice = double("total_price")
    val isPaid = bool("is_paid").default(false)
}

class OrderDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OrderDAO>(OrderTable)

    var customerId by OrderTable.customerId
    var status by OrderTable.status
    var createdAt by OrderTable.createdAt
    var totalPrice by OrderTable.totalPrice
    var isPaid by OrderTable.isPaid

    fun toDTO(): OrderDTO {
        return OrderDTO(
            id = id.value,
            customerId = customerId,
            status = status,
            totalPrice = totalPrice,
            isPaid = isPaid
        )
    }
}