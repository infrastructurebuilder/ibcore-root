ibcore-dag
====

# Description

Directed Acyclic Graph library that was almost entirely copied from inside Maven.

The `ibcore-dag` module is a runtime-unencumbered DAG implementation that allows for
all the same extension that the DAG code from Maven provides, but as a JPMS module
with no dependencies.


# Use

A DAG is made up of instances of `Vertex`.  A `Vertex` must implement `Comparable<T>`, with `.compareTo() == 0` indicating
a collision with an existing node.

## Create

Build a `DAG` with a `DAGBuilder` instance.  Initially, a `MutableDAG` instance is provided, but with a call to `.build()`
an immutable `DAG` instance is created.

```java

    DAG b = new DAGBuilderImpl<String>().addEdge("1","2").addEdge("2","3").addEdge("1","4").build();


    final MutableDAG<String> dag = new DAGBuilderImpl.MutableDAGImpl<>();

    dag.addVertex("a");
```

## Walk

Walk a `DAG` using a `DAGWalker` implementation, passing a `Collection<DAGVisitor>`

```java
    final DAG<String> dag = new DAGBuilderImpl<String>().addEdge("A", "B").build();
    final List<DAGVisitor<String>> visitors = Arrays.asList(new DAGVisitor<String>() {
      @Override
      public DAGVisitResult visitNode(Vertex<String> node) {
        // I am at `node`!
        System.out.println(String.format("Node: %s,node"));
        return DAGVisitResult.CONTINUE;
      }
    });
    d.walk(dag, visitors);
```

In the above example,