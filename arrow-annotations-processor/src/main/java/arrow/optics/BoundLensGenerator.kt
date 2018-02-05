package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class BoundLensGenerator(
        private val annotatedList: Collection<AnnotatedOptic>,
        private val generatedDir: File
) {

    private val lens = "arrow.optics.Lens"
    private val boundLens = "arrow.optics.BoundLens"

    fun generate() = annotatedList.map(this::processElement)
            .map { (element, funs) ->
                "${boundAnnotationClass.simpleName}.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
                        funs.joinToString(prefix = "package ${element.classData.`package`.escapedClassName}\n\n", separator = "\n")
            }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

    private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

    private fun processElement(annotatedOptic: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
            annotatedOptic to annotatedOptic.targets.map { variable ->
                val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
                val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
                val targetClassName = variable.fullName
                val targetName = variable.paramName

                """
                    |val <T> $boundLens<T, $sourceClassName>.$targetName: $boundLens<T, $targetClassName> get() = this.compose($sourceName${targetName.toUpperCamelCase()}())
                    """.trimMargin()
            }

}