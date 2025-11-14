package ru.vood.advanced.deprecated.ksp.util

data class DeprecatedAnnotationDto(
    val deletedInVersionValue: String?,
    val removalDateValue: String?,
    val messageValue: String
)
