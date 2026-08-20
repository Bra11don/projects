
class Main : IO {
    let g = new Graph;

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

    //hmmm. for dfs I need to have its stack implementation
    // proces the neighbors in reverse order
    // use the cons instead of append
    // return a list of the visited nodes in order
    dfs () {
        let output = new ArrayList;
        let s = new List;
        let cycle_detected = false;

        //pick a starting node
        let n = selectNode();

        // repeatedly dfs the graph
        while (!(n=="")){
            // mark as visited
            g.getNode(n).visit();

            //append to stack
            s = s.cons(n);

            //while the stack is not empty
            while (!(s.isEmpty())){
                //get the first element
                let el_name = s.head();
                //remove the first element
                s = s.tail();
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
                        //add the neighbor to the stack
                        s = s.cons(neighbor_name);
                    };
                    i = i + 1;
                };
            };
            n=selectNode();
        };
        output;
    };


    //pick a node to start from and return its name
    selectNode () {
        let i = 0;
        let n;
        let found = false;
        while (i < g.size()){
            n = g.getNodeByIdx(i);
            if(!(n.isVisited())){
                found = true;
                i = g.size();
            }else{
            i = i + 1;
            };
        };
        if (found){
            n.getName();
        }else{
            //just a void value
            "";
        };
    };


    main() {
        //loop to read the data
        let done = false;
        while (!done) {
            let dest = read_string();
            let src = read_string();

            //check the second variable to see if its empty
            if (src == "") {
                done = true;
            } else {
                //src -> dest
                g.addNode(src);
                g.addNode(dest);
                g.getNode(src).addNeighbor(dest);
            };
        };

        //run the bfs
        // let lst = bfs();
        // lst.print();

        //run the dfs
        // let lst2 = dfs();
        // lst2.print();

        // Run the DFS
    let lst2 = dfs();

    // Check if the list is empty (indicating a cycle was detected)
    if (lst2.size() < 1) {
        false;  // Do nothing since the "cycle" message is already printed
    } else {
        lst2.print();
    };

        //print the graph
        // g.print();

    };
};



//represent a full graph
class Graph : IO {
    let nodes = new ArrayList;

    addNode (name){
        // add node and return the size of the nodes
        if (isVoid(getNode(name))){
            let newNode = new Node;
            newNode.setName(name);
            nodes.add(newNode);
        }else{
            //return the existing size
            nodes.size();
        };
        // //deal with duplicates too
        // let i = 0;
        // let found = false;
        // while (i < nodes.size()){
        //     if (nodes.get(i).getName() == name){
        //         found = true;
        //     }
        //     i = i + 1;
        // }
        // if (!found){
        //     let newNode = new Node;
        //     newNode.setName(name);
        //     nodes.add(newNode);
        // }else{
        //     //return a void value
        //     let foo;
        //     foo;
        // }
    };

    //get node by index
    getNodeByIdx (idx){
        nodes.get(idx);
    };

    //get the size of the nodes
    size(){
        nodes.size();
    };

    //get a node that matches by name
    getNode (nodeName){
        let i = 0;
        let n;
        let found = false;
        while (i < nodes.size() ){
            n = nodes.get(i);
            if (n.getName() == nodeName){
                //break out of the loop
                i = nodes.size();
                //set found to true
                found = true;
            }else{
                //increment i
                i = i + 1;
            };
        };
        if (found){
            n;
        }else{
            //just a void value
            let foo;
            foo;
        };
    };

    //print the graph
    print(){
        let i = 0;
        while (i < nodes.size()){
            let n = nodes.get(i);
            print_string(n.getName().concat(": "));
            let j = 0;
            let neighbors = n.getNeighbors();
            while (j < neighbors.size()){
                print_string(neighbors.get(j).getName().concat(" "));
                j = j + 1;
            };
            print_string("\n");
            i = i + 1;
        };
    };
};

//store a node in the graph
class Node{
    let name = "";
    // visited indicates that a node has been fully processed and added to the output list.
    let visited = false;
    let neighbors = new ArrayList;

    //visiting marks nodes that are currently being processed in the stack.
    //If we encounter a node marked as visiting during DFS, it means there is a cycle in the graph.
    let visiting = false;

    // the node is currently being visited
    setVisiting() {
        visiting = true;
    };

    // the node is no longer being visited
    setNotVisiting() {
        visiting = false;
    };

    // will return true if the node is in the recursion block
    isVisiting() {
        visiting;
    };

    //return the name of the node
    getName (){
        name;
    };

    // set name and return the self object
    setName (theName){
        name = theName;
        self;
    };

    visit() {
        visited = true;
    };

    isVisited () {
        visited;
    };

    //retuurn the list of neighbors nodes
    getNeighbors () {
        neighbors;
    };

    // add a neighbor to the list of neighbors
    // addNeighbor (nodeName) {
    //     //check if the neighbor is already in the list
    //     //if not, add it
    //     // if it is, return the size of the neighbors
    //     //return the size of the neighbors
    //     let i = 0;
    //     let found = false;
    //     while (i < neighbors.size()) {
    //         if (neighbors.get(i).getName() == nodeName) {
    //             found = true;
    //             break;  // Exit the loop if found
    //         }else{
    //         i = i + 1;};
    //     };
    //     if (!found) {
    //         let n = new Node;
    //         n.setName(nodeName);
    //         neighbors.add(n);
    //     }else{
    //         //return the size of the neighbors
    //         neighbors.size();
    //     };

    // };

    addNeighbor (nodeName) {
        neighbors.add(nodeName);

        //check if the neighbor is already in the list
        // //if not, add it
        // let i = 0;
        // let found = false;

        // while (i < neighbors.size()) {
        //     if (neighbors.get(i).getName() == nodeName) {
        //         found = true;
        //         // No need to break; just note that it's found
        //         break;
        //     }else{
        //     i = i + 1;
        //     };
        // };

        // if (!found) {
        //     let n = new Node;
        //     n.setName(nodeName);
        //     neighbors.add(n);
        // } else{
        // neighbors.size();  // Return the size of the neighbors list
        // };
};

};

// While snail has arrays, there's nothing with dynamic sizing
// so let's make a rudimentary ArrayList/Vector
// Classes can appear in any order, so it's possible to place
// this *after* the Main definition
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

    // output the list
    print() {
        let i = 0;
        while (i < size) {
            // get the item, concatenate a newline, and print
            print_string(data[i].concat("\n"));

            i = i + 1;
        }; // while
    }; // print()
}; // class ArrayList


// The List type is not built into snail, so we'll have to define it here.
// Snail classes can appear in any order, so let's put it after Main.
class List : IO {
    // You can think of the List class as an empty List.  The Cons class
    // will contain data
    // we need three methods: cons, insert, and print

    // cons returns a list with the new element at the beginning of the list
    // (the head) and self as the tail (rest).
    cons(hd) {
        (new Cons).init(hd, self);
    }; // cons()

    //add to end
    append(el){
        //same as adding to the beginning
        cons(el);
    };

    //head and tail are illegal because the list is empty
    head(){
        //return the first element
        abort();
    };

    tail(){
        //return the last element
        abort();
    };

    //isempty?
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
}; // class List

// Cons is where all the cool stuff happens
// A Cons object is a non-empty list
class Cons : List {
    let xcar; // xcar is the contents of the head
    let xcdr; // xcdr is the tail

    // init will populate data into the Cons object and return itself
    init(hd, tl) {
        xcar = hd;
        xcdr = tl;
        self;
    }; // init()

    //add to end
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

    //isempty?
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
}; // class Cons