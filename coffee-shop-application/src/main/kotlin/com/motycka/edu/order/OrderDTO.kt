package com.motycka.edu.order


data class OrderDTO(
    val id: OrderId?,
    val customerId: Long,
    val status: OrderStatus,
    val totalPrice: Double, //added for total price
    val isPaid: Boolean // added for payment status
)
