package scala.tools.nsc.util

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Builder
import scala.collection.mutable.SetBuilder
import scala.runtime.AbstractFunction1

/** A bare-bones implementation of a mutable `Set` that uses weak references
 *  to hold the elements.
 *
 *  This implementation offers only add/remove/test operations,
 *  therefore it does not fulfill the contract of Scala collection sets.
 */
class WeakHashSet[T <: AnyRef] extends AbstractFunction1[T, Boolean] {
  private val underlying = mutable.HashSet[WeakReferenceWithEquals[T]]()

  /** Add the given element to this set. */
  def +=(elem: T): this.type = {
    underlying += new WeakReferenceWithEquals(elem)
    this
  }

  /** Remove the given element from this set. */
  def -=(elem: T): this.type = {
    underlying -= new WeakReferenceWithEquals(elem)
    this
  }

  /** Does the given element belong to this set? */
  def contains(elem: T): Boolean =
    underlying.contains(new WeakReferenceWithEquals(elem))

  /** Does the given element belong to this set? */
  def apply(elem: T): Boolean = contains(elem)

  /** Return the number of elements in this set, including reclaimed elements. */
  def size = underlying.size

  /** Remove all elements in this set. */
  def clear() = underlying.clear()
}

/** A WeakReference implementation that implements equals and hashCode by
 *  delegating to the referent.
 */
class WeakReferenceWithEquals[T <: AnyRef](ref: T) {
  def get(): T = (underlying.get()).asInstanceOf[T]

  override val GetHashCode = ref.GetHashCode

  override def Equals(other: Any): Boolean = other match {
    case wf: WeakReferenceWithEquals[_] =>
      (underlying.get()).asInstanceOf[T] == wf.get()
    case _ =>
      false
  }

  private val underlying = new java.lang.ref.WeakReference(ref)
}
