package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.core.ap as eitherAp
import arrow.core.combineK as eitherCombineK
import arrow.core.flatMap as eitherFlatMap
import arrow.instances.traverse as eitherTraverse

fun <L, R> Either<L, R>.combine(SGL: Semigroup<L>, SGR: Semigroup<R>, b: Either<L, R>): Either<L, R> {
  val a = this

  return when (a) {
    is Either.Left -> when (b) {
      is Either.Left -> Either.Left(SGL.run { a.a.combine(b.a) })
      is Either.Right -> a
    }
    is Either.Right -> when (b) {
      is Either.Left -> b
      is Either.Right -> Either.right(SGR.run { a.b.combine(b.b) })
    }
  }
}

@instance(Either::class)
interface EitherSemigroupInstance<L, R> : Semigroup<Either<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Either<L, R>.combine(b: Either<L, R>): Either<L, R> = fix().combine(SGL(), SGR(), b)
}

@instance(Either::class)
interface EitherMonoidInstance<L, R> : EitherSemigroupInstance<L, R>, Monoid<Either<L, R>> {
  fun MOL(): Monoid<L>
  fun MOR(): Monoid<R>

  override fun SGL(): Semigroup<L> = MOL()
  override fun SGR(): Semigroup<R> = MOR()

  override fun empty(): Either<L, R> = Right(MOR().empty())
}

@instance(Either::class)
interface EitherFunctorInstance<L> : Functor<EitherPartialOf<L>> {
  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)
}

@instance(Either::class)
interface EitherApplicativeInstance<L> : EitherFunctorInstance<L>, Applicative<EitherPartialOf<L>> {

  override fun <A> just(a: A): Either<L, A> = Right(a)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.ap(ff: Kind<EitherPartialOf<L>, (A) -> B>): Either<L, B> =
    fix().eitherAp(ff)
}

@instance(Either::class)
interface EitherMonadInstance<L> : EitherApplicativeInstance<L>, Monad<EitherPartialOf<L>> {

  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.ap(ff: Kind<EitherPartialOf<L>, (A) -> B>): Either<L, B> =
    fix().eitherAp(ff)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.flatMap(f: (A) -> Kind<EitherPartialOf<L>, B>): Either<L, B> =
    fix().eitherFlatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> =
    Either.tailRecM(a, f)
}

@instance(Either::class)
interface EitherApplicativeErrorInstance<L> : EitherApplicativeInstance<L>, ApplicativeError<EitherPartialOf<L>, L> {

  override fun <A> raiseError(e: L): Either<L, A> = Left(e)

  override fun <A> Kind<EitherPartialOf<L>, A>.handleErrorWith(f: (L) -> Kind<EitherPartialOf<L>, A>): Either<L, A> {
    val fea = fix()
    return when (fea) {
      is Either.Left -> f(fea.a).fix()
      is Either.Right -> fea
    }
  }
}

@instance(Either::class)
interface EitherMonadErrorInstance<L> : EitherApplicativeErrorInstance<L>, EitherMonadInstance<L>, MonadError<EitherPartialOf<L>, L>

@instance(Either::class)
interface EitherFoldableInstance<L> : Foldable<EitherPartialOf<L>> {

  override fun <A, B> Kind<EitherPartialOf<L>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <G, A, B, C> EitherOf<A, B>.traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Either<A, C>> = GA.run {
  fix().fold({ just(Either.Left(it)) }, { f(it).map({ Either.Right(it) }) })
}

@instance(Either::class)
interface EitherTraverseInstance<L> : EitherFoldableInstance<L>, Traverse<EitherPartialOf<L>> {

  override fun <G, A, B> Kind<EitherPartialOf<L>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<EitherPartialOf<L>, B>> =
    fix().eitherTraverse(AP, f)
}

@instance(Either::class)
interface EitherSemigroupKInstance<L> : SemigroupK<EitherPartialOf<L>> {

  override fun <A> Kind<EitherPartialOf<L>, A>.combineK(y: Kind<EitherPartialOf<L>, A>): Either<L, A> =
    fix().eitherCombineK(y)
}

@instance(Either::class)
interface EitherEqInstance<in L, in R> : Eq<Either<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Either<L, R>.eqv(b: Either<L, R>): Boolean = when (this) {
    is Either.Left -> when (b) {
      is Either.Left -> EQL().run { a.eqv(b.a) }
      is Either.Right -> false
    }
    is Either.Right -> when (b) {
      is Either.Left -> false
      is Either.Right -> EQR().run { this@eqv.b.eqv(b.b) }
    }
  }

}

@instance(Either::class)
interface EitherShowInstance<L, R> : Show<Either<L, R>> {
  override fun Either<L, R>.show(): String =
    toString()
}

class EitherContext<L> : EitherMonadErrorInstance<L>, EitherTraverseInstance<L>, EitherSemigroupKInstance<L> {
  override fun <A, B> Kind<EitherPartialOf<L>, A>.map(f: (A) -> B): Either<L, B> =
    fix().map(f)
}

class EitherContextPartiallyApplied<L> {
  infix fun <A> extensions(f: EitherContext<L>.() -> A): A =
    f(EitherContext())
}

fun <L> ForEither(): EitherContextPartiallyApplied<L> =
  EitherContextPartiallyApplied()
