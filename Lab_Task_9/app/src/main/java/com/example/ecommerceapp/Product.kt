package com.example.ecommerceapp

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val rating: Float,
    val category: String,
    val imageRes: Int,
    val inCart: Boolean = false
)