//38a1108d19f54a1fab115779487fbbcea3ae1843

//Given a unary function, f, and an integer tree, t, return a new integer tree with f applied to all nodes of t
open P1btree;

let rec int_tree_map = (f: int => int, t: int_tree) : int_tree => {
  switch (t) {
  | EmptyTree => EmptyTree
  | TreeNode(value, left, right) =>
      TreeNode(
        f(value),
        int_tree_map(f, left),
        int_tree_map(f, right)
      )
  }
};

//alternatively using tail recursion
/* let int_tree_map = (f: int => int, t: int_tree) : int_tree => {
  let rec helper = (node: int_tree, cont: int_tree => int_tree) : int_tree => {
    switch (node) {
    | EmptyTree => cont(EmptyTree)
    | TreeNode(value, left, right) =>
      helper(left, leftResult =>
        helper(right, rightResult =>
          cont(TreeNode(f(value), leftResult, rightResult))
        )
      )
    }
  };
  helper(t, tree => tree);
}; */
