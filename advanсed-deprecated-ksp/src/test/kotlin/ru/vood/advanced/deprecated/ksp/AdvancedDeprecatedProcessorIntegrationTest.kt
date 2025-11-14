package ru.vood.advanced.deprecated.ksp

//import com.google.devtools.ksp.testing.KspTestEnvironment
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AdvancedDeprecatedProcessorIntegrationTest {

    @TempDir
    lateinit var tempDir: File

//    @Test
//    fun `should generate errors for deprecated elements`() {
//        // Given
//        val testEnv = KspTestEnvironment.create()
//        val sourceCode = """
//            package test
//
//            import ru.vood.advansed.deprecated.DeprecatedWithRemoval
//
//            @DeprecatedWithRemoval(
//                removalDate = "2020-01-01",
//                deletedInVersion = "1.0.0"
//            )
//            class DeprecatedClass
//        """.trimIndent()
//
//        // When
//        val result = testEnv.runProcessor(
//            processor = AdvancedDeprecatedProcessor(testEnv.environment),
//            sources = listOf(testEnv.createSourceFile("test.kt", sourceCode)),
//            options = mapOf("currentVersion" to "2.0.0")
//        )
//
//        // Then
//        assertTrue(result.errors.isNotEmpty(), "Should generate errors for deprecated elements")
//    }
}