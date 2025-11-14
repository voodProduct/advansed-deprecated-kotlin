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

        return compareVersions(current, target)
    }

    private fun compareVersions(current: Version, target: Version): Boolean {
        // Сравниваем major
        when {
            current.major > target.major -> return true
            current.major < target.major -> return false
        }

        // major равны, сравниваем minor
        val currentMinor = current.minor ?: 0
        val targetMinor = target.minor ?: 0
        when {
            currentMinor > targetMinor -> return true
            currentMinor < targetMinor -> return false
        }

        // minor равны, сравниваем patch
        val currentPatch = current.patch ?: 0
        val targetPatch = target.patch ?: 0
        when {
            currentPatch > targetPatch -> return true
            currentPatch < targetPatch -> return false
        }

        // Все числовые компоненты равны, сравниваем суффиксы
        return compareSuffix(current.suffix, target.suffix)
    }

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