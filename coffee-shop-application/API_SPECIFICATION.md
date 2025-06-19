# Coffee Shop API Specification

## Overview
This document provides a specification for the Coffee Shop API, including endpoints, request/response formats, and business rules.

## Base URL
All API endpoints are prefixed with `/api/`.

## Authentication
Authentication is required for all endpoints. The API uses JWT tokens for authentication.

## Endpoints

### Orders

#### GET /orders
Retrieves a list of all orders.

**Requires STAFF or CUSTOMER role.**

**Response**
- Status Code: 200 OK
- Content Type: application/json
- Body: Array of Order objects

```json
[
  {
    "id": 1,
    "customerId": 1,
    "menuItems": [
      {
        "menuItem": {
          "id": 1,
          "name": "Espresso",
          "description": "Strong coffee shot",
          "price": 2.50
        },
        "quantity": 2
      }
    ],
    "totalPrice": 4.50,
    "status": "PENDING",
    "isPaid": false
  }
]
```

#### GET /orders/{id}
Retrieves a specific order by ID.

**Requires STAFF or CUSTOMER role.**

**Parameters**
- id: The ID of the order to retrieve (path parameter)

**Response**
- Status Code: 200 OK
- Content Type: application/json
- Body: Order object

```json
{
  "id": 1,
  "customerId": 1,
  "menuItems": [
    {
      "menuItem": {
        "id": 1,
        "name": "Espresso",
        "description": "Strong coffee shot",
        "price": 2.50
      },
      "quantity": 2
    }
  ],
  "totalPrice": 4.50,
  "status": "PENDING",
  "isPaid": false
}
```

**Error Responses**
- 400 Bad Request: If the ID is not a valid format
- 404 Not Found: If the order with the specified ID does not exist

#### POST /orders
Creates a new order.

**Requires STAFF or CUSTOMER role.**

**Request**
- Content Type: application/json
- Body: Order creation request

```json
{
  "customerId": 1,
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2
    }
  ]
}
```

**Response**
- Status Code: 201 Created
- Content Type: application/json
- Body: Created order

```json
{
  "id": 1,
  "customerId": 1,
  "menuItems": [
    {
      "menuItem": {
        "id": 1,
        "name": "Espresso",
        "description": "Strong coffee shot",
        "price": 2.50
      },
      "quantity": 2
    }
  ],
  "totalPrice": 4.50,
  "status": "PENDING",
  "isPaid": false
}
```

**Error Responses**
- 400 Bad Request: If the request is invalid (missing required fields, etc.)

#### PUT /orders/{id}
Updates an existing order.

**Parameters**
- id: The ID of the order to update (path parameter)

**Request**
- Content Type: application/json
- Body: Order update request

```json
{
  "status": "COMPLETED"
}
```

**Response**
- Status Code: 200 OK
- Content Type: application/json
- Body: Updated order

```json
{
  "id": 1,
  "customerId": 1,
  "menuItems": [
    {
      "menuItem": {
        "id": 1,
        "name": "Espresso",
        "description": "Strong coffee shot",
        "price": 2.50
      },
      "quantity": 2
    }
  ],
  "totalPrice": 4.50,
  "status": "COMPLETED",
  "isPaid": false
}
```

**Error Responses**
- 400 Bad Request: If the ID is not a valid format or the request is invalid
- 404 Not Found: If the order with the specified ID does not exist

## Business Rules

### Price Discount Based on Customer

Customers can have a discount percentage applied to their orders. The discount is stored in the customer record as a `discountPercent` field.

#### Discount Calculation
The discount is applied to the total price of the order using the following formula:

```
finalPrice = originalPrice * (1 - discountPercent / 100)
```

For example:
- If the original price is $10.00 and the customer has a 10% discount, the final price will be $9.00.
- If the original price is $10.00 and the customer has a 0% discount, the final price will be $10.00.

#### Discount Application
The discount is automatically applied when:
1. Retrieving an order (GET /orders/{id})
2. Creating a new order (POST /orders)
3. Updating an order (PUT /orders/{id})

The discount percentage is retrieved from the customer record associated with the user making the request.

## Data Models

### Order
- id: Long - The unique identifier for the order
- customerId: Long - The ID of the customer who placed the order
- menuItems: Array of OrderItem - The items in the order
- totalPrice: Double - The total price of the order after applying any discounts
- status: String - The status of the order (PENDING, COMPLETED, etc.)
- isPaid: Boolean - Whether the order has been paid for

### OrderItem
- menuItem: MenuItem - The menu item being ordered
- quantity: Int - The quantity of the menu item being ordered

### MenuItem
- id: Long - The unique identifier for the menu item
- name: String - The name of the menu item
- description: String - A description of the menu item
- price: Double - The price of the menu item

### Customer
- id: Long - The unique identifier for the customer
- userId: Long - The ID of the user associated with the customer
- name: String - The name of the customer
- discountPercent: Double - The discount percentage for the customer
