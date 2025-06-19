package com.motycka.edu.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val customerId: Long,
    val items: List<OrderItemRequest>
)
