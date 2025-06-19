// coffee-shop-application/src/main/kotlin/com/motycka/edu/order/PriceCalculator.kt
package com.motycka.edu.order

import com.motycka.edu.menu.MenuItemDTO
import com.motycka.edu.menu.MenuItemId
import kotlin.math.round

object PriceCalculator {

    fun calculatePrice(
        orderItems: List<OrderItemRequest>,
        menuItems: Map<MenuItemId, MenuItemDTO>,
        discountInPercent: Double
    ): Double {
        val originalPrice = orderItems.sumOf { orderItem ->
            val menuItem = menuItems[orderItem.menuItemId]
                ?: throw IllegalArgumentException("Menu item with id ${orderItem.menuItemId} not found for price calculation.")
            menuItem.price * orderItem.quantity
        }

        val finalPrice = originalPrice * (1 - discountInPercent / 100)

        return round(finalPrice * 100) / 100.0
    }
}