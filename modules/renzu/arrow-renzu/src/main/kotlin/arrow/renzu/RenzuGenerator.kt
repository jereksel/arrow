package arrow.renzu

import arrow.core.Try
import arrow.core.getOrDefault
import arrow.data.ListK
import arrow.higherkind
import com.google.common.reflect.ClassPath

fun main(args: Array<String>) {

    val classes = ClassPath.from(ListK::class.java.classLoader).allClasses
            .asSequence()
            .filter { it.packageName.startsWith("arrow") }
//            .map { it.load() }
//            .map { it to it.getAnnotationsByType(higherkind::class.java).toList() }
//            .filter { it.second.isNotEmpty() }
//            .map { it.first }
            .filter { it.packageName.contains("typeclass") }
            .filterNot { it.toString().contains("$") }
            .filterNot { it.toString().contains("_") }
            .filter { Try {it.load().isInterface}.getOrDefault { false } }
            .toList()
            .sortedBy { it.toString() }
//            .map { Try { it.kotlin.annotations.any { it.javaClass == higherkind::class.java } } }
//            .mapNotNull { it.getOrDefault { null } }
//            .filter { it }

    println(classes)

}