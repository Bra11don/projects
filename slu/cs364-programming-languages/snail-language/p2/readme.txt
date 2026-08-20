CS-364 - Programming Languages - Brandon Dickson & Anja Simic
Version 1.01 - Sat Sept  28 19:09:42 EST 2024

Topological Sorting with Cycle Detection in Snail Language

Overview

This project implements a topological sorting algorithm with cycle detection using the Snail programming language. The program reads a list of tasks and their dependencies from standard input and outputs an ordered list of tasks that respects these dependencies. If a cycle is detected in the dependencies, the program outputs "cycle".

The implementation uses a recursive Depth-First Search (DFS) algorithm to perform the topological sort and detect cycles. It also includes supporting data structures such as a Graph, Node, ArrayList, and custom List and Cons classes to manage collections and lists in Snail.

---

Files

- p2.sl: Contains the main program logic, including the recursive DFS implementation, and classes.
- references.txt: provides a citation for each resource used to complete the project.
- read-me.txt
- test-1.txt: a valid novel task list to test our program
- p2.py: the python implementation of the project

---

How It Works

Input Format

The program reads pairs of strings from standard input:

- First String: The task that depends on another task.
- Second String: The task it depends on.

The input ends when an empty string is encountered as the second string.

Example Input:

    TaskA
    TaskB
    TaskB
    TaskC
    TaskC
    TaskD

In this example:

- TaskA depends on TaskB.
- TaskB depends on TaskC.
- TaskC depends on TaskD.

Algorithm

1. Graph Construction:
   - Reads the input and builds a directed graph where nodes represent tasks and edges represent dependencies.
   - Each node maintains a list of its neighboring nodes (dependencies).

2. Recursive DFS with Cycle Detection:
   - Initializes a recursive DFS traversal starting from unvisited nodes with the smallest ASCII names.
   - Uses two flags in each node:
     - visiting: Indicates the node is currently being explored.
     - visited: Indicates the node and all its dependencies have been fully explored.
   - Cycle Detection:
     - If a node is encountered that is already visiting, a cycle is detected.
     - The traversal stops, and the program outputs "cycle".

3. Topological Sorting:
   - Upon fully exploring a node (after all its dependencies), the node's name is added to the output list.
   - The output list is printed in the order tasks should be completed.

Output

- If the graph has no cycles, the program outputs the tasks in a valid topological order, one per line.
- If a cycle is detected, the program outputs:

cycle

---

Usage Instructions

1. Run the compiled program using "snail {file_name}"
2. Provide Input:
   - Using txt test Files
      - "snail {file_name} < {test_file}"
   - Using standard input
      - Enter pairs of tasks and their dependencies.
      - End the input by providing an empty string as the second task.

Example:

    get a job
    have experience
    have experience
    work on a job
    work on a job
    get a job
    [Press Enter Twice to End Input]

4. View Output:
   - The program will display the tasks in the order they should be completed.
   - If there is a cycle, it will display "cycle".

---

Code Structure

1. Main Class (Main)

- Variables:
  - g: An instance of Graph to hold tasks and dependencies.
  - cycleDetected: A boolean flag for cycle detection.
  - output: An ArrayList to store the ordered tasks.

- Methods:
  - main(): Orchestrates the program flow, including reading input, building the graph, and initiating the DFS traversal.
  - dfs(): Manages the recursive DFS traversal and handles cycle detection.
  - dfs_visit(node_name): Recursively visits nodes to perform DFS and topological sorting.
  - selectNode(): Selects the next unvisited node with the smallest ASCII name.

2. Graph Class (Graph)

- Manages the collection of nodes.
- Methods:
  - addNode(name): Adds a node to the graph if it doesn't exist.
  - getNode(nodeName): Retrieves a node by its name.
  - getNodeByIdx(idx): Retrieves a node by its index.
  - size(): Returns the number of nodes in the graph.
  - print(): (Optional) Prints the graph's nodes and their neighbors.

3. Node Class (Node)

- Represents a task in the graph.
- Variables:
  - name: The task's name.
  - visited: Indicates if the node has been fully explored.
  - visiting: Indicates if the node is currently being explored.
  - neighbors: An ArrayList of neighboring nodes (dependencies).
- Methods:
  - setVisiting(): Marks the node as visiting.
  - setNotVisiting(): Marks the node as not visiting.
  - visit(): Marks the node as visited.
  - isVisiting(): Checks if the node is visiting.
  - isVisited(): Checks if the node is visited.
  - getName(): Returns the node's name.
  - setName(theName): Sets the node's name.
  - getNeighbors(): Returns the list of neighbor nodes.
  - addNeighbor(nodeName): Adds a neighbor to the node.

4. ArrayList Class (ArrayList)

- Implements a dynamic array to store elements.
- Variables:
  - data: An array to hold elements.
  - size: The current number of elements.
- Methods:
  - add(el): Adds an element to the array list.
  - get(i): Retrieves an element at a specific index.
  - size(): Returns the number of elements.
  - ascii_sort(): Sorts the elements in ascending ASCII order.
  - print(): Outputs the elements.
  - check_resize(): Resizes the array when it's full.

5. List and Cons Classes (List, Cons)

- Implement a linked list structure.
- List Class:
  - Represents an empty list.
  - Methods:
    - cons(hd): Adds an element to the list.
    - append(el): Adds an element to the end of the list.
    - isEmpty(): Checks if the list is empty.
    - print(): Prints the list.
- Cons Class:
  - Represents a non-empty list.
  - Variables:
    - xcar: The head element.
    - xcdr: The tail of the list.
  - Methods:
    - init(hd, tl): Initializes the list with a head and tail.
    - append(el): Adds an element to the end of the list.
    - head(): Returns the head element.
    - tail(): Returns the tail of the list.
    - isEmpty(): Checks if the list is empty.
    - print(): Prints the list recursively.

---

Design Decisions

- In designing this project, we chose to implement the graph using custom `Graph` and `Node` classes to effectively manage tasks and their dependencies.
- The `ArrayList` was utilized for dynamic storage of nodes and their neighbors, leveraging its `add` and `ascii_sort` functions to maintain order and scalability.
- Recursive DFS was selected over iterative approaches due to its simplicity and reliability

- Some of the challenges we encountered while using snail were:
   - The syntax. It really confused us but eventually forced us to not think of shortcuts or libraries and really go back to the basics (something that wasnt that clear in our heads before)
   - Ensuring accurate cycle detection demanded meticulous handling of node states during traversal.

---

Sample Run

Input:

    learn C
    understand C pointers
    learn C
    read the C tutorial
    do PA1
    learn C
    [Press Enter Twice to End Input]

Output:

    read the C tutorial
    understand C pointers
    learn C
    do PA1

---

Handling Cycles

If the input contains a cycle, the program detects it during the DFS traversal and outputs "cycle".

Example Input with Cycle:

    get a job
    have experience
    have experience
    work on a job
    work on a job
    get a job
    [Press Enter Twice to End Input]

Output:

    cycle

---

Notes

- The recursive DFS implementation is more straightforward and reliable for cycle detection .
- The iterative DFS version was considered but faced challenges in correctly choosing among unconstrained tasks and has been partly omitted in favor of the recursive approach.
- The program sorts neighbors in ascending ASCII order to ensure deterministic output when multiple nodes are available to process.

---

Conclusion

This project demonstrates how to perform topological sorting with cycle detection in Snail using a recursive DFS algorithm. It handles dependencies between tasks efficiently and provides accurate detection of cycles, ensuring reliable task ordering or appropriate error reporting.
