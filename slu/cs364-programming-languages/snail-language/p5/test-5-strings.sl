class Main : IO {
    main() {
        let s1 = "Hello";
        let s2 = "World";

        // Test string concatenation and methods
        print_string(s1.concat(" ").concat(s2));
        print_string("\n");

        // Test substring
        print_string(s1.substr(1, 3));  // Should print "ell"
        print_string("\n");

        // Test string length
        print_int(s2.length());  // Should print 5
        print_string("\n");
    };
};
