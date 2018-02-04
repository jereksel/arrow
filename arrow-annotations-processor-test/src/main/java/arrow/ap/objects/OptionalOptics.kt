package arrow.ap.objects

import arrow.Optics
import arrow.core.Option

@Optics
data class OptionalOptics(val field: String, val nullable: String?, val option: Option<String>)