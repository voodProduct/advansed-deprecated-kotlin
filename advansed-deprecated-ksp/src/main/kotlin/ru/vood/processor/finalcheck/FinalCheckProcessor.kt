package ru.vood.processor.finalcheck

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import ru.vood.processor.finalcheck.base.BaseSymbolProcessor

class FinalCheckProcessor(environment: SymbolProcessorEnvironment) : BaseSymbolProcessor(environment) {

    private val requiredModifiersParamName = "requiredModifiers"
    private val prohibitedModifiersParamName = "prohibitedModifiers"
    private val annotationParamName = "annotation"
    override fun processRound(resolver: Resolver): List<KSAnnotated> {
        // вычитка внешних настроек
        kspLogger.warn("Run with param $requiredModifiersParamName => ${environment.options[requiredModifiersParamName]}")
        kspLogger.warn("Run with param $prohibitedModifiersParamName => ${environment.options[prohibitedModifiersParamName]}")
        kspLogger.warn("Run with param $annotationParamName => ${environment.options[annotationParamName]}")

        val requiredModifiersParam = environment.options[requiredModifiersParamName]?:"PUBLIC;"
        val prohibitedModifiersParam = environment.options[prohibitedModifiersParamName]?:"FINAL;"
        val annotationParam = environment.options[annotationParamName]?:"kotlin.Deprecated;ru.vood.test.MyAnnotation"

        // валидация внешних настроек
        val requiredModifiers = extractParam(requiredModifiersParam) { paramStr ->
            kotlin.runCatching { Modifier.valueOf(paramStr) }.getOrElse { err ->
                error(
                    "for Parameter $requiredModifiersParamName No enum constant ${Modifier::class.simpleName}.$paramStr allow values are -> ${
                        Modifier.values().map { it.name }
                    }"
                )
            }
        }

        val prohibitedModifiers = extractParam(prohibitedModifiersParam) { paramStr ->
            kotlin.runCatching { Modifier.valueOf(paramStr) }.getOrElse { err ->
                error(
                    "for Parameter $prohibitedModifiersParamName No enum constant ${Modifier::class.simpleName}.$paramStr allow values are -> ${
                        Modifier.values().map { it.name }
                    }"
                )
            }

        }


        val annotation = extractParam(annotationParam) { it }

        kspLogger.warn("""Extracted parameters annotation => $annotation
            |prohibitedModifiers => $prohibitedModifiers
            |requiredModifiers => $requiredModifiers
        """.trimMargin())

        require(annotation.isNotEmpty()) { kspLogger.error("set param $annotationParamName") }
        require(requiredModifiers.isNotEmpty() || prohibitedModifiers.isNotEmpty()) { kspLogger.error("set param $requiredModifiersParamName or $prohibitedModifiersParamName") }


        // сбор аннотированных объектов
        val annotatedObjectKotlinObjectList = annotation.flatMap { annotation ->
            resolver.getSymbolsWithAnnotation(checkNotNull(annotation)).toList()
        }
        // сбор мета информации для генерации
        // анализ или кодогенерация
        annotatedObjectKotlinObjectList
            .forEach {ksAnno ->
                when(ksAnno){
                    is KSClassDeclaration ->check(requiredModifiers, ksAnno.modifiers, ksAnno, prohibitedModifiers, "Class")
                    is KSFunctionDeclaration ->check(requiredModifiers, ksAnno.modifiers, ksAnno, prohibitedModifiers, "Method")
                }
            }
        return listOf()
    }

    private fun check(
        requiredModifiers: List<Modifier>,
        modifiers: Set<Modifier>,
        ksAnno: KSAnnotated,
        prohibitedModifiers: List<Modifier>,
        objectName: String
    ) {
        val notExistModifier = requiredModifiers.filter { modifier ->
            !modifiers.contains(modifier)
        }


        if (notExistModifier.isNotEmpty()) {
            kspLogger.error("$objectName must contains Modifiers $notExistModifier. Current Modifiers are $modifiers", ksAnno)
        }

        val prohibitedModifier = prohibitedModifiers.filter { modifier ->
            modifiers.contains(modifier)
        }

        if (prohibitedModifier.isNotEmpty()) {
            kspLogger.error("$objectName contains prohibitedModifier Modifiers $prohibitedModifier", ksAnno)
        }
    }

    private fun <T> extractParam(paramValue: String?, extractor: (String) -> T): List<T> {

        return paramValue?.let {
            it
                .split(";")
                .map { param -> param.trim() }
                .filter {  param -> param.isNotEmpty() }
                .map { paramStr -> extractor(paramStr) }
        } ?: listOf()
    }
}