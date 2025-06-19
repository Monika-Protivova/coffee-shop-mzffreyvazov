package com.motycka.edu.order

import org.jetbrains.exposed.sql.transactions.transaction

class OrderRepositoryImpl : OrderRepository {
    override fun selectAll(): List<OrderDTO> = transaction {
        OrderDAO.all().map { it.toDTO() }
    }

    override fun selectById(id: OrderId): OrderDTO? = transaction {
        OrderDAO.findById(id)?.toDTO()
    }

    override fun create(order: OrderDTO, items: List<OrderItemRequest>): OrderDTO = transaction {
        val newOrder = OrderDAO.new {
            this.customerId = order.customerId
            this.status = order.status
            this.totalPrice = order.totalPrice
            this.isPaid = order.isPaid
        }

        items.forEach { itemRequest ->
            OrderItemDAO.new {
                this.orderId = newOrder.id.value
                this.menuItemId = itemRequest.menuItemId
                this.quantity = itemRequest.quantity
            }
        }

        newOrder.toDTO()
    }

    override fun updateStatus(id: OrderId, status: OrderStatus): OrderDTO? = transaction {
        OrderDAO.findById(id)?.apply {
            this.status = status
        }?.toDTO()
    }

    override fun update(order: OrderDTO): OrderDTO = transaction {
        val orderId = order.id ?: throw IllegalArgumentException("Order ID must not be null for an update operation.")
        val existingOrder = OrderDAO.findById(orderId)
            ?: throw NoSuchElementException("Order with ID $orderId not found.")

        existingOrder.apply {
            this.customerId = order.customerId
            this.status = order.status
            this.totalPrice = order.totalPrice
            this.isPaid = order.isPaid
        }

        existingOrder.toDTO()
    }
}