package com.motycka.edu.order

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val logger = KotlinLogging.logger {}

private const val ORDER_NOT_FOUND = "Order not found"
private const val INVALID_ID = "Invalid ID format"

fun Route.orderRoutes(
    orderService: OrderService,
    basePath: String
) {
    route("$basePath/orders") {
        authenticate("auth-jwt") {
            get {
                val orders = orderService.getAllOrders()
                call.respond(HttpStatusCode.OK, orders)
            }

            post {
                try {
                    val request = call.receive<OrderRequest>()
                    val newOrder = orderService.createOrder(request)
                    call.respond(HttpStatusCode.Created, newOrder)
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
                } catch (e: Exception) {
                    logger.error(e) { "Error creating order" }
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }

            route("/{id}") {
                get {
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                        return@get
                    }

                    orderService.getOrderById(id)?.let { order ->
                        call.respond(HttpStatusCode.OK, order)
                    } ?: call.respond(HttpStatusCode.NotFound, ORDER_NOT_FOUND)
                }

                put {
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                        return@put
                    }

                    try {
                        val request = call.receive<OrderUpdateRequest>()
                        orderService.updateOrderStatus(id, request.status)?.let { updatedOrder ->
                            call.respond(HttpStatusCode.OK, updatedOrder)
                        } ?: call.respond(HttpStatusCode.NotFound, ORDER_NOT_FOUND)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                    }
                }
            }
        }
    }
}