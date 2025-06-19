package com.motycka.edu.order

import com.motycka.edu.menu.InternalMenuService
import com.motycka.edu.menu.MenuItemDTO
import com.motycka.edu.menu.MenuItemResponse

class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val internalMenuService: InternalMenuService
) {

    suspend fun getAllOrders(): List<OrderResponse> {
        val orderDTOs = orderRepository.selectAll()
        return orderDTOs.map { buildOrderResponse(it) }
    }

    suspend fun getOrderById(id: OrderId): OrderResponse? {
        return orderRepository.selectById(id)?.let { buildOrderResponse(it) }
    }

    suspend fun createOrder(request: OrderRequest): OrderResponse {
        val menuItemIds = request.items.map { it.menuItemId }.toSet()
        val menuItems = internalMenuService.getMenuItems(menuItemIds)
        val menuItemsMap = menuItems.associateBy { it.id!! }

        if (menuItems.size != menuItemIds.size) {
            val foundIds = menuItems.map { it.id }.toSet()
            val notFoundIds = menuItemIds - foundIds
            throw NoSuchElementException("Menu item(s) with ID(s) $notFoundIds not found.")
        }

        // Assuming 0 discount for now, as customer details are not fetched.
        val totalPrice = PriceCalculator.calculatePrice(request.items, menuItemsMap, 0.0)

        val orderToCreate = OrderDTO(
            id = null,
            customerId = request.customerId,
            status = OrderStatus.PENDING,
            totalPrice = totalPrice,
            isPaid = false
        )

        val createdOrderDto = orderRepository.create(orderToCreate, request.items)
        return buildOrderResponse(createdOrderDto)
    }

    suspend fun updateOrderStatus(id: OrderId, status: OrderStatus): OrderResponse? {
        return orderRepository.updateStatus(id, status)?.let { buildOrderResponse(it) }
    }

    private suspend fun buildOrderResponse(orderDto: OrderDTO): OrderResponse {
        val orderId = orderDto.id ?: throw IllegalStateException("Order DTO must have an ID to build a response")
        val orderItemDTOs = orderItemRepository.selectByOrderId(orderId)

        val orderItemResponses = if (orderItemDTOs.isNotEmpty()) {
            val menuItemIds = orderItemDTOs.map { it.menuItemId }.toSet()
            val menuItems = internalMenuService.getMenuItems(menuItemIds)
            val menuItemsMap = menuItems.associateBy { it.id!! }

            orderItemDTOs.map { orderItemDto ->
                val menuItemDto = menuItemsMap[orderItemDto.menuItemId]
                    ?: throw NoSuchElementException("Menu item with ID ${orderItemDto.menuItemId} not found for order $orderId.")
                OrderItemResponse(
                    menuItem = menuItemDto.toResponse(),
                    quantity = orderItemDto.quantity
                )
            }
        } else {
            emptyList()
        }

        return OrderResponse(
            id = orderId,
            customerId = orderDto.customerId,
            menuItems = orderItemResponses,
            totalPrice = orderDto.totalPrice,
            status = orderDto.status,
            isPaid = orderDto.isPaid
        )
    }

    private fun MenuItemDTO.toResponse(): MenuItemResponse {
        return MenuItemResponse(
            id = requireNotNull(id) { "MenuItemDTO id cannot be null" },
            name = name,
            description = description,
            price = price
        )
    }
}