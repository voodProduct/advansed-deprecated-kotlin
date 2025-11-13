package ru.vood.processor.finalcheck.base

import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


internal val logWriter: Writer by lazy {
    val fos = Files.newOutputStream(
        Path.of(System.getProperty("user.home"), "log.txt").toAbsolutePath(),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.APPEND
    )

    OutputStreamWriter(fos)
}

internal val lock = ReentrantLock()

internal fun logToFile(message: String) = lock.withLock {
    logWriter.write("${LocalDateTime.now()} $message\n\n")
    logWriter.flush()
}