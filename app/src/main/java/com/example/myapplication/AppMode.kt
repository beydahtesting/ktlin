package com.example.myapplication

object AppMode {
    private var onlineMode: Boolean = true
    fun isOnlineMode(): Boolean = onlineMode
    fun setOnlineMode(mode: Boolean) {
        onlineMode = mode
    }
}
