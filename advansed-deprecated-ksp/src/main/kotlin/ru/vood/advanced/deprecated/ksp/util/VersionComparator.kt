package ru.vood.advanced.deprecated.ksp.util

object VersionComparator {

    fun parseVersion(version: String): Version {
        val pattern = Regex(VERSION_PATTERN)
        val match = pattern.find(version) ?: throw IllegalArgumentException("Invalid version format: $version")

        val (major, minor, patch, suffix) = match.destructured
        return Version(
            major = major.toIntOrNull() ?: throw IllegalArgumentException("Major version is required"),
            minor = minor.takeIf { it.isNotEmpty() }?.toIntOrNull(),
            patch = patch.takeIf { it.isNotEmpty() }?.toIntOrNull(),
            suffix = suffix.takeIf { it.isNotEmpty() }
        )
    }

    fun isCurrentVersionGreaterOrEqual(currentVersion: String, targetVersion: String): Boolean {
        val current = parseVersion(currentVersion)
        val target = parseVersion(targetVersion)

        return when {
            !compareRequiredInt(current.major, target.major, ::greater) -> false
            !compareOptionalInt(current.minor, target.minor, ::greater) -> false
            !compareOptionalInt(current.patch, target.patch, ::greater) -> false
            else -> compareSuffix(current.suffix, target.suffix)
        }
    }

    private fun compareRequiredInt(
        current: Int,
        target: Int,
        comparator: (Int, Int) -> Boolean
    ): Boolean {
        return comparator(current, target) || current == target
    }

    private fun compareOptionalInt(
        current: Int?,
        target: Int?,
        comparator: (Int, Int) -> Boolean
    ): Boolean {
        return when {
            current == null && target == null -> true // оба null - продолжаем сравнение
            current == null -> false // текущая версия не определена, а целевая есть
            target == null -> true // целевая версия не определена, а текущая есть
            else ->  current >= target
        }
    }

    private fun greater(a: Int, b: Int): Boolean = a > b

    private fun compareSuffix(current: String?, target: String?): Boolean {
        return when {
            current == null && target == null -> true // обе версии без суффикса
            current == null -> true // текущая версия без суффикса (стабильная) больше чем с суффиксом
            target == null -> false // целевая версия без суффикса (стабильная) больше чем текущая с суффиксом
            else -> current >= target // лексикографическое сравнение суффиксов
        }
    }

    data class Version(
        val major: Int,
        val minor: Int?,
        val patch: Int?,
        val suffix: String? = null
    )

    const val VERSION_PATTERN = """^(\d+)(?:\.(\d*))?(?:\.(\d*))?([._-]?[a-zA-Z0-9.+]+)?$"""

}