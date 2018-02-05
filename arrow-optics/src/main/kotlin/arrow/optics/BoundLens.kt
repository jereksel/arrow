package arrow.optics

import arrow.core.Option

interface BoundLensState<T, S> {
    fun <R> compose(optional: Optional<S, R>): BoundLensState<T, R>
    fun <R> compose(lens: Lens<S, R>): BoundLensState<T, R>
    fun set(initial: T, value: S): T
    fun modify(initial: T, f: (S) -> S): T
}

class OptionalLensState<T, S>(
        val optional: Optional<T, S>
) : BoundLensState<T, S> {

    override fun <R> compose(optional: Optional<S, R>): BoundLensState<T, R> = OptionalLensState(this.optional + optional)

    override fun <R> compose(lens: Lens<S, R>): BoundLensState<T, R> = OptionalLensState(this.optional + lens)

    override fun set(initial: T, value: S) = optional.set(initial, value)

    override fun modify(initial: T, f: (S) -> S) = optional.modify(initial, f)

}

class LensLensState<T, S>(
        val lens: Lens<T, S>
) : BoundLensState<T, S> {

    override fun <R> compose(optional: Optional<S, R>): BoundLensState<T, R> = OptionalLensState(lens + optional)

    override fun <R> compose(lens: Lens<S, R>): BoundLensState<T, R> = LensLensState(this.lens + lens)

    override fun set(initial: T, value: S) = lens.set(initial, value)

    override fun modify(initial: T, f: (S) -> S) = lens.modify(initial, f)
}

class InitialLensState<T> : BoundLensState<T, T> {
    override fun <R> compose(optional: Optional<T, R>): BoundLensState<T, R> = OptionalLensState(optional)

    override fun <R> compose(lens: Lens<T, R>): BoundLensState<T, R> = LensLensState(lens)

    override fun set(initial: T, value: T) = value

    override fun modify(initial: T, f: (T) -> T) = f(initial)

}

data class BoundLens<T, S>(
    val state: BoundLensState<T, S>,
    val obj: T
) {

    fun <R> compose(optional: Optional<S, R>): BoundLens<T, R> = BoundLens(state.compose(optional), obj)

    fun <R> compose(lens: Lens<S, R>): BoundLens<T, R> = BoundLens(state.compose(lens), obj)

    fun set(value: S): T = state.set(obj, value)

    fun modify(f: (S) -> S): T = state.modify(obj, f)

}

val <T, S> BoundLens<T, S?>.nullable: BoundLens<T, S>
    get() = compose(nullableOptional())

val <T, S> BoundLens<T, Option<S>>.option: BoundLens<T, S>
    get() = compose(optionOptional())

fun <T> T.lensing(): BoundLens<T, T> = BoundLens(InitialLensState(), this)