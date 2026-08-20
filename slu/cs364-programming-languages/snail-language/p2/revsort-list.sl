// Snail: Reverse-sort the lines from standard input
// This version will use a "List" data structure much like what
// Reason has (nested, singly-linked)

// Main is where the program starts
// Inheriting from IO allows us to read and write strings
class Main : IO {

    // this method is invoked when the program starts
    main() {
        let l = new List; // where we will store data
        let done = false; // are we done reading input?

        while (!done) {
            let s = read_string();
            if (s == "") {
                // if we are done reading lines, then s will be empty
                done = true;
            } else {
                // insertion sort it into our list
                // we need to overwrite our old value
                l = l.insert(s);
            };
        }; // while

        // print out the result
        l.print();
    }; // main()
}; // class Main

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