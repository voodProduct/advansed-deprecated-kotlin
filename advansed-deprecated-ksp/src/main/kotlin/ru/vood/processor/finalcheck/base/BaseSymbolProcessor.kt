package ru.vood.processor.finalcheck.base

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.NonExistLocation
import java.io.PrintWriter
import java.io.StringWriter

private const val debug = false

abstract class BaseSymbolProcessor(protected val environment: SymbolProcessorEnvironment) : SymbolProcessor {


    val kspLogger: KSPLogger = if (!debug) environment.logger else object : KSPLogger {

        override fun error(message: String, symbol: KSNode?) {
            environment.logger.error(message, symbol)
            writeMessage("error", message, symbol)
        }

        override fun exception(e: Throwable) {
            environment.logger.exception(e)
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            pw.flush()
            sw.flush()
            writeMessage("exception", sw.toString(), null)
        }

        override fun info(message: String, symbol: KSNode?) {
            writeMessage("info", message, symbol)
        }

        override fun logging(message: String, symbol: KSNode?) {
            writeMessage("logging", message, symbol)
        }

        override fun warn(message: String, symbol: KSNode?) {
            writeMessage("warn", message, symbol)
        }

        private fun writeMessage(level: String, message: String, symbol: KSNode?) {

            val msg = when (val location = symbol?.location) {
                is FileLocation -> "[$level] ${location.filePath}:${location.lineNumber}\n$message"
                is NonExistLocation, null -> "[$level] $message"
            }

            logToFile(msg)
        }
    }


    final override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            SymbolProcessingEnv.logger = kspLogger
            return processRound(resolver)
        } finally {
            SymbolProcessingEnv.resetLogger()
        }
    }

    abstract fun processRound(resolver: Resolver): List<KSAnnotated>
}