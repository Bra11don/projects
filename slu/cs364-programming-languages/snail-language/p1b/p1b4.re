//38a1108d19f54a1fab115779487fbbcea3ae1843

// Given an integer tree, t, return a list of the tree items in the order that they are visited by a pre-order traversal.

//pre-order
//visit the root node
//recursively traverse left
//recursively traverse right

open P1btree;

let rec pre_order = (t: int_tree): list(int) => {
  switch (t) {
  | EmptyTree => []
  | TreeNode(value, left, right) => List.concat([[value], pre_order(left), pre_order(right)])
  }
};


//alternatively, using tail recursion -- took a while to figure out
// let pre_order = (t: int_tree) : list(int) => {
//   let rec helper = (tree: int_tree, acc: list(int)) : list(int) => {
//     switch (tree) {
//     | EmptyTree => acc
//     | TreeNode(value, left, right) =>
//       let withRoot = [value, ...acc];
//       let withLeft = helper(left, withRoot);
//       helper(right, withLeft);
//     };
//   };
//   List.rev(helper(t, []));
// };
