open P1btree;
open P1b5;

// Test function: double the value
let double = x => x * 2;

// Create a test tree
let testTree =
  TreeNode(1,
    TreeNode(2,
      TreeNode(4, EmptyTree, EmptyTree),
      TreeNode(5, EmptyTree, EmptyTree)
    ),
    TreeNode(3,
      TreeNode(6, EmptyTree, EmptyTree),
      EmptyTree
    )
  );

// Apply int_tree_map
let resultTree = int_tree_map(double, testTree);

// Function to compare two trees
let rec treesEqual = (t1, t2) => {
  switch (t1, t2) {
  | (EmptyTree, EmptyTree) => true
  | (TreeNode(v1, l1, r1), TreeNode(v2, l2, r2)) =>
      v1 == v2 && treesEqual(l1, l2) && treesEqual(r1, r2)
  | _ => false
  }
};

// Expected result tree
let expectedTree =
  TreeNode(2,
    TreeNode(4,
      TreeNode(8, EmptyTree, EmptyTree),
      TreeNode(10, EmptyTree, EmptyTree)
    ),
    TreeNode(6,
      TreeNode(12, EmptyTree, EmptyTree),
      EmptyTree
    )
  );

// Print original tree
print_endline("Original Tree:");
draw_int_tree(testTree);

// Print resulting tree
print_endline("\nResulting Tree (after doubling all values):");
draw_int_tree(resultTree);

// Check if the result matches the expected output
if (treesEqual(resultTree, expectedTree)) {
  print_endline("\nTest passed: The int_tree_map function works correctly!");
} else {
  print_endline("\nTest failed: The int_tree_map function did not produce the expected output.");
};

// Test with an empty tree
let emptyResult = int_tree_map(double, EmptyTree);
if (emptyResult == EmptyTree) {
  print_endline("\nEmpty tree test passed!");
} else {
  print_endline("\nEmpty tree test failed!");
};

// Test with a different function: add 5 to each node
let add5 = x => x + 5;
let resultTreeAdd5 = int_tree_map(add5, testTree);

print_endline("\nOriginal Tree:");
draw_int_tree(testTree);

print_endline("\nResulting Tree (after adding 5 to all values):");
draw_int_tree(resultTreeAdd5);
