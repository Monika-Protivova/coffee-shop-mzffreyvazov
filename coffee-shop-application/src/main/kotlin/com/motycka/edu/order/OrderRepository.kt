package com.motycka.edu.order

interface OrderRepository {

    fun selectAll(): List<OrderDTO>

    fun selectById(id: OrderId): OrderDTO?

//    fun create(order: OrderDTO): OrderDTO

    fun create(order: OrderDTO, items: List<OrderItemRequest>): OrderDTO

    fun updateStatus(id: OrderId, status: OrderStatus): OrderDTO?

    fun update(order: OrderDTO): OrderDTO?

}

