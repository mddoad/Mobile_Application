package com.example.photogalleryapp

data class Photo(
    val id: Long,
    val resourceId: Int,
    val title: String,
    val category: String,
    var isSelected: Boolean = false
)