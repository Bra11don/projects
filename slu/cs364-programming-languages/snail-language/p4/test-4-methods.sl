class Test : IO {
    // Test arithmetic precedence
    let a = 1 + 2 * 3 / 4;     // * and / before +
    let b = (1 + 2) * (3 + 4); // Testing parentheses vs operators
    let c = ~5 + 3 * 2;        // Unary operator with binary operators

    test() {


        // Valid precedence tests
        let y = (new Test);
        y.print_int(1 + 2 * 3);        // Method call with arithmetic

        // Array operations with precedence
        let arr = new[2 + 3 * 2] Array;  // Complex size expression
        arr[1 + 2] = 3 * 4;              // Array access with arithmetic

        // Complex dispatch chains with arithmetic
        (y@Test.test()).print_int(5);    // Static dispatch with parentheses

        // Multiple unary operators
        let z = ~~5;                     // Double negation
        let w = not (5 < 3);             // Not with comparison

        0;  // Return value
    };

    main() {
        let t = new Test;
        t.test();
    };
};
