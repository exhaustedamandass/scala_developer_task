sealed trait D[A]
final case class H[A](cells: List[D[A]]) extends D[A]
final case class V[A](cells: List[D[A]]) extends D[A]
final case class Leaf[A](value: A)       extends D[A]

