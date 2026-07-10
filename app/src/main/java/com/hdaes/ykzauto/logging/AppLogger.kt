package com.hdaes.ykzauto.logging

interface AppLogger {
    fun debug(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

object NoOpAppLogger : AppLogger {
    override fun debug(tag: String, message: String) = Unit

    override fun error(tag: String, message: String, throwable: Throwable?) = Unit
}
