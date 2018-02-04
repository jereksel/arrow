package arrow.ap.tests

import arrow.optics.OptikalProcessor

class OpticsTest: APTest() {

    init {

        testProcessor(AnnotationProcessor(
                name = "@optionals test",
                sourceFile = "OptionalOptics.java",
                destFiles = listOf("Optional.kt"),
                processor = OptikalProcessor()
        ))

        testProcessor(AnnotationProcessor(
                name = "@optionals sealed test",
                sourceFile = "OptionalSealed.java",
                processor = OptikalProcessor(),
                errorMessage = "It can only be used on data class."
        ))

    }

}