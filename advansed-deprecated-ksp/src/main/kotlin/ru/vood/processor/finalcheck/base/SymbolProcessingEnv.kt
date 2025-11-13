package ru.vood.processor.finalcheck.base

import com.google.devtools.ksp.processing.KSPLogger

object SymbolProcessingEnv {
    private val loggerLocal = ThreadLocal<KSPLogger>()

    var logger: KSPLogger
        get() = loggerLocal.get()
        set(value) {
            loggerLocal.set(value)
        }

    fun resetLogger() {
        loggerLocal.set(null)
    }
}
