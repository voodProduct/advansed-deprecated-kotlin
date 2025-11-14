package ru.vood.advanced.deprecated.ksp.util

import ru.vood.advansed.deprecated.DeprecatedWithRemoval

object VersionComparator {

    fun parseVersion(version: String): Version {
        val pattern = Regex(DeprecatedWithRemoval.VERSION_PATTERN)
        val match = pattern.find(version) ?: throw IllegalArgumentException("Invalid version format: $version")

        val (major, minor, patch, suffix) = match.destructured
        return Version(
            major.toInt(),
            minor.toInt(),
            patch.toInt(),
            suffix.removePrefix("-")
        )
    }

    fun isCurrentVersionGreaterOrEqual(currentVersion: String, targetVersion: String): Boolean {
        val current = parseVersion(currentVersion)
        val target = parseVersion(targetVersion)

        return when {
            current.major != target.major -> current.major > target.major
            current.minor != target.minor -> current.minor > target.minor
            current.patch != target.patch -> current.patch > target.patch
            else -> true // Версии равны
        }
    }

    data class Version(
        val major: Int,
        val minor: Int,
        val patch: Int,
        val suffix: String = ""
    )
}