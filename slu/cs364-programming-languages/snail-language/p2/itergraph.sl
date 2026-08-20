// Main class that contains the program logic
class Main : IO {
    let g = new Graph;             // Graph to hold the tasks and their dependencies
    let cycleDetected = false;     // Flag to indicate if a cycle has been detected
    let output = new List;         // List to store the tasks in topological order (using List and Cons)

    // Iterative DFS function
    dfs() {
        cycleDetected = false;     // Initialize the cycle detection flag
        output = new List;         // Initialize the output list as an empty List

        let n = selectNode();      // Select the next unvisited node

        // Continue DFS until all nodes are visited
        while (!(n == "")) {
            let s = new List;      // Initialize the stack
            s = s.cons(n);         // Push the starting node onto the stack

            // Iterative DFS using the stack
            while (!(s.isEmpty())) {
                let node_name = s.head();  // Get the node at the top of the stack
                s = s.tail();              // Pop the node from the stack
                let node = g.getNode(node_name);

                if (node.isVisited()) {
                    // Node already visited; do nothing
                    false;
                } else {
                    // Since Snail doesn't have 'else if', we nest the 'if' statements
                    if (node.isVisiting()) {
                        // Node is being processed; we have returned to it after processing its neighbors
                        node.visit();          // Mark the node as visited
                        output = output.cons(node_name);  // Add the node to the output list
                    } else {
                        // Mark the node as visiting
                        node.setVisiting();

                        // Push the node back onto the stack to process after its neighbors
                        s = s.cons(node_name);

                        // Get neighbors and sort them in ascending order
                        let neighbors = node.getNeighbors();
                        neighbors.sort();

                        // Push neighbors onto the stack
                        let size = neighbors.size();
                        let i = 0;
                        while (i < size) {
                            // Access neighbors in reverse order to maintain correct processing order
                            let index = size - 1 - i;
                            let neighbor_name = neighbors.get(index);
                            let neighbor_node = g.getNode(neighbor_name);

                            if (neighbor_node.isVisiting()) {
                                // Cycle detected
                                cycleDetected = true;
                                s = new List;  // Clear the stack to exit the loop
                                i = size;      // Exit the loop
                            } else {
                                // Since we can't use 'else if', we nest another 'if'
                                if (neighbor_node.isVisited()) {
                                    // Neighbor already visited; do nothing
                                    false;
                                } else {
                                    // Push unvisited neighbor onto the stack
                                    s = s.cons(neighbor_name);
                                };
                            };
                            i = i + 1;
                        };
                    };
                };
            };

            if (cycleDetected) {
                print_string("cycle\n");
                n = "";  // Exit the loop
            } else {
                n = selectNode();  // Select the next unvisited node
            };
        };

        if (!cycleDetected) {
            output.print();  // Print the output list
        }else{
            // Return an empty list to indicate a cycle
            (new ArrayList);
        };
    };

    // Select the next unvisited node with the smallest ASCII name
    selectNode () {
        let i = 0;
        let n;
        let found = false;
        let minName = "";

        // Iterate through all nodes in the graph
        while (i < g.size()){
            n = g.getNodeByIdx(i);

            if(!(n.isVisited())){
                if (minName == "") {
                    minName = n.getName();
                    found = true;
                } else {
                    if (n.getName() < minName) {
                        minName = n.getName();
                        found = true;
                    } else {
                        // Placeholder for required `else`
                        false;
                    };
                };
            } else {
                // Placeholder for required `else`
                false;
            };
            i = i + 1;
        };

        if (found){
            minName;  // Return the name of the node with the smallest name
        } else {
            // Return an empty string if all nodes are visited
            "";
        };
    };

    main() {
        // Loop to read the data and build the graph
        let done = false;
        while (!done) {
            let dest = read_string();  // Read the task name
            let src = read_string();   // Read the task it depends on

            // Check the second variable to see if it's empty
            if (src == "") {
                done = true;           // End of input
            } else {
                // dest depends on src
                // Add nodes to the graph if they don't exist
                g.addNode(dest);
                g.addNode(src);
                // Add an edge from 'dest' to 'src' to represent the dependency
                g.getNode(dest).addNeighbor(src);  // Corrected edge direction
            };
        };

        // Run the DFS to perform topological sorting
        dfs();
    };
};

// Graph class to represent the dependency graph
class Graph : IO {
    let nodes = new ArrayList;  // List of nodes in the graph

    // Add a node to the graph if it doesn't already exist
    addNode(name) {
        if (isVoid(getNode(name))) {
            let newNode = new Node;
            newNode.setName(name);
            nodes.add(newNode);
        } else {
            // Node already exists; do nothing
            false;
        };
    };

    // Get a node by its index in the nodes list
    getNodeByIdx(idx) {
        nodes.get(idx);
    };

    // Get the number of nodes in the graph
    size() {
        nodes.size();
    };

    // Get a node by its name
    getNode(nodeName) {
        let i = 0;
        let n;
        let found = false;
        // Iterate through the nodes to find the one with the given name
        while (i < nodes.size()) {
            n = nodes.get(i);
            if (n.getName() == nodeName) {
                found = true;
                i = nodes.size();  // Exit the loop
            } else {
                i = i + 1;
            };
        };
        if (found) {
            n;  // Return the node
        } else {
            let foo;
            foo;  // Return void if the node is not found
        };
    };

    // Print the graph (optional)
    print(){
        let i = 0;
        while (i < nodes.size()){
            let n = nodes.get(i);
            print_string(n.getName().concat(": "));
            let j = 0;
            let neighbors = n.getNeighbors();
            while (j < neighbors.size()){
                print_string(neighbors.get(j).concat(" "));
                j = j + 1;
            };
            print_string("\n");
            i = i + 1;
        };
    };
};

// Node class to represent each task
class Node{
    let name = "";                   // Name of the task
    let visited = false;             // Flag to indicate if the node has been visited
    let visiting = false;            // Flag to indicate if the node is currently being visited
    let neighbors = new ArrayList;   // List of dependencies (neighboring nodes)

    // Mark the node as currently being visited
    setVisiting() {
        visiting = true;
    };

    // Mark the node as not currently being visited
    setNotVisiting() {
        visiting = false;
    };

    // Check if the node is currently being visited
    isVisiting() {
        visiting;
    };

    // Return the name of the node
    getName (){
        name;
    };

    // Set the name of the node and return self
    setName (theName){
        name = theName;
        self;
    };

    // Mark the node as visited
    visit() {
        visited = true;
    };

    // Check if the node has been visited
    isVisited () {
        visited;
    };

    // Return the list of neighbor node names
    getNeighbors () {
        neighbors;
    };

    // Add a neighbor to the list of neighbors
    addNeighbor (nodeName) {
        neighbors.add(nodeName);
    };
};

// ArrayList class to implement a dynamic array
class ArrayList : IO {
    let data = new[10] Array;  // Underlying array to store elements
    let size = 0;              // Current number of elements

    // Add an element to the array list
    add(el) {
        check_resize();        // Check if resizing is needed
        data[size] = el;       // Add the element
        size = size + 1;       // Increment the size
    };

    // Get the current size of the array list
    size() {
        size;
    };

    // Get an element at a specific index
    get(i) {
        if (i < 0) {
            abort();           // Index out of bounds
        } else {
            if (size <= i) {
                abort();       // Index out of bounds
            } else {
                data[i];       // Return the element
            };
        };
    };

    // Check if the array needs to be resized and resize if necessary
    check_resize() {
        if (data.length() == size) {
            // The array is full

            // Store a temp copy of the data
            let tmp = data;

            // Make a new array that is twice the length
            data = new[size * 2] Array;

            // Loop through the data and copy it over
            let i = 0;
            while (i < size) {
                data[i] = tmp[i];
                i = i + 1;
            };
        } else {
            // No resizing needed
            false;
        };
    };

    // Sort the elements in ascending ASCII order
    sort() {
        let i = 0;
        while (i < size) {
            let j = size - 1;
            while (i < j) {
                if (data[j] < data[j - 1]) {
                    // Swap the elements
                    let tmp = data[j - 1];
                    data[j - 1] = data[j];
                    data[j] = tmp;
                } else {
                    // No swap needed
                    false;
                };
                j = j - 1;
            };
            i = i + 1;
        };
    };

    // Output the elements of the array list
    print() {
        let i = 0;
        while (i < size) {
            // Get the item, concatenate a newline, and print
            print_string(data[i].concat("\n"));
            i = i + 1;
        };
    };
};

// List and Cons classes
class List : IO {
    // Represents an empty list
    cons(hd) {
        (new Cons).init(hd, self);
    };

    // Adding to an empty list returns a new list with one element
    append(el){
        cons(el);
    };

    // Head and tail are illegal because the list is empty
    head(){
        abort();  // Cannot get head of empty list
    };

    tail(){
        abort();  // Cannot get tail of empty list
    };

    // Check if the list is empty
    isEmpty(){
        true;
    };

    // Print does nothing for an empty list
    print() {
        self;  // Do nothing
    };
};

class Cons : List {
    let xcar; // xcar is the contents of the head
    let xcdr; // xcdr is the tail (rest of the list)

    // Initialize the Cons cell with head and tail
    init(hd, tl) {
        xcar = hd;
        xcdr = tl;
        self;
    };

    // Append to the list (adds to the end)
    append(el){
        (new Cons).init(xcar, xcdr.append(el));
    };

    // Get the head element
    head(){
        xcar;
    };

    // Get the tail of the list
    tail(){
        xcdr;
    };

    // Check if the list is empty
    isEmpty(){
        false;
    };

    // Output the list recursively
    print() {
        xcdr.print();  // First print the tail
        print_string(xcar.concat("\n"));  // Then print the head
    };
};
