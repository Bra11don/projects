open P1btree;
open P1b4;

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

// Test the pre_order function
let result = pre_order(testTree);

// Expected result
let expected = [1, 2, 4, 5, 3, 6];

// Print the tree
print_endline("Test Tree:");
draw_int_tree(testTree);

// Print the result
print_endline("\nPre-order traversal result:");
List.iter(i => Printf.printf("%d ", i), result);
print_newline();

// Check if the result matches the expected output
if (result == expected) {
  print_endline("Test passed: The pre_order function works correctly!");
} else {
  print_endline("Test failed: The pre_order function did not produce the expected output.");
  print_endline("Expected:");
  List.iter(i => Printf.printf("%d ", i), expected);
  print_newline();
};

// Test with an empty tree
let emptyResult = pre_order(EmptyTree);
if (emptyResult == []) {
  print_endline("\nEmpty tree test passed!");
} else {
  print_endline("\nEmpty tree test failed!");
};
