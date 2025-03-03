import cats._
import cats.implicits._

object DTransformer {
  // A function that traverses the D-structure in the M monad
  def f[M[_]: Monad, A, B](g: A => M[B])(doc: D[A]): M[D[B]] =
    doc match {
      case Leaf(a)  => g(a).map(b => Leaf(b))
      case H(cells) => cells.traverse(f(g)).map(newCells => H(newCells))
      case V(cells) => cells.traverse(f(g)).map(newCells => V(newCells))
    }
}