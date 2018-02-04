package arrow

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(SOURCE)
@Target(CLASS)
@Deprecated("Use @Optics")
annotation class lenses

@Retention(SOURCE)
@Target(CLASS)
@Deprecated("Use @Optics")
annotation class prisms

@Retention(SOURCE)
@Target(CLASS)
@Deprecated("Use @Optics")
annotation class isos

@Retention(SOURCE)
@Target(CLASS)
@Deprecated("Use @Optics")
annotation class optionals

/**
 * By default list is empty - it means "everything that is correct for given class"
 */
@Retention(SOURCE)
@Target(CLASS)
annotation class Optics(val targets: Array<OpticTarget> = [])

enum class OpticTarget {
    ISO, LENS, PRISM, OPTIONAL
}