package arrow.ap.tests

import io.kotlintest.specs.StringSpec

import arrow.optics.OptikalProcessor
import arrow.syntax.monoid.empty
import com.google.common.collect.ImmutableList
import com.google.common.io.Files
import com.google.testing.compile.Compilation
import com.google.testing.compile.Compilation.Status.*
import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import io.kotlintest.matchers.shouldBe
import java.io.File
import java.nio.file.Paths

open class APTest: StringSpec() {

    fun testProcessor(
        vararg processor: AnnotationProcessor
    ) {

        processor.forEach { (name, source, dest, proc, error) ->

            val stubs = Paths.get("", "build", "tmp", "kapt3", "stubs", "main", "arrow", "ap", "objects").toFile()
            val expectedDir = Paths.get("", "src", "test", "resources", "arrow", "ap", "objects").toFile()

            if (dest.isEmpty() && error == null) {
                throw Exception("Destination file and error cannot be both null")
            }

            if (dest.isNotEmpty() && error != null) {
                throw Exception("Destination file and error cannot be both nonnull")
            }

            name {

                val temp = Files.createTempDir()

                val stub = File(stubs, source).toURI().toURL()

                val compilation = javac()
                        .withProcessors(proc)
                        .withOptions(ImmutableList.of("-Akapt.kotlin.generated=$temp"))
                        .compile(JavaFileObjects.forResource(stub))

                if (error != null) {

                    assertThat(compilation)
                            .failed()
                    assertThat(compilation)
                            .hadErrorContaining(error)

                    return@name
                }

                assertThat(compilation)
                        .succeeded()

                temp.listFiles().size shouldBe dest.size

                val expected = dest.map { File(expectedDir,it).readText() }.toSet()
                val actual = temp.listFiles().map { it.readText() }.toSet()

//                val expected = File(expectedDir, dest).readText()
//                val actual = temp.listFiles()[0].readText()

                expected shouldBe actual

            }

        }



    }

}
