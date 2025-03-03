# Samba.ai test task
`D[A]` represents a “document” that’s subdivided into cells horizontally, vertically, or just holding a single value (a “leaf”).

Function `f` that walks through the document and applies a transformation to every leaf. 

### Overview
- **`D[A]`**: A sealed trait with three cases:
    - `H[A](cells: List[D[A]])`
    - `V[A](cells: List[D[A]])`
    - `Leaf[A](value: A)`

- **`f[M[_]: Monad, A, B](g: A => M[B])`**: A function that takes your monadic transformation `g`, applies it to every leaf of type `A`, and returns an `M[D[B]]`.

### Usage
1. Add a `cats` dependency to use `Monad` and `traverse`.
2. Use the `D[A]` type in your codebase.
3. Write your transformation `(A => M[B])` and pass it to `f` to process your `D[A]`.

### Project Structure

```
├── main
│   └── scala
│       ├── D.scala            -> defines type D 
│       └── DTransformer.scala -> defines function f
└── test
    └── scala
        └── DSpec.scala        -> contains unit tests for type D
```
