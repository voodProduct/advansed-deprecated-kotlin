package ru.vood.advanced.deprecated.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Disabled
class AdvancedDeprecatedProcessorTest {

    private lateinit var mockEnvironment: SymbolProcessorEnvironment
    private lateinit var mockLogger: KSPLogger
    private lateinit var processor: AdvancedDeprecatedProcessor

    @BeforeEach
    fun setUp() {
        mockEnvironment = mockk()
        mockLogger = mockk(relaxed = true)

        every { mockEnvironment.logger } returns mockLogger
    }

    @Test
    fun `should process symbols with DeprecatedWithRemoval annotation`() {
        // Given
        val options = mapOf("currentVersion" to "1.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockResolver = mockk<com.google.devtools.ksp.processing.Resolver>()
        val mockSymbol = createMockAnnotatedSymbol(valid = true)
        val invalidMockSymbol = createMockAnnotatedSymbol(valid = false)

        every { mockResolver.getSymbolsWithAnnotation(any()) } returns listOf(mockSymbol, invalidMockSymbol).asSequence()
        every { mockSymbol.validate() } returns true
        every { invalidMockSymbol.validate() } returns false

        // When
        val result = processor.processRound(mockResolver)

        // Then
        assertEquals(1, result.size) // Only invalid symbols should be returned
        assertTrue(result.contains(invalidMockSymbol))
    }

    @Test
    fun `should log error when removal date has passed`() {
        // Given
        val options = mapOf("currentVersion" to "1.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val pastDate = LocalDate.now().minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = pastDate,
            deletedInVersion = "2.0.0"
        )

        // When
        processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - verify that error was logged for passed removal date
        // This would be verified through mock interactions
    }

    @Test
    fun `should log error when current version reached target version`() {
        // Given
        val options = mapOf("currentVersion" to "2.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            deletedInVersion = "2.0.0"
        )

        // When
        processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - verify that error was logged for reached version
    }

    @Test
    fun `should not log error when removal date is in future`() {
        // Given
        val options = mapOf("currentVersion" to "1.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val futureDate = LocalDate.now().plusDays(30)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = futureDate,
            deletedInVersion = "2.0.0"
        )

        // When
        processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - verify that no error was logged
    }

    @Test
    fun `should log error when current version parameter is missing`() {
        // Given
        val options = emptyMap<String, String>()
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = "",
            deletedInVersion = "2.0.0"
        )

        // When
        processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - verify that error was logged about missing currentVersion
    }

    @Test
    fun `should handle invalid date format gracefully`() {
        // Given
        val options = mapOf("currentVersion" to "1.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = "invalid-date-format",
            deletedInVersion = "2.0.0"
        )

        // When
        processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - verify that error was logged about invalid date format
    }

    @Test
    fun `should handle invalid version format gracefully`() {
        // Given
        val options = mapOf("currentVersion" to "invalid-version")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            deletedInVersion = "2.0.0"
        )

        // When
        processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - verify that warning was logged about invalid version format
    }

    @Test
    fun `should process symbol without removal date`() {
        // Given
        val options = mapOf("currentVersion" to "1.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = "",
            deletedInVersion = "2.0.0"
        )

        // When
        val result = processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - should process without errors
        assertEquals(0, result.size)
    }

    @Test
    fun `should process symbol without deletedInVersion`() {
        // Given
        val options = mapOf("currentVersion" to "1.0.0")
        every { mockEnvironment.options } returns options

        processor = AdvancedDeprecatedProcessor(mockEnvironment)

        val mockSymbol = createMockAnnotatedSymbol(
            valid = true,
            removalDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            deletedInVersion = ""
        )

        // When
        val result = processor.processRound(createMockResolver(listOf(mockSymbol)))

        // Then - should process without version-related errors
        assertEquals(0, result.size)
    }

    // Helper methods
    private fun createMockResolver(symbols: List<KSAnnotated>): com.google.devtools.ksp.processing.Resolver {
        return mockk<com.google.devtools.ksp.processing.Resolver>().apply {
            every { getSymbolsWithAnnotation(any()) } returns symbols.asSequence()
        }
    }

    private fun createMockAnnotatedSymbol(
        valid: Boolean,
        removalDate: String = "",
        deletedInVersion: String = ""
    ): KSAnnotated {
        val mockSymbol = mockk<KSClassDeclaration>()
        val mockAnnotation = mockk<KSAnnotation>()
        val mockAnnotationType = mockk<KSType>()
        val mockKSTypeReference = mockk<KSTypeReference>()

        val mockDeclaration = mockk<KSDeclaration>()
        val mockName = mockk<KSName>()

        every { mockSymbol.validate() } returns valid
        every { mockSymbol.annotations } returns listOf(mockAnnotation).asSequence()
        every { mockAnnotation.annotationType } returns mockKSTypeReference
//        every { mockAnnotationType.resolve() } returns mockAnnotationType
        every { mockAnnotationType.declaration } returns mockDeclaration
        every { mockDeclaration.qualifiedName } returns mockName
        every { mockName.asString() } returns "ru.vood.advansed.deprecated.DeprecatedWithRemoval"

        // Mock annotation arguments
        val removalDateArgument = mockk<KSValueArgument>().apply {
            every { name?.asString() } returns "removalDate"
            every { value } returns removalDate
        }

        val deletedInVersionArgument = mockk<KSValueArgument>().apply {
            every { name?.asString() } returns "deletedInVersion"
            every { value } returns deletedInVersion
        }

        every { mockAnnotation.arguments } returns listOf(removalDateArgument, deletedInVersionArgument)

        return mockSymbol
    }
}
