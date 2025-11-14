package ru.vood.advanced.deprecated.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import ru.vood.advanced.deprecated.ksp.base.BaseSymbolProcessor
import ru.vood.advanced.deprecated.ksp.util.VersionComparator
import ru.vood.advansed.deprecated.DeprecatedWithRemoval
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdvancedDeprecatedProcessor(environment: SymbolProcessorEnvironment) : BaseSymbolProcessor(environment) {

    private val currentVersion: String? by lazy {
        environment.options[currentVersionName]
    }

    private val currentVersionName = "currentVersion"
    override fun processRound(resolver: Resolver): List<KSAnnotated> {
        // вычитка внешних настроек
        kspLogger.warn("Run with param $currentVersionName => $currentVersion")

        val annotatedObjectKotlinObjectList =
            resolver.getSymbolsWithAnnotation(DeprecatedWithRemoval::class.java.canonicalName)
        val symbols = annotatedObjectKotlinObjectList.filter { !it.validate() }.toList()

        symbols
            .filter { it.validate() }
            .forEach { symbol ->
                checkRemovalDate(symbol)
            }

        return symbols
    }

    private fun checkRemovalDate(symbol: KSAnnotated) {
        symbol.annotations
            .firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() ==
                        DeprecatedWithRemoval::class.java.canonicalName
            }
            ?.let { annotation ->
                annotation.arguments
                    .firstOrNull { it.name?.asString() == "removalDate" }
                    ?.value as? String
            }?.let { removalDate ->
                if (removalDate.isNotEmpty() && isDatePassed(removalDate, symbol)) {
                    environment.logger.error(
                        "Element $symbol should be removed - removal date $removalDate has passed",
                        symbol
                    )
                }
            }
    }


    private fun isDatePassed(dateString: String, annotated: KSAnnotated): Boolean {
        return try {

            val formatter = DateTimeFormatter.ofPattern(patternRemovalDate)
            val removalDate = LocalDate.parse(dateString, formatter)
            removalDate.isBefore(LocalDate.now())
        } catch (e: Exception) {
            environment.logger.error(
                "Attribute '${DeprecatedWithRemoval::removalDate.name}' in annotation '${DeprecatedWithRemoval::class.simpleName}' should have format $patternRemovalDate",
                annotated
            )
            false
        }
    }

    private fun isVersionReached(targetVersion: String, currentVersion: String): Boolean {
        return try {
            VersionComparator.isCurrentVersionGreaterOrEqual(currentVersion, targetVersion)
        } catch (e: Exception) {
            environment.logger.warn("Invalid version format: $targetVersion", null)
            false
        }
    }

    companion object {
        const val patternRemovalDate = "yyyy-MM-dd"
    }
}