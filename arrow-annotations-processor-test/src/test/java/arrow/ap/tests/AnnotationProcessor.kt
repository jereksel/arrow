package arrow.ap.tests

import javax.annotation.processing.Processor

data class AnnotationProcessor(
        val name: String,
        val sourceFile: String,
        val destFiles: List<String> = listOf(),
        val processor: Processor,
        val errorMessage: String? = null
)