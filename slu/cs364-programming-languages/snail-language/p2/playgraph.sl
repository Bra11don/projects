// Main class that contains the program logic
class Main : IO {
    let g = new Graph;             // Graph to hold the tasks and their dependencies
    let cycleDetected = false;     // Flag to indicate if a cycle has been detected
    let output = new ArrayList;    // List to store the tasks in topological order

    //where bfs() was

    /**
    // Iterative DFS function
    //hmmm. for dfs I need to have its stack implementation
    // proces the neighbors in reverse order
    // use the cons instead of append
    // return a list of the visited nodes in order
    dfs() {
        cycleDetected = false;     // Initialize the cycle detection flag
        output = new List;         // Initialize the output list as an empty List

        let n = selectNode();      // pick a starting node

        // Continue DFS until all nodes are visited
        while (!(n == "")) {
            let s = new List;      // Initialize the stack
            s = s.cons(n);         // Push the starting node onto the stack

            // Iterative DFS using the stack
            while (!(s.isEmpty())) {
                let el_name = s.head();  // Get the node at the top of the stack
                s = s.tail();              // Pop the node from the stack
                let node = g.getNode(el_name);

                if (node.isVisited()) {
                    // Node already visited; do nothing
                    false;
                } else {
                    if (node.isVisiting()) {
                        // Node is being processed; we have returned to it after processing its neighbors
                        node.visit();          // Mark the node as visited
                        output = output.cons(el_name);  // Add the node to the output list
                    } else {
                        // Mark the node as visiting
                        node.setVisiting();

                        // Push the node back onto the stack to process after its neighbors
                        s = s.cons(el_name);

                        // Get neighbors and sort them in ascending order
                        let neighbors = node.getNeighbors();
                        neighbors.ascii_sort();

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
            output;  // return the output list
        }else{
            // Return an empty list to indicate a cycle
            new List;
        };
    };
    **/

    dfs() {
        cycleDetected = false;     // Initialize the cycle detection flag
        output = new List;         // Initialize the output list as an empty List

        let n = selectNode();      // Pick a starting node

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
                } else if (node.isVisiting()) {
                    // All neighbors have been processed; mark node as visited
                    node.visit();
                    output = output.cons(node_name);  // Add the node to the output list
                } else {
                    // Node is unvisited; process it
                    node.setVisiting();   // Mark the node as visiting

                    // Push the node back onto the stack to process after its neighbors
                    s = s.cons(node_name);

                    // Get neighbors and sort them in ascending order
                    let neighbors = node.getNeighbors();
                    neighbors.ascii_sort();

                    // Push neighbors onto the stack
                    let size = neighbors.size();
                    let i = size - 1;
                    while (i >= 0) {
                        let neighbor_name = neighbors.get(i);
                        let neighbor_node = g.getNode(neighbor_name);

                        if (neighbor_node.isVisiting()) {
                            // Cycle detected
                            cycleDetected = true;
                            s = new List;  // Clear the stack to exit the loop
                            i = -1;        // Exit the loop
                        } else if (neighbor_node.isVisited()) {
                            // Neighbor already visited; do nothing
                            false;
                        } else {
                            // Push unvisited neighbor onto the stack
                            s = s.cons(neighbor_name);
                        };
                        i = i - 1;
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
            output;  // Return the output list
        } else {
            new List;  // Return an empty list to indicate a cycle
        };
    };


    /**
    //ALTERNATIVELY DOING IT RECURSIVELY
    // Recursive DFS function to perform topological sort with cycle detection
    dfs() {
        cycleDetected = false;     // Initialize the cycle detection flag
        output = new ArrayList;    // Initialize the output list

        let n = selectNode();      // Select the next unvisited node

        // Continue DFS until all nodes are visited or a cycle is detected
        while (!(n == "")) {
            if (!cycleDetected) {
                dfs_visit(n);      // Perform DFS starting from node 'n'
                n = selectNode();  // Select the next unvisited node
            } else {
                n = "";            // Exit the loop if a cycle is detected
            };
        };

        if (cycleDetected) {
            print_string("cycle\n");  // Print "cycle" if a cycle is detected
            // Return an empty list to indicate a cycle
            (new ArrayList);
        } else {
            // Output is already populated
            output;
        };
    };

    // Recursive DFS visit function for a given node
    dfs_visit(el_name) {
        let node = g.getNode(el_name);  // Get the node object by name

        if (node.isVisited()) {
            // Node has already been visited; do nothing
            false;
        } else {
            if (node.isVisiting()) {
                // Node is currently being visited; a cycle is detected
                cycleDetected = true;
            } else {
                // Mark the node as visiting
                node.setVisiting();

                // Get the list of dependencies (neighbors) and sort them
                let neighbors = node.getNeighbors();
                neighbors.ascii_sort();  // Sort neighbors in ascending ASCII order

                // Traverse each dependency
                let i = 0;
                while (i < neighbors.size()) {
                    if (!cycleDetected) {
                        let neighbor_name = neighbors.get(i);
                        dfs_visit(neighbor_name);  // Recursively visit the neighbor
                        i = i + 1;
                    } else {
                        // Exit the loop if a cycle is detected
                        i = neighbors.size();
                    };
                };

                // Mark the node as visited and not visiting
                node.visit();
                node.setNotVisiting();

                // Add the node to the output list
                output.add(el_name);
            };
        };
    };
    **/

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
                        false;
                    };
                };
            } else {
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
        let result = dfs();

        // Reverse the output list
        let reversedOutput = new List;
        while (!result.isEmpty()) {
            reversedOutput = reversedOutput.cons(result.head());
            result = result.tail();
        };

        // Print the reversed output
        reversedOutput.print();


        /**
        //for the recursive version
        // Run the DFS
        let lst = dfs();

        // Check if a cycle was detected
        if (cycleDetected) {
            // "cycle" message already printed
            false;
        } else {
            output.print();
        };

        **/

        // Print the graph (optional)
        // g.print();
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

    // Print the graph
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

// While snail has arrays, there's nothing with dynamic sizing
// so let's make a rudimentary ArrayList/Vector
// Classes can appear in any order, so it's possible to place
// this *after* the Main definition
// ArrayList class to implement a dynamic array
class ArrayList : IO {
    // we will initially make room for 10 elements
    let data = new[10] Array;
    // the starting size is 0
    let size = 0;

    // add to the end of the ArrayList
    add(el) {
        // see if we need to resize
        check_resize();

        // insert the value at the end
        data[size] = el;

        // increment the size
        // incidentally, this is also the return value
        size = size + 1;
    }; // add()

    // return size
    size(){
        size;
    };

    // get a particular index
    get(i){
        if(i<0){
            //index too low
            abort();
        }else{
            if (size <=i){
                //index too high
                abort();
            }else{
                // index just right
                data[i];
            };
        };
    };


    // if the ArrayList fills up, we double the number of elements
    check_resize() {
        if (data.length() == size) {
            // the array is full

            // store a temp copy of the data
            let tmp = data;

            // make a new array that is twice the length
            data = new[size * 2] Array;

            // loop through the data and copy it over
            let i = 0;
            while (i < size) {
                // copy the value from tmp to data
                data[i] = tmp[i];
                // increment i
                i = i + 1;
            }; // while
        } else {
            // we need something in the else, so just produce false
            false;
        };
    }; // check_resize()

    // a simple bubble sorting algorithm that works in reverse order
    sort() {
        let i = 0;
        while (i < size) {
            let j = size - 1;

            // bubble sort by bubbling large values to the left
            while (i < j) {
                // if the value to the left is smaller than the current
                // swap the two values
                if (data[j-1] < data[j]) {
                    let tmp = data[j-1];
                    data[j-1] = data[j];
                    data[j] = tmp;
                } else {
                    // just a place holder
                    0;
                };
                // decrement j
                j = j - 1;
            }; // while_j

            // increment i
            i = i + 1;
        }; // while_i
    }; // sort()

    // Sort the elements in ascending ASCII order
    ascii_sort () {
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


// The List type is not built into Snail, so we'll define it here.
// Snail classes can appear in any order, so let's put it after Main.
class List : IO {
    // You can think of the List class as an empty List. The Cons class
    // will contain data
    // We need methods: cons, append, head, tail, isEmpty, print

    // cons returns a list with the new element at the beginning of the list
    // (the head) and self as the tail (rest).
    cons(hd) {
        (new Cons).init(hd, self);
    };//cons()

    // Add to end (append)
    append(el){
        // Same as adding to the beginning
        cons(el);
    };

    head(){
        //return the first element
        abort();  // Cannot get head of empty list
    };

    tail(){
        //return the last element
        abort();  // Cannot get tail of empty list
    };

    // Check if the list is empty
    isEmpty(){
        true;
    };

    // insertion sorting into an empty list
    // just returns the singleton list
    insert(s) {
        cons(s);
    }; // insert()

    // print does nothing for an empty list
    // IO returns self, so let's be consistent
    print() {
        self;
    }; // print()
};//class List

// Cons is where all the elements are stored
// A Cons object is a non-empty list
class Cons : List {
    let xcar; // xcar is the contents of the head
    let xcdr; // xcdr is the tail (rest of the list)

    // init will populate data into the Cons object and return itself
    init(hd, tl) {
        xcar = hd;
        xcdr = tl;
        self;
    };//init()

    // Add to end (append)
    append(el){
        (new Cons).init(xcar, xcdr.append(el));
    };

    //take off and remove the first element
    tail(){
        xcdr;
    };

    //take off and remove the last element
    head(){
        xcar;
    };

    // Check if the list is empty
    isEmpty(){
        false;
    };

    // insert does insertion sort (using a reverse comparison)
    insert(s) {
        if (!(s < xcar)) { // reverse order of comparison
            // s is bigger than the current head,
            // so add it to the beginning
            (new Cons).init(s, self);
        } else {
            // otherwise, pass it down the list to insert
            (new Cons).init(xcar, xcdr.insert(s));
        };
    }; // insert()

    // output the list recursively
    // where is the base case?
    print() {
        print_string(xcar.concat("\n"));
        xcdr.print();
    }; // print()
};//class cons

/**
// bfs implementation
// return a list of  the visited nodes in order
    bfs() {
        let output = new ArrayList;
        let q = new List;

        //pick a starting node
        let n = selectNode();

        // repeatedly bfs the graph
        while (!(n == "")){
            // mark start as visited
            g.getNode(n).visit();

            //append to queue
            q = q.append(n);

            //while the queue is not empty
            while (!(q.isEmpty())){
                //get the first element
                let el_name = q.head();
                //remove the first element
                q = q.tail();
                //add the node to the output
                output.add(el_name);
                //get the neighbors of the node
                let neighbors = g.getNode(el_name).getNeighbors();
                //loop through the neighbors
                let i = 0;
                while (i < neighbors.size()){
                    //get the neighbor
                    let neighbor_name = neighbors.get(i);
                    //if the neighbor has not been visited
                    if (g.getNode(neighbor_name).isVisited()){
                        //do nothing
                        false;
                    } else {
                        //mark the neighbor as visited
                        g.getNode(neighbor_name).visit();
                        //add the neighbor to the queue
                        q = q.append(neighbor_name);
                    };
                    i = i + 1;
                };
            };
            n = selectNode();
        };

        output;
    };
    **/
