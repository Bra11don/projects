// Snail: Reverse-sort the lines from standard input

// Main is where the program starts
// Inheriting from IO allows us to read and write strings
class Main : IO {

    // this method is invoked when the program starts
    main() {
        let l = new ArrayList; // where we will store data
        let done = false; // are we done reading input?

        while (!done) {
            let s = read_string();
            if (s == "") {
                // if we are done reading lines, then s will be empty
                done = true;
            } else {
                // add to the end of the list
                l.add(s);
            };
        }; // while

        // sort the list
        l.sort();

        // print out the result
        l.print();
    }; // main()
}; // class Main

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