/*
 * Prints the string "Hello, world.\n" to standard output
 */

class Main : IO {
    main() {
        print_string("Hello, world.\n");
        //read input from user then print it
        print_string("Enter a string: ");
        print_string(read_string());

    };
};