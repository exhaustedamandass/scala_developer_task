import DTransformer.f
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DSpec extends AnyFunSuite with Matchers {

  test("D data structure can be constructed correctly") {
    val doc: D[Int] = H(List(Leaf(1), V(List(Leaf(2), Leaf(3))), Leaf(4)))
    doc shouldBe a[H[_]]
    val hDoc = doc.asInstanceOf[H[Int]]

    // The top-level is H with 3 cells
    hDoc.cells.size shouldBe 3
    hDoc.cells.head shouldBe Leaf(1)
    hDoc.cells(1) shouldBe a[V[_]]
    hDoc.cells(2) shouldBe Leaf(4)
  }

  test("f with identity monad should act as identity transformation") {
    import cats.Id
    val doc: D[Int] = V(List(Leaf(10), H(List(Leaf(20), Leaf(30)))))

    // g is the identity function when M = Id
    val g: Int => Id[Int] = identity

    // Apply f in the identity monad
    val result: D[Int] = f[Id, Int, Int](g)(doc)

    // We expect no change to the structure or values
    result shouldBe doc
  }

  test("f with Option monad yields Some(...) if all transformations succeed") {
    val doc: D[Int] = H(List(Leaf(1), Leaf(2), V(List(Leaf(3)))))

    // A => Option[B] that always succeeds (returns Some)
    val g: Int => Option[String] = n => Some(s"value-$n")

    // We expect a Some(...) wrapping the transformed document
    val maybeDoc: Option[D[String]] = f[Option, Int, String](g)(doc)
    maybeDoc.isDefined shouldBe true

    // Check structure inside the Some
    val transformedDoc = maybeDoc.get
    transformedDoc shouldBe
      H(List(Leaf("value-1"), Leaf("value-2"), V(List(Leaf("value-3")))))
  }

  test("f with Option monad yields None if at least one leaf fails") {
    // A doc with a negative leaf
    val doc: D[Int] = V(List(Leaf(10), Leaf(-5), Leaf(7)))

    // A => Option[B] that fails on negative inputs
    val g: Int => Option[String] = {
      case n if n >= 0 => Some(n.toString)
      case _           => None
    }

    val maybeDoc: Option[D[String]] = f[Option, Int, String](g)(doc)
    maybeDoc shouldBe None
  }

  test("f with Either to accumulate or propagate an error") {
    type ErrorOr[A] = Either[String, A]

    val doc: D[Int] = H(List(Leaf(1), Leaf(0), Leaf(2)))

    // A => Either[String, B] that fails if the value is zero
    val g: Int => ErrorOr[String] = {
      case 0 => Left("Zero is not allowed.")
      case n => Right(s"$n!")
    }

    // 1. Leaves are 1, 0, 2 => the second leaf is zero => should fail
    val res1: ErrorOr[D[String]] = f[ErrorOr, Int, String](g)(doc)
    res1 shouldBe Left("Zero is not allowed.")

    // 2. If we remove the zero from doc => success
    val doc2: D[Int] = H(List(Leaf(1), Leaf(2)))
    val res2: ErrorOr[D[String]] = f[ErrorOr, Int, String](g)(doc2)
    res2 shouldBe Right(H(List(Leaf("1!"), Leaf("2!"))))
  }
}